package com.c23ps105.prodify.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.CardDataClass
import com.c23ps105.prodify.databinding.ItemLinearBinding

class BlogsAdapter(private val onClick: (CardDataClass) -> Unit) :
    ListAdapter<CardDataClass, BlogsAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemLinearBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pokemon = getItem(position)
        holder.bind(pokemon)

        holder.binding.root.setOnClickListener {
            onClick(pokemon)
        }
    }

    inner class MyViewHolder(val binding: ItemLinearBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(pokemon: CardDataClass) {
            binding.apply {
                Glide.with(binding.root).load(pokemon.imgData).placeholder(R.drawable.placeholder)
                    .into(ivBlogs)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<CardDataClass> =
            object : DiffUtil.ItemCallback<CardDataClass>() {
                override fun areItemsTheSame(
                    oldItem: CardDataClass,
                    newItem: CardDataClass
                ): Boolean {
                    return oldItem.titleData == newItem.titleData
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: CardDataClass,
                    newItem: CardDataClass
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}