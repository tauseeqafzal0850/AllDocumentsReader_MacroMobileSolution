package com.example.alldocumentsreaderimagescanner.converter.util

import androidx.annotation.WorkerThread
import com.itextpdf.text.pdf.PdfReader
import java.io.IOException

object PDFUtils {
    /**
     * Check if a PDF at given path is encrypted
     *
     * @param path - path of PDF
     * @return true - if encrypted otherwise false
     */
    @WorkerThread
    fun isPDFEncrypted(path: String?): Boolean {
        var isEncrypted: Boolean
        var pdfReader: PdfReader? = null
        try {
            pdfReader = PdfReader(path)
            isEncrypted = pdfReader.isEncrypted
        } catch (e: IOException) {
            isEncrypted = true
        } finally {
            pdfReader?.close()
        }
        return isEncrypted
    }

}