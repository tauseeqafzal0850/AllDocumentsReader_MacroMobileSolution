package com.faisaldeveloper.office.pg.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.faisaldeveloper.office.common.IOfficeToPicture;
import com.faisaldeveloper.office.common.hyperlink.Hyperlink;
import com.faisaldeveloper.office.common.picture.PictureKit;
import com.faisaldeveloper.office.common.shape.AbstractShape;
import com.faisaldeveloper.office.common.shape.IShape;
import com.faisaldeveloper.office.common.shape.TextBox;
import com.faisaldeveloper.office.constant.EventConstant;
import com.faisaldeveloper.office.java.awt.Dimension;
import com.faisaldeveloper.office.java.awt.Rectangle;
import com.faisaldeveloper.office.pg.model.PGModel;
import com.faisaldeveloper.office.pg.model.PGSlide;
import com.faisaldeveloper.office.pg.view.SlideDrawKit;
import com.faisaldeveloper.office.simpletext.model.AttrManage;
import com.faisaldeveloper.office.simpletext.model.IElement;
import com.faisaldeveloper.office.simpletext.model.ParagraphElement;
import com.faisaldeveloper.office.simpletext.view.STRoot;
import com.faisaldeveloper.office.system.IControl;
import com.faisaldeveloper.office.system.SysKit;
import com.faisaldeveloper.office.system.beans.pagelist.APageListItem;
import com.faisaldeveloper.office.system.beans.pagelist.APageListView;
import com.faisaldeveloper.office.system.beans.pagelist.IPageListViewListener;

public class PGPrintMode extends FrameLayout implements IPageListViewListener {

    public PGPrintMode(Context context) {
        super(context);
    }


