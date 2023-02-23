package com.example.alldocumentsreaderimagescanner.converter.model

open class PDFOptions {
    var outFileName: String? = null
    var isPasswordProtected = false
    var password: String? = null
    var pageSize: String? = null
    var borderWidth = 0
    var pageColor = 0
    var mWatermarkAdded = false
    var mWatermark: Watermark? = null

    internal constructor() {}
    internal constructor(
        mFileName: String?, mPageSize: String?, mPasswordProtected: Boolean, mPassword: String?,
        mBorderWidth: Int, pageColor: Int, watermark: Watermark?, watermarkAdd: Boolean
    ) {
        outFileName = mFileName
        pageSize = mPageSize
        isPasswordProtected = mPasswordProtected
        password = mPassword
        borderWidth = mBorderWidth
        this.pageColor = pageColor
        mWatermark = watermark
        mWatermarkAdded = watermarkAdd
    }
}