package com.example.NeonTv.data.repository

import android.content.Context
import com.example.NeonTv.data.IptvChannel
import com.example.NeonTv.data.remote.IptvApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IptvRepository @Inject constructor(
    private val apiService: IptvApiService,
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("neon_tv_prefs", Context.MODE_PRIVATE)

    suspend fun getIptvChannels(): List<IptvChannel> {
        val channels = apiService.getChannels()
        val streams = apiService.getStreams()
        val logos = apiService.getLogos()
        val blocklist = apiService.getBlocklist()
        val countries = apiService.getCountries()
        val languages = apiService.getLanguages()
        val feeds = apiService.getFeeds()

        val blockedIds = blocklist.map { it.channel }.toSet()
        val streamsMap = streams.filter { it.channel != null }.associateBy { it.channel!! }
        val logosMap = logos.associateBy { it.channel }
        val countriesMap = countries.associateBy { it.code }
        val languagesMap = languages.associateBy { it.code }
        val feedsMap = feeds.groupBy { it.channel }
        val favorites = getFavoriteIds()

        return channels
            .filter { !blockedIds.contains(it.id) }
            .mapNotNull { channel ->
                val stream = streamsMap[channel.id]
                if (stream != null) {
                    val country = countriesMap[channel.country]
                    val channelFeeds = feedsMap[channel.id] ?: emptyList()
                    val channelLanguages = channelFeeds
                        .flatMap { it.languages }
                        .distinct()
                        .mapNotNull { languagesMap[it]?.name }

                    IptvChannel(
                        id = channel.id,
                        name = channel.name,
                        streamUrl = stream.url,
                        logoUrl = logosMap[channel.id]?.url,
                        category = channel.categories.firstOrNull(),
                        countryName = country?.name,
                        countryFlag = country?.flag,
                        languages = channelLanguages,
                        isFavorite = favorites.contains(channel.id),
                        isGeoBlocked = stream.label?.contains("Geo-blocked", ignoreCase = true) == true
                    )
                } else {
                    null
                }
            }
    }

    fun toggleFavorite(channelId: String) {
        val favorites = getFavoriteIds().toMutableSet()
        if (favorites.contains(channelId)) {
            favorites.remove(channelId)
        } else {
            favorites.add(channelId)
        }
        prefs.edit().putStringSet("favorites", favorites).apply()
    }

    private fun getFavoriteIds(): Set<String> {
        return prefs.getStringSet("favorites", emptySet()) ?: emptySet()
    }
}
