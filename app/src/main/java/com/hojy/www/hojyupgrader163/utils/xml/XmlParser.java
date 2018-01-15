package com.hojy.www.hojyupgrader163.utils.xml;

import java.util.List;

public interface XmlParser
{

    /**
     * 创建只有一个节点的xml
     * 
     * @param node
     *            节点
     * @param value
     *            值
     */
    public String createXml(String node, String value);

    /**
     * 解析得一个
     * 
     * @param source
     * @param node
     * @return
     */
    public String getString(String source, String node);

    /**
     * 判断此xml字符串,是否标xml格式.
     * 
     * @param source
     * @return
     */
    public boolean isXml(String source);

    /**
     * 解析得一个结果集.
     * 
     * @param source
     * @param node
     * @return
     */
    public List<XmlObject> parseString(String source, String node);

    /**
     * 解析一个source字符串, 提供参数node名称.可以是全路径也可以是相对路径. index 为第几个找到的结点.为遍历提供入口.
     * 
     * @param source
     * @param node
     * @param index
     * @return
     */
    public XmlObject parseStringWithIndex(String source, String node, int index);

    /**
     * 取得node结点在 source中出现的次数.为遍历提供边界.
     * 
     * @param source
     * @param node
     * @return
     */
    public int sizeOf(String source, String node);

}
