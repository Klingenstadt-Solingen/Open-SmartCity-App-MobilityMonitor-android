package de.osca.android.mobility.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import de.osca.android.essentials.R
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs
import de.osca.android.mobility.entity.TransportOption
import java.text.SimpleDateFormat
import java.util.TimeZone

@OptIn(ExperimentalCoilApi::class)
@Composable
fun TransportRow(
    masterDesignArgs: MasterDesignArgs,
    moduleDesignArgs: ModuleDesignArgs,
    transportOption: TransportOption,
    onRowClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = if(onRowClick != null) Modifier
            .fillMaxWidth()
            .clickable {
                onRowClick()
            }
            .padding(vertical = 8.dp)
        else
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = if(transportOption.iconUrl.isEmpty())
                    Modifier
                        .heightIn(20.dp)
                        .background(
                            Color.Red,
                            RoundedCornerShape(5.dp)
                        )
                else
                    Modifier
            ) {
                if(transportOption.iconUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(transportOption.iconUrl),
                        contentDescription = "testImage",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .width(50.dp)
                            .height(20.dp)
                    )
                } else {
                    Text(
                        text = transportOption.shortName,
                        style = masterDesignArgs.bodyTextStyle,
                        color = moduleDesignArgs.mDialogsTextColor
                            ?: masterDesignArgs.mDialogsTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(50.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = transportOption.name,
                style = masterDesignArgs.bodyTextStyle,
                color = moduleDesignArgs.mCardTextColor ?: masterDesignArgs.mCardTextColor
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // show low battery
            // show battery image
            if(transportOption.energyLevel >= 0f) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.ic_battery_base),
                        contentDescription = "batteryOuter",
                        colorFilter = ColorFilter.tint(moduleDesignArgs.mCardTextColor ?: masterDesignArgs.mCardTextColor),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp)
                    )

                    Image(
                        painter = if (transportOption.energyLevel < .35f)
                            painterResource(id = R.drawable.ic_battery_low)
                        else if (transportOption.energyLevel > .75f)
                            painterResource(id = R.drawable.ic_battery_full)
                        else
                            painterResource(id = R.drawable.ic_battery_middle),
                        contentDescription = "batteryInner",
                        colorFilter = ColorFilter.tint(if (transportOption.energyLevel < .35f) Color.Red else if (transportOption.energyLevel > .75f) Color.Green else Color.Yellow),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp)
                    )
                }
            }

            var rightText = ""
            // show walking or "in"
            if(transportOption.iconUrl.isNotEmpty() || transportOption.energyLevel >= 0f) {
                rightText = "${getWalkTime(transportOption.distance.toInt())}min"
            } else {
                if(transportOption.departureTimeEstimated != null || transportOption.departureTimePlanned != null) {
                    val minutesUntilDeparture = transportOption.getMinutesToDeparture()
                    rightText = if(minutesUntilDeparture <= 0L) {
                        "jetzt"
                    } else if (minutesUntilDeparture <= 15L) {
                        "in $minutesUntilDeparture min"
                    } else {
                        "um ${getFormattedDateTime(transportOption.departureTimeDisplay)}"
                    }
                }
            }
            Text(
                text = rightText,
                modifier = Modifier.width(70.dp),
                textAlign = TextAlign.Right,
                style = masterDesignArgs.bodyTextStyle,
                color = moduleDesignArgs.mCardTextColor
                    ?: masterDesignArgs.mCardTextColor
            )
        }
    }

    Divider(color = moduleDesignArgs.mHintTextColor ?: masterDesignArgs.mHintTextColor)
}

fun getFormattedDateTime(unformatted: String?): String {
    if(unformatted != null) {
        val utcFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        utcFormatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormatter.parse(unformatted)

        val localFormatter = SimpleDateFormat("HH:mm")
        localFormatter.timeZone = TimeZone.getDefault()
        return if (date != null) {
            localFormatter.format(date)
        } else {
            "---"
        }
    }

    return "---"
}

fun getWalkTime(distanceInMeter: Int): Int {
    return distanceInMeter / 100
}
