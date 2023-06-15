package com.c23ps105.prodify.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "products")
data class ProductEntity(
    @field:ColumnInfo("id")
    @field:PrimaryKey
    val id: Int,

    @field:ColumnInfo("createdAt")
    val createdAt: String?= null,

    @field:ColumnInfo("updatedAt")
    val updatedAt: String?= null,

    @field:ColumnInfo("title")
    val title: String?= null,

    @field:ColumnInfo("category")
    val category: String?= null,

    @field:ColumnInfo("description")
    val description: String?= null,

    @field:ColumnInfo("imageURL")
    val imageURL: String?= null,

    @field:ColumnInfo("bookmarked")
    var isBookmarked: Boolean,
) : Parcelable