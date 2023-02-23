package com.example.alldocumentsreaderimagescanner.converter.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alldocumentsreaderimagescanner.converter.interfaces.IRearrangeMergeFiles
import com.example.alldocumentsreaderimagescanner.converter.util.FileUtils
import com.example.alldocumentsreaderimagescanner.databinding.ItemMergeSelectedFileBinding

class MergeSelectedFileAdapter(
    private val list: List<String>?,
    private val iRearrangeMergeFiles: IRearrangeMergeFiles
) : RecyclerView.Adapter<MergeSelectedFileAdapter.MergeSelectedHolder>() {

    inner class MergeSelectedHolder(val binding: ItemMergeSelectedFileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MergeSelectedHolder {
        return MergeSelectedHolder(
            ItemMergeSelectedFileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: MergeSelectedHolder, position: Int) {

        holder.binding.run {
            fileName.text = list?.let { FileUtils.getFileName(it[position]) }
//            try {
//                if (position==1){
//                    upFile.visibility= View.GONE
//                }
//                val lastPos=list!!.size-1
//                if (position==lastPos)
//                    downFile.visibility=View.GONE
//            } catch (e: Exception) {
//
//            }
            downFile.setOnClickListener {
                if (list?.size != position + 1){
                    iRearrangeMergeFiles.moveDown(position)
                }

            }
            upFile.setOnClickListener {
                if (position != 0) {
                    iRearrangeMergeFiles.moveUp(position)
                }
            }
            viewFile.setOnClickListener {
                list?.let {
                    iRearrangeMergeFiles.viewFile(it[position])
                }
            }
            remove.setOnClickListener {
                list?.let {
                    iRearrangeMergeFiles.removeFile(it[position])
                }
            }
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0
}