/*
 * 文件名称:          IPageListViewListener.java
 *  
 * 编译器:            android2.2
 * 时间:              下午4:39:56
 */
package com.faisaldeveloper.office.system.beans.pagelist;

import com.faisaldeveloper.office.macro.TouchEventListener;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * page list component listener
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            ljj8494
 * <p>
 * 日期:            2013-1-6
 * <p>
 * 负责人:          ljj8494
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public interface IPageListViewListener
{
    // onTouch
    byte ON_TOUCH = 0;
    // onDown
    byte ON_DOWN = 1;
    // onShowPress
    byte ON_SHOW_PRESS = 2;
    // onSingleTapUp
    byte ON_SINGLE_TAP_UP = 3;
    // onScroll
    byte ON_SCROLL = 4;
    // onLongPress
    byte ON_LONG_PRESS = 5;
    // onFling
    byte ON_FLING = 6;
    // onSingleTapConfirmed
    byte ON_SINGLE_TAP_CONFIRMED = 7;
    // onDoubleTap
    byte ON_DOUBLE_TAP = 8;
    // onDoubleTapEvent
    byte ON_DOUBLE_TAP_EVENT = 9;
    // onClick
    byte ON_CLICK = 10;
    
    //print mode moving position
    byte Moving_Horizontal = 0;
    byte Moving_Vertical = 1;
    /**
     * 
     */
    Object getModel();
    
    /**
     * 
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    APageListItem getPageListItem(int position, View convertView, ViewGroup parent);
    
    /**
     * 
     * @param view
     * @param srcBitmap
     */
    void exportImage(final APageListItem view, final Bitmap srcBitmap);
   
   /**
    * 
    */
   int getPageCount();
   
   /**
    * 
    * @param pageIndex
    * @return
    */
   Rect getPageSize(int pageIndex);
   
   /**
    * event method, office engine dispatch 
    * 
    * @param       v             event source
    * @param       e1            MotionEvent instance
    * @param       e2            MotionEvent instance
    * @param       velocityX     x axis velocity
    * @param       velocityY     y axis velocity  
    * @param       eventNethodType  event method      
    *              @see TouchEventListener#EVENT_CLICK
    *              @see TouchEventListener#EVENT_DOUBLE_TAP
    *              @see TouchEventListener#EVENT_DOUBLE_TAP_EVENT
    *              @see TouchEventListener#EVENT_DOWN
    *              @see TouchEventListener#EVENT_FLING
    *              @see TouchEventListener#EVENT_LONG_PRESS
    *              @see TouchEventListener#EVENT_SCROLL
    *              @see TouchEventListener#EVENT_SHOW_PRESS
    *              @see TouchEventListener#EVENT_SINGLE_TAP_CONFIRMED
    *              @see TouchEventListener#EVENT_SINGLE_TAP_UP
    *              @see TouchEventListener#EVENT_TOUCH
    */
   boolean onEventMethod(View v, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY, byte eventMethodType);
   
   /**
    * action派发
    *
    * @param actionID 动作ID  
    * @param obj 动作ID的Value
    */
   void updateStutus(Object obj);
   
   /**
    * 
    */
   void resetSearchResult(APageListItem pageItem);
   
   /**
    * is support zoom in / zoom out
    * 
    * @return  true  touch zoom
    *           false don’t touch zoom
    */

   boolean isTouchZoom();
   
   /**
    * true: show message when zooming
    * false: not show message when zooming
    * @return
    */
   boolean isShowZoomingMsg();
   
   /**
    * callback this method after zoom change
    */
   void changeZoom();
   
   /**
    *  set change page flag, Only when effectively the PageSize greater than ViewSize.
    *  (for PPT, word print mode, PDF)
    *  
    *  @param b    = true, change page
    *              = false, don't change page
    */
   boolean isChangePage();
   

   /**
    * @param isDrawPictrue The isDrawPictrue to set.
    */
   void setDrawPictrue(boolean isDrawPictrue);
   
   /**
    * 
    */
   boolean isInit();
   
   /**
    * 
    * @return
    * true fitzoom may be larger than 100% but smaller than the max zoom
    * false fitzoom can not larger than 100%
    */
   boolean isIgnoreOriginalSize();
   
   /**
    * page list view moving position
    * @param position horizontal or vertical
    */
   byte getPageListViewMovingPosition();
}
