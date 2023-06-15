package com.c23ps105.prodify.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.c23ps105.prodify.data.local.entity.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getProducts(): LiveData<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Int): LiveData<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertProduct(product: List<ProductEntity>)

    @Update
    fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products where Bookmarked = 0")
    fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM products WHERE id = :id AND bookmarked = 1)")
    fun isProductBookmarked(id: Int): Boolean

    @Query("SELECT * FROM products WHERE bookmarked = 1")
    fun getBookmarkedProduct(): LiveData<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: Int)

    @Query("SELECT EXISTS(SELECT * FROM products WHERE id = :productId AND bookmarked = 1)")
    fun isProductBookmarkAlready(productId: Int): LiveData<Boolean>
}