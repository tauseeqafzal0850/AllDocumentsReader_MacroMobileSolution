/*
 * 文件名称:          IReader.java
 *  
 * 编译器:            android2.2
 * 时间:              下午1:58:11
 */
package com.faisaldeveloper.office.system;

import java.io.File;

/**
 * 
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            ljj8494
 * <p>
 * 日期:            2012-4-23
 * <p>
 * 负责人:          ljj8494
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public interface IReader
{

    /**
     * get model data
     * @param filePath
     * @return
     */
    Object getModel() throws Exception;
    
    /**
     * search content 
     * @param file
     * @param key
     * @return
     */
    boolean searchContent(File file, String key) throws Exception;
    
    /**
     * 
     * @return
     */
    boolean isReaderFinish();
    
    /**
     * 后台读取数据
     * @throws Exception
     */
    void backReader() throws Exception;
    
    /**
     * 中断文档解析
     */
    void abortReader();
    
    /**
     * 
     * @return
     */
    boolean isAborted();
    
    /**
     * 
     * @param password password of document
     * @return true: succeed authenticate False: fail authenticate
     */
    //public boolean authenticate(String password);
    
    /**
     * cancel authenticate
     */
     //public void cancelAuthenticate();
    /**
     * 
     */
    IControl getControl();
     
    /**
     * 
     */
    void dispose();
}
