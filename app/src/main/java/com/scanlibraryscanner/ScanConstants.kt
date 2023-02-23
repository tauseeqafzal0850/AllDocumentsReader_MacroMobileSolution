package com.scanlibraryscanner

import java.text.SimpleDateFormat
import java.util.*

object ScanConstants {

    const val PICKFILE_REQUEST_CODE = 1
    const val START_CAMERA_REQUEST_CODE = 2
    const val OPEN_INTENT_PREFERENCE = "selectContent"
    const val SCANNED_RESULT = "scannedResult"


    const val SELECTED_BITMAP = "selectedBitmap"
    const val SCAN_MORE = "scanMore"
    const val SAVE_PDF = "savePdf"


    val PDFScanner_tmp = "All Document Reader/PDF"
    val PDFScanner_stage = "PDF Scanner/stage/"
    val PDFScanner_scantmp = "PDF Scanner/scantmp/"
    val PDF_Converter_tmp = "PDF Converter/tmp"
    val URI_Tmp = "URI_Tmp/"

    val cameraType = "cameraType"
    val frontCamera = 111
    val backCamera = 222
    val passportCamera = 333
    const val OPEN_CAMERA = 4
    const val OPEN_MEDIA = 5
    val saveImageTemporary = "ID Card/"
    val CameraActivityResult = 0

    val addPdfSharedKey = "addPdfSharedKey"
    val addPdfSharedKeyValueKey = "addPdfSharedKeyValueKey"


}