/*
 * 文件名称:          IWord.java
 *  
 * 编译器:            android2.2
 * 时间:              上午9:56:16
 */
package com.faisaldeveloper.office.simpletext.control;

import com.faisaldeveloper.office.common.shape.IShape;
import com.faisaldeveloper.office.java.awt.Rectangle;
import com.faisaldeveloper.office.pg.animate.IAnimation;
import com.faisaldeveloper.office.simpletext.model.IDocument;
import com.faisaldeveloper.office.system.IControl;

/**
 * IWord 接口
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            ljj8494
 * <p>
 * 日期:            2012-7-27
 * <p>
 * 负责人:          ljj8494
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public interface IWord
{
    /**
     * @return Returns the highlight.
     */
    IHighlight getHighlight();
    /**
     * 
     */
    Rectangle modelToView(long offset, Rectangle rect, boolean isBack);
    /**
     * 
     */
    IDocument getDocument();
    /**
     * 
     */
    String getText(long start, long end);
    
    /**
     * @param x 为100%的值
     * @param y 为100%的值
     */
    long viewToModel(int x, int y, boolean isBack);
    /**
     * 
     */
    byte getEditType();
    
    /**
     * 
     * @param para
     * @return
     */
    IAnimation getParagraphAnimation(int pargraphID);
    
    /**
     * 
     */
    IShape getTextBox();
    
    /**
     * 
     */
    IControl getControl();
    
    /**
     * 
     */
    void dispose();
}
