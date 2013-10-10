package com.osum.updater.util;

import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public class XMLWriter
{

    private final List<ClassIdentity> identities;

    public XMLWriter(final List<ClassIdentity> identities)
    {
        this.identities = identities;
    }

    public void write()
    {
        try
        {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document rootDocument = builder.newDocument();
            final Element rootElement = rootDocument.createElement("hooks");
            rootDocument.appendChild(rootElement);
            for (final ClassIdentity classIdentity : identities)
            {
                if (classIdentity != null)
                {
                    final Element classElement = rootDocument.createElement("class");
                    rootElement.appendChild(classElement);
                    classElement.setAttribute("name", classIdentity.getClassName());
                    if (classIdentity.getName().equals("Canvas") || classIdentity.getName().equals("Mouse") || classIdentity.getName().equals("Keyboard"))
                    {
                        classElement.setAttribute("super", classIdentity.getName());
                    } else
                    {
                        classElement.setAttribute("interface", classIdentity.getName());
                    }
                    for (final FieldIdentity fieldIdentity : classIdentity.getFieldIdentities())
                    {
                        if (fieldIdentity != null)
                        {
                            final Element fieldElement = rootDocument.createElement("getter");
                            classElement.appendChild(fieldElement);
                            fieldElement.setAttribute("methodName", fieldIdentity.getMethodName());
                            fieldElement.setAttribute("methodDesc", fieldIdentity.getMethodType());
                            fieldElement.setAttribute("fieldName", fieldIdentity.getFieldName());
                            fieldElement.setAttribute("fieldOwner", fieldIdentity.getFieldClass());
                            fieldElement.setAttribute("fieldDesc", fieldIdentity.getFieldType());
                            //fieldElement.setAttribute("type", new ClassResolver(fieldIdentity.getFieldType()).resolve());
                            if (fieldIdentity.getMultiplier() != -1)
                            {
                                fieldElement.setAttribute("multiplier", Integer.toString(fieldIdentity.getMultiplier()));
                            }
                        }
                    }
                }
            }
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(rootDocument);
            StreamResult result = new StreamResult(new File("./data/hooks.xml"));
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException pce)
        {
            pce.printStackTrace();
        }
    }

}
