package com.niyaj.feature.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun ViewLastSevenDaysReports(
    navController: NavController,
    reportsViewModel: ReportsViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyListState()
    val reportBarData = reportsViewModel.reportsBarData.collectAsState().value.reportBarData
    val reportBarIsLoading = reportsViewModel.reportsBarData.collectAsState().value.isLoading
    val reportBarError = reportsViewModel.reportsBarData.collectAsState().value.error

    TrackScreenViewEvent(screenName = "ViewLastSevenDaysReports")
    
    StandardScaffoldNew(
        navController = navController,
        title = "Last 7 Days Reports",
        showBottomBar = false,
        showBackButton = true,
        floatingActionButton = {},
        navActions = {},
    ) {
        if(reportBarIsLoading){
            LoadingIndicator()
        } else if(reportBarError != null){
            ItemNotAvailable(text = reportBarError)
        } else {
            TrackScrollJank(scrollableState = lazyListState, stateName = "Previous Day Reports::List")

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall),
                state = lazyListState,
            ) {
                items(reportBarData){ report ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(SpaceSmall),
                        elevation = CardDefaults.cardElevation(2.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column{
                                Text(
                                    text = report.yValue.toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Text(
                                    text = report.xValue.toString().substringBefore(".").toRupee,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }

                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                    contentDescription = "View Details",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
    }
}