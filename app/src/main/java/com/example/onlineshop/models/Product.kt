package com.example.onlineshop.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Int,
    @SerializedName("description") val description: String,
    @SerializedName("images") val images: List<String>,
    @SerializedName("category") val category: String,
    @SerializedName("discountPercentage") val discountPercentage: Double,
    @SerializedName("rating") val rating: Double,
    @SerializedName("stock") val stock: Int,
    @SerializedName("brand") val brand: String,
    @SerializedName("thumbnail") val thumbnail: String
) : Parcelable {
    constructor() : this(
        0,
        "",
        0,
        "",
        listOf(),
        "",
        0.0,
        0.0,
        0,
        "",
        ""
    )

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        mutableListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeInt(price)
        parcel.writeString(description)
        parcel.writeList(images)
        parcel.writeString(category)
        parcel.writeDouble(discountPercentage)
        parcel.writeDouble(rating)
        parcel.writeInt(stock)
        parcel.writeString(brand)
        parcel.writeString(thumbnail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}




