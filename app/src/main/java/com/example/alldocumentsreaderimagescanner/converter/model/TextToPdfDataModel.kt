package com.example.alldocumentsreaderimagescanner.converter.model

import android.content.Context
import android.net.Uri
import com.example.alldocumentsreaderimagescanner.converter.util.TextToPdfPreferences
import com.itextpdf.text.Font

data class TextToPdfDataModel(
    var fileName: String? = null,
    var mPageSize: String,
    var mPasswordProtected: Boolean,
    var mPassword: String? = null,
    var mPageColor: Int,
    var inFileUri: Uri? = null,
    var fontSize: Int,
    var fontFamily: Font.FontFamily,
    var fontColor: Int,
    var fontSizeTitle: String? = null ,
            var context: Context?
){
    init {

        val preferences = TextToPdfPreferences(context!!)
        mPageSize = preferences.pageSize
        mPasswordProtected = false
        fontColor = preferences.fontColor
        fontFamily = Font.FontFamily.valueOf(preferences.fontFamily)
        fontSize = preferences.fontSize
        mPageColor = preferences.pageColor
    }
}
