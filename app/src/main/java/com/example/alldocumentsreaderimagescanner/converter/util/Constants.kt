package com.example.alldocumentsreaderimagescanner.converter.util

import android.Manifest
import android.graphics.Color

object Constants {

    const val AUTHORITY_APP = "com.camerascanner.documents.pdf.viewer.provider"
    const val STORAGE_LOCATION = "storage_location"
    const val PATH_SEPARATOR = "/"
    const val DEFAULT_COMPRESSION = "DefaultCompression"
    const val DEFAULT_FONT_SIZE_TEXT = "DefaultFontSize"
    const val DEFAULT_FONT_SIZE = 11
    const val PREVIEW_IMAGES = "preview_images"
    const val DEFAULT_FONT_FAMILY_TEXT = "DefaultFontFamily"
    const val DEFAULT_FONT_FAMILY = "TIMES_ROMAN"
    const val DEFAULT_FONT_COLOR_TEXT = "DefaultFontColor"
    const val DEFAULT_FONT_COLOR = -16777216
    const val DEFAULT_PAGE_COLOR_TTP = "DefaultPageColorTTP"
    const val DEFAULT_PAGE_COLOR_ITP = "DefaultPageColorITP"
    const val DEFAULT_PAGE_COLOR = Color.WHITE
    const val DEFAULT_IMAGE_BORDER_TEXT = "Image_border_text"
    const val RESULT = "result"
    const val DEFAULT_PAGE_SIZE_TEXT = "DefaultPageSize"
    const val DEFAULT_PAGE_SIZE = "A4"
    const val DEFAULT_QUALITY_VALUE = 30
    const val DEFAULT_BORDER_WIDTH = 0
    const val DEFAULT_IMAGE_SCALE_TYPE_TEXT = "image_scale_type"
    const val IMAGE_SCALE_TYPE_STRETCH = "stretch_image"
    const val IMAGE_SCALE_TYPE_ASPECT_RATIO = "maintain_aspect_ratio"
    const val PG_NUM_STYLE_PAGE_X_OF_N = "pg_num_style_page_x_of_n"
    const val PG_NUM_STYLE_X_OF_N = "pg_num_style_x_of_n"
    const val PG_NUM_STYLE_X = "pg_num_style_x"
    const val MASTER_PWD_STRING = "master_password"
    const val pdfDirectory = "/Documents/PDF Converter/"
    const val pdfExtension = ".pdf"
    const val PARAM_URI = "uri"
    const val PARAM_Int = "int"
    const val appName = "All Documents Reader"
    const val PATH_SEPERATOR = "/"
    const val textExtension = ".txt"
    const val excelExtension = ".xls"
    const val excelWorkbookExtension = ".xlsx"
    const val docExtension = ".doc"
    const val docxExtension = ".docx"
    const val tempDirectory = "temp"
    const val OPEN_SELECT_IMAGES = "open_select_images"
    const val PREF_PAGE_STYLE = "pref_page_number_style"
    const val PREF_PAGE_STYLE_ID = "pref_page_number_style_rb_id"
    const val REQUEST_CODE_FOR_WRITE_PERMISSION = 4

    val WRITE_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val READ_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
}