package com.example.alldocumentsreaderimagescanner.converter.interfaces

interface IRearrangeListener {
    fun onUpClick(position: Int)
    fun onDownClick(position: Int)
    fun onRemoveClick(position: Int)
}