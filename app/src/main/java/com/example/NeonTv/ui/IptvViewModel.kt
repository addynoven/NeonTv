package com.example.NeonTv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.NeonTv.data.IptvChannel
import com.example.NeonTv.data.repository.IptvRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IptvViewModel @Inject constructor(
    private val repository: IptvRepository
) : ViewModel() {

    private val _allChannels = MutableStateFlow<List<IptvChannel>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _selectedCountry = MutableStateFlow<String?>(null)
    private val _selectedLanguage = MutableStateFlow<String?>(null)
    private val _showFavoritesOnly = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val isLoading: StateFlow<Boolean> = _isLoading
    val error: StateFlow<String?> = _error
    val searchQuery: StateFlow<String> = _searchQuery
    val selectedCategory: StateFlow<String?> = _selectedCategory
    val selectedCountry: StateFlow<String?> = _selectedCountry
    val selectedLanguage: StateFlow<String?> = _selectedLanguage
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly

    val categories: StateFlow<List<String>> = _allChannels.map { channels ->
        channels.mapNotNull { it.category }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val countries: StateFlow<List<String>> = _allChannels.map { channels ->
        channels.mapNotNull { it.countryName }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val languages: StateFlow<List<String>> = _allChannels.map { channels ->
        channels.flatMap { it.languages }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val channels: StateFlow<List<IptvChannel>> = combine(
        _allChannels, _searchQuery, _selectedCategory, _selectedCountry, _selectedLanguage, _showFavoritesOnly
    ) { array ->
        val all = array[0] as List<IptvChannel>
        val query = array[1] as String
        val category = array[2] as String?
        val country = array[3] as String?
        val language = array[4] as String?
        val favsOnly = array[5] as Boolean

        all.filter { channel ->
            val matchesQuery = query.isEmpty() || channel.name.contains(query, ignoreCase = true)
            val matchesCategory = category == null || channel.category == category
            val matchesCountry = country == null || channel.countryName == country
            val matchesLanguage = language == null || channel.languages.contains(language)
            val matchesFavorites = !favsOnly || channel.isFavorite
            matchesQuery && matchesCategory && matchesCountry && matchesLanguage && matchesFavorites
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadChannels()
    }

    fun loadChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _allChannels.value = repository.getIptvChannels()
            } catch (e: Exception) {
                _error.value = "Failed to load channels: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun onCountrySelect(country: String?) {
        _selectedCountry.value = if (_selectedCountry.value == country) null else country
    }

    fun onLanguageSelect(language: String?) {
        _selectedLanguage.value = if (_selectedLanguage.value == language) null else language
    }

    fun toggleShowFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun toggleFavorite(channel: IptvChannel) {
        repository.toggleFavorite(channel.id)
        _allChannels.value = _allChannels.value.map {
            if (it.id == channel.id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }
}
