package compass_system.storagecores.base.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import compass_system.storagecores.base.data.ReloadableDataHolder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

object StylesCommands {
    private val INVALID_RESOURCE_LOCATION = SimpleCommandExceptionType(Component.translatable("commands.storagecores.invalid_resource_location"))
    private val UNKNOWN_STYLE = SimpleCommandExceptionType(Component.translatable("commands.storagecores.invalid_style"))

    fun register(builder: LiteralArgumentBuilder<CommandSourceStack?>) {
        builder.then(Commands.literal("styles")
                .then(Commands.argument("type", StringArgumentType.string())
                    .then(Commands.literal("unlocked").executes(StylesCommands::runUnlockedStylesCommand))
                    .then(Commands.literal("all").executes(StylesCommands::runAllStylesCommand))
                )
        )
    }

    private fun runUnlockedStylesCommand(context: CommandContext<CommandSourceStack>) : Int {
        val player = context.source.playerOrException
        val baseId = ResourceLocation.tryParse(StringArgumentType.getString(context, "type")) ?: throw INVALID_RESOURCE_LOCATION.create()
        val styles = ReloadableDataHolder.getStyles(baseId) ?: throw UNKNOWN_STYLE.create()

        val unlockedStyles = styles.entries.mapNotNull {
            val advancement = context.source.server.advancements.getAdvancement(it.value.advancement)

            if (player.advancements.getOrStartProgress(advancement).isDone) {
                it
            } else {
                null
            }
        }.distinct().associateBy({ it.key }, { it.value })

        if (unlockedStyles.isEmpty()) {
            context.source.sendSuccess({ Component.literal("No styles unlocked for \"$baseId\"") }, false)
        } else {
            context.source.sendSuccess( { Component.literal(buildString {
                append("Styles unlocked for \"$baseId\":\n")

                unlockedStyles.entries.forEach { (key, _) ->
                    append("  $key\n")
                }
            }) }, false)
        }

        return Command.SINGLE_SUCCESS
    }

    private fun runAllStylesCommand(context: CommandContext<CommandSourceStack>) : Int {
        val baseId = ResourceLocation.tryParse(StringArgumentType.getString(context, "type")) ?: throw INVALID_RESOURCE_LOCATION.create()
        val styles = ReloadableDataHolder.getStyles(baseId) ?: throw UNKNOWN_STYLE.create()

        context.source.sendSuccess({ Component.literal(buildString {
            append("Styles for \"$baseId\":\n")

            styles.entries.forEach { (key, _) ->
                append("  $key\n")
            }
        })}, false)

        return Command.SINGLE_SUCCESS
    }
}