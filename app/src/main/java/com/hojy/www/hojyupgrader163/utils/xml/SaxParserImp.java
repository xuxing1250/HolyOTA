/**
 * 
 */
package com.hojy.www.hojyupgrader163.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;

/**
 * sax方式
 */
class SaxParserImp implements XmlParser
{

    private SaxDefHandler defHandler = new SaxDefHandler();
    private SAXParserFactory factory;
    private InputStream inStream = null;
    private SAXParser parser;

    /*
     * (non-Javadoc)
     * @see
     * com.hojy.wifihotspot.supports.XmlParser#parseStringWithIndex(java.lang
     * .String, java.lang.String, int)
     */

    @Override
    public String createXml(String node, String value)
    {
        // TODO Auto-generated method stub
        StringWriter xmlWriter = new StringWriter();
        String[] nodeName = null;
        nodeName = node.split("/"); // 将节点分离
        int nodeNum = nodeName.length; // 获取有几个节点

        try
        {
            SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory
                    .newInstance();
            TransformerHandler handler = factory.newTransformerHandler();
            Transformer transformer = handler.getTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "1.0");

            StreamResult result = new StreamResult(xmlWriter);
            handler.setResult(result);
            handler.startDocument();

            for (int j = 0; j < nodeNum; j++)
            {
                handler.startElement("", "", nodeName[j], null);
            }
            handler.characters(value.toCharArray(), 0, value.length());
            for (int j = nodeNum - 1; j >= 0; j--)
            {
                handler.endElement("", "", nodeName[j]);
            }
            handler.endDocument();

        } catch (TransformerConfigurationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return xmlWriter.toString();
    }

    /*
     * (non-Javadoc)
     * @see com.hojy.wifihotspot.supports.XmlParser#sizeOf(java.lang.String,
     * java.lang.String)
     */

    @SuppressWarnings("finally")
    public List<XmlObject> getNodeString(String source, String node)
    {
        // TODO Auto-generated method stub
        try
        {
            inStream = string2InStream(source); // 将字符串转为输入流
            if (inStream != null)
            {
                factory = SAXParserFactory.newInstance();
                parser = factory.newSAXParser();
                defHandler.setXml(node);
                parser.parse(inStream, defHandler);
                inStream.close();
            }
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally
        {
            return defHandler.getXmlData();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.hojy.wifihotspot.supports.XmlParser#isXml(java.lang.String)
     */

    /**
     * 
     * @param xmlString
     *            ： xml文件
     * @param node
     *            ：节点
     * @return 返回节点下的value
     */
    @Override
    public synchronized String getString(String xmlString, String node)
    {
        List<XmlObject> xmlObj = parseString(xmlString, node);
        if (xmlObj != null && xmlObj.size() == 1)
        {
            String xmlValue = xmlObj.get(0).getValue();
            if (xmlValue != null && xmlValue.length() > 0)
            {
                return xmlValue;
            }
        }
        return "";
    }

    @Override
    public boolean isXml(String source)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<XmlObject> parseString(String xml, String xmlString)
    {
        String[] nodeName = null;
        nodeName = xmlString.split("/"); // 将节点分离
        int nodeNum = nodeName.length; // 获取有几个节点
        List<XmlObject> xmlObjects = new ArrayList<XmlObject>();
        for (int i = 0; i < nodeNum; i++)
        {
            xmlObjects = getNodeString(xml, nodeName[i]);
            if (xmlObjects != null)
            {
                xml = xmlObjects.get(0).getXml();
            }
        }
        return xmlObjects;
    }

    @Override
    public XmlObject parseStringWithIndex(String source, String node, int index)
    {
        // TODO Auto-generated method stub
        List<XmlObject> xmls = parseString(source, node);
        if (xmls == null)
            return null;
        if (index >= xmls.size())
        {
            return null;
        }
        return xmls.get(index);
    }

    @Override
    public int sizeOf(String source, String node)
    {
        List<XmlObject> xmls = parseString(source, node);
        if (xmls != null)
            return xmls.size();
        else
        {
            return 0;
        }
    }

    // 将字符串转为流
    public InputStream string2InStream(String xmlStr)
    {
        if (xmlStr != null && !xmlStr.trim().equals(""))
        {
            try
            {
                inStream = new ByteArrayInputStream(xmlStr.getBytes());
                return inStream;
            } catch (Exception e)
            {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        return null;
    }
}
