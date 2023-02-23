/*
 * 文件名称:          IDocument.java
 *  
 * 编译器:            android2.2
 * 时间:              下午3:49:19
 */
package com.faisaldeveloper.office.simpletext.model;


/**
 * 文本model接口
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            ljj8494
 * <p>
 * 日期:            2011-11-11
 * <p>
 * 负责人:          ljj8494
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public interface IDocument
{
    
    /**
     * 得到指定的offset的区域
     * @see com.faisaldeveloper.office.constant.wp.WPModelConstant
     */
    long getArea(long offset);
    
    /**
     * 得到区域开始位置
     * @see com.faisaldeveloper.office.constant.wp.WPModelConstant
     */
    long getAreaStart(long offset);
    
    /**
     * 得到区域结束位置
     * @see com.faisaldeveloper.office.constant.wp.WPModelConstant
     */
    long getAreaEnd(long offset);
    
    /**
     * 得到区域文本长度
     */
    long getLength(long offset);
    
    /**
     * 得到章节元素
     */
    IElement getSection(long offset);
    
    /**
     * 得到段落元素 
     */
    IElement getParagraph(long offset);
    /**
     * 
     */
    IElement getParagraphForIndex(int index, long area);
    
    /**
     * 添加段落
     * @param sectionElement    章节元素
     * @param paraElement       段落无素       
     */
    void appendParagraph(IElement element, long offset);
    
    /**
     * 
     */
    void appendSection(IElement elem);
    
    /**
     * 
     */
    void appendElement(IElement elem, long offset);
    
    /**
     * 得到leaf元素
     */
    IElement getLeaf(long offset);
    
    /**
     * 得到页眉、页脚元素
     * @param area 区域
     * @param type Element类型，首页、奇数页、偶数页
     */
    IElement getHFElement(long area, byte type);
    

    
    /**
     * get 段落总数
     */
    int getParaCount(long area);
    
    /**
     * get 字符串
     */
    String getText(long start, long end);
    
    /**
     * 
     */
    void dispose();
    
}
