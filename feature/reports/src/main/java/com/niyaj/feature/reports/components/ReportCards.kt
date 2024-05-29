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

package com.niyaj.feature.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_TAG
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.CategoryWiseReport
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.ExpensesReport
import com.niyaj.model.ProductWiseReport
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardExpandable

@Composable
fun CustomerReportCard(
    modifier: Modifier = Modifier,
    customerReport: CustomerWiseReport,
    onClickCustomer: (Int) -> Unit,
) = trace("CustomerReportCard") {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickCustomer(customerReport.customerId) }
            .padding(SpaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
        ) {
            customerReport.customerName?.let {
                IconWithText(
                    text = it,
                    icon = PoposIcons.Person,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(SpaceMini))
            }

            IconWithText(
                text = customerReport.customerPhone,
                icon = PoposIcons.PhoneAndroid,
                fontWeight = FontWeight.SemiBold,
            )

            customerReport.customerEmail?.let { email ->
                Spacer(modifier = Modifier.height(SpaceMini))
                IconWithText(
                    text = email,
                    icon = PoposIcons.Email,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = customerReport.totalSales.toRupee,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = customerReport.totalOrders.toString(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
fun AddressReportCard(
    modifier: Modifier = Modifier,
    report: AddressWiseReport,
    onAddressClick: (Int) -> Unit,
) = trace("AddressReportCard") {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onAddressClick(report.addressId) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconWithText(
                text = report.addressName,
                icon = PoposIcons.LocationOn,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1F),
            )

            Text(
                text = report.shortName,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.5F),
            )

            Text(
                text = report.totalSales.toRupee,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.5F),
            )

            Text(
                text = report.totalOrders.toString(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(0.5F),
            )
        }
    }
}

@Composable
fun ProductReportCard(
    report: ProductWiseReport,
    onProductClick: (Int) -> Unit,
) = trace("ProductReportCard") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onProductClick(report.productId)
            }
            .padding(horizontal = SpaceSmall, vertical = SpaceSmallMax),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = report.productName,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            text = report.quantity.toString(),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(0.5F),
        )
    }
}

@Composable
fun CategoryWiseReportCard(
    report: List<CategoryWiseReport>,
    selectedCategory: String,
    onExpandChanged: (String) -> Unit,
    onProductClick: (Int) -> Unit,
) = trace("CategoryWiseReportCard") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        report.forEachIndexed { index, (categoryName, products) ->
            val totalQty = products.sumOf { it.quantity }
            if (products.isNotEmpty()) {
                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth(),
                    expanded = categoryName == selectedCategory,
                    onExpandChanged = {
                        onExpandChanged(categoryName)
                    },
                    title = {
                        IconWithText(
                            text = categoryName,
                            icon = PoposIcons.Category,
                            isTitle = true,
                        )
                    },
                    trailing = {
                        CountBox(count = totalQty.toString())
                    },
                    rowClickable = true,
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                onExpandChanged(categoryName)
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.ArrowDown,
                                contentDescription = "Expand More",
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        ) {
                            products.forEachIndexed { index, productWithQty ->
                                ProductReportCard(
                                    report = productWithQty,
                                    onProductClick = onProductClick,
                                )

                                if (index != products.size - 1) {
                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    },
                )

                if (index != report.size - 1) {
                    Spacer(modifier = Modifier.height(SpaceMini))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(SpaceMini))
                }
            }
        }
    }
}

@Composable
fun ExpensesReportCard(
    modifier: Modifier = Modifier,
    report: ExpensesReport,
    onClickEnable: Boolean = false,
    onExpenseClick: (Int) -> Unit = {},
) = trace("ExpensesReportCard") {
    Row(
        modifier = modifier
            .testTag(EXPENSE_TAG.plus(report.expenseId))
            .fillMaxWidth()
            .clickable(onClickEnable) { onExpenseClick(report.expenseId) }
            .padding(SpaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconWithText(
            text = report.expenseName,
            icon = PoposIcons.Person,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            text = report.expenseAmount.toRupee,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}
