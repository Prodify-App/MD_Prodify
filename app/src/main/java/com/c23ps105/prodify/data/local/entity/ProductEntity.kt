package com.c23ps105.prodify.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "products")
data class ProductEntity(
    @field:ColumnInfo("id")
    @field:PrimaryKey
    val id: Int,

    @field:ColumnInfo("createdAt")
    val createdAt: String,

    @field:ColumnInfo("updatedAt")
    val updatedAt: String,

    @field:ColumnInfo("title")
    val title: String,

    @field:ColumnInfo("category")
    val category: String,

    @field:ColumnInfo("description")
    val description: String,

    @field:ColumnInfo("imageURL")
    val imageURL: String,

    @field:ColumnInfo("Bookmarked")
    val isBookmarked: Boolean,
)