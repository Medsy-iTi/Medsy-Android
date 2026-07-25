package com.medsy.medsy.nav.nestednavigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.presentation.aichat.components.MedsyAiNavIcon

@Composable
fun BottomNavigationButton(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    @StringRes label: Int,
    prominent: Boolean = false,
) {
    ShortNavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            if (prominent) {
                MedsyAiNavIcon(
                    selected = selected,
                    modifier = Modifier
                        .offset(y = (-12).dp),
                )
            } else {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                )
            }
        },
        label = {
            Text(
                text = stringResource(label),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                modifier = if (prominent) Modifier.offset(y = (-6).dp) else Modifier,
            )
        },
        modifier = modifier,
        colors = ShortNavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onBackground,
            unselectedTextColor = MaterialTheme.colorScheme.onBackground,
            selectedIndicatorColor = Color.Transparent,
        ),
    )
}
