package com.osum.updater.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarLoader
{

    private final File file;

    public JarLoader(final File file)
    {
        this.file = file;
    }

    public HashMap<String, ClassNode> load()
    {
        System.out.println("\nLoading classes");
        final HashMap<String, ClassNode> classes = new HashMap<String, ClassNode>();
        try
        {
            final JarFile jarFile = new JarFile(file);
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements())
            {
                final JarEntry jarEntry = entries.nextElement();
                final String name = jarEntry.getName();
                if (name.endsWith(".class"))
                {
                    final ClassReader cr = new ClassReader(jarFile.getInputStream(jarEntry));
                    final ClassNode cn = new ClassNode();
                    cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(name.replace(".class", ""), cn);
                }
            }
            System.out.println("Loaded " + classes.size() + " classes");
            return classes;
        } catch (final IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
