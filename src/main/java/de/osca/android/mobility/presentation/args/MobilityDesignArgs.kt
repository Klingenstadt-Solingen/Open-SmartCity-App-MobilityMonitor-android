package de.osca.android.mobility.presentation.args

import androidx.compose.ui.unit.Dp
import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs
import de.osca.android.essentials.presentation.component.design.WidgetDesignArgs

interface MobilityDesignArgs : ModuleDesignArgs, WidgetDesignArgs {
    val mapCardHeight: Dp
    val mapStyle: Int?
}