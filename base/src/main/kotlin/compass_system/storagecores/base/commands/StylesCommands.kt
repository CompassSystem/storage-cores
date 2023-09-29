package compass_system.storagecores.base.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import compass_system.storagecores.base.data.ReloadableDataHolder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.network.chat.Component

object StylesCommands {
    private val UNKNOWN_STYLE = SimpleCommandExceptionType(Component.translatable("commands.storagecores.invalid_style"))

    fun register(builder: LiteralArgumentBuilder<CommandSourceStack?>) {
        builder.then(Commands.literal("styles")
                .then(Commands.argument("base", StorageBaseArgument.storageBase())
                    .then(Commands.literal("unlocked").executes(StylesCommands::runUnlockedStylesCommand))
                    .then(Commands.literal("all").executes(StylesCommands::runAllStylesCommand))
                )
        )
    }

    private fun runUnlockedStylesCommand(context: CommandContext<CommandSourceStack>) : Int {
        val player = context.source.playerOrException
        val baseId = ResourceLocationArgument.getId(context, "base")
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
        val baseId = ResourceLocationArgument.getId(context, "base")
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