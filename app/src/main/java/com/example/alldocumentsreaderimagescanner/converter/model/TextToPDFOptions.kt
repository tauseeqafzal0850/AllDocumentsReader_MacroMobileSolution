package com.example.alldocumentsreaderimagescanner.converter.model

import android.content.Context
import android.net.Uri
import com.example.alldocumentsreaderimagescanner.converter.model.PDFOptions
import com.example.alldocumentsreaderimagescanner.converter.util.TextToPdfPreferences
import com.example.alldocumentsreaderimagescanner.converter.model.TextToPDFOptions
import com.itextpdf.text.Font

class TextToPDFOptions(
    mFileName: String?,
    mPageSize: String?,
    mPasswordProtected: Boolean,
    mPassword: String?,
    val inFileUri: Uri?,
    val fontSize: Int,
    val fontFamily: Font.FontFamily,
    val fontColor: Int,
    pageColor: Int
) : PDFOptions(mFileName, mPageSize, mPasswordProtected, mPassword, 0, pageColor, null, false) {

    class Builder(context: Context?) {
        var fileName: String? = null
            private set
        private var mPageSize: String
        private var mPasswordProtected: Boolean
        private var mPassword: String? = null
        private var mPageColor: Int
        var inFileUri: Uri? = null
            private set
        var fontSize: Int
            private set
        var fontFamily: Font.FontFamily
            private set
        var fontColor: Int
            private set
        var fontSizeTitle: String? = null
            private set

        init {
            val preferences = TextToPdfPreferences(context!!)
            mPageSize = preferences.pageSize
            mPasswordProtected = false
            fontColor = preferences.fontColor
            fontFamily = Font.FontFamily.valueOf(preferences.fontFamily)
            fontSize = preferences.fontSize
            mPageColor = preferences.pageColor
        }

        fun setFileName(fileName: String?): Builder {
            this.fileName = fileName
            return this
        }

        fun getPageSize(): String {
            return mPageSize
        }

        fun setPageSize(pageSize: String): Builder {
            mPageSize = pageSize
            return this
        }

        fun isPasswordProtected(): Boolean {
            return mPasswordProtected
        }

        fun setPasswordProtected(passwordProtected: Boolean): Builder {
            mPasswordProtected = passwordProtected
            return this
        }

        fun getPassword(): String? {
            return mPassword
        }

        fun setPassword(password: String?): Builder {
            mPassword = password
            return this
        }

        fun getPageColor(): Int {
            return mPageColor
        }

        fun setPageColor(pageColor: Int): Builder {
            mPageColor = pageColor
            return this
        }

        fun setInFileUri(inFileUri: Uri?): Builder {
            this.inFileUri = inFileUri
            return this
        }

        fun setFontSize(fontSize: Int): Builder {
            this.fontSize = fontSize
            return this
        }

        fun setFontFamily(fontFamily: Font.FontFamily): Builder {
            this.fontFamily = fontFamily
            return this
        }

        fun setFontColor(fontColor: Int): Builder {
            this.fontColor = fontColor
            return this
        }

        fun setFontSizeTitle(fontSizeTitle: String?): Builder {
            this.fontSizeTitle = fontSizeTitle
            return this
        }

        fun build(): TextToPDFOptions {
            return TextToPDFOptions(
                fileName, mPageSize, mPasswordProtected,
                mPassword, inFileUri, fontSize, fontFamily, fontColor, mPageColor
            )
        }
    }
}