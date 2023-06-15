package com.c23ps105.prodify.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.remote.response.BlogsItem
import com.c23ps105.prodify.databinding.ItemLinearBinding

class BlogsAdapter(private val onClick: (BlogsItem) -> Unit) :
    ListAdapter<BlogsItem, BlogsAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemLinearBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val blog = getItem(position)
        holder.bind(blog)

        holder.binding.root.setOnClickListener {
            onClick(blog)
        }
    }

    inner class MyViewHolder(val binding: ItemLinearBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(blog: BlogsItem) {
            binding.apply {
                Glide.with(ivBlogs).load(blog.imageURL).placeholder(R.drawable.placeholder)
                    .into(ivBlogs)
                titleTextview.text = blog.title
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<BlogsItem> =
            object : DiffUtil.ItemCallback<BlogsItem>() {
                override fun areItemsTheSame(
                    oldItem: BlogsItem,
                    newItem: BlogsItem,
                ): Boolean {
                    return oldItem.blogId == newItem.blogId
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: BlogsItem,
                    newItem: BlogsItem,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}