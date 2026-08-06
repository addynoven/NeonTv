package com.example.NeonTv.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.NeonTv.data.IptvChannel
import com.example.NeonTv.ui.IptvViewModel

private enum class FilterType {
    CATEGORY, COUNTRY, LANGUAGE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelListScreen(
    viewModel: IptvViewModel,
    onChannelClick: (IptvChannel) -> Unit
) {
    val channels by viewModel.channels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val countries by viewModel.countries.collectAsState()
    val languages by viewModel.languages.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsState()

    var activeFilterType by remember { mutableStateOf<FilterType?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Neon TV", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { viewModel.toggleShowFavoritesOnly() }) {
                            Icon(
                                imageVector = if (showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorites",
                                tint = if (showFavoritesOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search channels...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory != null,
                            onClick = { activeFilterType = FilterType.CATEGORY },
                            label = { Text(selectedCategory ?: "Category") },
                            trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedCountry != null,
                            onClick = { activeFilterType = FilterType.COUNTRY },
                            label = { Text(selectedCountry ?: "Country") },
                            trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedLanguage != null,
                            onClick = { activeFilterType = FilterType.LANGUAGE },
                            label = { Text(selectedLanguage ?: "Language") },
                            trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
                
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadChannels() }) {
                        Text("Retry")
                    }
                }
            } else if (channels.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (showFavoritesOnly) "No favorites yet" else "No channels found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(channels, key = { it.id }) { channel ->
                        ChannelItem(
                            channel = channel,
                            onClick = { onChannelClick(channel) },
                            onFavoriteToggle = { viewModel.toggleFavorite(channel) }
                        )
                    }
                }
            }
        }
    }

    if (activeFilterType != null) {
        ModalBottomSheet(
            onDismissRequest = { activeFilterType = null },
            sheetState = sheetState
        ) {
            val title = when (activeFilterType) {
                FilterType.CATEGORY -> "Select Category"
                FilterType.COUNTRY -> "Select Country"
                FilterType.LANGUAGE -> "Select Language"
                else -> ""
            }
            
            val options = when (activeFilterType) {
                FilterType.CATEGORY -> categories
                FilterType.COUNTRY -> countries
                FilterType.LANGUAGE -> languages
                else -> emptyList()
            }
            
            val selected = when (activeFilterType) {
                FilterType.CATEGORY -> selectedCategory
                FilterType.COUNTRY -> selectedCountry
                FilterType.LANGUAGE -> selectedLanguage
                else -> null
            }
            
            FilterPickerContent(
                title = title,
                options = options,
                selectedOption = selected,
                onOptionSelect = { option ->
                    when (activeFilterType) {
                        FilterType.CATEGORY -> viewModel.onCategorySelect(option)
                        FilterType.COUNTRY -> viewModel.onCountrySelect(option)
                        FilterType.LANGUAGE -> viewModel.onLanguageSelect(option)
                        else -> {}
                    }
                    activeFilterType = null
                }
            )
        }
    }
}

@Composable
private fun FilterPickerContent(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelect: (String?) -> Unit
) {
    var pickerSearchQuery by remember { mutableStateOf("") }
    val filteredOptions = remember(options, pickerSearchQuery) {
        options.filter { it.contains(pickerSearchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = pickerSearchQuery,
            onValueChange = { pickerSearchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                ListItem(
                    modifier = Modifier.clickable { onOptionSelect(null) },
                    headlineContent = { Text("All", fontWeight = if (selectedOption == null) FontWeight.Bold else FontWeight.Normal) },
                    trailingContent = { if (selectedOption == null) RadioButton(selected = true, onClick = null) }
                )
            }
            items(filteredOptions) { option ->
                ListItem(
                    modifier = Modifier.clickable { onOptionSelect(option) },
                    headlineContent = { Text(option, fontWeight = if (selectedOption == option) FontWeight.Bold else FontWeight.Normal) },
                    trailingContent = { if (selectedOption == option) RadioButton(selected = true, onClick = null) }
                )
            }
        }
    }
}

@Composable
fun ChannelItem(channel: IptvChannel, onClick: () -> Unit, onFavoriteToggle: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = channel.name, fontWeight = FontWeight.SemiBold)
                if (channel.isGeoBlocked) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Geo-blocked",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        },
        supportingContent = { 
            Column {
                channel.category?.let { 
                    Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) 
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    channel.countryFlag?.let { Text(text = it) }
                    channel.countryName?.let { 
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = it, style = MaterialTheme.typography.bodySmall) 
                    }
                }
                if (channel.languages.isNotEmpty()) {
                    Text(
                        text = channel.languages.joinToString(", "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        leadingContent = {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                AsyncImage(
                    model = channel.logoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(4.dp)
                )
            }
        },
        trailingContent = {
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (channel.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
}
