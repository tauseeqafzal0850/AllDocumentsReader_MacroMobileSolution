package com.example.alldocumentsreaderimagescanner.converter.model


class ImageToPDFOptions() : PDFOptions() {
    private var mQualityString: String? = null
    private var mImagesUri: ArrayList<String>? = null
    private var mMarginTop = 0
    private var mMarginBottom = 0
    private var mMarginRight = 0
    private var mMarginLeft = 0
    private var mImageScaleType: String? = null
    private var mPageNumStyle: String? = null
    private var mMasterPwd: String? = null



    fun ImageToPDFOptions() {
        isPasswordProtected = (false)
        mWatermarkAdded = (false)
        borderWidth = (0)
    }


    fun getQualityString(): String? {
        return mQualityString
    }

    fun getImagesUri(): ArrayList<String>? {
        return mImagesUri
    }

    fun setQualityString(mQualityString: String?) {
        this.mQualityString = mQualityString
    }

    fun setImagesUri(mImagesUri: ArrayList<String>) {
        this.mImagesUri = mImagesUri
    }

    fun setMargins(top: Int, bottom: Int, right: Int, left: Int) {
        mMarginTop = top
        mMarginBottom = bottom
        mMarginRight = right
        mMarginLeft = left
    }

    fun setMasterPwd(pwd: String?) {
        mMasterPwd = pwd
    }

    fun getMarginTop(): Int {
        return mMarginTop
    }

    fun getMarginBottom(): Int {
        return mMarginBottom
    }

    fun getMarginRight(): Int {
        return mMarginRight
    }

    fun getMarginLeft(): Int {
        return mMarginLeft
    }

    fun getImageScaleType(): String? {
        return mImageScaleType
    }

    fun setImageScaleType(mImageScaleType: String?) {
        this.mImageScaleType = mImageScaleType
    }

    fun getPageNumStyle(): String? {
        return mPageNumStyle
    }

    fun setPageNumStyle(mPageNumStyle: String?) {
        this.mPageNumStyle = mPageNumStyle
    }

    fun getMasterPwd(): String? {
        return mMasterPwd
    }
}