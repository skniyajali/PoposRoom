package com.niyaj.product.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.IconSizeMedium
import com.niyaj.designsystem.theme.ProfilePictureSizeSmall
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Product
import kotlin.random.Random


@Composable
fun ProductItems(
    modifier : Modifier = Modifier,
    lazyListState : LazyListState,
    cartProducts: List<Product>,
    isLoading: Boolean = false,
    onProductLeftClick: (Int) -> Unit = {},
    onProductRightClick: (Int) -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
){
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        state = lazyListState,
    ) {
        itemsIndexed(
            items = cartProducts,
            key = { _, product ->
                product.productId
            }
        ){ index, product ->
            val quantity = Random.nextInt(5)

            ElevatedCard(
                modifier = modifier
                    .fillMaxWidth(),
                shape =  RoundedCornerShape(4.dp),
                colors = CardDefaults.elevatedCardColors(
//                    containerColor = backgroundColor,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable(
                                enabled = quantity != 0
                            ) {
                                onProductLeftClick(product.productId)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(SpaceSmall),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ){
                                Text(
                                    text = product.productName,
                                    style = MaterialTheme.typography.labelMedium,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Text(
                                    text = product.productPrice.toString().toRupee,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }

                            if(quantity != 0) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = null,
                                    modifier = Modifier.size(IconSizeMedium)
                                )
                            }
                        }
                    }

                    Divider(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )

                    Row(
                        modifier = Modifier
                            .clickable {
                                onProductRightClick(product.productId)
                            }
                            .weight(1.5f)
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if(quantity != 0) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(IconSizeMedium)
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }else{
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(IconSizeMedium)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            if(index == cartProducts.size - 1) {
                Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
            }
        }
    }
}


@Composable
fun ProductItems(
    modifier : Modifier = Modifier,
    lazyListState : LazyListState,
    cartProducts: List<Product>,
    onProductLeftClick: (Int) -> Unit = {},
    onProductRightClick: (Int) -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
){
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        state = lazyListState,
    ) {
        itemsIndexed(
            items = cartProducts,
            key = { _, product ->
                product.productId
            }
        ){ index, product ->
            val quantity = Random.nextInt(5)

            ElevatedCard(
                modifier = modifier
                    .fillMaxWidth(),
                shape =  RoundedCornerShape(4.dp),
                colors = CardDefaults.elevatedCardColors(
//                    containerColor = backgroundColor,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable(
                                enabled = quantity != 0
                            ) {
                                onProductLeftClick(product.productId)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(SpaceSmall),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ){
                                Text(
                                    text = product.productName,
                                    style = MaterialTheme.typography.labelSmall,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Text(
                                    text = product.productPrice.toString().toRupee,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black
                                )
                            }

                            if(quantity != 0) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = null,
                                    modifier = Modifier.size(IconSizeMedium)
                                )
                            }
                        }
                    }

                    Divider(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )

                    Row(
                        modifier = Modifier
                            .clickable {
                                onProductRightClick(product.productId)
                            }
                            .weight(1.5f)
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if(quantity != 0) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(IconSizeMedium)
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }else{
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(IconSizeMedium)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            if(index == cartProducts.size - 1) {
                Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
            }
        }
    }
}