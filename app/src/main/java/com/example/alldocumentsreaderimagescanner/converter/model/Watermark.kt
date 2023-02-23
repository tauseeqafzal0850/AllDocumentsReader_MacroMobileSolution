package com.example.alldocumentsreaderimagescanner.converter.model

import com.itextpdf.text.BaseColor
import com.itextpdf.text.Font

class Watermark {
    var watermarkText: String? = null
    var rotationAngle = 0
    var textColor: BaseColor? = null
    var textSize = 0
    var fontFamily: Font.FontFamily? = null
    var fontStyle = 0
}