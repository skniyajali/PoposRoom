package com.niyaj.daily_market.market_list.add_edit

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddEditMarketListViewModel @Inject constructor(
    private val repository: MarketListRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val marketId = savedStateHandle.get<Int>("marketId") ?: 0

    private val _selectedDate = MutableStateFlow("")
    val selectedDate = _selectedDate.asStateFlow()

    val marketList = snapshotFlow { marketId }.flatMapLatest {
        repository.getMarketListById(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null,
    )

    val marketItems = snapshotFlow { mSearchText.value }.flatMapLatest { searchText ->
        repository.getMarketItemsWithQuantityById(marketId, searchText)
    }.mapLatest { itemList ->
        if (itemList.isEmpty()) UiState.Empty else {
            totalItems = itemList.map { it.item.itemId }

            UiState.Success(itemList)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UiState.Loading,
    )

    private val _showList = MutableStateFlow(false)
    val showList = _showList.asStateFlow()

    private val _listItems = MutableStateFlow<List<MarketItemAndQuantity>>(emptyList())
    val listItems = _listItems.asStateFlow()

    fun onAddItem(itemId: Int) {
        viewModelScope.launch {
            when (val result = repository.addMarketListItem(marketId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun onRemoveItem(itemId: Int) {
        viewModelScope.launch {
            when (val result = repository.removeMarketListItem(marketId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun onIncreaseQuantity(itemId: Int) {
        viewModelScope.launch {
            when (val result = repository.increaseMarketListItemQuantity(marketId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun onDecreaseQuantity(itemId: Int) {
        viewModelScope.launch {
            when (val result = repository.decreaseMarketListItemQuantity(marketId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun selectDate(selectedDate: String) {
        viewModelScope.launch {
            _selectedDate.value = selectedDate
        }
    }

    private fun getListItems() {
        viewModelScope.launch {
            repository.getMarketItemsAndQuantity(marketId).collectLatest {
                _listItems.value = it
            }
        }
    }

    fun onDismissList() {
        viewModelScope.launch {
            _showList.value = false
        }
    }

    fun onShowList() {
        viewModelScope.launch {
            getListItems()
            _showList.value = true
        }
    }

    fun shareContent(
        context: Context,
        title: String,
        uri: Uri,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                setDataAndType(uri, "image/png")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val shareIntent = Intent.createChooser(sendIntent, title)
            startActivity(context, shareIntent, null)
        }
    }

    suspend fun saveImage(image: Bitmap, context: Context): Uri? {
        return withContext(Dispatchers.IO) {
            val imagesFolder = File(context.cacheDir, "images")
            try {
                imagesFolder.mkdirs()
                val file = File(imagesFolder, "shared_image.png")

                val stream = FileOutputStream(file)
                image.compress(Bitmap.CompressFormat.PNG, 90, stream)
                stream.flush()
                stream.close()

                FileProvider.getUriForFile(context, "com.popos.fileprovider", file)
            } catch (e: IOException) {
                Log.d("saving bitmap", "saving bitmap error ${e.message}")
                null
            }
        }
    }
}