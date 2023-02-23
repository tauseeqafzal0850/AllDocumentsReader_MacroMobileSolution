package com.example.alldocumentsreaderimagescanner.reader.models

import android.net.Uri
import java.util.Date

data class AllFileListModel(val id:Long,val name: String, val path: String,val date: String,val fileUri: Uri)
