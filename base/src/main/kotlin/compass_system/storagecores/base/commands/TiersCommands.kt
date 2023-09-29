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

object TiersCommands {
    private val UNKNOWN_CORE = SimpleCommandExceptionType(Component.translatable("commands.storagecores.invalid_core"))

    fun register(builder: LiteralArgumentBuilder<CommandSourceStack?>) {
        builder.then(Commands.literal("tiers").then(
                Commands.argument("core", StorageCoreArgument.storageCore()).executes(TiersCommands::listTiers)
        ))
    }

    private fun listTiers(context: CommandContext<CommandSourceStack>) : Int {
        val coreId = ResourceLocationArgument.getId(context, "core")
        val tiers = ReloadableDataHolder.getTiers(coreId) ?: throw UNKNOWN_CORE.create()

        context.source.sendSuccess({ Component.literal(buildString {
            append("Tiers for \"$coreId\":\n")

            tiers.entries.forEach { (key, tier) ->
                append("  $key = {\"capacity\": ${tier.capacity}}\n")
            }
        })}, false)

        return Command.SINGLE_SUCCESS
    }
}