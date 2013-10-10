package com.osum.updater;

import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.GamePackLoaderServer;
import com.osum.updater.util.bytecode.ClassResolver;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Server
{

    private final int maxConnections = 3;
    private int currentConnections = 0;
    private final int clientIndex = 0;

    public void run()
    {
        try
        {
            final ServerSocket serverSocket = new ServerSocket(1337);
            System.out.println("Server has started");
            while (true)
            {
                if (currentConnections <= maxConnections)
                {
                    final Socket socket;
                    if ((socket = serverSocket.accept()) != null)
                    {
                        currentConnections++;
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    if (reader.readLine().equals("Updater"))
                                    {
                                        final String[] array = new String[3];
                                        for (int i = 0; i < 3; i++)
                                        {
                                            final String line = reader.readLine();
                                            array[i] = line;
                                        }
                                        new Request(serverSocket, socket, clientIndex).send(array);
                                        socket.close();
                                        currentConnections--;
                                    }
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private class Request
    {

        private final ServerSocket serverSocket;
        private final Socket socket;
        private final int clientIndex;

        public Request(ServerSocket serverSocket, Socket socket, int index)
        {
            this.serverSocket = serverSocket;
            this.socket = socket;
            this.clientIndex = index;
        }

        public String getType()
        {
            return "Updater";
        }

        public void send(String[] info)
        {
            System.out.println("Client(" + clientIndex + ") Running updater");
            final HashMap<String, ClassNode> classes = new GamePackLoaderServer(info[0], info[1], info[2]).getContents();
            if (classes != null)
            {
                final Analyzer analyzer = new Analyzer(classes);
                analyzer.run();
                final List<ClassIdentity> identities = analyzer.getIdentities();
                System.out.println("Client(" + clientIndex + ") Identitified " + getFieldCount(identities) + " Fields");
                System.out.println("Client(" + clientIndex + ") Replying with identities");
                try
                {
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    for (final ClassIdentity classIdentity : identities)
                    {
                        if (classIdentity.getFieldIdentities().size() == 0)
                        {
                            writer.println("Class|" + classIdentity.getName() + "|" + classIdentity.getClassName());
                        }
                        for (final FieldIdentity fieldIdentity : classIdentity.getFieldIdentities())
                        {
                            if (fieldIdentity.getMethodName().equals("NullSpace")) continue;
                            if (fieldIdentity.getMultiplier() != -1)
                            {
                                writer.println(fieldIdentity.getMethodName() + "|" + fieldIdentity.getFieldName() + "|" + fieldIdentity.getFieldClass() + "|" +
                                        classIdentity.getName() + "|" + fieldIdentity.getFieldType() + "|" + new ClassResolver(fieldIdentity.getFieldType()).resolve() + "|" +
                                        Boolean.toString(fieldIdentity.isStatic()) + "|" + fieldIdentity.getMultiplier());
                            } else
                            {
                                writer.println(fieldIdentity.getMethodName() + "|" + fieldIdentity.getFieldName() + "|" + fieldIdentity.getFieldClass() + "|" +
                                        classIdentity.getName() + "|" + fieldIdentity.getFieldType() + "|" + new ClassResolver(fieldIdentity.getFieldType()).resolve() + "|" +
                                        Boolean.toString(fieldIdentity.isStatic()));
                            }
                        }
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            } else
            {
                System.out.println("Client(" + clientIndex + ") Failed to load classes");
            }
        }

        public int getFieldCount(final List<ClassIdentity> identities)
        {
            int count = 0;
            for (final ClassIdentity identity : identities)
            {
                count += identity.getFieldIdentities().size();
            }
            return count;
        }

    }

}
