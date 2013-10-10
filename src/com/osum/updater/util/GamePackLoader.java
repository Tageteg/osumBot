package com.osum.updater.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;

public class GamePackLoader
{

    //public static WebCrawler crawler;
    public static byte[] gamePackBytes;
    public WebCrawler crawler;

    private static byte[] hash = null;

    public final static HashMap<String, byte[]> classes = new HashMap<String, byte[]>();

    public GamePackLoader(final WebCrawler crawler)
    {
        this.crawler = crawler;
        gamePackBytes = getGamePackBytes();
        loadGamePack();
        System.out.println("\n" + crawler.parameterMap.get("0"));
        System.out.println(crawler.parameterMap.get("-1"));
        try
        {
            System.out.println(crawler.getGamePackUrl().toString());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public byte[] getGamePackBytes()
    {
        System.out.println("Receiving Game Pack bytes...");
        try
        {
            final URLConnection conn = crawler.getGamePackUrl().openConnection();
            final int contentLength = conn.getContentLength();
            final byte[] buffer;
            final DataInputStream dataInputStream = new DataInputStream(conn.getInputStream());
            buffer = new byte[contentLength];
            dataInputStream.readFully(buffer);
            return buffer;
        } catch (MalformedURLException e1)
        {
            e1.printStackTrace();
        } catch (IOException e1)
        {
            e1.printStackTrace();
        }
        return null;
    }

    public void loadGamePack()
    {
        System.out.println("Decrypting game pack...");
        try
        {
            final JarOutputStream packOutputStream = new JarOutputStream(new FileOutputStream(new File("GamePack(Decrypted).jar")));
            JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(gamePackBytes));
            JarEntry entry;
            byte[] innerPackBytes = null;
            while ((entry = jarInputStream.getNextJarEntry()) != null)
            {
                if (entry.getName().equals("inner.pack.gz"))
                {
                    final ByteArrayOutputStream ipgBytes = new ByteArrayOutputStream();
                    final byte[] ipgBuf = new byte[2048];
                    int read;
                    while (jarInputStream.available() > 0 && (read = jarInputStream.read(ipgBuf, 0, ipgBuf.length)) >= 0)
                    {
                        ipgBytes.write(ipgBuf, 0, read);
                    }
                    innerPackBytes = ipgBytes.toByteArray();
                    break;
                }
            }
            if (innerPackBytes != null && innerPackBytes.length > 0)
            {
                hash = MessageDigest.getInstance("SHA-1").digest(innerPackBytes);
                final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(2, new SecretKeySpec(toByte(crawler.parameterMap.get("0")), "AES"), new IvParameterSpec(toByte(crawler.parameterMap.get("-1"))));
                final Pack200.Unpacker unpacker = Pack200.newUnpacker();
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(0x500000);
                unpacker.unpack(new GZIPInputStream(new ByteArrayInputStream(cipher.doFinal(innerPackBytes))), new JarOutputStream(byteArrayOutputStream));
                jarInputStream = new JarInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                while ((entry = jarInputStream.getNextJarEntry()) != null)
                {
                    String entryName = entry.getName();
                    if (entryName.startsWith("META-INF")) continue;
                    if (entryName.endsWith(".class"))
                    {
                        final ByteArrayOutputStream jisBytes = new ByteArrayOutputStream();
                        final byte[] jcmBuffer = new byte[2048];
                        int read;
                        while (jarInputStream.available() > 0 && (read = jarInputStream.read(jcmBuffer, 0, jcmBuffer.length)) >= 0)
                        {
                            jisBytes.write(jcmBuffer, 0, read);
                        }
                        String mapEntry = entryName.replaceAll("/", ".");
                        mapEntry = mapEntry.substring(0, mapEntry.length() - 6);
                        classes.put(mapEntry, jisBytes.toByteArray());
                        packOutputStream.putNextEntry(new JarEntry(mapEntry.replaceAll("\\.", "/") + ".class"));
                        packOutputStream.write(jisBytes.toByteArray());
                        packOutputStream.closeEntry();
                    }
                }
            }
            if (packOutputStream != null)
            {
                packOutputStream.close();
            }
        } catch (final Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static byte[] toByte(final String key)
    {
        final int keyLength = key.length();
        if (keyLength == 0)
        {
            return new byte[0];
        } else
        {
            int unscrambledLength;
            int lengthMod = -4 & keyLength + 3;
            unscrambledLength = lengthMod / 4 * 3;
            if (keyLength <= lengthMod - 2 || charIndex(key.charAt(lengthMod - 2)) == -1)
            {
                unscrambledLength -= 2;
            } else if (keyLength <= lengthMod - 1 || -1 == charIndex(key.charAt(lengthMod - 1)))
            {
                --unscrambledLength;
            }
            final byte[] keyBytes = new byte[unscrambledLength];
            unscramble(keyBytes, 0, key);
            return keyBytes;
        }
    }

    private static int charIndex(final char character)
    {
        return character >= 0 && character < charSet.length ? charSet[character] : -1;
    }

    private static int unscramble(final byte[] bytes, int offset, final String key)
    {
        final int initialOffset = offset;
        final int keyLength = key.length();
        int pos = 0;
        while (keyLength > pos)
        {
            final int currentChar = charIndex(key.charAt(pos));
            final int pos_1 = keyLength > (pos + 1) ? charIndex(key.charAt(pos + 1)) : -1;
            final int pos_2 = pos + 2 < keyLength ? charIndex(key.charAt(2 + pos)) : -1;
            final int pos_3 = keyLength > (pos + 3) ? charIndex(key.charAt(3 + pos)) : -1;
            bytes[offset++] = (byte) (pos_1 >>> 4 | currentChar << 2);
            if (pos_2 != -1)
            {
                bytes[offset++] = (byte) (pos_1 << 4 & 240 | pos_2 >>> 2);
                if (pos_3 != -1)
                {
                    bytes[offset++] = (byte) (192 & pos_2 << 6 | pos_3);
                    pos += 4;
                    continue;
                }
            }
            break;
        }
        return offset - initialOffset;
    }

    private static int[] charSet;

    static
    {
        charSet = new int[128];
        charSet[42] = 62;
        charSet[43] = 62;
        charSet[45] = 63;
        charSet[47] = 63;
        for (byte i = 48; i < 58; ++i)
        {
            charSet[i] = i + 4;
        }
        for (byte i = 65; i < 91; ++i)
        {
            charSet[i] = i - 65;
        }
        for (byte i = 97; i < 123; ++i)
        {
            charSet[i] = i - 71;
        }
    }

    public static boolean hasLoaded()
    {
        return hash != null;
    }
}
