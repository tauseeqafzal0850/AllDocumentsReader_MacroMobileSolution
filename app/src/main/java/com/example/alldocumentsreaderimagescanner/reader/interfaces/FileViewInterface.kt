package com.example.alldocumentsreaderimagescanner.reader.interfaces

import com.example.alldocumentsreaderimagescanner.reader.models.AllFileListModel

interface FileViewInterface {
    fun onFileClick(path:String)
    fun onFileDelete(path: AllFileListModel, pos:Int)
}