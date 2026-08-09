package com.medsy.presentation.cart.cartrequest.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.presentation.R

@Composable
internal fun FulfillmentSection(
    selected: DeliveryMethod,
    onSelected: (DeliveryMethod) -> Unit,
) {
    CartRequestSection(
        icon = Icons.Outlined.LocalShipping,
        title = stringResource(R.string.cart_request_fulfillment_title),
        description = stringResource(R.string.cart_request_fulfillment_description),
    ) {
        ChoiceRow {
            CartRequestChoiceCard(
                selected = selected == DeliveryMethod.DELIVERY,
                icon = Icons.Outlined.LocalShipping,
                title = stringResource(R.string.cart_request_delivery),
                description = stringResource(R.string.cart_request_delivery_description),
                onClick = { onSelected(DeliveryMethod.DELIVERY) },
                modifier = Modifier.weight(1f),
            )
            CartRequestChoiceCard(
                selected = selected == DeliveryMethod.PICKUP,
                icon = Icons.Outlined.Storefront,
                title = stringResource(R.string.cart_request_pickup),
                description = stringResource(R.string.cart_request_pickup_description),
                onClick = { onSelected(DeliveryMethod.PICKUP) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
