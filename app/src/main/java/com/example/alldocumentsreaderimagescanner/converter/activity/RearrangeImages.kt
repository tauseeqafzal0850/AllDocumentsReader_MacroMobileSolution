package com.example.alldocumentsreaderimagescanner.converter.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.adapter.RearrangeImagesAdapter
import com.example.alldocumentsreaderimagescanner.converter.interfaces.IRearrangeListener
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.PREVIEW_IMAGES
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.RESULT
import com.example.alldocumentsreaderimagescanner.converter.util.ImageSortUtils
import com.example.alldocumentsreaderimagescanner.databinding.ActivityRearrangeImagesBinding

class RearrangeImages : AppCompatActivity(), IRearrangeListener {

    private var mImages: ArrayList<String>? = null
    private var mRearrangeImagesAdapter: RearrangeImagesAdapter? = null
    private lateinit var binding: ActivityRearrangeImagesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRearrangeImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        mImages = intent.getStringArrayListExtra(PREVIEW_IMAGES)
        mImages?.let { initRecyclerView(it) }

        binding.sort.setOnClickListener {
            sortImages()
        }
    }

    private fun initRecyclerView(images: ArrayList<String>) {
        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(
                this@RearrangeImages,
                LinearLayoutManager.VERTICAL, false
            )
            mRearrangeImagesAdapter =
                RearrangeImagesAdapter(this@RearrangeImages, images, this@RearrangeImages)
            adapter = mRearrangeImagesAdapter
        }

    }

    override fun onUpClick(position: Int) {
        mImages!!.add(position - 1, mImages!!.removeAt(position))
        mRearrangeImagesAdapter!!.positionChanged(mImages!!)
    }

    override fun onDownClick(position: Int) {
        mImages!!.add(position + 1, mImages!!.removeAt(position))
        mRearrangeImagesAdapter!!.positionChanged(mImages!!)
    }

    override fun onRemoveClick(position: Int) {
        try {
            mImages?.removeAt(position)
            mRearrangeImagesAdapter!!.positionChanged(mImages!!)
        } catch (e: IndexOutOfBoundsException) {

        }
    }

    private fun passUris() {
        val returnIntent = Intent()
        returnIntent.putStringArrayListExtra(RESULT, mImages)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    override fun onBackPressed() {
        passUris()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            passUris()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun sortImages() {
        MaterialDialog.Builder(this)
            .title(R.string.sort_by_title)
            .items(R.array.sort_options_images)
            .itemsCallback { _: MaterialDialog?, _: View?, position: Int, _: CharSequence? ->
                mImages?.let { ImageSortUtils.getInstance().performSortOperation(position, it) }
                mRearrangeImagesAdapter!!.positionChanged(mImages!!)
            }
            .negativeText(getString(R.string.cancel))
            .show()
    }

    companion object {
        fun getStartIntent(context: Context?, uris: ArrayList<String>): Intent {
            val intent = Intent(context, RearrangeImages::class.java)
            intent.putExtra(PREVIEW_IMAGES, uris)
            return intent
        }
    }
}