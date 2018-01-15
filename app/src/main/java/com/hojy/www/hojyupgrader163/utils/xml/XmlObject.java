package com.hojy.www.hojyupgrader163.utils.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @category 本类代表,xml格式的字符串的类. 包括在一对标签中.
 */

public class XmlObject
{

    String headString = "<?xml version = \"1.0\" encoding = \"utf-8\"?>";
    // <index >xxx</index>
    String valueString;

    public XmlObject()
    {
        valueString = "";
    }

    public XmlObject(String xml)
    {
        valueString = xml;
    }

    public XmlObject(String node, Object value)
    {
        StringBuffer buf = new StringBuffer();
        buf.append('<').append(node).append('>').append(value).append("</")
                .append(node).append('>');
        valueString = buf.toString();
    }

    // 获取属性值
    public String getAttribute()
    {
        int index0 = valueString.indexOf("=") + 2;
        int index1 = valueString.indexOf(">") - 1;
        if (index0 < 0 || index1 < 0)
        {
            return "";
        }
        // 返回这两个字符中间的字符串.
        return valueString.substring(index0, index1);
    }

    // 获取子结点.没有子结点时,取空值.
    public List<XmlObject> getChild()
    {
        if (!hasChildNode())
        {
            return null;
        }
        // ...取出子结点.
        String childNode = null;
        int i = valueString.indexOf('>');
        if (i != -1)
        {
            int j = valueString.indexOf('<', i);
            if (j != -1)
            {
                int k = valueString.indexOf('>', j);
                childNode = valueString.substring(j + 1, k);
                childNode = childNode.trim();
            }
        }
        List<XmlObject> childrenList = new ArrayList<XmlObject>();
        if (childNode != null && childNode != "")
        {
            XmlParser parser = XmlFactory.getDefaultParser();
            childrenList = parser.parseString(valueString, childNode);
        }

        return childrenList;
    }

    // 取得结点的值 .可以包含子结点.
    public String getValue()
    {
        int index0 = valueString.indexOf('>') + 1;
        int index1 = valueString.lastIndexOf("</");
        if (index0 < 0 || index1 < 0)
        {
            return "";
        }
        // 返回这两个字符中间的字符串.
        return valueString.substring(index0, index1);
    }

    public String getXml()
    {
        return headString + valueString;
    }

    // 判断有没有子结点.
    public boolean hasChildNode()
    {
        if (valueString == null || valueString == "")
            return false;
        int i = valueString.indexOf('>');
        if (i != -1)
        {
            if (valueString.indexOf('<', i) != -1)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return valueString;
    }
}
