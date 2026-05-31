package friends.mobile.designkit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import friends.mobile.designkit.theme.DesignColors
import friends.mobile.designkit.theme.DesignTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, style = DesignTypography.body) },
        modifier = modifier
            .background(DesignColors.textField, RoundedCornerShape(12.dp))
            .border(0.dp, Color.Transparent),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = DesignColors.primary.copy(alpha = 0.5f),
            focusedLabelColor = DesignColors.primary
        ),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        textStyle = DesignTypography.body
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormSecureField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, style = DesignTypography.body) },
        modifier = modifier
            .background(DesignColors.textField, RoundedCornerShape(12.dp))
            .border(0.dp, Color.Transparent),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = DesignColors.primary.copy(alpha = 0.5f),
            focusedLabelColor = DesignColors.primary
        ),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        textStyle = DesignTypography.body
    )
}
