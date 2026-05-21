package friends.mobile.designkit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

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
        placeholder = { Text(placeholder, style = DesignTheme.Typography.body) },
        modifier = modifier
            .background(DesignTheme.Colors.textField, RoundedCornerShape(DesignTheme.CornerRadius.medium))
            .border(0.dp, Color.Transparent),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = DesignTheme.Colors.primary.copy(alpha = 0.5f),
            focusedLabelColor = DesignTheme.Colors.primary
        ),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        textStyle = DesignTheme.Typography.body
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
        placeholder = { Text(placeholder, style = DesignTheme.Typography.body) },
        modifier = modifier
            .background(DesignTheme.Colors.textField, RoundedCornerShape(DesignTheme.CornerRadius.medium))
            .border(0.dp, Color.Transparent),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = DesignTheme.Colors.primary.copy(alpha = 0.5f),
            focusedLabelColor = DesignTheme.Colors.primary
        ),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        textStyle = DesignTheme.Typography.body
    )
}
