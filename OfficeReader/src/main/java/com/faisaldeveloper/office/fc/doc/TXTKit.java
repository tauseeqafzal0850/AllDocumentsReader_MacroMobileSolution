/*
 * 文件名称:          TXTKit.java
 *  
 * 编译器:            android2.2
 * 时间:              下午5:16:41
 */
package com.faisaldeveloper.office.fc.doc;

import java.io.FileInputStream;
import java.util.Vector;

import com.faisaldeveloper.office.common.ICustomDialog;
import com.faisaldeveloper.office.constant.DialogConstant;
import com.faisaldeveloper.office.fc.fs.storage.HeaderBlock;
import com.faisaldeveloper.office.fc.fs.storage.LittleEndian;
import com.faisaldeveloper.office.system.ErrorUtil;
import com.faisaldeveloper.office.system.FileReaderThread;
import com.faisaldeveloper.office.system.IControl;
import com.faisaldeveloper.office.system.IDialogAction;
import com.faisaldeveloper.office.system.MainControl;
import com.faisaldeveloper.office.thirdpart.mozilla.intl.chardet.CharsetDetector;
import com.faisaldeveloper.office.wp.dialog.TXTEncodingDialog;

import android.os.Handler;

/**
 * text kit
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            ljj8494
 * <p>
 * 日期:            2012-3-12
 * <p>
 * 负责人:          ljj8494
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public class TXTKit
{
    //
    private static final TXTKit kit = new TXTKit();
    //
    public static TXTKit instance()
    {
        return kit;
    }
    
    /**
     * 
     * @param control
     * @param handler
     * @param filePath
     */
    public void readText(final IControl control, final Handler handler, final String filePath)
    {              
        try
        {
            FileInputStream in = new FileInputStream(filePath);
            byte[] b = new byte[16];
            in.read(b);            
            long signature = LittleEndian.getLong(b, 0);
            if (signature == HeaderBlock._signature // doc, ppt, xls
                || signature == 0x0006001404034b50L) // docx, pptx, xls
            {
                in.close();
                control.getSysKit().getErrorKit().writerLog(new Exception("Format error"), true);
                return;
            }
            signature = signature & 0x00FFFFFFFFFFFFFFL;
            if (signature == 0x002e312d46445025L)
            {
                in.close();
                control.getSysKit().getErrorKit().writerLog(new Exception("Format error"), true);
                return;
            }
            in.close();
            
            String code = control.isAutoTest() ? "GBK" : CharsetDetector.detect(filePath);
            if (code != null)
            {
                new FileReaderThread(control, handler, filePath, code).start();
                return;
            }
            
            if(control.getMainFrame().isShowTXTEncodeDlg())
            {
                Vector<Object> vector = new Vector<Object>();
                vector.add(filePath);
                IDialogAction da = new IDialogAction()
                {                    
                    /**
                     * 
                     *
                     */
                    public IControl getControl()
                    {
                        return control;
                    }
                    
                    /**
                     * 
                     *
                     */
                    public void doAction(int id, Vector<Object> model)
                    {
                        if (TXTEncodingDialog.BACK_PRESSED.equals(model.get(0)))
                        {
                            control.getMainFrame().getActivity().onBackPressed();
                        }
                        else
                        {
                            new FileReaderThread(control, handler, filePath, model.get(0).toString()).start();
                        }
                    }
                    
                    /**
                     * 
                     *
                     */
                    public void dispose()
                    {
                        
                    }
                };                

                new TXTEncodingDialog(control, control.getMainFrame().getActivity(), da, 
                    vector, DialogConstant.ENCODING_DIALOG_ID).show();
                
            }
            else
            {
                String encode = control.getMainFrame().getTXTDefaultEncode();
                if(encode == null)
                {
                    ICustomDialog dlgListener = control.getCustomDialog();
                    if(dlgListener != null)
                    {
                        dlgListener.showDialog(ICustomDialog.DIALOGTYPE_ENCODE);
                    }
                    else
                    {
                        new FileReaderThread(control, handler, filePath, "UTF-8").start();
                    }
                }
                else
                {
                    new FileReaderThread(control, handler, filePath, encode).start();
                }
                return;
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return;
    }
    
    /**
     * 
     */
    public void reopenFile(final IControl control, final Handler handler, final String filePath, String encode)
    {
        new FileReaderThread(control, handler, filePath, encode).start();
    }
}
