package com.domochevsky.quiverbow.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

public class Resources
{
    public static void findFileResources(ClassLoader classLoader, String rootPath, Consumer<String> fileProcessor)
    {
        Queue<String> toVisit = new ArrayDeque<>();
        toVisit.add(rootPath);
        while (!toVisit.isEmpty())
        {
            String current = toVisit.remove();
            if (current.contains("."))
                fileProcessor.accept(current);
            else
            {
                try (InputStream in = classLoader.getResourceAsStream(current))
                {
                    if (in == null)
                        throw new FileNotFoundException("No such path '" + current + "'");
                    StringBuilder childPath = new StringBuilder(rootPath);
                    childPath.append('/');
                    int parentIndex = childPath.length();
                    int read = 0;
                    while ((read = in.read()) != -1)
                    {
                        if (read != '\n')
                            childPath.append((char) read);
                        else
                        {
                            toVisit.add(childPath.toString());
                            childPath.delete(parentIndex, childPath.length());
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
