package com.example.alldocumentsreaderimagescanner.converter.util

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import com.example.alldocumentsreaderimagescanner.converter.util.StringUtils.Companion.getInstance

import com.example.alldocumentsreaderimagescanner.converter.model.TextToPDFOptions
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.MASTER_PWD_STRING
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.STORAGE_LOCATION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.appName
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream
import java.io.IOException

class TextToPDFUtils(private val mContext: Activity) {
    private val mSharedPreferences: SharedPreferences
    private val mTextFileReader: TextFileReader
    private val mDocFileReader: DocFileReader
    private val mDocxFileReader: DocxFileReader

    init {
        mTextFileReader = TextFileReader(mContext)
        mDocFileReader = DocFileReader(mContext)
        mDocxFileReader = DocxFileReader(mContext)
        mSharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
    }

    /**
     * Create a PDF from a Text File
     *
     * @param mTextToPDFOptions TextToPDFOptions Object
     * @param fileExtension     file extension represented as string
     */
    @Throws(DocumentException::class, IOException::class)
   suspend fun createPdfFromTextFile(mTextToPDFOptions: TextToPDFOptions, fileExtension: String?) {
        val masterpwd = mSharedPreferences.getString(MASTER_PWD_STRING, appName)
        val pageSize = Rectangle(PageSize.getRectangle(mTextToPDFOptions.pageSize))
        pageSize.backgroundColor = getBaseColor(mTextToPDFOptions.pageColor)
        val document = Document(pageSize)
        val finalOutput = mSharedPreferences.getString(
            STORAGE_LOCATION,
            getInstance().defaultStorageLocation
        ) +
                mTextToPDFOptions.outFileName + ".pdf"
        val writer = PdfWriter.getInstance(document, FileOutputStream(finalOutput))
        writer.setPdfVersion(PdfWriter.VERSION_1_7)
        if (mTextToPDFOptions.isPasswordProtected) {
            writer.setEncryption(
                mTextToPDFOptions.password!!.toByteArray(),
                masterpwd!!.toByteArray(),
                PdfWriter.ALLOW_PRINTING or PdfWriter.ALLOW_COPY,
                PdfWriter.ENCRYPTION_AES_128
            )
        }
        document.open()
        val myfont = Font(mTextToPDFOptions.fontFamily)
        myfont.style = Font.NORMAL
        myfont.size = mTextToPDFOptions.fontSize.toFloat()
        myfont.color = getBaseColor(mTextToPDFOptions.fontColor)
        document.add(Paragraph("\n"))
        addContentToDocument(mTextToPDFOptions, fileExtension, document, myfont)
        document.close()
    }

    @Throws(DocumentException::class)
    private fun addContentToDocument(
        mTextToPDFOptions: TextToPDFOptions, fileExtension: String?,
        document: Document, myfont: Font
    ) {
        if (fileExtension == null) throw DocumentException()
        when (fileExtension) {
            Constants.docExtension -> mDocFileReader.read(
                mTextToPDFOptions.inFileUri,
                document,
                myfont
            )
            Constants.docxExtension -> mDocxFileReader.read(
                mTextToPDFOptions.inFileUri,
                document,
                myfont
            )
            else -> mTextFileReader.read(mTextToPDFOptions.inFileUri, document, myfont)
        }
    }

    /**
     * Read the BaseColor of passed color
     *
     * @param color value of color in int
     */
    private fun getBaseColor(color: Int): BaseColor {
        return BaseColor(
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }
}