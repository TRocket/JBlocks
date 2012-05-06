package org.jblocks.utils;

import java.awt.*;
import javax.script.ScriptException;
import javax.swing.*;

import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Method;
import java.io.InputStream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jblocks.JBlocks;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author ZeroLuck
 */
public class XMLSwingLoader {

    private static final List<String> ignore = Arrays.asList("constrains", "icon");
    private static Node currentNode;

    private static Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.equalsIgnoreCase("panel")) {
            return JPanel.class;
        }
        if (name.equalsIgnoreCase("button")) {
            return JButton.class;
        }
        if (name.equalsIgnoreCase("list")) {
            return JList.class;
        }
        if (name.equalsIgnoreCase("textfield")) {
            return JTextField.class;
        }
        if (name.equalsIgnoreCase("label")) {
            return JLabel.class;
        }
        if (name.equalsIgnoreCase("checkbox")) {
            return JCheckBox.class;
        }
        if (name.equalsIgnoreCase("progressbar")) {
            return JProgressBar.class;
        }
        if (name.equalsIgnoreCase("toolbar")) {
            return JToolBar.class;
        }
        if (name.equalsIgnoreCase("combobox")) {
            return JComboBox.class;
        }

        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex1) {
            try {
                return Class.forName("javax.swing." + name);
            } catch (ClassNotFoundException ex2) {
                return Class.forName("java.awt." + name);
            }
        }
    }

    private static Object createInstance(String name) throws Exception {
        return findClass(name).newInstance();
    }

    private static void executeMethods(Component c, Node n, ScriptEngine engine) throws ScriptException {
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String name = child.getNodeName();
                String value = child.getTextContent();
                Class<?> clazz = c.getClass();
                boolean found = false;
                for (Method m : clazz.getMethods()) {
                    String mn = m.getName();
                    if (mn.equals(name)) {
                        engine.put("comp", c);
                        engine.eval("importPackage(javax.swing);importPackage(java.awt); comp." + mn + "(" + value + ");");
                        found = true;
                    }
                }
                if (!found) {
                    throw new RuntimeException("method not found: " + name);
                }
            }
        }
    }

    private static Component parseNode(Node n, ScriptEngine engine) throws Exception {
        currentNode = n;

        final String name = n.getNodeName();
        final Component instance = (Component) createInstance(name);
        final NodeList children = n.getChildNodes();
        final NamedNodeMap attributes = n.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            Node child = attributes.item(i);
            String attr = child.getNodeName();
            String value = child.getNodeValue();
            if (!ignore.contains(attr)) {
                Class<?> clazz = instance.getClass();
                boolean found = false;
                for (Method m : clazz.getMethods()) {
                    String mn = m.getName();
                    if (mn.equalsIgnoreCase(attr) || mn.equalsIgnoreCase("set" + attr)) {
                        engine.put("comp", instance);
                        engine.eval("importPackage(javax.swing);importPackage(java.awt); comp." + mn + "(" + value + ");");
                        found = true;
                    }
                }
                if (!found) {
                    throw new RuntimeException("method not found: " + attr);
                }
            } else if (attr.equalsIgnoreCase("icon")) {
                Class<?> clazz = instance.getClass();
                boolean found = false;
                for (Method m : clazz.getMethods()) {
                    String mn = m.getName();
                    if (mn.equalsIgnoreCase(attr) || mn.equalsIgnoreCase("set" + attr)) {
                        engine.put("comp", instance);
                        engine.eval("importPackage(javax.swing);importPackage(java.awt); comp." + mn + "("
                                + "org.jblocks.JBlocks.getIcon('" + value+ "'))" + ";");
                        found = true;
                    }
                }
                if (!found) {
                    throw new RuntimeException("method not found: " + attr);
                }
            }
        }

        if (instance instanceof Container) {
            Container cont = (Container) instance;
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    if (child.getNodeName().equalsIgnoreCase("methods")) {
                        executeMethods(instance, child, engine);
                    } else {
                        Node constrains = child.getAttributes().getNamedItem("constrains");
                        if (constrains == null) {
                            cont.add(parseNode(children.item(i), engine));
                        } else {
                            engine.put("comp", instance);
                            Object val = engine.eval("importPackage(javax.swing);importPackage(java.awt); " + constrains.getTextContent());
                            cont.add(parseNode(children.item(i), engine), val);
                        }
                    }
                }
            }
        }

        return instance;
    }

    /**
     * Reads a XML GUI. <br />
     * 
     * @param in the InputStream (XML format)
     * @return the parsed GUI
     */
    public static Component load(InputStream in, Object handler) {
        try {
            DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(in);

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
            engine.put("handler", handler);
            return parseNode(doc.getFirstChild(), engine);
        } catch (Exception ex) {
            throw new RuntimeException("ParseError at: " + currentNode.getNodeName(), ex);
        }
    }

    public static Component forName(Container c, String name) {
        for (Component comp : c.getComponents()) {
            if (comp.getName().equals(name)) {
                return comp;
            }
            if (comp instanceof Container) {
                Component ret = forName((Container) comp, name);
                if (ret != null) {
                    return ret;
                }
            }
        }
        return null;
    }
}
