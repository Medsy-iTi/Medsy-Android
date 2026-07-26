package com.medsy.presentation.cart.cartrequest.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.EditLocationAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.location.MedsyLocationPreview
import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.domain.cart.model.PaymentOption
import com.medsy.presentation.R
import com.medsy.presentation.cart.cartrequest.CartRequestAddressOption
import com.medsy.presentation.cart.cartrequest.CartRequestState
import com.medsy.presentation.cart.components.cartPriceText

@Composable
internal fun CartRequestOrderSummary(
    state: CartRequestState,
    onRetry: () -> Unit,
) {
    CartRequestSection(
        icon = Icons.Outlined.ReceiptLong,
        title = stringResource(R.string.cart_request_order_summary),
    ) {
        if (state.isCartLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 12.dp)
                    .size(28.dp),
                strokeWidth = 2.dp,
            )
        } else if (state.cartErrorMessageRes != null) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(state.cartErrorMessageRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                TextButton(onClick = onRetry) {
                    Text(text = stringResource(R.string.cart_request_retry))
                }
            }
        } else {
            val medicineCount = state.items.sumOf { it.quantity }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = pluralStringResource(
                        R.plurals.cart_request_item_count,
                        medicineCount,
                        medicineCount,
                    ),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = cartPriceText(state.totalPriceEgp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (state.draft.prescriptionImage != null) {
                StatusPill(
                    label = stringResource(R.string.cart_request_prescription_attached),
                )
            }
            if (!state.hasProducts) {
                Text(
                    text = stringResource(R.string.cart_request_empty_request),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

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

@Composable
internal fun DeliveryAddressSection(
    state: CartRequestState,
    onAddressOptionSelected: (CartRequestAddressOption) -> Unit,
    onCustomAddressChanged: (String) -> Unit,
    onChooseLocation: () -> Unit,
    onRetryProfile: () -> Unit,
) {
    AnimatedVisibility(visible = state.deliveryMethod == DeliveryMethod.DELIVERY) {
        CartRequestSection(
            icon = Icons.Outlined.Home,
            title = stringResource(R.string.cart_request_address_title),
            modifier = Modifier.animateContentSize(),
        ) {
            when {
                state.isProfileLoading -> CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp)
                        .size(28.dp),
                    strokeWidth = 2.dp,
                )

                state.profileErrorMessageRes != null -> {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.cart_request_profile_address_unavailable
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = stringResource(state.profileErrorMessageRes),
                                style = MaterialTheme.typography.bodySmall,
                            )
                            TextButton(onClick = onRetryProfile) {
                                Text(text = stringResource(R.string.cart_request_retry))
                            }
                        }
                    }
                }

                !state.hasDefaultAddress -> {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.cart_request_no_default_address),
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            if (state.hasDefaultAddress) {
                AddressOptionCard(
                    selected = state.addressOption == CartRequestAddressOption.DEFAULT,
                    title = stringResource(R.string.cart_request_default_address),
                    badge = stringResource(R.string.cart_request_default_badge),
                    address = requireNotNull(state.defaultAddress),
                    onClick = {
                        onAddressOptionSelected(CartRequestAddressOption.DEFAULT)
                    },
                ) {
                    MedsyLocationPreview(
                        latitude = state.defaultLatitude,
                        longitude = state.defaultLongitude,
                    )
                }
            }

            AddressOptionCard(
                selected = state.addressOption == CartRequestAddressOption.CUSTOM,
                title = stringResource(R.string.cart_request_other_address),
                onClick = { onAddressOptionSelected(CartRequestAddressOption.CUSTOM) },
            ) {
                AnimatedVisibility(
                    visible = state.addressOption == CartRequestAddressOption.CUSTOM,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = state.customAddress,
                            onValueChange = onCustomAddressChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.cart_request_address_label)) },
                            placeholder = {
                                Text(stringResource(R.string.cart_request_address_hint))
                            },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            ),
                        )
                        MedsyLocationPreview(
                            latitude = state.customLatitude,
                            longitude = state.customLongitude,
                        )
                        Button(
                            onClick = onChooseLocation,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EditLocationAlt,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(
                                    if (state.hasConfirmedCustomLocation) {
                                        R.string.cart_request_change_location
                                    } else {
                                        R.string.cart_request_choose_location
                                    }
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (state.hasConfirmedCustomLocation) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                            Text(
                                text = stringResource(
                                    if (state.hasConfirmedCustomLocation) {
                                        R.string.cart_request_location_confirmed
                                    } else {
                                        R.string.cart_request_location_required
                                    }
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

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

@Composable
private fun CartRequestSection(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (description != null) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            content()
        }
    }
}

@Composable
private fun ChoiceRow(
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}

@Composable
private fun CartRequestChoiceCard(
    selected: Boolean,
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.selectable(
            selected = selected,
            onClick = onClick,
            role = Role.RadioButton,
        ),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
                Spacer(modifier = Modifier.weight(1f))
                RadioButton(selected = selected, onClick = null)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AddressOptionCard(
    selected: Boolean,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    address: String? = null,
    content: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            ),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selected, onClick = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                if (badge != null) StatusPill(label = badge)
            }
            if (address != null) {
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            content?.invoke()
        }
    }
}

@Composable
private fun StatusPill(
    label: String,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
