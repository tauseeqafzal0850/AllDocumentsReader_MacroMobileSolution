package com.example.alldocumentsreaderimagescanner.converter.util

import android.content.Context
import android.net.Uri
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import kotlin.Throws
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import java.io.InputStream
import java.lang.Exception

class DocxFileReader(context: Context?) : FileReader(context) {
    @Throws(Exception::class)
    override fun createDocumentFromStream(
        uri: Uri, document: Document, myfont: Font, inputStream: InputStream
    ) {
        val doc = XWPFDocument(inputStream)
        val extractor = XWPFWordExtractor(doc)
        val fileData = extractor.text
        val documentParagraph = Paragraph(
            """
    $fileData
    
    """.trimIndent(), myfont
        )
        documentParagraph.alignment = Element.ALIGN_JUSTIFIED
        document.add(documentParagraph)
    }
}