package com.medsy.presentation.auth.login.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.BorderGray
import com.medsy.designsystem.ui.theme.NeutralWhite
import com.medsy.designsystem.ui.theme.SecondaryText
import com.medsy.presentation.auth.login.LoginConstants

@Composable
fun LoginSocialButton(
    iconResId: Int,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(LoginConstants.SocialButtonHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(LoginConstants.SocialButtonCornerRadius),
        border = BorderStroke(1.dp, BorderGray),
        color = NeutralWhite
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(LoginConstants.IconSize)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = text,
                modifier = Modifier.width(LoginConstants.SocialButtonTextWidth), 
                textAlign = TextAlign.Start,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
