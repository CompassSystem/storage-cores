package compass_system.storagecores.base.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import compass_system.storagecores.base.Main
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

class StorageBaseArgument : ArgumentType<ResourceLocation> {
    override fun parse(reader: StringReader): ResourceLocation = ResourceLocation.read(reader)

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        if (context.source is SharedSuggestionProvider) {
            return SharedSuggestionProvider.suggestResource(Main.registry.getKnownBases(), builder)
        }

        return Suggestions.empty()
    }

    companion object {
        fun storageBase() = StorageBaseArgument()
    }
}