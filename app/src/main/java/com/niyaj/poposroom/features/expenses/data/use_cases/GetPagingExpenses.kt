package com.niyaj.poposroom.features.expenses.data.use_cases

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.niyaj.poposroom.features.expenses.domain.model.Expense
import com.niyaj.poposroom.features.expenses.domain.repository.ExpenseRepository

class GetPagingExpenses(
    private val expenseRepository: ExpenseRepository,
    private val searchText: String,
): PagingSource<Int, Expense>() {
    override fun getRefreshKey(state: PagingState<Int, Expense>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Expense> =
        try {
            val page = params.key ?: 0
            val size = params.loadSize
            val from = page * size
            val data = expenseRepository.getAllPagingExpenses(searchText = searchText, limit = size, offset = from)

            if (params.placeholdersEnabled) {
                val itemsAfter = data.count() - from + data.size
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (data.isEmpty()) null else page + 1,
                    itemsAfter = if (itemsAfter > size) size else itemsAfter,
                    itemsBefore = from
                )
            } else {
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (data.isEmpty()) null else page + 1
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
}