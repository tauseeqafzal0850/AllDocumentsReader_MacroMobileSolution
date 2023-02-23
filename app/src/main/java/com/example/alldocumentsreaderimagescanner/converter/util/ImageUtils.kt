package com.example.alldocumentsreaderimagescanner.converter.util

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.AUTHORITY_APP
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.IMAGE_SCALE_TYPE_STRETCH
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.pdfDirectory
import com.example.alldocumentsreaderimagescanner.utils.MyPreference
import com.itextpdf.text.Rectangle
import java.io.File
import java.io.FileOutputStream

class ImageUtils {
    var mImageScaleType: String? = null


    /**
     * Creates a rounded bitmap from any bitmap
     *
     * @param bmp - input bitmap
     * @return - output bitmap
     */
    fun getRoundBitmap(bmp: Bitmap): Bitmap {
        val width = bmp.width
        val height = bmp.height
        val radius = Math.min(width, height) // set the smallest edge as radius.
        val bitmap: Bitmap
        bitmap = if (bmp.width != radius || bmp.height != radius) {
            Bitmap.createScaledBitmap(
                bmp,
                (bmp.width / 1.0f).toInt(),
                (bmp.height / 1.0f).toInt(), false
            )
        } else {
            bmp
        }
        val output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, radius, radius)
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = Color.parseColor("#BAB399")
        canvas.drawCircle(
            radius / 2f + 0.7f, radius / 2f + 0.7f,
            radius / 2f + 0.1f, paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    /**
     * Get round bitmap from file path
     *
     * @param path - file path
     * @return - output round bitmap
     */
    fun getRoundBitmapFromPath(path: String?): Bitmap? {
        val file = File(path)

        // First decode with inJustDecodeBounds=true to check dimensions
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, bmOptions)

        // Calculate inSampleSize
        bmOptions.inSampleSize = calculateInSampleSize(bmOptions)

        // Decode bitmap with actual size
        bmOptions.inJustDecodeBounds = false
        val smallBitmap = BitmapFactory.decodeFile(file.absolutePath, bmOptions) ?: return null
        return getRoundBitmap(smallBitmap)
    }

    /**
     * Calculate the inSampleSize value for given bitmap options & image dimensions
     *
     * @param options - bitmap options
     * @return inSampleSize value
     * https://developer.android.com/topic/performance/graphics/load-bitmap.html#java
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > 500 || width > 500) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= 500
                && halfWidth / inSampleSize >= 500
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun showImageScaleTypeDialog(context: Context?, saveValue: Boolean) {
        val builder = DialogUtils.getInstance().createCustomDialogWithoutContent(
            context as Activity?,
            R.string.image_scale_type
        )
        val materialDialog = builder.customView(R.layout.image_scale_type_dialog, true)
            .onPositive { dialog1: MaterialDialog, which: DialogAction? ->
                val view = dialog1.customView
                val radioGroup = view!!.findViewById<RadioGroup>(R.id.scale_type)
                val selectedId = radioGroup.checkedRadioButtonId
                mImageScaleType =
                    if (selectedId == R.id.aspect_ratio) IMAGE_SCALE_TYPE_ASPECT_RATIO else IMAGE_SCALE_TYPE_STRETCH
                val mSetAsDefault = view.findViewById<CheckBox>(R.id.cbSetDefault)
                if (saveValue || mSetAsDefault.isChecked) {
                    context?.let {
                        MyPreference.with(it)
                            .save(Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT, mImageScaleType)
                    }

                }
            }.build()
        if (saveValue) {
            val customView = materialDialog.customView
            customView!!.findViewById<View>(R.id.cbSetDefault).visibility = View.GONE
        }
        materialDialog.show()
    }

    /**
     * convert a bitmap to gray scale and return it
     *
     * @param bmpOriginal original bitmap which is converted to a new
     * grayscale bitmap
     */
    fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
        val height: Int = bmpOriginal.height
        val width: Int = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, bmpOriginal.config)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmpOriginal, 0f, 0f, paint)
        return bmpGrayscale
    }

    companion object {


        private var instance: ImageUtils? = null

        fun getInstance(): ImageUtils {
            if (instance == null) {
                instance = ImageUtils()
            }
            return instance!!
        }


        /**
         * Calculates the optimum size for an image, such that it scales to fit whilst retaining its aspect ratio
         *
         * @param originalWidth  the original width of the image
         * @param originalHeight the original height of the image
         * @param documentSize   a rectangle specifying the width and height that the image must fit within
         * @return a rectangle that provides the scaled width and height of the image
         */
        fun calculateFitSize(
            originalWidth: Float,
            originalHeight: Float,
            documentSize: Rectangle
        ): Rectangle {
            val widthChange = (originalWidth - documentSize.width) / originalWidth
            val heightChange = (originalHeight - documentSize.height) / originalHeight
            val changeFactor = Math.max(widthChange, heightChange)
            val newWidth = originalWidth - originalWidth * changeFactor
            val newHeight = originalHeight - originalHeight * changeFactor
            return Rectangle(
                Math.abs(newWidth.toInt()).toFloat(), Math.abs(
                    newHeight.toInt()
                ).toFloat()
            )
        }

        /**
         * Saves bitmap to external storage
         *
         * @param filename    - name of the file
         * @param finalBitmap - bitmap to save
         */
        fun saveImage(filename: String, finalBitmap: Bitmap?): String? {
            if (finalBitmap == null || checkIfBitmapIsWhite(finalBitmap)) return null
            val root = Environment.getExternalStorageDirectory().toString()
            val myDir = File(root + pdfDirectory)
            val fileName = "$filename.png"
            val file = File(myDir, fileName)
            if (file.exists()) file.delete()
            try {
                val out = FileOutputStream(file)
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                Log.v("saving", fileName)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "$myDir/$fileName"
        }


        /**
         * Checks of the bitmap is just all white pixels
         *
         * @param bitmap - input bitmap
         * @return - true, if bitmap is all white
         */
        private fun checkIfBitmapIsWhite(bitmap: Bitmap?): Boolean {
            if (bitmap == null) return true
            val whiteBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val canvas = Canvas(whiteBitmap)
            canvas.drawColor(Color.WHITE)
            return bitmap.sameAs(whiteBitmap)
        }
    }


}