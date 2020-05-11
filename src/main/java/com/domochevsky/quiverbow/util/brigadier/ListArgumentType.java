package com.domochevsky.quiverbow.util.brigadier;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class ListArgumentType<E> implements ArgumentType<List<E>>
{
    private static final SimpleCommandExceptionType PARSING_STALLED = new SimpleCommandExceptionType(new LiteralMessage("List argument parsing stalled"));
    private final ArgumentType<? extends E>[] elementTypes;

    private ListArgumentType(ArgumentType<? extends E>[] expectedElementTypes)
    {
        this.elementTypes = expectedElementTypes;
    }

    @SafeVarargs
    public static <E> ListArgumentType<E> list(ArgumentType<? extends E>... expectedElementTypes)
    {
        return new ListArgumentType<>(expectedElementTypes);
    }

    @SuppressWarnings("unchecked")
    public static <E, S> List<E> getList(CommandContext<S> context, String name)
    {
        return context.getArgument(name, List.class);
    }

    @Override
    public List<E> parse(StringReader reader) throws CommandSyntaxException
    {
        List<E> result = new ArrayList<>();

        while (reader.canRead())
        {
            int tries = 0;
            int initialCursor = reader.getCursor();
            for (ArgumentType<? extends E> type : elementTypes)
            {
                int cursorMark = reader.getCursor();
                E element = null;
                try
                {
                    tries += 1;
                    element = type.parse(reader);
                }
                catch (CommandSyntaxException e)
                {
                    System.out.println(e.getMessage());
                    reader.setCursor(cursorMark);
                }
                if (element != null)
                {
                    tries = 0;
                    result.add(element);
                    //Skip over whitespace between arguments
                    while (reader.canRead() && Character.isWhitespace(reader.peek()))
                        reader.read();
                    break;
                }
            }
            //Detect stalling
            if (reader.getCursor() == initialCursor)
                throw PARSING_STALLED.createWithContext(reader);
            if (tries >= elementTypes.length)
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
        }
        return result;
    }

    @Override
    public Collection<String> getExamples()
    {
        return Arrays.stream(elementTypes)
            .flatMap(type -> type.getExamples().stream())
            .collect(toList());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        for (ArgumentType<? extends E> argumentType : elementTypes)
            argumentType.listSuggestions(context, builder);
        return builder.buildFuture();
    }
}