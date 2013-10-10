package com.osum.common;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

/**
 * User: Marty
 * Date: 3/28/13
 * Time: 7:26 PM
 */
public class FileUtils
{

    public static HashMap<String, ClassNode> parseJAR(JarInputStream stream) throws IOException
    {
        HashMap<String, ClassNode> classes = new HashMap<String, ClassNode>();
        JarEntry entry = null;

        while ((entry = stream.getNextJarEntry()) != null)
        {
            if (!entry.getName().contains(".class"))
            {
                continue;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int size;

            while ((size = stream.read(buffer, 0, buffer.length)) != -1)
            {
                out.write(buffer, 0, size);
            }

            out.flush();
            byte[] data = out.toByteArray();
            ClassReader reader = new ClassReader(data);
            ClassNode node = new ClassNode();
            reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            classes.put(node.name, node);
            stream.closeEntry();
        }

        return classes;
    }

    public static HashMap<String, ClassNode> parseJar(JarFile jar)
    {
        HashMap<String, ClassNode> classes = new HashMap<String, ClassNode>();
        try
        {
            Enumeration<?> enumeration = jar.entries();
            while (enumeration.hasMoreElements())
            {
                JarEntry entry = (JarEntry) enumeration.nextElement();
                if (entry.getName().endsWith(".class"))
                {
                    ClassReader classReader = new ClassReader(jar.getInputStream(entry));
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(classNode.name, classNode);
                }
            }
            jar.close();
            return classes;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void injectHooks(HashMap<String, ClassNode> classes)
    {
        try
        {
            File fXmlFile = new File("./data/hooks.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            NodeList classList = doc.getElementsByTagName("class");

            for (int i = 0; i < classList.getLength(); i++)
            {
                Node classNode = classList.item(i);
                if (classNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element clazz = (Element) classNode;
                    if (clazz.hasAttribute("interface"))
                    {
                        String iface = "com.osum.bot.interfaces." + clazz.getAttribute("interface");
                        try {
                            Class.forName(iface);
                            AsmUtils.addInterface(classes.get(clazz.getAttribute("name")), iface.replace(".", "/"));
                        } catch(ClassNotFoundException e) {

                        }
                    } else
                    {
                        String _super = "com.osum.bot.interfaces." + clazz.getAttribute("super");
                        try {
                            Class.forName(_super);
                            String name = clazz.getAttribute("name");
                            String superName = clazz.getAttribute("super");
                            if(superName.equals("Canvas")) {
                                AsmUtils.setSuper(classes.get(name), "java/awt/Canvas", _super.replace(".", "/"));
                            } else if(superName.equals("Mouse")) {
                                AsmUtils.setSuper(classes.get(name), "java/lang/Object", _super.replace(".", "/"));
                                AsmUtils.renameMethod(classes.get(name), "mouseClicked", "_mouseClicked");
                                AsmUtils.renameMethod(classes.get(name), "mouseDragged", "_mouseDragged");
                                AsmUtils.renameMethod(classes.get(name), "mouseEntered", "_mouseEntered");
                                AsmUtils.renameMethod(classes.get(name), "mouseExited", "_mouseExited");
                                AsmUtils.renameMethod(classes.get(name), "mouseMoved", "_mouseMoved");
                                AsmUtils.renameMethod(classes.get(name), "mousePressed", "_mousePressed");
                                AsmUtils.renameMethod(classes.get(name), "mouseReleased", "_mouseReleased");
                                AsmUtils.renameMethod(classes.get(name), "mouseWheelMoved", "_mouseWheelMoved");
                            } else if(superName.equals("Keyboard")) {
                                AsmUtils.setSuper(classes.get(name), "java/lang/Object", _super.replace(".", "/"));
                                AsmUtils.renameMethod(classes.get(name), "keyPressed", "_keyPressed");
                                AsmUtils.renameMethod(classes.get(name), "keyReleased", "_keyReleased");
                                AsmUtils.renameMethod(classes.get(name), "keyTyped", "_keyTyped");

                            }
                        } catch(ClassNotFoundException e) {

                        }
                    }
                    NodeList methodList = clazz.getElementsByTagName("getter");
                    for (int j = 0; j < methodList.getLength(); j++)
                    {
                        Node methodNode = methodList.item(j);
                        if (methodNode.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element method = (Element) methodNode;
                            String methodDesc = method.getAttribute("methodDesc");
                            try {
                                String arrayDimensions = method.getAttribute("fieldDesc").split("L")[0];
                                methodDesc = "()" + arrayDimensions + "L" + Class.forName("com.osum.bot.interfaces." + methodDesc).getName().replace(".", "/") + ";";
                            } catch(ClassNotFoundException e) {
                                methodDesc = "()" + methodDesc;
                            }
                            int multiplier = -1;
                            if(method.hasAttribute("multiplier")) {
                                multiplier = Integer.parseInt(method.getAttribute("multiplier"));
                            }
                            AsmUtils.addGetter(classes.get(clazz.getAttribute("name")), method.getAttribute("fieldName"),
                                    classes.get(method.getAttribute("fieldOwner")), method.getAttribute("fieldDesc"),
                                    method.getAttribute("methodName"), methodDesc, multiplier);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void dumpJar(File jar, ClassNode[] classes)
            throws IOException
    {
        if (!jar.getName().endsWith(".jar"))
        {
            throw new IllegalArgumentException(
                    "The provided file must be a JAR file!");
        }
        JarOutputStream output = new JarOutputStream(new FileOutputStream(jar));
        for (ClassNode clazz : classes)
        {
            JarEntry entry = new JarEntry(clazz.name + ".class");
            output.putNextEntry(entry);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            clazz.accept(writer);
            output.write(writer.toByteArray());
        }
        output.close();
    }


}
