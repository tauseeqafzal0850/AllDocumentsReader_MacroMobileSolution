package com.example.alldocumentsreaderimagescanner.converter.util

import java.io.File
import java.util.*


class ImageSortUtils() {


    /**
     * performs sorting operation.
     *
     * @param option sorting operation
     * @param images list of image paths
     */
    fun performSortOperation(option: Int, images: List<String>) {
        if (option < 0 || option > 3) throw IllegalArgumentException(
            "Invalid sort option. "
                    + "Sort option must be in [0; 3] range!"
        )
        when (option) {
            NAME_ASC -> sortByNameAsc(images)
            NAME_DESC -> sortByNameDesc(images)
            DATE_ASC -> sortByDateAsc(images)
            DATE_DESC -> sortByDateDesc(images)
        }
    }

    /**
     * Sorts the given list in ascending alphabetical  order
     *
     * @param imagePaths list of image paths to be sorted
     */
    private fun sortByNameAsc(imagePaths: List<String>) {
        Collections.sort(
            imagePaths
        ) { path1: String, path2: String ->
            path1.substring(path1.lastIndexOf('/'))
                .compareTo(path2.substring(path2.lastIndexOf('/')))
        }
    }

    /**
     * Sorts the given list in descending alphabetical  order
     *
     * @param imagePaths list of image paths to be sorted
     */
    private fun sortByNameDesc(imagePaths: List<String>) {
        Collections.sort(
            imagePaths
        ) { path1: String, path2: String ->
            path2.substring(path2.lastIndexOf('/'))
                .compareTo(path1.substring(path1.lastIndexOf('/')))
        }
    }

    /**
     * Sorts the given list in ascending  date  order
     *
     * @param imagePaths list of image paths to be sorted
     */
    private fun sortByDateAsc(imagePaths: List<String>) {
        Collections.sort(
            imagePaths
        ) { path1: String?, path2: String? ->
            File(path2).lastModified().compareTo(File(path1).lastModified())
        }
    }

    /**
     * Sorts the given list in descending date  order
     *
     * @param imagePaths list of image paths to be sorted
     */
    private fun sortByDateDesc(imagePaths: List<String>) {
        Collections.sort(
            imagePaths
        ) { path1: String?, path2: String? ->
            File(path1).lastModified().compareTo(File(path2).lastModified())
        }
    }

    companion object {
        private val NAME_ASC = 0
        private val NAME_DESC = 1
        private val DATE_ASC = 2
        private val DATE_DESC = 3
        private var instance: ImageSortUtils? = null
        fun getInstance(): ImageSortUtils {
            if (instance == null) {
                instance = ImageSortUtils()
            }
            return instance!!
        }
    }
}
