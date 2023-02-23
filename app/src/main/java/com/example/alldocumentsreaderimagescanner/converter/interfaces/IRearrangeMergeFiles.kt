package com.example.alldocumentsreaderimagescanner.converter.interfaces

interface IRearrangeMergeFiles {
    fun viewFile(path: String)
    fun removeFile(path: String)
    fun moveUp(position: Int)
    fun moveDown(position: Int)
}