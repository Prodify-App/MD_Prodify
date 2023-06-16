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
import com.c23ps105.prodify.data.remote.response.ProductsUserItem
import com.c23ps105.prodify.databinding.ItemStaggeredGridBinding

class ResultAdapter(
    private val onProductClick: (ProductsUserItem) -> Unit,
) :
    ListAdapter<ProductsUserItem, ResultAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemStaggeredGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
        holder.binding.root.setOnClickListener {
            onProductClick(product)
        }
    }

    inner class MyViewHolder(val binding: ItemStaggeredGridBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(product: ProductsUserItem) {
            binding.apply {
                tvCategoryCardMiddle.text = product.category
                tvTitle.text = product.title
                Glide.with(ivHistory).load(product.imageURL).placeholder(R.drawable.placeholder)
                    .into(ivHistory)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ProductsUserItem> =
            object : DiffUtil.ItemCallback<ProductsUserItem>() {
                override fun areItemsTheSame(
                    oldItem: ProductsUserItem,
                    newItem: ProductsUserItem,
                ): Boolean {
                    return oldItem.title == newItem.title
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: ProductsUserItem,
                    newItem: ProductsUserItem,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}