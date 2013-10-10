package com.osum.updater;

import com.osum.updater.util.JarLoader;

import java.io.File;
import java.io.IOException;

public class Application
{

    public static void main(final String[] args) throws IOException
    {
        System.out.println("Running osumBot Updater");
        new Analyzer(new JarLoader(new File("./data/client.jar")).load()).run();
    }

}
