/*
 * 文件名称:          ErrorListener.java
 *  
 * 编译器:            android2.2
 * 时间:              下午2:55:51
 */
package com.faisaldeveloper.office.macro;

import com.faisaldeveloper.office.system.ErrorUtil;

/**
 * error listener
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            ljj8494
 * <p>
 * 日期:            2012-11-12
 * <p>
 * 负责人:          ljj8494
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public interface ErrorListener
{

    int INSUFFICIENT_MEMORY = ErrorUtil.INSUFFICIENT_MEMORY;//"Unable to complete operation due to insufficient memory";
    //
    int SYSTEM_CRASH = ErrorUtil.SYSTEM_CRASH; //"System crash, terminate running";
    //
    int BAD_FILE = ErrorUtil.BAD_FILE; //"Bad file";
    //
    int OLD_DOCUMENT = ErrorUtil.OLD_DOCUMENT;//"The document is too old - Office 95 or older, which is not supported";
    //
    int PARSE_ERROR = ErrorUtil.PARSE_ERROR; //"File parsing error";
    //
    int RTF_DOCUMENT = ErrorUtil.RTF_DOCUMENT; //"The document is really a RTF file, which is not supported";
    //
    int PASSWORD_DOCUMENT = ErrorUtil.PASSWORD_DOCUMENT; //"It's a document with password which cannot parsed";
    //
    int PASSWORD_INCORRECT = ErrorUtil.PASSWORD_INCORRECT; //"document's password sent to engine is error";
    //
    int SD_CARD_ERROR = ErrorUtil.SD_CARD_ERROR; //"SD Card read or write error"
    //
    int SD_CARD_WRITEDENIED = ErrorUtil.SD_CARD_WRITEDENIED; //"SD Card Write Permission denied"
    //
    int SD_CARD_NOSPACELEFT = ErrorUtil.SD_CARD_NOSPACELEFT; //"SD Card has no space left"
    /**
     * when engine error occurred callback this method
     * 
     * @param   codeCode  error code 
     *          @see ErrorListener#INSUFFICIENT_MEMORY
     *          @see ErrorListener#SYSTEM_CRASH
     *          @see ErrorListener#BAD_FILE
     *          @see ErrorListener#OLD_DOCUMENT
     *          @see ErrorListener#RTF_DOCUMENT
     */
    void error(int errorCode);
    
    /**
     * when need destroy office engine instance callback this method
     */
    //public void destroyEngine();
}
