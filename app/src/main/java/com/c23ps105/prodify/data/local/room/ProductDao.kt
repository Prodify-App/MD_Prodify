package com.c23ps105.prodify.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.c23ps105.prodify.data.local.entity.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getProducts(): LiveData<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: String): LiveData<List<ProductEntity>>

    @Insert
    fun insertProduct(product: List<ProductEntity>)

    @Update
    fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products where bookmarked = 0")
    fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM products WHERE title = :title AND bookmarked = 1)")
    fun isProductBookmarked(title: String): Boolean
}