/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.feature.market.marketItem

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_MEASURE_EMPTY_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_DIGIT_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_EMPTY_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_LENGTH_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_PRICE_INVALID
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_PRICE_LESS_THAN_FIVE_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_TYPE_EMPTY_ERROR
import com.niyaj.domain.market.ValidateItemTypeUseCase
import com.niyaj.domain.market.ValidateMarketItemNameUseCase
import com.niyaj.domain.market.ValidateMarketItemPriceUseCase
import com.niyaj.domain.market.ValidateMeasureUnitUseCase
import com.niyaj.feature.market.marketItem.createOrUpdate.AddEditMarketItemEvent
import com.niyaj.feature.market.marketItem.createOrUpdate.AddEditMarketItemViewModel
import com.niyaj.model.MarketTypeIdAndName
import com.niyaj.model.MeasureUnit
import com.niyaj.model.searchItems
import com.niyaj.model.searchMeasureUnit
import com.niyaj.testing.repository.TestMarketItemRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.MarketTypePreviewData
import com.niyaj.ui.parameterProvider.MeasureUnitPreviewData
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AddEditMarketItemViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestMarketItemRepository()
    private val analyticsHelper = TestAnalyticsHelper()

    private val validateItemName =
        ValidateMarketItemNameUseCase(repository, UnconfinedTestDispatcher())
    private val validateItemPrice = ValidateMarketItemPriceUseCase()
    private val validateItemType = ValidateItemTypeUseCase()
    private val validateMeasureUnit = ValidateMeasureUnitUseCase()
    private val savedStateHandle = SavedStateHandle()

    private lateinit var viewModel: AddEditMarketItemViewModel

    @Before
    fun setup() {
        viewModel = AddEditMarketItemViewModel(
            repository = repository,
            validateItemName = validateItemName,
            validateItemPrice = validateItemPrice,
            validateItemType = validateItemType,
            validateMeasureUnit = validateMeasureUnit,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun initWithTypeId_shouldPopulate_marketType() {
        val item = repository.createTestItem()
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["itemId"] = item.itemId

        viewModel = AddEditMarketItemViewModel(
            repository = repository,
            validateItemName = validateItemName,
            validateItemPrice = validateItemPrice,
            validateItemType = validateItemType,
            validateMeasureUnit = validateMeasureUnit,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(item.itemType, viewModel.state.marketType)
        assertEquals(item.itemName, viewModel.state.itemName)
        assertEquals(item.itemMeasureUnit, viewModel.state.itemMeasureUnit)
        assertEquals(item.itemPrice, viewModel.state.itemPrice)
        assertEquals(item.itemDescription, viewModel.state.itemDesc)
    }

    @Test
    fun itemTypes_initialIsEmpty_whenNoDataAvailable() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemTypeChanged(MarketTypeIdAndName()))

        viewModel.itemTypes.test {
            assertEquals(emptyList(), awaitItem())
        }
    }

    @Test
    fun itemTypes_populatedAll_whenDataAvailable() = runTest {
        val items = MarketTypePreviewData.marketTypeIdAndNames
        repository.updateMarketTypeAndIdData(items)

        viewModel.onEvent(AddEditMarketItemEvent.ItemTypeChanged(MarketTypeIdAndName()))

        viewModel.itemTypes.test {
            assertEquals(items, awaitItem())
        }
    }

    @Test
    fun itemTypes_onPerformSearch_searchedItemOnlyPopulated() = runTest {
        val items = MarketTypePreviewData.marketTypeIdAndNames
        repository.updateMarketTypeAndIdData(items)

        viewModel.onEvent(
            AddEditMarketItemEvent.ItemTypeChanged(MarketTypeIdAndName(typeName = "Snacks")),
        )

        viewModel.itemTypes.test {
            assertEquals(items.searchItems("Snacks"), awaitItem())
        }
    }

    @Test
    fun measureUnits_initialIsEmpty_whenNoDataAvailable() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemMeasureUnitChanged(MeasureUnit()))

        viewModel.measureUnits.test {
            assertEquals(emptyList(), awaitItem())
        }
    }

    @Test
    fun measureUnits_populatedAll_whenDataAvailable() = runTest {
        val items = MeasureUnitPreviewData.measureUnits
        repository.updateMeasureUnitData(items)

        viewModel.onEvent(AddEditMarketItemEvent.ItemMeasureUnitChanged(MeasureUnit()))

        viewModel.measureUnits.test {
            assertEquals(items, awaitItem())
        }
    }

    @Test
    fun measureUnits_populatedSearchedItems_whenPerformSearch() = runTest {
        val items = MeasureUnitPreviewData.measureUnits
        repository.updateMeasureUnitData(items)

        viewModel.onEvent(
            AddEditMarketItemEvent.ItemMeasureUnitChanged(MeasureUnit(unitName = "Inch")),
        )

        viewModel.measureUnits.test {
            assertEquals(items.searchMeasureUnit("Inch"), awaitItem())
        }
    }

    @Test
    fun typeError_updated_whenMarketTypeIsEmpty() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemTypeChanged(MarketTypeIdAndName()))

        viewModel.typeError.test {
            assertEquals(MARKET_ITEM_TYPE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun typeError_updated_whenMarketTypeIsNotEmpty() = runTest {
        viewModel.onEvent(
            AddEditMarketItemEvent.ItemTypeChanged(
                MarketTypeIdAndName(typeId = 1, typeName = "Test"),
            ),
        )

        viewModel.typeError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenItemNameIsEmpty() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemNameChanged(""))

        viewModel.nameError.test {
            assertEquals(MARKET_ITEM_NAME_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenItemNameIsLessThanThreeCharacters() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemNameChanged("Te"))

        viewModel.nameError.test {
            assertEquals(MARKET_ITEM_NAME_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenItemNameContainsDigit() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemNameChanged("Test1"))

        viewModel.nameError.test {
            assertEquals(MARKET_ITEM_NAME_DIGIT_ERROR, awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenItemNameAlreadyExist() = runTest {
        val item = repository.createTestItem()
        viewModel.onEvent(AddEditMarketItemEvent.ItemNameChanged(item.itemName))

        viewModel.nameError.test {
            assertEquals(MARKET_ITEM_NAME_ALREADY_EXIST_ERROR, awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenItemNameAlreadyExistWithId() = runTest {
        val item = repository.createTestItem()
        viewModel.setItemId(item.itemId)
        viewModel.onEvent(AddEditMarketItemEvent.ItemNameChanged(item.itemName))

        viewModel.nameError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenItemNameIsNotEmpty() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemNameChanged("Test"))

        viewModel.nameError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun priceError_updated_whenItemPriceIsEmpty() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemPriceChanged(""))

        viewModel.priceError.test {
            assertEquals(MARKET_ITEM_PRICE_LESS_THAN_FIVE_ERROR, awaitItem())
        }
    }

    @Test
    fun priceError_updated_whenItemPriceIsLessThanFive() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemPriceChanged("4"))

        viewModel.priceError.test {
            assertEquals(MARKET_ITEM_PRICE_LESS_THAN_FIVE_ERROR, awaitItem())
        }
    }

    @Test
    fun priceError_updated_whenItemPriceContainsLetter() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemPriceChanged("10a"))

        viewModel.priceError.test {
            assertEquals(MARKET_ITEM_PRICE_INVALID, awaitItem())
        }
    }

    @Test
    fun priceError_updated_whenItemPriceIsNotEmpty() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemPriceChanged("10"))

        viewModel.priceError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun unitError_updated_whenItemMeasureUnitIsEmpty() = runTest {
        viewModel.onEvent(AddEditMarketItemEvent.ItemMeasureUnitChanged(MeasureUnit()))

        viewModel.unitError.test {
            assertEquals(MARKET_ITEM_MEASURE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun unitError_updated_whenItemMeasureUnitIsNotEmpty() = runTest {
        viewModel.onEvent(
            AddEditMarketItemEvent.ItemMeasureUnitChanged(
                MeasureUnit(unitId = 1, unitName = "Test"),
            ),
        )

        viewModel.unitError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun unitError_updated_whenItemMeasureUnitNameIsNotEmpty() = runTest {
        viewModel.onEvent(
            AddEditMarketItemEvent.ItemMeasureUnitNameChanged("Test"),
        )

        viewModel.unitError.test {
            assertEquals(MARKET_ITEM_MEASURE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun onEvent_itemTypeChanged_shouldUpdateState() {
        val marketType = MarketTypeIdAndName(typeId = 1, typeName = "Test")
        viewModel.onEvent(AddEditMarketItemEvent.ItemTypeChanged(marketType))

        assertEquals(marketType, viewModel.state.marketType)
    }

    @Test
    fun onEvent_itemNameChanged_shouldUpdateState() {
        val name = "Test"
        viewModel.onEvent(AddEditMarketItemEvent.ItemNameChanged(name))

        assertEquals(name, viewModel.state.itemName)
    }

    @Test
    fun onEvent_itemMeasureUnitChanged_shouldUpdateState() {
        val unit = MeasureUnit(unitId = 1, unitName = "Test")
        viewModel.onEvent(AddEditMarketItemEvent.ItemMeasureUnitChanged(unit))

        assertEquals(unit, viewModel.state.itemMeasureUnit)
    }

    @Test
    fun onEvent_itemMeasureUnitNameChanged_shouldUpdateState() {
        val unitName = "Test"
        viewModel.onEvent(AddEditMarketItemEvent.ItemMeasureUnitNameChanged(unitName))

        assertEquals(unitName.lowercase(), viewModel.state.itemMeasureUnit.unitName)
    }

    @Test
    fun onEvent_itemDescriptionChanged_shouldUpdateState() {
        val description = "Test"
        viewModel.onEvent(AddEditMarketItemEvent.ItemDescriptionChanged(description))

        assertEquals(description, viewModel.state.itemDesc)
    }

    @Test
    fun onEvent_itemPriceChanged_shouldUpdateState() {
        val price = "10"
        viewModel.onEvent(AddEditMarketItemEvent.ItemPriceChanged(price))

        assertEquals(price, viewModel.state.itemPrice)
    }

    @Test
    fun onEvent_AddOrUpdateItem_createNewItem() = runTest {
        val marketType = MarketTypeIdAndName(typeId = 1, typeName = "Test")
        val name = "Test"
        val unit = MeasureUnit(unitId = 1, unitName = "Test")
        val description = "Test"
        val price = "10"

        viewModel.onEvent(AddEditMarketItemEvent.ItemTypeChanged(marketType))
        viewModel.onEvent(AddEditMarketItemEvent.ItemNameChanged(name))
        viewModel.onEvent(AddEditMarketItemEvent.ItemMeasureUnitChanged(unit))
        viewModel.onEvent(AddEditMarketItemEvent.ItemDescriptionChanged(description))
        viewModel.onEvent(AddEditMarketItemEvent.ItemPriceChanged(price))
        viewModel.onEvent(AddEditMarketItemEvent.AddOrUpdateItem)

        viewModel.eventFlow.test {
            assertEquals(
                UiEvent.OnSuccess("Item has been successfully created"),
                awaitItem(),
            )
        }

        val item = repository.getMarketItemById(0)
        assertNotNull(item)
        assertNotNull(item.data)
        assertEquals(marketType, item.data!!.itemType)
        assertEquals(name, item.data!!.itemName)
        assertEquals(unit, item.data!!.itemMeasureUnit)
        assertEquals(description, item.data!!.itemDescription)
        assertEquals(price, item.data!!.itemPrice)
    }

    @Test
    fun onEvent_AddOrUpdateItemWithValidId_shouldUpdateItem() = runTest {
        viewModel.setItemId(1)

        val marketType = MarketTypeIdAndName(typeId = 1, typeName = "Test")
        val name = "Test"
        val unit = MeasureUnit(unitId = 1, unitName = "Test")
        val description = "Test"
        val price = "10"

        viewModel.onEvent(AddEditMarketItemEvent.ItemTypeChanged(marketType))
        viewModel.onEvent(AddEditMarketItemEvent.ItemNameChanged(name))
        viewModel.onEvent(AddEditMarketItemEvent.ItemMeasureUnitChanged(unit))
        viewModel.onEvent(AddEditMarketItemEvent.ItemDescriptionChanged(description))
        viewModel.onEvent(AddEditMarketItemEvent.ItemPriceChanged(price))
        viewModel.onEvent(AddEditMarketItemEvent.AddOrUpdateItem)

        val item = repository.getMarketItemById(1)
        assertNotNull(item)
        assertNotNull(item.data)
        assertEquals(marketType, item.data!!.itemType)
        assertEquals(name, item.data!!.itemName)
        assertEquals(unit, item.data!!.itemMeasureUnit)
        assertEquals(description, item.data!!.itemDescription)
        assertEquals(price, item.data!!.itemPrice)

        val updatedType = MarketTypeIdAndName(typeId = 1, typeName = "Test")
        val updatedName = "Updated"
        val updatedUnit = MeasureUnit(unitId = 2, unitName = "Updated")
        val updatedDescription = "Updated"
        val updatedPrice = "20"

        viewModel.onEvent(AddEditMarketItemEvent.ItemTypeChanged(updatedType))
        viewModel.onEvent(AddEditMarketItemEvent.ItemNameChanged(updatedName))
        viewModel.onEvent(AddEditMarketItemEvent.ItemMeasureUnitChanged(updatedUnit))
        viewModel.onEvent(AddEditMarketItemEvent.ItemDescriptionChanged(updatedDescription))
        viewModel.onEvent(AddEditMarketItemEvent.ItemPriceChanged(updatedPrice))
        viewModel.onEvent(AddEditMarketItemEvent.AddOrUpdateItem)

        viewModel.eventFlow.test {
            assertEquals(
                UiEvent.OnSuccess("Item has been successfully updated"),
                awaitItem(),
            )
        }

        val updatedItem = repository.getMarketItemById(1)
        assertNotNull(updatedItem)
        assertNotNull(updatedItem.data)
        assertEquals(updatedType, updatedItem.data!!.itemType)
        assertEquals(updatedName, updatedItem.data!!.itemName)
        assertEquals(updatedUnit, updatedItem.data!!.itemMeasureUnit)
        assertEquals(updatedDescription, updatedItem.data!!.itemDescription)
        assertEquals(updatedPrice, updatedItem.data!!.itemPrice)
    }
}
