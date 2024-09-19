package com.funda.smartphotomanager.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.funda.smartphotomanager.data.model.PhotoModel
import com.funda.smartphotomanager.databinding.ItemImageBinding

class PhotoAdapter(private val onItemClick: (PhotoModel) -> Unit) :
    ListAdapter<PhotoModel, PhotoAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    private val TAG = "PhotoAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)
        holder.bind(photo)
    }

    inner class PhotoViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: PhotoModel) {
            Log.d(TAG, "bind: Fotoğraf gösteriliyor - ${photo.uri}")
            // Upload photo
            Glide.with(binding.imageView.context)
                .load(photo.uri)
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.imageView)

            // Click photo
            binding.imageView.setOnClickListener {
                onItemClick(photo)
                Log.d(TAG, "Photo clicked: ${photo.uri}")
            }
        }
    }
}

class PhotoDiffCallback : DiffUtil.ItemCallback<PhotoModel>() {
    override fun areItemsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
        return oldItem == newItem
    }
}
