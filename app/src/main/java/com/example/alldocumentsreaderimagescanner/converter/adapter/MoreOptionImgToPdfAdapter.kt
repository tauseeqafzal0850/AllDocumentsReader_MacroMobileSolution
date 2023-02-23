package com.example.alldocumentsreaderimagescanner.converter.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alldocumentsreaderimagescanner.databinding.ItemViewMoreOptionImgToPdfBinding
import com.example.alldocumentsreaderimagescanner.converter.interfaces.IOnItemClickListener
import com.example.alldocumentsreaderimagescanner.converter.model.EnhancementOptionsEntity

class MoreOptionImgToPdfAdapter(
    private val list: List<EnhancementOptionsEntity>,
    private val iOnItemClickListener: IOnItemClickListener
) :
    RecyclerView.Adapter<MoreOptionImgToPdfAdapter.ImgToPdfHolder>() {

    inner class ImgToPdfHolder(val binding: ItemViewMoreOptionImgToPdfBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgToPdfHolder {
        return ImgToPdfHolder(
            ItemViewMoreOptionImgToPdfBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private val bgArray = arrayOf(
        "#fff6f3",
        "#f3fffb",
        "#f3f6ff",
        "#f3f6ff",
        "#fdf3ff",
        "#fffbf3",
        "#f3fff5",
        "#fffff3",
        "#fff3fb"
    )

    override fun onBindViewHolder(holder: ImgToPdfHolder, position: Int) {
        with(holder) {
            binding.containerCardView.setOnClickListener {
                iOnItemClickListener.onItemClick(position)
            }
            binding.containerCardView.setCardBackgroundColor(Color.parseColor(bgArray[position]))

            binding.optionImg.setImageDrawable(list[position].image)
            binding.optionTv.text = list[position].name
        }
    }

    override fun getItemCount(): Int = list.size
}