package com.example.waterbug.components



import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 16.dp, // Vertical offset
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = RoundedCornerShape(12.dp), // Rounded corners
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier.padding(bottom = bottomPadding),
        snackbar = { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = backgroundColor,
                contentColor = contentColor,
                shape = shape,
                actionColor = contentColor
            )
        }
    )
}