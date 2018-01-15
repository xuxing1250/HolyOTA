/**
 * 
 */
package com.hojy.www.hojyupgrader163.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import android.util.Log;

/**
 * Dom4jImp 方式
 */
public class Dom4jImp implements XmlParser
{
    protected final static String XMLHEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

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
        String[] nodes = null;
        if (node.contains("//"))
        {
            nodes = node.split("//");
        } else if (node.contains("/"))
        {
            nodes = node.split("/");
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(XMLHEADER);
        int i = 0;
        for (; i < nodes.length; i++)
        {
            buffer.append("<");
            buffer.append(nodes[i]).append(">");
        }
        buffer.append(value.trim());

        for (; i >= 0; i--)
        {
            buffer.append("</");
            buffer.append(nodes[i]).append(">");
        }
        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     * @see com.hojy.wifihotspot.supports.XmlParser#sizeOf(java.lang.String,
     * java.lang.String)
     */

    @Override
    public synchronized String getString(String source, String node)
    {
        // TODO Auto-generated method stub
        String value = "";
        long time0 = System.currentTimeMillis();
        try
        {
            if (!source.startsWith("<?xml"))
            {
                source += XMLHEADER;
            }
            if (!node.contains("//"))
            {
                node = node.replace("/", "//");

                if (!node.startsWith("/"))
                {
                    node = "//" + node;
                }
            }

            InputStream is = new ByteArrayInputStream(source.getBytes("UTF-8"));
            SAXReader reader = new SAXReader();
            Document doc = reader.read(is);

            Element eleRoot = doc.getRootElement();
            Node e = eleRoot.selectSingleNode(node);
            value = e.getText();
            is.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.d("Dom4jImpl.parseString(" + node + ")=",
                (System.currentTimeMillis() - time0) + " ms.");
        return value;
    }

    /*
     * (non-Javadoc)
     * @see com.hojy.wifihotspot.supports.XmlParser#isXml(java.lang.String)
     */

    @Override
    public boolean isXml(String source)
    {
        // TODO Auto-generated method stub

        return false;
    }

    @Override
    public List<XmlObject> parseString(String source, String node)
    {
        // TODO Auto-generated method stub
        long time0 = System.currentTimeMillis();
        List<XmlObject> objs = new ArrayList<XmlObject>();

        try
        {

            if (!source.startsWith("<?xml"))
            {
                source += XMLHEADER;
            }
            if (!node.contains("//"))
            {
                node = node.replace("/", "//");

                if (!node.startsWith("/"))
                {
                    node = "//" + node;
                }
            }

            InputStream is = new ByteArrayInputStream(source.getBytes("UTF-8"));
            SAXReader reader = new SAXReader();
            Document doc = reader.read(is);

            Element eleRoot = doc.getRootElement();
            List<Element> idList = (List<Element>) doc.selectNodes(node);
            Iterator<Element> idIter = idList.iterator();
            while (idIter.hasNext())
            {
                Element i = idIter.next();
                XmlObject object = new XmlObject(i.asXML().trim());
                objs.add(object);
            }

            is.close();
        } catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d("Dom4jImpl.parseString(" + node + ")=",
                (System.currentTimeMillis() - time0) + " ms.");
        return objs;
    }

    @Override
    public XmlObject parseStringWithIndex(String source, String node, int index)
    {
        // TODO Auto-generated method stub
        List<XmlObject> objs = parseString(source, node);

        // 让系统来抛出index超范围异常.
        return objs.get(index);
    }

    @Override
    public int sizeOf(String source, String node)
    {
        // TODO Auto-generated method stub
        List<XmlObject> objs = parseString(source, node);
        return objs.size();
    }

}
