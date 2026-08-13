package com.medsy.presentation.cart.cartrequest.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EditLocationAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.location.MedsyLocationPreview
import com.medsy.presentation.R
import com.medsy.presentation.cart.cartrequest.CartRequestAddressOption
import com.medsy.presentation.cart.cartrequest.CartRequestState

@Composable
internal fun DeliveryAddressSection(
    state: CartRequestState,
    onAddressOptionSelected: (CartRequestAddressOption) -> Unit,
    onCustomAddressChanged: (String) -> Unit,
    onChooseLocation: () -> Unit,
    onRetryProfile: () -> Unit,
) {
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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.customAddress,
                    onValueChange = onCustomAddressChanged,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isResolvingAddress,
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
                    trailingIcon = if (state.isResolvingAddress) {
                        {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                            )
                        }
                    } else {
                        null
                    },
                )
                MedsyLocationPreview(
                    latitude = state.customLatitude,
                    longitude = state.customLongitude,
                )
                Button(
                    onClick = onChooseLocation,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isResolvingAddress,
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
