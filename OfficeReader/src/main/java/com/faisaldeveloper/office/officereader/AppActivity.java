/*
 * 文件名称:           MainControl.java
 *
 * 编译器:             android2.2
 * 时间:               下午1:34:44
 */

package com.faisaldeveloper.office.officereader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.faisaldeveloper.office.R;
import com.faisaldeveloper.office.common.IOfficeToPicture;
import com.faisaldeveloper.office.constant.EventConstant;
import com.faisaldeveloper.office.constant.MainConstant;
import com.faisaldeveloper.office.constant.wp.WPViewConstant;
import com.faisaldeveloper.office.macro.DialogListener;
import com.faisaldeveloper.office.res.ResKit;
import com.faisaldeveloper.office.ss.sheetbar.SheetBar;
import com.faisaldeveloper.office.system.IControl;
import com.faisaldeveloper.office.system.IMainFrame;
import com.faisaldeveloper.office.system.MainControl;
import com.faisaldeveloper.office.system.beans.pagelist.IPageListViewListener;
import com.faisaldeveloper.office.system.dialog.ColorPickerDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 文件注释
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

public class AppActivity extends Activity implements IMainFrame {
    /**
     * 构造器
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        control = new MainControl(this);
        appFrame = new AppFrame(getApplicationContext());

        appFrame.post(new Runnable() {
            /**
             */
            public void run() {
                init();
            }
        });
        control.setOffictToPicture(new IOfficeToPicture() {
            public Bitmap getBitmap(int componentWidth, int componentHeight) {
                if (componentWidth == 0 || componentHeight == 0) {
                    return null;
                }
                if (bitmap == null
                        || bitmap.getWidth() != componentWidth
                        || bitmap.getHeight() != componentHeight) {
                    // custom picture size
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    //bitmap = Bitmap.createBitmap(800, 600,  Config.ARGB_8888);
                    // 
                    bitmap = Bitmap.createBitmap(componentWidth, componentHeight, Config.ARGB_8888);
                }
                return bitmap;
                //return null;
            }

            public void callBack(Bitmap bitmap) {
                saveBitmapToFile(bitmap);
            }

            //
            private Bitmap bitmap;

            @Override
            public void setModeType(byte modeType) {
                // TODO Auto-generated method stub

            }

            @Override
            public byte getModeType() {
                // TODO Auto-generated method stub
                return VIEW_CHANGE_END;
            }

            @Override
            public boolean isZoom() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }
        });
        setContentView(appFrame);

    }

    private void saveBitmapToFile(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        if (tempFilePath == null) {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                tempFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            File file = new File(tempFilePath + File.separatorChar + "tempPic");
            if (!file.exists()) {
                file.mkdir();
            }
            tempFilePath = file.getAbsolutePath();
        }

        File file = new File(tempFilePath + File.separatorChar + "export_image.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

        } catch (IOException e) {
        } finally {
            //bitmap.recycle();
        }
    }


    /**
     * (non-Javadoc)
     *
     * @see IMainFrame#showProgressBar(boolean)
     */
    public void showProgressBar(boolean visible) {
        setProgressBarIndeterminateVisibility(visible);
    }

    /**
     *
     */
    private void init() {
        Intent intent = getIntent();
        filePath = intent.getStringExtra(MainConstant.INTENT_FILED_FILE_PATH);
        if (filePath == null) {
            this.filePath = intent.getDataString();
            int index = getFilePath().indexOf(":");
            if (index > 0) {
                filePath = filePath.substring(index + 3);
            }
            filePath = Uri.decode(filePath);
        }

        // 显示打开文件名称
        int index = filePath.lastIndexOf(File.separator);
        if (index > 0) {
            setTitle(filePath.substring(index + 1));
        } else {
            setTitle(filePath);
        }
        // create view
        createView();
        // open file
        control.openFile(filePath);

    }

    private static final String TAG = "";

    /**
     * true: show message when zooming
     * false: not show message when zooming
     *
     * @return
     */
    public boolean isShowZoomingMsg() {
        return true;
    }

    /**
     * true: pop up dialog when throw err
     * false: not pop up dialog when throw err
     *
     * @return
     */
    public boolean isPopUpErrorDlg() {
        return true;
    }

    /**
     *
     */
    String fileType = "Word";

    private void createView() {
        // word
        String file = filePath.toLowerCase();
        if (file.endsWith(MainConstant.FILE_TYPE_DOC) || file.endsWith(MainConstant.FILE_TYPE_DOCX)
                || file.endsWith(MainConstant.FILE_TYPE_DOT)
                || file.endsWith(MainConstant.FILE_TYPE_DOTX)
                || file.endsWith(MainConstant.FILE_TYPE_DOTM)) {
            fileType = "Word";
            applicationType = MainConstant.APPLICATION_TYPE_WP;
        } else if (file.endsWith(MainConstant.FILE_TYPE_TXT)) {
            fileType = "Text";
            applicationType = MainConstant.APPLICATION_TYPE_TXT;
        }
        // excel
        else if (file.endsWith(MainConstant.FILE_TYPE_XLS)
                || file.endsWith(MainConstant.FILE_TYPE_XLSX)
                || file.endsWith(MainConstant.FILE_TYPE_XLT)
                || file.endsWith(MainConstant.FILE_TYPE_XLTX)
                || file.endsWith(MainConstant.FILE_TYPE_XLTM)
                || file.endsWith(MainConstant.FILE_TYPE_XLSM)) {
            fileType = "Excel";
            applicationType = MainConstant.APPLICATION_TYPE_SS;
        }
        // PowerPoint
        else if (file.endsWith(MainConstant.FILE_TYPE_PPT)
                || file.endsWith(MainConstant.FILE_TYPE_PPTX)
                || file.endsWith(MainConstant.FILE_TYPE_POT)
                || file.endsWith(MainConstant.FILE_TYPE_PPTM)
                || file.endsWith(MainConstant.FILE_TYPE_POTX)
                || file.endsWith(MainConstant.FILE_TYPE_POTM)) {
            fileType = "Power Point";
            applicationType = MainConstant.APPLICATION_TYPE_PPT;
        } else {
            applicationType = MainConstant.APPLICATION_TYPE_WP;
        }

    }


    /**
     * @return
     */
    private void markFile() {
        marked = !marked;
    }


    /**
     *
     */
    public Dialog onCreateDialog(int id) {
        return control.getDialog(this, id);
    }


    /**
     *
     */
    public IControl getControl() {
        return this.control;
    }

    /**
     *
     */
    public int getApplicationType() {
        return this.applicationType;
    }

    /**
     * @return Returns the filePath.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     *
     */
    public Activity getActivity() {
        return this;
    }

    /**
     * do action，this is method don't call <code>control.actionEvent</code> method, Easily lead to infinite loop
     *
     * @param actionID action ID
     * @param obj      acValue
     * @return True if the listener has consumed the event, false otherwise.
     */
    public boolean doActionEvent(int actionID, Object obj) {
        try {
            switch (actionID) {
                case EventConstant.SYS_RESET_TITLE_ID:
                    setTitle((String) obj);
                    break;

                case EventConstant.SYS_ONBACK_ID:
                    onBackPressed();
                    break;

                case EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS: //update toolsbar state
                    break;

                case EventConstant.SYS_HELP_ID: //show help net
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources()
                            .getString(R.string.sys_url_wxiwei)));
                    startActivity(intent);
                    break;

                case EventConstant.APP_FIND_ID: //show search bar
                    break;

                case EventConstant.APP_SHARE_ID: //share file
                    fileShare();
                    break;

                case EventConstant.FILE_MARK_STAR_ID: //mark
                    markFile();
                    break;

                case EventConstant.APP_FINDING:
                    String content = ((String) obj).trim();
                    if (content.length() > 0 && control.getFind().find(content)) {
                    } else {
                        toast.setText(getLocalString("DIALOG_FIND_NOT_FOUND"));
                        toast.show();
                    }
                    break;

                case EventConstant.APP_FIND_BACKWARD:
                    if (!control.getFind().findBackward()) {

                        toast.setText(getLocalString("DIALOG_FIND_TO_BEGIN"));
                        toast.show();
                    } else {

                    }
                    break;

                case EventConstant.APP_FIND_FORWARD:
                    if (!control.getFind().findForward()) {
                        toast.setText(getLocalString("DIALOG_FIND_TO_END"));
                        toast.show();
                    } else {

                    }
                    break;

                case EventConstant.SS_CHANGE_SHEET:
                    bottomBar.setFocusSheetButton((Integer) obj);
                    break;

                case EventConstant.APP_DRAW_ID:
                    control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_CALLOUTDRAW);
                    appFrame.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            control.actionEvent(EventConstant.APP_INIT_CALLOUTVIEW_ID, null);

                        }
                    });

                    break;

                case EventConstant.APP_BACK_ID:

                    control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
                    break;

                case EventConstant.APP_PEN_ID:
                    if ((Boolean) obj) {
                        control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_CALLOUTDRAW);
                        appFrame.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                control.actionEvent(EventConstant.APP_INIT_CALLOUTVIEW_ID, null);

                            }
                        });
                    } else {
                        control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
                    }
                    break;

                case EventConstant.APP_ERASER_ID:
                    if ((Boolean) obj) {
                        control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_CALLOUTERASE);
                    } else {
                        control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
                    }
                    break;

                case EventConstant.APP_COLOR_ID:
                    ColorPickerDialog dlg = new ColorPickerDialog(this, control);
                    dlg.show();
                    break;

                default:
                    return false;
            }
        } catch (Exception e) {
            control.getSysKit().getErrorKit().writerLog(e);
        }
        return true;
    }

    String pkg = "com.camerascanner.documents.pdf.viewer";

    public void fileShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(filePath);
        if (fileWithinMyDir.exists()) {
            shareIntent.setType("application/*");
            Uri uri = FileProvider.getUriForFile(this,
                    pkg + ".provider", fileWithinMyDir);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            Intent chooser = new Intent(Intent.createChooser(shareIntent, "Share File"));
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resInfo : resInfoList) {
                String packageName = resInfo.activityInfo.packageName;
                grantUriPermission(packageName, uriFromFile(this, new File(filePath)), Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            startActivity(chooser);
        }

    }

    private Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, pkg + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }

    }


    private int dpToPixel(int dp) {
        DisplayMetrics r = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r);
    }

    public void openFileFinish() {

        toolbarLayoutInit();
        LinearLayout toolbarLayout = new LinearLayout(this);
        toolbarLayout.setBackgroundColor(getResources().getColor(R.color.app_blue));
        toolbarLayout.setOrientation(LinearLayout.HORIZONTAL);
        toolbarLayout.setWeightSum(10);
        toolbarLayout.setGravity(Gravity.CENTER_VERTICAL);

        appFrame.addView(toolbarLayout, new LayoutParams(LayoutParams.MATCH_PARENT, dpToPixel(60)));

        TextView categoryTitle = new TextView(this);
        categoryTitle.setText(fileType);
        categoryTitle.setTextSize(24);
        categoryTitle.setTextColor(getResources().getColor(R.color.white));
        categoryTitle.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 5.5f));

        backBtn.setImageResource(R.drawable.ic_back);
        backBtn.setLayoutParams(new LayoutParams(dpToPixel(24), dpToPixel(24), 1.5f));

        fullScreenBtn.setImageResource(R.drawable.ic_fullscreen);
        fullScreenBtn.setLayoutParams(new LayoutParams(dpToPixel(24), dpToPixel(24), 1.5f));

        shareFileBtn.setImageResource(R.drawable.ic_share_icon);
        shareFileBtn.setLayoutParams(new LayoutParams(dpToPixel(24), dpToPixel(24), 1.5f));

        toolbarLayout.addView(backBtn);
        toolbarLayout.addView(categoryTitle);

        View app = control.getView();
        appFrame.addView(app,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        backBtn.setOnClickListener(v -> AppActivity.super.onBackPressed());
        fullScreenBtn.setOnClickListener(v -> toolbarLayout.setVisibility(View.GONE));
        shareFileBtn.setOnClickListener(v -> fileShare());


    }

    /**
     *
     */
    public int getBottomBarHeight() {
        if (bottomBar != null) {
            return bottomBar.getSheetbarHeight();
        }
        return 0;
    }

    /**
     *
     */
    public int getTopBarHeight() {
        return 0;
    }

    /**
     * event method, office engine dispatch
     *
     * @param v      event source
     * @param e1     MotionEvent instance
     * @param e2     MotionEvent instance
     * @param xValue eventNethodType is ON_SCROLL, this is value distanceX
     *               eventNethodType is ON_FLING, this is value velocityY
     *               eventNethodType is other type, this is value -1
     * @param yValue eventNethodType is ON_SCROLL, this is value distanceY
     *               eventNethodType is ON_FLING, this is value velocityY
     *               eventNethodType is other type, this is value -1
     * @param
     * @see IMainFrame#ON_CLICK
     * @see IMainFrame#ON_DOUBLE_TAP
     * @see IMainFrame#ON_DOUBLE_TAP_EVENT
     * @see IMainFrame#ON_DOWN
     * @see IMainFrame#ON_FLING
     * @see IMainFrame#ON_LONG_PRESS
     * @see IMainFrame#ON_SCROLL
     * @see IMainFrame#ON_SHOW_PRESS
     * @see IMainFrame#ON_SINGLE_TAP_CONFIRMED
     * @see IMainFrame#ON_SINGLE_TAP_UP
     * @see IMainFrame#ON_TOUCH
     */
    public boolean onEventMethod(View v, MotionEvent e1, MotionEvent e2, float xValue,
                                 float yValue, byte eventMethodType) {
        return false;
    }


    public void changePage() {
    }

    /**
     *
     */
    public String getAppName() {
        return getString(R.string.sys_name);
    }

    /**
     * 是否绘制页码
     */
    public boolean isDrawPageNumber() {
        return true;
    }

    /**
     * 是否支持zoom in / zoom out
     */
    public boolean isTouchZoom() {
        return true;
    }

    /**
     * Word application 默认视图(Normal or Page)
     *
     * @return WPViewConstant.PAGE_ROOT or WPViewConstant.NORMAL_ROOT
     */
    public byte getWordDefaultView() {
        return WPViewConstant.PAGE_ROOT;
        //return WPViewConstant.NORMAL_ROOT;
    }

    /**
     * normal view, changed after zoom bend, you need to re-layout
     *
     * @return true   re-layout
     * false  don't re-layout
     */
    public boolean isZoomAfterLayoutForWord() {
        return true;
    }


    /**
     *
     */

    public void changeZoom() {
    }

    /**
     *
     */
    public void error(int errorCode) {
    }

    /**
     * when need destroy the office engine instance callback this method
     */
    public void destroyEngine() {
        super.onBackPressed();
    }

    /**
     * get Internationalization resource
     *
     * @param resName Internationalization resource name
     */
    public String getLocalString(String resName) {
        return ResKit.instance().getLocalString(resName);
    }

    @Override
    public boolean isShowPasswordDlg() {
        return true;
    }

    @Override
    public boolean isShowProgressBar() {
        return true;
    }

    @Override
    public boolean isShowFindDlg() {
        return true;
    }

    @Override
    public boolean isShowTXTEncodeDlg() {
        return true;
    }

    /**
     * get txt default encode when not showing txt encode dialog
     *
     * @return null if showing txt encode dialog
     */
    public String getTXTDefaultEncode() {
        return "GBK";
    }

    /**
     *
     */
    public DialogListener getDialogListener() {
        return null;
    }


    @Override
    public void completeLayout() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isChangePage() {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @param saveLog
     */
    public void setWriteLog(boolean saveLog) {
        this.writeLog = saveLog;
    }

    /**
     * @return
     */
    public boolean isWriteLog() {
        return writeLog;
    }

    /**
     * @param isThumbnail
     */
    public void setThumbnail(boolean isThumbnail) {
        this.isThumbnail = isThumbnail;
    }

    /**
     * get view backgrouond
     *
     * @return
     */
    public Object getViewBackground() {
        return bg;
    }

    /**
     * set flag whether fitzoom can be larger than 100% but smaller than the max zoom
     *
     * @param ignoreOriginalSize
     */
    public void setIgnoreOriginalSize(boolean ignoreOriginalSize) {

    }

    /**
     * @return true fitzoom may be larger than 100% but smaller than the max zoom
     * false fitzoom can not larger than 100%
     */
    public boolean isIgnoreOriginalSize() {
        return false;
    }

    public byte getPageListViewMovingPosition() {
        return IPageListViewListener.Moving_Horizontal;
    }

    /**
     * @return
     */
    public boolean isThumbnail() {
        return isThumbnail;
    }

    /**
     * @param viewList
     */
    public void updateViewImages(List<Integer> viewList) {

    }

    /**
     * @return
     */
    public File getTemporaryDirectory() {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = getExternalFilesDir(null);
        if (file != null) {
            return file;
        } else {
            return getFilesDir();
        }
    }

    /**
     * 释放内存
     */
    public void dispose() {
        isDispose = true;
        if (control != null) {
            control.dispose();
            control = null;
        }
        bottomBar = null;

        if (appFrame != null) {
            int count = appFrame.getChildCount();
            for (int i = 0; i < count; i++) {
                View v = appFrame.getChildAt(i);
            }
            appFrame = null;
        }

        if (wm != null) {
            wm = null;
            wmParams = null;
        }
    }

    private void toolbarLayoutInit() {
        backBtn = new ImageView(this);
        fullScreenBtn = new ImageView(this);
        shareFileBtn = new ImageView(this);
    }

    //
    private boolean isDispose;
    // 当前标星状态
    private boolean marked;
    //
    private int applicationType = -1;
    //
    private String filePath;
    // application activity control
    private MainControl control;
    //
    private AppFrame appFrame;

    private SheetBar bottomBar;

    //float button: PageUp/PageDown
    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;


    //whether write log to temporary file
    private boolean writeLog = true;
    //open file to get thumbnail, or not
    private boolean isThumbnail;
    //view background
    private final Object bg = Color.GRAY;
    private Toast toast;
    private boolean fullscreen;
    //
    private String tempFilePath;

    ImageView backBtn;
    ImageView fullScreenBtn;
    ImageView shareFileBtn;
}
