package com.example.alldocumentsreaderimagescanner.converter.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.fragments.ImageToPdfFragment
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.pdfDirectory
import com.example.alldocumentsreaderimagescanner.converter.util.FileUtils
import com.example.alldocumentsreaderimagescanner.converter.util.StringUtils
import com.example.alldocumentsreaderimagescanner.databinding.ActivityImageCropBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.theartofdev.edmodo.cropper.CropImageView.CropResult
import java.io.File

class ImageCropActivity : AppCompatActivity() {
    lateinit var binding: ActivityImageCropBinding
    private var mCurrentImageIndex = 0
    private var mImages: ArrayList<String>? = null
    private var mCroppedImageUris: HashMap<Int, Uri>? = null
    private var mCurrentImageEdited = false
    private var mFinishedClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mCroppedImageUris = HashMap()
        setUpCropImageView()

        mImages = ImageToPdfFragment.mImagesUri
        mFinishedClicked = false
        mImages?.let {
            for (i in it.indices) mCroppedImageUris!![i] = Uri.fromFile(File(mImages!![i]))
        }

        if (mImages?.size == 0) finish()

        setImage(0)

        binding.cropButton.setOnClickListener {
            cropButtonClicked()
        }
        binding.rotateButton.setOnClickListener {
            rotateButtonClicked()
        }
        binding.nextimageButton.setOnClickListener {
            nextImageClicked()
        }
        binding.previousImageButton.setOnClickListener {
            prevImgBtnClicked()
        }
//        binding.skipButton.setOnClickListener {
//            mCurrentImageEdited = false
//            nextImageClicked()
//        }
        binding.doneButton.setOnClickListener {
            mFinishedClicked = true
            cropButtonClicked()
        }

    }

    private fun cropButtonClicked() {
        mCurrentImageEdited = false
        val root = Environment.getExternalStorageDirectory()
        val folder = File("$root$pdfDirectory")
        val uri = binding.cropImageView.imageUri
        if (uri == null) {
            StringUtils.getInstance().showSnackbar(this, R.string.error_uri_not_found)
            return
        }
        val path = uri.path
        var filename = "cropped_im"
        if (path != null) filename = "cropped_" + FileUtils.getFileName(path)
        val file = File(folder, filename)
        binding.cropImageView.saveCroppedImageAsync(Uri.fromFile(file))
    }

    private fun rotateButtonClicked() {
        mCurrentImageEdited = true
        binding.cropImageView.rotateImage(90)
    }

    private fun nextImageClicked() {
        if (mImages!!.size == 0) return
//        binding.skipButton.isEnabled=true
        if (!mCurrentImageEdited) {
            mCurrentImageIndex = (mCurrentImageIndex + 1) % mImages!!.size
            setImage(mCurrentImageIndex)
        } else {
            StringUtils.getInstance().showSnackbar(this, R.string.save_first)
        }
    }

    private fun prevImgBtnClicked() {
        if (mImages!!.size == 0) return

        if (!mCurrentImageEdited) {
            if (mCurrentImageIndex == 0) {
                mCurrentImageIndex = mImages!!.size
            }
            mCurrentImageIndex = (mCurrentImageIndex - 1) % mImages!!.size
            setImage(mCurrentImageIndex)
        } else {
            StringUtils.getInstance().showSnackbar(this, R.string.save_first)
        }
    }


    /**
     * Initial setup of crop image view
     */
    private fun setUpCropImageView() {
        binding.cropImageView.setOnCropImageCompleteListener { _: CropImageView?, result: CropResult ->
            result.uri?.let {
                mCroppedImageUris?.set(mCurrentImageIndex, it)
                binding.cropImageView.setImageUriAsync(mCroppedImageUris?.get(mCurrentImageIndex))
                if (mFinishedClicked) {
                    val intent = Intent()
                    intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, mCroppedImageUris)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_crop_image, menu)
        return true
    }


    /**
     * Set image in crop image view & increment counters
     * @param index - image index
     */
    private fun setImage(index: Int) {
        mCurrentImageEdited = false
        if (index < 0 || index >= mImages!!.size) return
        val mImageCount = findViewById<TextView>(R.id.imagecount)
        mImageCount.text = String.format(
            "%s %d of %d",
            getString(R.string.crop_image),
            index + 1,
            mImages?.size
        )
        binding.cropImageView.setImageUriAsync(mCroppedImageUris?.get(index))
    }

    override fun onDestroy() {
        setResult(RESULT_CANCELED)
        super.onDestroy()
    }


}