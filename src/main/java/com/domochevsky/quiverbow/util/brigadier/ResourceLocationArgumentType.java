package com.domochevsky.quiverbow.util.brigadier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.util.ResourceLocation;

public class ResourceLocationArgumentType implements ArgumentType<ResourceLocation>
{
    private static final Collection<String> EXAMPLES = new ArrayList<>(3);
    static
    {
        EXAMPLES.add("foo:bar");
        EXAMPLES.add("qux:bar");
    }

    public static ResourceLocationArgumentType resourceLocation()
    {
        return new ResourceLocationArgumentType();
    }

    public static <S> ResourceLocation getResourceLocation(CommandContext<S> ctx, String name)
    {
        return ctx.getArgument(name, ResourceLocation.class);
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException
    {
        String domain = reader.readStringUntil(':');
        String path = reader.readUnquotedString();
        return new ResourceLocation(domain, path);
    }

    @Override
    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return builder.buildFuture();
    }
}