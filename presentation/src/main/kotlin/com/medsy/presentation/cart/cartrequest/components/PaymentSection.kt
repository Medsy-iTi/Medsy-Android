package com.medsy.presentation.cart.cartrequest.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.medsy.domain.cart.model.PaymentOption
import com.medsy.presentation.R

@Composable
internal fun PaymentSection(
    selected: PaymentOption,
    onSelected: (PaymentOption) -> Unit,
) {
    CartRequestSection(
        icon = Icons.Outlined.Payments,
        title = stringResource(R.string.cart_request_payment_title),
        description = stringResource(R.string.cart_request_payment_description),
    ) {
        ChoiceRow {
            CartRequestChoiceCard(
                selected = selected == PaymentOption.CASH,
                icon = Icons.Outlined.Payments,
                title = stringResource(R.string.cart_request_cash),
                description = stringResource(R.string.cart_request_cash_description),
                onClick = { onSelected(PaymentOption.CASH) },
                modifier = Modifier.weight(1f),
            )
            CartRequestChoiceCard(
                selected = selected == PaymentOption.VISA,
                icon = Icons.Outlined.CreditCard,
                title = stringResource(R.string.cart_request_visa),
                description = stringResource(R.string.cart_request_visa_description),
                onClick = { onSelected(PaymentOption.VISA) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
