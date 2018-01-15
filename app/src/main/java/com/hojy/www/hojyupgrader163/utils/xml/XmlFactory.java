/**
 * 
 */
package com.hojy.www.hojyupgrader163.utils.xml;

/**
 * @author Administrator
 * 
 */
public class XmlFactory
{

    final class XmlAnalys
    {
        final static String DOM4J = "dom4j";
        final static String MESSAGE = "messaage";
        final static String PULL = "pull";
        final static String SAX = "sax";
    }

    // 获取一个默认的解析器.
    public static XmlParser getDefaultParser()
    {
        return new SaxParserImp();
        // return new PullParserImp();
        // return new Dom4jImp();
    }

    // 根据传入的解析器名称,获取一个解析器.默认获取默认的解析器.
    public static XmlParser getXmlParserByName(String xmlparser)
    {
        if (xmlparser.contains(XmlAnalys.SAX))
        {
            return new SaxParserImp();
        }
        if (xmlparser.contains(XmlAnalys.PULL))
        {
            return new PullParserImp();
        }
        if (xmlparser.contains(XmlAnalys.DOM4J))
        {
            return new Dom4jImp();
        }
        if (xmlparser.contains(XmlAnalys.MESSAGE))
            ;
        return getDefaultParser();
    }
}
