/*
 * 文件名称:          IShape.java
 *  
 * 编译器:            android2.2
 * 时间:              下午3:23:12
 */
package com.faisaldeveloper.office.common.shape;

import com.faisaldeveloper.office.java.awt.Rectangle;
import com.faisaldeveloper.office.pg.animate.IAnimation;


/**
 * PowerPoint的shape的接口
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            ljj8494
 * <p>
 * 日期:            2012-2-13
 * <p>
 * 负责人:          ljj8494
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public interface IShape
{
    /**
     * 
     * @param id
     */
    void setGroupShapeID(int id);
    
    /**
     * 
     * @return
     */
    int getGroupShapeID();
    
    /**
     * 
     * @param id
     */
    void setShapeID(int id);
    
    /**
     * 
     * @return
     */
    int getShapeID();
    
    /**
     * 
     * @return
     */
    short getType();
    /**
     *  @return the parent of this shape
     */
    IShape getParent();
    
    /**
     * set parent of this shape;
     */
    void setParent(IShape shape);
    
    /**
     * get size of this shape
     */
    Rectangle getBounds();
    /**
     * set size of this shape
     */
    void setBounds(Rectangle rect);
    /**
     * get data of this shape
     */
    Object getData();
    /**
     * set data of this shape
     */
    void setData(Object data);
    /**
     * get horizontal flip of this shape
     */
    boolean getFlipHorizontal();
    /**
     * set horizontal flip of this shape
     */
    void setFlipHorizontal(boolean flipH);
    /**
     * get vertical flip of this shape
     */
    boolean getFlipVertical();
    /**
     * set vertical flip of this shape
     */
    void setFlipVertical(boolean flipV);
    /**
     * get rotation of this shape
     */
    float getRotation();
    /**
     * set rotation of this shape
     */
    void setRotation(float angle);
    
    /**
     * 
     * @param hidden
     */
    void setHidden(boolean hidden);
    
    /**
     * 
     * @return
     */
    boolean isHidden();
    
    /**
     * set shape animation
     * @param animation
     */
    void setAnimation(IAnimation animation);
    
    /**
     * 
     * @return
     */
    IAnimation getAnimation();
    
    /**
     * 
     * @return
     */
    int getPlaceHolderID();
    
    /**
     * 
     * @param placeHolderID
     */
    void setPlaceHolderID(int placeHolderID);
    
    /**
     * dispose
     */
    void dispose();
    
}
