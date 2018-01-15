/**
 * 
 */
package com.hojy.www.hojyupgrader163.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

/**
 * pull方式
 */
class PullParserImp implements XmlParser
{
    private final static int MAX_INDEX = 10000;
    int size = -1;

    /*
     * (non-Javadoc) 直接去找第几个结点的值. 跳过其他节点的解析.
     * @see
     * com.hojy.wifihotspot.supports.XmlParser#parseStringWithIndex(java.lang
     * .String, java.lang.String, int)
     */

    @Override
    public String createXml(String node, String value)
    {
        // TODO Auto-generated method stub
        StringBuffer buf = new StringBuffer();
        String[] nodes = node.split("/");
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        for (int i = 0; i < nodes.length; i++)
        {
            buf.append("<").append(nodes[i]).append(">");
        }
        buf.append(value);
        for (int i = nodes.length - 1; i >= 0; i--)
        {
            buf.append("</").append(nodes[i]).append(">");
        }
        return buf.toString();
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
        XmlObject obj = null;
        if ((obj = parseStringWithIndex(source, node, 0)) != null)
        {
            return obj.getValue();
        }
        return null;
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
        if (source.equals("") || node.equals(""))
            return null;

        ByteArrayInputStream is = new ByteArrayInputStream(source.getBytes());
        // XmlPullParser parser = Xml.newPullParser();
        XmlPullParser parser = null;
        try
        {
            parser = XmlPullParserFactory.newInstance().newPullParser();
        } catch (XmlPullParserException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String[] nodes = node.split("/");
        List<XmlObject> list = new ArrayList<XmlObject>();
        int iCount = 0;
        try
        {
            parser.setInput(is, "UTF-8");
            int event = parser.getEventType();// 产生第一个事件
            while (event != XmlPullParser.END_DOCUMENT)
            {
                switch (event)
                {

                case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
                {
                    // 单独处理==1的情况 , 加快处理速度.
                    if (parser.getName().equals(nodes[iCount]))
                    {
                        if (iCount >= nodes.length - 1)
                        {
                            Log.d("PullParserImp", parser.getName());
                            XmlObject obj = new XmlObject(nodes[iCount],
                                    parser.nextText());
                            Log.d("PullParserImp", iCount + "-parserName:"
                                    + nodes[iCount]);
                            Log.d("PullParserImp", iCount + "-parserValue:"
                                    + obj.getValue());
                            list.add(obj);
                        } else
                        {
                            iCount++;
                        }
                    }
                }
                    break;
                case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
                {
                    if (parser.getName().equals(nodes[0]))
                    {
                        iCount = 0;
                    }
                }
                    break;
                }

                event = parser.next();// 进入下一个元素并触发相应事件
            }// end while

        } catch (XmlPullParserException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            is.close();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public XmlObject parseStringWithIndex(String source, String node, int index)
    {
        // TODO Auto-generated method stub
        if (source.equals("") || node.equals("") || index < 0
                || index > MAX_INDEX)
            return null;
        Log.d("PullParserImp", "nodeName:" + node);
        ByteArrayInputStream is = new ByteArrayInputStream(source.getBytes());
        XmlPullParser parser = null;
        try
        {
            parser = XmlPullParserFactory.newInstance().newPullParser();
        } catch (XmlPullParserException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            try
            {
                is.close();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String[] nodes = node.split("/");
        int iCount = 0;
        int index0 = 0;
        try
        {
            parser.setInput(is, "UTF-8");
            int event = parser.getEventType();// 产生第一个事件
            while (event != XmlPullParser.END_DOCUMENT)
            {
                switch (event)
                {

                case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
                {
                    if (parser.getName().equals(nodes[iCount]))
                    {
                        if (iCount >= nodes.length - 1)
                        {
                            if (index0 >= index)
                            {
                                Log.d("PullParserImp", parser.getName());
                                XmlObject obj = new XmlObject(nodes[iCount],
                                        parser.nextText());
                                Log.d("PullParserImp", iCount + "-parserName:"
                                        + nodes[iCount]);
                                Log.d("PullParserImp", iCount + "-parserValue:"
                                        + obj.getValue());
                                is.close();
                                return obj;
                            } else
                            {
                                index0++;
                            }
                        } else
                        {
                            iCount++;
                        }
                    }
                }
                    break;
                case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
                {
                    if (parser.getName().equals(nodes[0]))
                    {
                        iCount = 0;
                    }
                }
                    break;
                }

                event = parser.next();// 进入下一个元素并触发相应事件
            }// end while

        } catch (XmlPullParserException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            is.close();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int sizeOf(String source, String node)
    {
        // TODO Auto-generated method stub
        return parseString(source, node).size();
    }

}
