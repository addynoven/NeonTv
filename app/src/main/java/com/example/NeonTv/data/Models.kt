package com.example.NeonTv.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("categories") val categories: List<String> = emptyList(),
    @SerialName("country") val country: String? = null,
    @SerialName("website") val website: String? = null
)

@Serializable
data class Stream(
    @SerialName("channel") val channel: String? = null,
    @SerialName("url") val url: String,
    @SerialName("quality") val quality: String? = null,
    @SerialName("label") val label: String? = null
)

@Serializable
data class Logo(
    @SerialName("channel") val channel: String,
    @SerialName("url") val url: String
)

@Serializable
data class Country(
    @SerialName("name") val name: String,
    @SerialName("code") val code: String,
    @SerialName("flag") val flag: String? = null
)

@Serializable
data class Language(
    @SerialName("name") val name: String,
    @SerialName("code") val code: String
)

@Serializable
data class Feed(
    @SerialName("channel") val channel: String,
    @SerialName("languages") val languages: List<String> = emptyList()
)

@Serializable
data class BlocklistEntry(
    @SerialName("channel") val channel: String,
    @SerialName("reason") val reason: String? = null
)

data class IptvChannel(
    val id: String,
    val name: String,
    val streamUrl: String,
    val logoUrl: String? = null,
    val category: String? = null,
    val countryName: String? = null,
    val countryFlag: String? = null,
    val languages: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isGeoBlocked: Boolean = false
)
