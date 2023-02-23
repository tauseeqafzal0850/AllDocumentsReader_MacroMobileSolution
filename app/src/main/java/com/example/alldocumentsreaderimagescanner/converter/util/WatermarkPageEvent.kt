package com.example.alldocumentsreaderimagescanner.converter.util


import com.example.alldocumentsreaderimagescanner.converter.model.Watermark
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfPageEventHelper
import com.itextpdf.text.pdf.PdfWriter

internal class WatermarkPageEvent : PdfPageEventHelper() {
    private var mWatermark: Watermark? = null
    private var mPhrase: Phrase? = null
    override fun onEndPage(writer: PdfWriter, document: Document) {
        val canvas = writer.directContent
        val x = (document.pageSize.left + document.pageSize.right) / 2
        val y = (document.pageSize.top + document.pageSize.bottom) / 2
        ColumnText.showTextAligned(
            canvas,
            Element.ALIGN_CENTER,
            mPhrase,
            x,
            y,
            mWatermark!!.rotationAngle.toFloat()
        )
    }

    var watermark: Watermark?
        get() = mWatermark
        set(watermark) {
            mWatermark = watermark
            mPhrase = Phrase(
                mWatermark!!.watermarkText,
                Font(
                    mWatermark!!.fontFamily, mWatermark!!.textSize.toFloat(),
                    mWatermark!!.fontStyle, mWatermark!!.textColor
                )
            )
        }
}