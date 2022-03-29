/* Copyright 2012 Yaqiang Wang,
 * yaqiang.wang@gmail.com
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 */
package org.meteothink.weather;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.util.Properties;

/**
 *
 * @author yaqiang
 */
public class Options {
    // <editor-fold desc="Variables">

    private String fileName;
    private String currentPath;
    private Point mainFormLocation = new Point(0, 0);
    private Dimension mainFormSize = new Dimension(1000, 650);
    private String lookFeel = "Nimbus";
    private boolean doubleBuffer = true;
    private boolean lafDecorated = false;
    // </editor-fold>
    // <editor-fold desc="Constructor">
    
    // </editor-fold>
    // <editor-fold desc="Get Set Methods">

    /**
     * Get current path
     * @return Current path
     */
    public String getCurrentPath() {
        return this.currentPath;
    }

    /**
     * Set current path
     * @param value Current path
     */
    public void setCurrentPath(String value) {
        this.currentPath = value;
    }

    /**
     * Get file name
     *
     * @return File name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Get main form location
     * @return Main form location
     */
    public Point getMainFormLocation(){
        return this.mainFormLocation;
    }
    
    /**
     * Set main form location
     * @param value Main form location
     */
    public void setMainFormLocation(Point value){
        this.mainFormLocation = value;
    }
    
    /**
     * Get main form size
     * @return Main form size
     */
    public Dimension getMainFormSize(){
        return this.mainFormSize;
    }
    
    /**
     * Set main form size
     * @param value Main form size
     */
    public void setMainFormSize(Dimension value){
        this.mainFormSize = value;
    }

    /**
     * Get look and feel
     * @return Look and feel
     */
    public String getLookFeel() {
        return this.lookFeel;
    }

    /**
     * Set look and feel
     * @param value look and feel
     */
    public void setLookFeel(String value) {
        this.lookFeel = value;
    }
    
    /**
     * Get if using off screen image double buffering.
     * Using double buffering will be faster but lower view quality in
     * high dpi screen computer.
     *
     * @return Boolean
     */
    public boolean isDoubleBuffer() {
        return this.doubleBuffer;
    }

    /**
     * Set using off screen image double buffering or not.
     * @param value Boolean
     */
    public void setDoubleBuffer(boolean value) {
        this.doubleBuffer = value;
    }
    
    /**
     * Get if enable look and feel decorated
     * @return Boolean
     */
    public boolean isLafDecorated() {
        return this.lafDecorated;
    }
    
    /**
     * Set enable or not of look and feel decorated
     * @param value Boolean
     */
    public void setLafDecorated(boolean value) {
        this.lafDecorated = value;
    }   

    // </editor-fold>
    // <editor-fold desc="Methods">

    /**
     * Save configure file
     *
     * @param fileName File name
     * @throws ParserConfigurationException
     */
    public void saveConfigFile(String fileName) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("MeteoInfo");
        File af = new File(fileName);
        Attr fn = doc.createAttribute("File");
        Attr type = doc.createAttribute("Type");
        fn.setValue(af.getName());
        type.setValue("configurefile");
        root.setAttributeNode(fn);
        root.setAttributeNode(type);
        doc.appendChild(root);

        //Path
        Element path = doc.createElement("Path");
        Attr pAttr = doc.createAttribute("OpenPath");
        pAttr.setValue(currentPath);
        path.setAttributeNode(pAttr);
        root.appendChild(path);

        //Look and feel
        Element lf = doc.createElement("LookFeel");
        Attr lfAttr = doc.createAttribute("Name");
        Attr lafDecoratedAttr = doc.createAttribute("LafDecorated");
        lfAttr.setValue(this.lookFeel);
        lafDecoratedAttr.setValue(String.valueOf(this.lafDecorated));
        lf.setAttributeNode(lfAttr);
        lf.setAttributeNode(lafDecoratedAttr);
        root.appendChild(lf);
        
        //Figure element
        Element eFigure = doc.createElement("Figure");
        Attr dbAttr = doc.createAttribute("DoubleBuffering");
        dbAttr.setValue(String.valueOf(this.doubleBuffer));
        eFigure.setAttributeNode(dbAttr);
        root.appendChild(eFigure);
        
        //Start up form setting
        Element startForm = doc.createElement("Startup");
        Attr mfLocationAttr = doc.createAttribute("MainFormLocation");
        Attr mfSizeAttr = doc.createAttribute("MainFormSize");
        mfLocationAttr.setValue(String.valueOf(this.mainFormLocation.x) + "," +
                String.valueOf(this.mainFormLocation.y));
        mfSizeAttr.setValue(String.valueOf(this.mainFormSize.width) + "," +
                String.valueOf(this.mainFormSize.height));
        startForm.setAttributeNode(mfLocationAttr);
        startForm.setAttributeNode(mfSizeAttr);
        root.appendChild(startForm);
                
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            
            Properties properties = transformer.getOutputProperties();
            properties.setProperty(OutputKeys.ENCODING, "UTF-8");
            properties.setProperty(OutputKeys.INDENT, "yes");
            properties.setProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperties(properties);
//            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//            PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
            FileOutputStream out = new FileOutputStream(fileName);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
        } catch (TransformerException mye) {
        } catch (IOException exp) {
        }
    }

    /**
     * Load configure file
     *
     * @param fileName File name
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void loadConfigFile(String fileName) throws ParserConfigurationException, SAXException, IOException {
        this.fileName = fileName;

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
        InputSource is = new InputSource(br);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(is);

        Element root = doc.getDocumentElement();        
        try {
            //Path
            Node path = root.getElementsByTagName("Path").item(0);
            currentPath = path.getAttributes().getNamedItem("OpenPath").getNodeValue();
            if (! new File(currentPath).isDirectory()) {
                currentPath = System.getProperty("user.dir");
            }

            //Look and feel
            if (root.getElementsByTagName("LookFeel") != null) {
                Element lf = (Element) root.getElementsByTagName("LookFeel").item(0);
                this.lookFeel = lf.getAttributes().getNamedItem("Name").getNodeValue();
                this.lafDecorated = Boolean.valueOf(lf.getAttributes().getNamedItem("LafDecorated").getNodeValue());
            }
            
            //Figure element
            if (root.getElementsByTagName("Figure").item(0) != null) {
                Element eFigure = (Element) root.getElementsByTagName("Figure").item(0);
                this.doubleBuffer = Boolean.valueOf(eFigure.getAttributes().getNamedItem("DoubleBuffering").getNodeValue());
            }
            
            //Start up form setting
            Node startForm = root.getElementsByTagName("Startup").item(0);
            String loc = startForm.getAttributes().getNamedItem("MainFormLocation").getNodeValue();
            this.mainFormLocation.x = Integer.parseInt(loc.split(",")[0]);
            this.mainFormLocation.y = Integer.parseInt(loc.split(",")[1]);            
            String size = startForm.getAttributes().getNamedItem("MainFormSize").getNodeValue();
            this.mainFormSize.width = Integer.parseInt(size.split(",")[0]);
            this.mainFormSize.height = Integer.parseInt(size.split(",")[1]);
        } catch (Exception e) {
        }
    }
    // </editor-fold>
}
