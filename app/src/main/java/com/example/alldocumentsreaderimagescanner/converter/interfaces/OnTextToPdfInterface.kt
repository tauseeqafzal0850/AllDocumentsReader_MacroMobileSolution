package com.example.alldocumentsreaderimagescanner.converter.interfaces

interface OnTextToPdfInterface {
    fun onPDFCreationStarted()
    fun onPDFCreated(success: Boolean)
}