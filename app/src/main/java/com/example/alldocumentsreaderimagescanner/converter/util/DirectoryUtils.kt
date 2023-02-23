package com.example.alldocumentsreaderimagescanner.converter.util


import com.example.alldocumentsreaderimagescanner.converter.util.StringUtils.Companion.getInstance
import java.io.File

class DirectoryUtils {


    /**
     * create PDF directory if directory does not exists
     */
    val orCreatePdfDirectory: File
        get() {
            val folder = File(
                getInstance().defaultStorageLocation
            )
            folder.let {
                if (!it.exists()) it.mkdir()
            }
            return folder
        }

}