package com.example.alldocumentsreaderimagescanner.converter.util

import android.graphics.Color

class ColorUtils {
    companion object {
        private var instance: ColorUtils? = null
        private const val COLOR_DIFF_THRESHOLD = 30.0
        fun getInstance(): ColorUtils {
            if (instance == null) {
                instance = ColorUtils()
            }
            return instance!!
        }
    }

    /**
     * Every RGB color consists three components: red, green and blue. That's why can we put 2 colors in a 3D coordinate
     * system and calculate distance between them. When distance is lower than COLOR_DIFF_THRESHOLD it means that these
     * colors are similar.
     * @see [
     * Distance between 2 points in 3D
    ](https://www.engineeringtoolbox.com/distance-relationship-between-two-points-d_1854.html) *
     *
     * @return true for similar colors
     */
    fun colorSimilarCheck(color1: Int, color2: Int): Boolean {
        val colorDiff = Math.sqrt(
            Math.pow((Color.red(color1) - Color.red(color2)).toDouble(), 2.0) +
                    Math.pow((Color.green(color1) - Color.green(color2)).toDouble(), 2.0) +
                    Math.pow((Color.blue(color1) - Color.blue(color2)).toDouble(), 2.0)
        )
        return colorDiff < COLOR_DIFF_THRESHOLD
    }



}
