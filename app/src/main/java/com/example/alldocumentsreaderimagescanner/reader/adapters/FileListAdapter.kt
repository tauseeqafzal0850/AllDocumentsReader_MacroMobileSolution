package com.example.alldocumentsreaderimagescanner.reader.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.databinding.FilesItemListBinding
import com.example.alldocumentsreaderimagescanner.reader.interfaces.FileViewInterface
import com.example.alldocumentsreaderimagescanner.reader.models.AllFileListModel
import com.example.alldocumentsreaderimagescanner.utils.Constant.isCollumNull
import java.io.File

class FileListAdapter(
    private val context: Context,
    private val fileViewInterface: FileViewInterface
) : ListAdapter<AllFileListModel, FileListAdapter.PdfListHolder>(FilesDiffCallback) {


    inner class PdfListHolder(private val binding: FilesItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(response: AllFileListModel, position: Int) {
            if (response.path.endsWith(".pdf")) {
                Glide.with(context).load(R.drawable.ic_pdf_icon).into(binding.fileIcon)
            } else if (response.path.endsWith(".doc") || response.path.endsWith(".docx")) {
                Glide.with(context).load(R.drawable.ic_word_icon).into(binding.fileIcon)
            } else if (response.path.endsWith(".xls") || response.path.endsWith(".xlsx")) {
                Glide.with(context).load(R.drawable.ic_excel).into(binding.fileIcon)
            } else if (response.path.endsWith(".ppt") || response.path.endsWith(".pptx")) {
                Glide.with(context).load(R.drawable.ic_ppt).into(binding.fileIcon)
            } else if (response.path.endsWith(".txt")) {
                Glide.with(context).load(R.drawable.ic_text).into(binding.fileIcon)
            }
            if (isCollumNull) {
                binding.fileName.text = response.path.substringAfterLast("/")
            } else {
                binding.fileName.text = response.name
            }
            binding.fileDelete.setOnClickListener {
                fileViewInterface.onFileDelete(response, position )
            }
            binding.constraintCard.setOnClickListener {
               fileViewInterface.onFileClick(response.path)
            }
            val result = (File(response.path).length() / 1024)         //in kb
            if (result < 1024) {
                binding.fileSize.text = result.toString() + "Kb"
            } else {
                binding.fileSize.text = result.div(1024).toString() + "Mb"
            }
           if (response.date.isNotEmpty()) {
               binding.filedate.text = response.date
           }

            binding.fileShare.setOnClickListener {
                shareItem(response.path)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfListHolder {
        return PdfListHolder(
            FilesItemListBinding.inflate(LayoutInflater.from(context), parent, false)
        )

    }

    override fun onBindViewHolder(holder: PdfListHolder, position: Int) {
        holder.bind(getItem(position),position)
    }


    private fun shareItem(uri: String) {
        try {
            val u =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", File(uri))
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, u)
            intent.type = "file/*"
            context.startActivity(Intent.createChooser(intent, "Share Via"))
        } catch (e: Exception) {
        }

    }

    object FilesDiffCallback : DiffUtil.ItemCallback<AllFileListModel>() {
        override fun areItemsTheSame(
            oldItem: AllFileListModel,
            newItem: AllFileListModel
        ): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(
            oldItem: AllFileListModel,
            newItem: AllFileListModel
        ): Boolean {
            return oldItem == newItem
        }
    }

}