/*
 * 文件名称:          SlideshowListener.java
 *  
 * 编译器:            android2.2
 * 时间:              上午9:19:37
 */
package com.faisaldeveloper.office.macro;

import com.faisaldeveloper.office.common.ISlideShow;

/**
 * TODO: 文件注释
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            jqin
 * <p>
 * 日期:            2012-12-28
 * <p>
 * 负责人:           jqin
 * <p>
 * 负责小组:           
 * <p>
 * <p>
 */
public interface SlideShowListener
{
    //begin slideshow
    byte SlideShow_Begin = ISlideShow.SlideShow_Begin;                           //0
    //exit slideshow
    byte SlideShow_Exit = ISlideShow.SlideShow_Exit;          //1
    //previous step of animation
    byte SlideShow_PreviousStep = ISlideShow.SlideShow_PreviousStep;   //2
    //next step of animation
    byte SlideShow_NextStep = ISlideShow.SlideShow_NextStep;//3
    //previous slide
    byte SlideShow_PreviousSlide = ISlideShow.SlideShow_PreviousSlide;//4
    //next slide
    byte SlideShow_NextSlide = ISlideShow.SlideShow_NextSlide;//5
    
//    /**
//     * 
//     * @param actionType
//     */
//    public void slideshow(byte actionType);
    
    /**
     * exit slideshow
     */
    void exit();
}
