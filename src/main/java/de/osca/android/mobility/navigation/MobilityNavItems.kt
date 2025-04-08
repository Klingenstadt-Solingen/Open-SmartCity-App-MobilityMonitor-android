package de.osca.android.mobility.navigation

import androidx.navigation.navDeepLink
import de.osca.android.essentials.domain.entity.navigation.NavigationItem
import de.osca.android.mobility.R
import de.osca.android.mobility.navigation.MobilityNavItems.MobilityDashboardNavItem.icon
import de.osca.android.mobility.navigation.MobilityNavItems.MobilityDashboardNavItem.route
import de.osca.android.mobility.navigation.MobilityNavItems.MobilityDashboardNavItem.title

/**
 * Navigation Routes for Mobility
 */
sealed class MobilityNavItems {
    /**
     * Route for the default/main screen
     * @property title title of the route (a name to display)
     * @property route route for this navItem (name is irrelevant)
     * @property icon the icon to display
     */
    object MobilityDashboardNavItem : NavigationItem(
        title = R.string.mobility_title,
        route = "mobility_form",
        icon = R.drawable.ic_circle,
        deepLinks = listOf(navDeepLink { uriPattern = "solingen://mobilitymonitor" }),
    )
}
