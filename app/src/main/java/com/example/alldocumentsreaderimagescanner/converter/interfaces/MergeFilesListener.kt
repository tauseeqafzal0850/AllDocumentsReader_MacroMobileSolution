package com.example.alldocumentsreaderimagescanner.converter.interfaces

interface MergeFilesListener {
    fun resetValues(isPDFMerged: Boolean, path: String?)
    fun mergeStarted()
}