package com.hojy.www.hojyupgrader163.utils.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxDefHandler extends DefaultHandler
{
    private List<XmlObject> list = null;
    private String node;
    private String nodeName;// 节点名称
    private String nodeValue;// 节点值
    private String result = "";
    private XmlObject xmlObject;

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        nodeValue = new String(ch, start, length); //
        result += nodeValue;
    }

    @Override
    public void endDocument() throws SAXException
    {
        // TODO Auto-generated method stub
        super.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String qname)
            throws SAXException
    {
        result += "</" + localName + ">";
        if (localName.equals(node))
        {
            xmlObject = new XmlObject(result);
            list.add(xmlObject);
            xmlObject = null;
        }

    }

    public List<XmlObject> getXmlData()
    {
        /**
         * 2014年3月24日10:01:49 zhangx 修改添加
         * 
         * list == null||
         */
        if (list == null || list.size() == 0)
        {
            list = null;
        }
        return list;
    }

    public void setXml(String node)
    {
        this.node = node;
    }

    @Override
    public void startDocument() throws SAXException
    {
        // TODO Auto-generated method stub
        super.startDocument();
        list = new ArrayList<XmlObject>();

    }

    @Override
    public void startElement(String uri, String localName, String qname,
            Attributes attributes) throws SAXException
    {
        nodeName = localName;
        if (localName.equals(node))
        {
            result = "<" + nodeName + ">";
        } else
        {
            result += "<" + nodeName + ">";
        }
    }

}
