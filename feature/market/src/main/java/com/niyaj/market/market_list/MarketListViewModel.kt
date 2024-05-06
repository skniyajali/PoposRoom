/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.market.market_list

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.startOfDayTime
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketList
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MarketListViewModel @Inject constructor(
    private val marketListRepository: MarketListRepository,
) : BaseViewModel() {

    val items = snapshotFlow { mSearchText.value }.flatMapLatest {
        marketListRepository.getAllMarketLists(it)
    }.mapLatest { items ->
        totalItems = items.map { it.marketList.marketId }
        if (items.isEmpty()) UiState.Empty else UiState.Success(items)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UiState.Loading
    )

    private val _showList = MutableStateFlow(0L)
    val showList = _showList.asStateFlow()

    private val _listItems = MutableStateFlow<List<MarketItemAndQuantity>>(emptyList())
    val listItems = _listItems.asStateFlow()

    fun createNewList() {
        viewModelScope.launch {
            val newList = MarketList(
                marketDate = startOfDayTime.toLong(),
                createdAt = System.currentTimeMillis()
            )

            marketListRepository.addOrIgnoreMarketList(newList)
        }
    }

    fun onDismissList() {
        viewModelScope.launch {
            _listItems.update { emptyList() }
            _showList.update { 0L }
        }
    }

    fun onShowList(marketId: Int, marketDate: Long) {
        viewModelScope.launch {
            _showList.value = marketDate
            getListItems(marketId)
        }
    }

    private fun getListItems(marketId: Int) {
        viewModelScope.launch {
            marketListRepository.getMarketItemsAndQuantity(marketId).collectLatest {
                _listItems.value = it
                it.ifEmpty { _showList.value = 0L }
            }
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
            ContextCompat.startActivity(context, shareIntent, null)
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

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = marketListRepository.deleteMarketLists(mSelectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "unable"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.OnSuccess("${mSelectedItems.size} items has been deleted"))
                }
            }

            mSelectedItems.clear()
        }
    }
}