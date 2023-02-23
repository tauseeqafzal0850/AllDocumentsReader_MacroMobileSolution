/*
 * 文件名称:          ISearch.java
 *  
 * 编译器:            android2.2
 * 时间:              下午4:36:19
 */
package com.faisaldeveloper.office.system;

/**
 * 查找接口
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            jqin
 * <p>
 * 日期:            2012-3-26
 * <p>
 * 负责人:           jqin
 * <p>
 * 负责小组:           
 * <p>
 * <p>
 */
public interface IFind
{
    /**
     * 
     * @param value 
     * @return true: finded  false: not finded
     */
    boolean find(String value);
    
    /**
     * need call function find first and finded
     * @return
     */
    boolean findBackward();
    
    /**
     * need call function find first and finded
     * @return
     */
    boolean findForward();
    
    
    /**
     * 
     */
    int getPageIndex();
    /**
     * 
     */
    void resetSearchResult();
    
    /**
     * 
     */
    void dispose();
}
