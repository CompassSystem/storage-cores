package compass_system.storagecores.base.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import compass_system.storagecores.base.data.ReloadableDataHolder;
import compass_system.storagecores.base.data.styles.StyleEntry;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StylesCommands {
    private static final SimpleCommandExceptionType INVALID_RESOURCE_LOCATION =  new SimpleCommandExceptionType(Component.translatable("commands.storagecores.invalid_resource_location"));
    private static final SimpleCommandExceptionType UNKNOWN_STYLE =  new SimpleCommandExceptionType(Component.translatable("commands.storagecores.invalid_style"));

    public static void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(Commands.literal("styles")
                .then(Commands.argument("type", StringArgumentType.string())
                        .then(Commands.literal("unlocked").executes(StylesCommands::runUnlockedStylesCommand))
                        .then(Commands.literal("all").executes(StylesCommands::runAllStylesCommand))));
    }

    private static int runUnlockedStylesCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayer();

        if (player == null) {
            throw CommandSourceStack.ERROR_NOT_PLAYER.create();
        }

        ResourceLocation id = ResourceLocation.tryParse(StringArgumentType.getString(context, "type"));

        if (id == null) {
            throw INVALID_RESOURCE_LOCATION.create();
        }

        Map<ResourceLocation, StyleEntry> styles = ReloadableDataHolder.getStyles(id);

        if (styles == null) {
            throw UNKNOWN_STYLE.create();
        }

        Map<ResourceLocation, StyleEntry> unlockedAdvancements = styles.entrySet().stream().map(entry -> {
            Advancement advancement = context.getSource().getServer().getAdvancements().getAdvancement(entry.getValue().advancement());

            if (player.getAdvancements().getOrStartProgress(advancement).isDone()) {
                return entry;
            }

            return null;
        }).filter(Objects::nonNull).distinct().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        if (unlockedAdvancements.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("No styles unlocked for \"" + id + "\""), false);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("Styles unlocked for \"").append(id).append("\":\n");

            for (Map.Entry<ResourceLocation, StyleEntry> entry : unlockedAdvancements.entrySet()) {
                builder.append("  ").append(entry.getKey()).append("\n");
            }

            context.getSource().sendSuccess(() -> Component.literal(builder.toString()), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int runAllStylesCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.tryParse(StringArgumentType.getString(context, "type"));

        if (id == null) {
            throw INVALID_RESOURCE_LOCATION.create();
        }

        Map<ResourceLocation, StyleEntry> styles = ReloadableDataHolder.getStyles(id);

        if (styles == null) {
            throw UNKNOWN_STYLE.create();
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Styles for ").append(id).append(":\n");

        for (Map.Entry<ResourceLocation, StyleEntry> entry : styles.entrySet()) {
            builder.append("  ").append(entry.getKey()).append("\n");
        }

        context.getSource().sendSuccess(() -> Component.literal(builder.toString()), false);

        return Command.SINGLE_SUCCESS;
    }
}
