package com.faisaldeveloper.office.common.picture;

public class PictureConverterThread extends Thread
{
	public  PictureConverterThread(PictureConverterMgr converterMgr, String srcPath, String dstPath, String type )
    {
        this.converterMgr = converterMgr;
        this.type = type;
        
        this.sourPath = srcPath;
        this.destPath = dstPath;
    }
    
    public void run() 
    {
    	converterMgr.convertPNG(sourPath, destPath, type,  false);
    }   
    
    
    private final PictureConverterMgr converterMgr;
    private final String type;
    private final String sourPath;
    private final String destPath;
}
