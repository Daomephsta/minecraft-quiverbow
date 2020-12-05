package com.domochevsky.quiverbow.util;

import static com.google.common.base.Predicates.not;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

import net.minecraftforge.fml.common.Loader;

public class Resources
{
    public static void findFileResources(String search, Consumer<? super Path> fileProcessor)
    {
        Path modPath = Loader.instance().activeModContainer().getSource().toPath();
        if (!Files.isDirectory(modPath))
        {
            try(FileSystem fs = FileSystems.newFileSystem(modPath, null))
            {
                walkDirectory(fs.getPath(search), fileProcessor);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            walkDirectory(modPath.resolve(search), fileProcessor);
        }
    }

    private static void walkDirectory(Path directory, Consumer<? super Path> fileProcessor)
    {
        try (Stream<Path> files = Files.walk(directory).filter(not(Files::isDirectory)))
        {
            for (java.util.Iterator<Path> iter = files.iterator(); iter.hasNext();)
                fileProcessor.accept(iter.next());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to walk " + directory, e);
        }
    }
}
