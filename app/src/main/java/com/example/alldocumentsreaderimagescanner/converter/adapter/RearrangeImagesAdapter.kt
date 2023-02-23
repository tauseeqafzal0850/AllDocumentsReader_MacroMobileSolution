package com.example.alldocumentsreaderimagescanner.converter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alldocumentsreaderimagescanner.databinding.ItemRearrangeImagesBinding
import com.example.alldocumentsreaderimagescanner.converter.interfaces.IRearrangeListener
import java.io.File

class RearrangeImagesAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private val iRearrangeListener: IRearrangeListener
) :
    RecyclerView.Adapter<RearrangeImagesAdapter.RearrangeHolder>() {

    inner class RearrangeHolder(val binding: ItemRearrangeImagesBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RearrangeHolder {
        return RearrangeHolder(
            ItemRearrangeImagesBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RearrangeHolder, position: Int) {
        val imageFile = File(list[position])

        with(holder) {
            if (position == 0) {
                binding.buttonUp.visibility = View.GONE
            } else {
                binding.buttonUp.visibility = View.VISIBLE
            }
            if (position == itemCount - 1) {
                binding.buttonDown.visibility = View.GONE
            } else {
                binding.buttonDown.visibility = View.VISIBLE
            }
            binding.pageNumber.text = (position + 1).toString()
            Glide.with(context).load(imageFile).into(binding.image)

            binding.buttonDown.setOnClickListener {
                iRearrangeListener.onDownClick(position)
            }
            binding.buttonUp.setOnClickListener {
                iRearrangeListener.onUpClick(position)
            }
            binding.removeImage.setOnClickListener {
                iRearrangeListener.onRemoveClick(position)
            }

        }


    }

    fun positionChanged(images: ArrayList<String>) {
        list = images
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}