package com.example.alldocumentsreaderimagescanner.converter.interfaces

interface OnPDFCreatedInterface {
    fun onPDFCreationStarted()
    fun onPDFCreated(success: Boolean, path: String?)
}