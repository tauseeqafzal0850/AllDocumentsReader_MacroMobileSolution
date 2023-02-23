/*
 * 文件名称:           PGPlaceholder.java
 *  
 * 编译器:             android2.2
 * 时间:               上午10:14:15
 */
package com.faisaldeveloper.office.pg.model;

/**
 * placeholder type
 * <p>
 * <p>
 * Read版本:       Read V1.0
 * <p>
 * 作者:           jhy1790
 * <p>
 * 日期:           2012-3-9
 * <p>
 * 负责人:         jhy1790
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public class PGPlaceholderUtil
{
    // title
    public static final String TITLE = "title";
    // ctrTitle
    public static final String CTRTITLE = "ctrTitle";
    // subTitle
    public static final String SUBTITLE = "subTitle";
    // body
    public static final String BODY = "body";
    // half
    public static final String HALF = "half";
    // date
    public static final String DT = "dt";
    // footer
    public static final String FTR = "ftr";
    // slide number
    public static final String SLDNUM = "sldNum";   
    // header
    public static final String HEADER = "hdr";
    //Object
    public static final String OBJECT = "obj";
    //chart
    public static final String CHART = "chart";
    //table
    public static final String TABLE = "tbl";
    //Clip Art
    public static final String CLIPART = "clipArt";
    //Diagram
    public static final String DIAGRAM = "dgm";
    //media
    public static final String MEDIA = "media";
    //Slide Image
    public static final String SLIDEIMAGE = "sldImg";
    //Picture
    public static final String PICTURE = "pic";
    
    private static final PGPlaceholderUtil kit = new PGPlaceholderUtil();
    
    /**
     * 
     */
    public static PGPlaceholderUtil instance()
    {
        return kit;
    }
    
    /**
     * 
     */
    public boolean isTitle(String type)
    {
        return TITLE.equals(type) || CTRTITLE.equals(type);
    }
    
    /**
     * 
     */
    public boolean isBody(String type)
    {
        return !TITLE.equals(type) && !CTRTITLE.equals(type) && !DT.equals(type)
                && !FTR.equals(type) && !SLDNUM.equals(type);
    }    
    
    /**
     * 
     * @param type
     * @return
     */
    public boolean isTitleOrBody(String type)
    {
        return TITLE.equals(type) || CTRTITLE.equals(type) || SUBTITLE.equals(type)
                || BODY.equals(type);
    }
    
    /**
     * 页眉页脚，其中包括日期和时间、幻灯片编号、页脚
     * @param type
     * @return
     */
    public boolean isHeaderFooter(String type)
    {
        return DT.equals(type) || FTR.equals(type) || SLDNUM.equals(type);
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public boolean isDate(String type)
    {
        return DT.equals(type);
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public boolean isFooter(String type)
    {
        return FTR.equals(type);
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public boolean isSldNum(String type)
    {
        return SLDNUM.equals(type);
    }
    
    /**
     * ctrtitle按title处理
     */
    public String checkTypeName(String type)
    {
        if (PGPlaceholderUtil.CTRTITLE.equals(type))
        {
            type = PGPlaceholderUtil.TITLE;
        }
        return type;
    }
    
    /**
     * 
     * @param name
     * @param type
     */
    public String processType(String name, String type)
    {
        /*if (name != null && type == null)
        {
            if(name.contains("日期"))
            {
                type = PGPlaceholder.DT;
            }
            else if (name.contains("页脚"))
            {
                type = PGPlaceholder.FTR;
            }
            else if (name.contains("幻灯片编号"))
            {
                type = PGPlaceholder.SLDNUM;
            }
        }*/
        return type;
    }
}
