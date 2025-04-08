package de.osca.android.mobility.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import de.osca.android.essentials.presentation.component.design.BaseCardContainer
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs
import de.osca.android.mobility.entity.TransportData
import de.osca.android.mobility.entity.TransportOption

@OptIn(ExperimentalCoilApi::class)
@Composable
fun MobilityElement(
    masterDesignArgs: MasterDesignArgs,
    moduleDesignArgs: ModuleDesignArgs,
    transportData: TransportData,
    onRowWasClicked: ((option: TransportOption) -> Unit)? = null
) {
    if(transportData.availableOptions.isNotEmpty()) {
        Box(
            modifier = Modifier
                .padding(top = 30.dp)
        ) {
            BaseCardContainer(
                masterDesignArgs = masterDesignArgs,
                moduleDesignArgs = moduleDesignArgs
            ) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = transportData.getNameFromType(LocalContext.current),
                            style = masterDesignArgs.overlineTextStyle,
                            color = moduleDesignArgs.mCardTextColor
                                ?: masterDesignArgs.mCardTextColor,
                            modifier = Modifier
                                .offset(x = 90.dp)
                        )

                        if (transportData.stop != null) {
                            Text(
                                text = "${transportData.stop.distance.toInt()}m",
                                style = masterDesignArgs.overlineTextStyle,
                                color = moduleDesignArgs.mCardTextColor
                                    ?: masterDesignArgs.mCardTextColor,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Column {
                        transportData.availableOptions.forEach {
                            TransportRow(
                                masterDesignArgs = masterDesignArgs,
                                moduleDesignArgs = moduleDesignArgs,
                                transportOption = it,
                                onRowClick = {
                                    if (onRowWasClicked != null) {
                                        onRowWasClicked(it)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = 16.dp, y = (-16).dp)
                    .background(
                        moduleDesignArgs.mCardBackColor ?: masterDesignArgs.mCardBackColor,
                        RoundedCornerShape(
                            moduleDesignArgs.mShapeCard ?: masterDesignArgs.mShapeCard
                        )
                    )
            ) {
                Image(
                    painter = rememberAsyncImagePainter(transportData.iconUrl),
                    contentDescription = "testImage",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(75.dp)
                        .height(75.dp)
                        .clip(
                            RoundedCornerShape(
                                moduleDesignArgs.mShapeCard ?: masterDesignArgs.mShapeCard
                            )
                        )
                )
            }
        }
    }
}