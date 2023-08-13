package com.niyaj.feature.reports.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.CategoryWiseReport
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.ProductWiseReport
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon

@Composable
fun CustomerReportCard(
    modifier: Modifier = Modifier,
    customerReport: CustomerWiseReport,
    onClickCustomer: (Int) -> Unit,
) {
    customerReport.customer.let { customer ->
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClickCustomer(customer.customerId) }
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                customer.customerName?.let { it ->
                    TextWithIcon(
                        text = it,
                        icon = Icons.Default.Person,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(SpaceMini))
                }

                TextWithIcon(
                    text = customer.customerPhone,
                    icon = Icons.Default.PhoneAndroid,
                    fontWeight = FontWeight.SemiBold,
                )

                customer.customerEmail?.let { email ->
                    Spacer(modifier = Modifier.height(SpaceMini))
                    TextWithIcon(
                        text = email,
                        icon = Icons.Default.Email,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceMini))

            Text(
                text = customerReport.orderQty.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
fun AddressReportCard(
    report: AddressWiseReport,
    onAddressClick: (Int) -> Unit,
) {
    report.address.let { address ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddressClick(address.addressId) }
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = address.addressName,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(2F)
            )

            Text(
                text = address.shortName,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.5F)
            )

            Text(
                text = report.orderQty.toString(),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(0.5F)
            )
        }
    }
}

@Composable
fun ProductReportCard(
    report: ProductWiseReport,
    onProductClick: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onProductClick(report.productId)
            }
            .padding(SpaceSmall),
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
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(0.5F)
        )
    }
}


@Composable
fun CategoryWiseReportCard(
    report: List<CategoryWiseReport>,
    selectedCategory: String,
    onExpandChanged: (String) -> Unit,
    onProductClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        report.forEachIndexed { index, (category, products) ->
            val totalQty = products.sumOf { it.quantity }
            if (products.isNotEmpty()) {
                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth(),
                    expanded = category.categoryName == selectedCategory,
                    onExpandChanged = {
                        onExpandChanged(category.categoryName)
                    },
                    title = {
                        TextWithIcon(
                            text = category.categoryName,
                            icon = Icons.Default.Category,
                            isTitle = true
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
                                onExpandChanged(category.categoryName)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand More",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    content = {
                        Column {
                            products.forEachIndexed { index, productWithQty ->
                                ProductReportCard(
                                    report = productWithQty,
                                    onProductClick = onProductClick
                                )

                                if (index != products.size - 1) {
                                    Spacer(modifier = Modifier.height(SpaceMini))
                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(SpaceMini))
                                }
                            }
                        }
                    }
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