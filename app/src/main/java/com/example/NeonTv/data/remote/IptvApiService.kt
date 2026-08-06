package com.example.NeonTv.data.remote

import com.example.NeonTv.data.BlocklistEntry
import com.example.NeonTv.data.Channel
import com.example.NeonTv.data.Country
import com.example.NeonTv.data.Feed
import com.example.NeonTv.data.Language
import com.example.NeonTv.data.Logo
import com.example.NeonTv.data.Stream
import retrofit2.http.GET

interface IptvApiService {
    @GET("channels.json")
    suspend fun getChannels(): List<Channel>

    @GET("streams.json")
    suspend fun getStreams(): List<Stream>

    @GET("logos.json")
    suspend fun getLogos(): List<Logo>

    @GET("blocklist.json")
    suspend fun getBlocklist(): List<BlocklistEntry>

    @GET("countries.json")
    suspend fun getCountries(): List<Country>

    @GET("languages.json")
    suspend fun getLanguages(): List<Language>

    @GET("feeds.json")
    suspend fun getFeeds(): List<Feed>
}