    public PGPrintMode(Context context, IControl control, PGModel pgModel, PGEditor editor) {
        super(context);
        this.control = control;
        this.pgModel = pgModel;
        this.editor = editor;

        listView = new APageListView(context, this);
        addView(listView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setTextSize(24);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.GONE);
        }

    }


    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        if (listView != null) {
            listView.setBackgroundColor(color);
        }
    }


    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        if (listView != null) {
            listView.setBackgroundResource(resid);
        }
    }


    public void setBackgroundDrawable(Drawable d) {
        super.setBackgroundDrawable(d);
        if (listView != null) {
            listView.setBackgroundDrawable(d);
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) ;
        {
            exportImage(listView.getCurrentPageView(), null);
        }
    }


    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawPageNubmer(canvas);
    }


    public void init() {
        if ((int) (getZoom() * 100) == 100) {
            setZoom(getFitZoom(), Integer.MIN_VALUE, Integer.MIN_VALUE);
        }
    }


    public void setZoom(float zoom, int pointX, int pointY) {
        listView.setZoom(zoom, pointX, pointY);
    }


    public void setFitSize(int value) {
        listView.setFitSize(value);
    }

    public int getFitSizeState() {
        return listView.getFitSizeState();
    }

    public float getZoom() {
        return listView.getZoom();
    }


    public float getFitZoom() {
        return listView.getFitZoom();
    }


    public int getCurrentPageNumber() {
        return listView.getCurrentPageNumber();
    }


    public APageListView getListView() {
        return this.listView;
    }


    public void nextPageView() {
        listView.nextPageView();
    }


    public void previousPageview() {
        listView.previousPageview();
    }


    public void showSlideForIndex(int index) {
        listView.showPDFPageForIndex(index);
    }

    public int getPageCount() {
        return Math.max(pgModel.getSlideCount(), 1);
    }


    public APageListItem getPageListItem(int position, View convertView, ViewGroup parent) {
        Rect rect = getPageSize(position);
        return new PGPageListItem(listView, control, editor, rect.width(), rect.height());
    }


    public Rect getPageSize(int pageIndex) {
        Dimension d = pgModel.getPageSize();
        if (d == null) {
            pageSize.set(0, 0, getWidth(), getHeight());
        } else {
            pageSize.set(0, 0, d.width, d.height);
        }
        return pageSize;
    }


    public void exportImage(final APageListItem pageItem, final Bitmap srcBitmap) {
        if (getControl() == null || !(getParent() instanceof Presentation)) {
            return;
        }
        PGFind find = (PGFind) control.getFind();
        if (find.isSetPointToVisible()) {
            find.setSetPointToVisible(false);
            Rectangle rect = editor.modelToView(editor.getHighlight().getSelectStart(), new Rectangle(), false);
            if (!listView.isPointVisibleOnScreen(rect.x, rect.y)) {
                listView.setItemPointVisibleOnScreen(rect.x, rect.y);
                return;
            }

        }
        post(new Runnable() {
            @Override
            public void run() {
                try {
                    PGSlide slide = pgModel.getSlide(pageItem.getPageIndex());
                    if (slide != null) {
                        IOfficeToPicture otp = getControl().getOfficeToPicture();
                        if (otp != null && otp.getModeType() == IOfficeToPicture.VIEW_CHANGE_END) {
                            int rW = Math.min(getWidth(), pageItem.getWidth());
                            int rH = Math.min(getHeight(), pageItem.getHeight());
                            Bitmap dstBitmap = otp.getBitmap(rW, rH);
                            if (dstBitmap == null) {
                                return;
                            }

                            if (dstBitmap.getWidth() == rW && dstBitmap.getHeight() == rH) {
                                Canvas canvas = new Canvas(dstBitmap);
                                canvas.drawColor(Color.WHITE);
                                float zoom = listView.getZoom();
                                int left = pageItem.getLeft();
                                int top = pageItem.getTop();
                                canvas.translate(-(Math.max(left, 0) - left), -(Math.max(top, 0) - top));
                                SlideDrawKit.instance().drawSlide(canvas, pgModel, editor, slide, zoom);
                                control.getSysKit().getCalloutManager().drawPath(canvas, pageItem.getPageIndex(), zoom);
                            } else {
                                float paintZoom = Math.min(dstBitmap.getWidth() / (float) rW, dstBitmap.getHeight() / (float) rH);
                                float zoom = listView.getZoom() * paintZoom;
                                int left = (int) (pageItem.getLeft() * paintZoom);
                                int top = (int) (pageItem.getTop() * paintZoom);
                                Canvas canvas = new Canvas(dstBitmap);
                                canvas.drawColor(Color.WHITE);
                                canvas.translate(-(Math.max(left, 0) - left), -(Math.max(top, 0) - top));
                                SlideDrawKit.instance().drawSlide(canvas, pgModel, editor, slide, zoom);
                                control.getSysKit().getCalloutManager().drawPath(canvas, pageItem.getPageIndex(), zoom);
                            }
                            otp.callBack(dstBitmap);
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    public Bitmap getSnapshot(final Bitmap dstBitmap) {
        if (getControl() == null || !(getParent() instanceof Presentation)) {
            return null;
        }

        PGPageListItem pageItem = (PGPageListItem) getListView().getCurrentPageView();
        if (pageItem == null) {
            return null;
        }

        PGSlide slide = pgModel.getSlide(pageItem.getPageIndex());
        if (slide != null) {
            int rW = Math.min(getWidth(), pageItem.getWidth());
            int rH = Math.min(getHeight(), pageItem.getHeight());

            if (dstBitmap.getWidth() == rW && dstBitmap.getHeight() == rH) {
                Canvas canvas = new Canvas(dstBitmap);
                canvas.drawColor(Color.WHITE);
                float zoom = listView.getZoom();
                int left = pageItem.getLeft();
                int top = pageItem.getTop();
                canvas.translate(-(Math.max(left, 0) - left), -(Math.max(top, 0) - top));
                SlideDrawKit.instance().drawSlide(canvas, pgModel, editor, slide, zoom);
            } else {
                float paintZoom = Math.min(dstBitmap.getWidth() / (float) rW, dstBitmap.getHeight() / (float) rH);
                float zoom = listView.getZoom() * paintZoom;
                int left = (int) (pageItem.getLeft() * paintZoom);
                int top = (int) (pageItem.getTop() * paintZoom);
                Canvas canvas = new Canvas(dstBitmap);
                canvas.drawColor(Color.WHITE);
                canvas.translate(-(Math.max(left, 0) - left), -(Math.max(top, 0) - top));
                SlideDrawKit.instance().drawSlide(canvas, pgModel, editor, slide, zoom);
            }
        }

        return dstBitmap;
    }

    public boolean isInit() {
        return true;
    }


    public boolean isIgnoreOriginalSize() {
        return control.getMainFrame().isIgnoreOriginalSize();
    }


    public byte getPageListViewMovingPosition() {
        return control.getMainFrame().getPageListViewMovingPosition();
    }

    public Object getModel() {
        return pgModel;
    }

    public IControl getControl() {
        return this.control;
    }

    public boolean onEventMethod(View v, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY, byte eventMethodType) {
        if (eventMethodType == ON_SINGLE_TAP_UP && e1 != null
                && e1.getAction() == MotionEvent.ACTION_UP) {
            APageListItem item = listView.getCurrentPageView();
            if (item != null) {
                float zoom = listView.getZoom();

                int x = (int) ((e1.getX() - item.getLeft()) / zoom);
                int y = (int) ((e1.getY() - item.getTop()) / zoom);
                IShape shape = pgModel.getSlide(item.getPageIndex()).getTextboxShape(x, y);
                if (shape != null && shape.getType() == AbstractShape.SHAPE_TEXTBOX) {
                    TextBox textBox = (TextBox) shape;
                    STRoot root = textBox.getRootView();
                    if (root != null) {
                        long offset = root.viewToModel(x - shape.getBounds().x, y - shape.getBounds().y, false);
                        if (offset >= 0) {
                            ParagraphElement paraElem = (ParagraphElement) textBox.getElement().getElement(offset);
                            if (paraElem != null) {
                                IElement leaf = paraElem.getLeaf(offset);
                                if (leaf != null) {
                                    int hyID = AttrManage.instance().getHperlinkID(leaf.getAttribute());
                                    if (hyID >= 0) {
                                        Hyperlink hylink = control.getSysKit().getHyperlinkManage().getHyperlink(hyID);
                                        if (hylink != null) {
                                            control.actionEvent(EventConstant.APP_HYPERLINK, hylink);
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return control.getMainFrame().onEventMethod(v, e1, e2, velocityX, velocityY, eventMethodType);
    }


    public void updateStutus(Object obj) {
        control.actionEvent(EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS, obj);
    }


    public void resetSearchResult(APageListItem pageItem) {
        if (getParent() instanceof Presentation) {
            Presentation pg = (Presentation) getParent();
            if (pg.getFind().getPageIndex() != pageItem.getPageIndex()) {
                pg.getEditor().getHighlight().removeHighlight();
            }
        }
    }


    public boolean isTouchZoom() {
        return control.getMainFrame().isTouchZoom();
    }


    public boolean isShowZoomingMsg() {
        return control.getMainFrame().isShowZoomingMsg();
    }


    public void changeZoom() {
        control.getMainFrame().changeZoom();
    }


    public void setDrawPictrue(boolean isDrawPictrue) {
        PictureKit.instance().setDrawPictrue(isDrawPictrue);
    }


    public PGSlide getCurrentPGSlide() {
        APageListItem item = listView.getCurrentPageView();
        if (item != null) {
            return pgModel.getSlide(item.getPageIndex());
        } else {
            return pgModel.getSlide(0);
        }
    }


    private void drawPageNubmer(Canvas canvas) {
        if (control.getMainFrame().isDrawPageNumber()) {
            String pn = (listView.getCurrentPageNumber()) + " / " + pgModel.getSlideCount();
            int w = (int) paint.measureText(pn);
            int h = (int) (paint.descent() - paint.ascent());
            int x = (getWidth() - w) / 2;
            int y = (getHeight() - h) - 20;

            Drawable drawable = SysKit.getPageNubmerDrawable();
            drawable.setBounds(x - 10, y - 10, x + w + 10, y + h + 10);
            drawable.draw(canvas);

            y -= paint.ascent();
            canvas.drawText(pn, x, y, paint);
        }

        if (preShowPageIndex != listView.getCurrentPageNumber()) {
            changePage();
            preShowPageIndex = listView.getCurrentPageNumber();
        }
    }


    public void changePage() {
        control.getMainFrame().changePage();
    }


    public boolean isChangePage() {
        return control.getMainFrame().isChangePage();
    }


    public void dispose() {
        control = null;
        if (listView != null) {
            listView.dispose();
        }
        pgModel = null;
        pageSize = null;
    }

    private int preShowPageIndex = -1;

    private IControl control;

    private APageListView listView;
    private Paint paint;

    private PGModel pgModel;

    private PGEditor editor;

    private Rect pageSize = new Rect();

}
