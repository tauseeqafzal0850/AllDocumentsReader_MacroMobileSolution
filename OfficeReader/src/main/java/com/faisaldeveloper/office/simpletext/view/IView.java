/*
 * 文件名称:          IView.java
 *  
 * 编译器:            android2.2
 * 时间:              下午3:16:53
 */
package com.faisaldeveloper.office.simpletext.view;

import com.faisaldeveloper.office.constant.wp.WPViewConstant;
import com.faisaldeveloper.office.java.awt.Rectangle;
import com.faisaldeveloper.office.simpletext.control.IWord;
import com.faisaldeveloper.office.simpletext.model.IDocument;
import com.faisaldeveloper.office.simpletext.model.IElement;
import com.faisaldeveloper.office.system.IControl;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 视图接口
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
public interface IView
{
    /**
     * 得到视图对应Model的Element
     */
    IElement getElement();
    /**
     * 
     */
    void setElement(IElement elem);
    /**
     * 视图类型，页、栏、段落、行、leaf等
     */
    short getType();
    /**
     * x值，相对父视图
     */
    void setX(int x);
    int getX();
    /**
     * y值, 相对父视图
     */
    void setY(int y);
    int getY();
    /**
     * 宽度
     */
    void setWidth(int w);
    int getWidth();
    /**
     * 高度
     */
    void setHeight(int h);
    int getHeight();
    /**
     * 设置大小
     */
    void setSize(int w, int h);
    /**
     * 设置位置
     */
    void setLocation(int x, int y);
    /**
     * 设置范围
     */
    void setBound(int x, int y, int w, int h);
    /**
     * 上边距
     */
    void setTopIndent(int top);
    int getTopIndent();
    /**
     * 下边距
     */
    void setBottomIndent(int bottom);
    int getBottomIndent();
    /**
     * 左边距 
     */
    void setLeftIndent(int left);
    int getLeftIndent();
    /**
     * 左边距
     */
    void setRightIndent(int right);
    int getRightIndent();
    /**
     * 设置边距
     */
    void setIndent(int left, int top, int right, int bottom);
    /**
     * 得到组件
     */
    IWord getContainer();
    
    /**
     * 
     */
    IControl getControl();
    
    /**
     * 得到model
     */
    IDocument getDocument();
    /**
     * 第一个子视图
     */
    void setChildView(IView view);
    IView getChildView();
    /**
     * 父视图
     */
    void setParentView(IView view);
    IView getParentView();
    /**
     * 前一个视图
     */
    void setPreView(IView view);
    IView getPreView();
    /**
     * 后一个视图
     */
    void setNextView(IView view);
    IView getNextView();
    /**
     * 得到最后一个子视图，此方法尽量少用，非常耗时
     */
    IView getLastView();
    /**
     * 得到子视图数量，此方法尽量少调用，非常耗时
     */
    int getChildCount();
    /**
     * 追加一个子视图
     */
    void appendChlidView(IView view);
    /**
     * 指定视图后面插入一个子视图
     * 
     * @param view 指定视图，如果 = null, 则把newView设置成第一个视图
     * @param newView 要插入的视图
     */
    void insertView(IView view, IView newView);
    /**
     * 删除一个指定视图
     * 
     * @param idDeleteChlid = ture，则连子视图也删除。
     */
    void deleteView(IView view, boolean isDeleteChild);
    
    /**
     * 视图开始位置
     */
    void setStartOffset(long start);
    long getStartOffset(IDocument doc);
    /**
     * 视图结束位置
     * @param end 相对于model的element开始位置的
     */
    void setEndOffset(long end);
    long getEndOffset(IDocument doc);
    
    /**
     * 得到Element开始位置
     */
    long getElemStart(IDocument doc);
    /**
     * 得到Element结束位置
     */
    long getElemEnd(IDocument doc);
    
    /**
     * get view rect
     * @param originX
     * @param originY
     * @param zoom
     * @return
     */
    Rect getViewRect(int originX, int originY, float zoom);
    
    /**
     * 视图是否在指定区间内
     * 
     * @param rect
     */
    boolean intersection(Rect rect, int originX, int originY, float zoom);
    /**
     * 视图是否包含指定 offset
     * 
     * @param offset 指定的offset
     * @param isBack 是否向后取，是为在视图上，上一行的结束位置与下一行开始位置相同
     */
    boolean contains(long offset, boolean isBack);
    
    /**
     * 视图是否包含指定 x，y值
     * @param x
     * @param y
     * @param isBack
     * @return
     */
    boolean contains(int x, int y, boolean isBack);
    
    /**
     * model到视图
     * @param offset 指定的offset
     * @param isBack 是否向后取，是为在视图上，上一行的结束位置与下一行开始位置相同
     */
    Rectangle modelToView(long offset, Rectangle rect, boolean isBack);
    /**
     * @param x
     * @param y
     * @param isBack 是否向后取，是为在视图上，上一行的结束位置与下一行开始位置相同
     */
    long viewToModel(int x, int y, boolean isBack);
    
    /**
     * 得到下一个Offset位置，根据坐标
     * 
     * @param offset 当前Offset
     * @param dir 方向，上、下、左、右
     * @param x
     * @param y
     */
    long getNextForCoordinate(long offset, int dir, int x, int y);
    /**
     * 得到下一个Offset位置，根据Offset
     * @param offset 当前Offset
     * @param dir 方向，上、下、左、右
     * @param x
     * @param y
     */
    long getNextForOffset(long offset, int dir, int x, int y);
    /**
     * 
     * @param canvas
     * @param originX
     * @param originY
     * @param zoom
     */
    void draw(Canvas canvas, int originX, int originY, float zoom);
    
    /**
     * 视图布局
     * @param x
     * @param y
     * @param w
     * @param h
     * @param maxEnd 
     * @param flag 布局标记，传递一些布尔值，位操作
     */
    int doLayout(int x, int y, int w, int h, long maxEnd, int flag);
    
    /**
     * 得视图布局大小，包括上下左右Indent
     * 
     * @param axis 方向
     * @see WPViewConstant.X_AXIS
     * @see WPViewConstant.Y_AXIS
     */
    int getLayoutSpan(byte axis);
    
    /**
     * 得到包括offset的指定视图
     * 
     * @param offset
     * @param type
     * @param isBack
     * @return
     */
    IView getView(long offset, int type, boolean isBack);
    
    /**
     * 得到包括x, y值的指定视图
     */
    IView getView(int x, int y, int type, boolean isBack);
    
    /**
     * 释放内存
     */
    void dispose();
    
    /**
     * 
     */
    void free();
     
}
