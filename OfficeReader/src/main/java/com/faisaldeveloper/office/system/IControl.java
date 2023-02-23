/*
 * 文件名称:          IControl.java
 *  
 * 编译器:            android2.2
 * 时间:              下午1:50:27
 */
package com.faisaldeveloper.office.system;

import com.faisaldeveloper.office.common.ICustomDialog;
import com.faisaldeveloper.office.common.IOfficeToPicture;
import com.faisaldeveloper.office.common.ISlideShow;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

/**
 * control接口
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            梁金晶
 * <p>
 * 日期:            2011-10-27
 * <p>
 * 负责人:          梁金晶
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public interface IControl
{
    /**
     * 布局视图
     * @param x
     * @param y
     * @param w
     * @param h
     */
    void layoutView(int x, int y, int w, int h);
    
    /**
     * action派发
     *
     * @param actionID 动作ID  
     * @param obj 动作ID的Value
     */
    void actionEvent(int actionID, Object obj);
    
    /**
     * 得到action的状态的值
     * 
     * @return obj
     */
    Object getActionValue(int actionID, Object obj);
       
    /**
     * current view index
     * @return
     */
    int getCurrentViewIndex();
    
    /**
     * 获取应用组件
     */
    View getView();
    
    /**
     * 
     */
    Dialog getDialog(Activity activity, int id);
    
    /**
     * 
     */
    IMainFrame getMainFrame();
    
    /**
     * 
     */
    Activity getActivity();
    
    /**
     * get find instance
     */
    IFind getFind();
    
    /**
     * 
     */
    boolean isAutoTest();
    
    /**
     * 
     */
    IOfficeToPicture getOfficeToPicture();
    
    /**
     * 
     */
    ICustomDialog getCustomDialog();
    
    /**
     * 
     */
    boolean isSlideShow();
    
    /**
     * 
     * @return
     */
    ISlideShow getSlideShow();
    
    /**
     * 
     */
    IReader getReader();
    
    /**
     * 
     */
    boolean openFile(String filePath);
    
    /**
     * 
     */
    byte getApplicationType();
    
    /**
     * 
     */
    SysKit getSysKit();
    
    /**
     * 释放内存
     */
    void dispose();
}
