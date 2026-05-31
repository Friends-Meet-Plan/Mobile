package friends.mobile.designsystem.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import friends.mobile.designsystem.theme.DesignTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val surfaceBg = MaterialTheme.colorScheme.surfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, style = DesignTheme.Typography.body) },
        modifier = modifier
            .background(surfaceBg, RoundedCornerShape(12.dp))
            .border(0.dp, Color.Transparent),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedLabelColor = primary
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
    val surfaceBg = MaterialTheme.colorScheme.surfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, style = DesignTheme.Typography.body) },
        modifier = modifier
            .background(surfaceBg, RoundedCornerShape(12.dp))
            .border(0.dp, Color.Transparent),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedLabelColor = primary
        ),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        textStyle = DesignTheme.Typography.body
    )
}
