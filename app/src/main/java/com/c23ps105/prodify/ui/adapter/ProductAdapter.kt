package com.c23ps105.prodify.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.local.entity.ProductEntity
import com.c23ps105.prodify.databinding.ItemGridBinding

class ProductAdapter(private val onClick: (ProductEntity) -> Unit) :
    ListAdapter<ProductEntity, ProductAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)

        holder.binding.root.setOnClickListener {
            onClick(product)
        }
    }

    inner class MyViewHolder(val binding: ItemGridBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(product: ProductEntity) {
            binding.apply {
                tvCategoryCard.text = product.category
                tvTitle.text = product.title
                Glide.with(ivHistory).load(product.imageURL).placeholder(R.drawable.placeholder)
                    .into(ivHistory)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ProductEntity> =
            object : DiffUtil.ItemCallback<ProductEntity>() {
                override fun areItemsTheSame(
                    oldItem: ProductEntity,
                    newItem: ProductEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: ProductEntity,
                    newItem: ProductEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}