package com.example.waterbug.qrscanner

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.ui.theme.WaterbugTheme
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(navController: NavController, onResult: (String) -> Unit) {
    var showModal by remember { mutableStateOf(false) }
    var cameraGranted by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val barcodeView = remember { com.journeyapps.barcodescanner.BarcodeView(context) }

    // Handle lifecycle events using DisposableEffect
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> barcodeView.pause()
                Lifecycle.Event.ON_RESUME -> barcodeView.resume()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            barcodeView.pause()
        }
    }

    // QR Scanner Layout
    Box(modifier = Modifier.fillMaxSize()) {

        RequestCameraPermission({
            Log.d("PERMISSIONS", "Camera permisions granted")
            cameraGranted = true
        })

        if (cameraGranted) {
            // AndroidView for barcode scanner
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    barcodeView.apply {
                        decodeContinuous { result ->
                            // Process scanned QR code
                            if (validateQRCode(result.text)) {
                                onResult(result.text)
//                                navController.popBackStack()
                            }
                        }
                    }
                },
                update = { null }
            )
        } else {
            Text("Camera permissions required")
        }



        // Overlay
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Scan QR Code", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .border(2.dp, MaterialTheme.colorScheme.secondary, shape = MaterialTheme.shapes.medium)
                ) {
                    // Placeholder for viewfinder
                }
            }

            Button(onClick = { showModal = true }) {
                Text("Show my QR code")
            }
        }
    }

    // Bottom sheet for showing QR code
    // Bottom sheet for showing QR code
    if (showModal) {
        ModalBottomSheet(onDismissRequest = { showModal = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("My QR Code", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                val qrBitmap = generateQRCodeBitmap("Your address here") // Replace this text as needed
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Generated QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                } else {
                    Text("Failed to generate QR Code")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showModal = false }) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun RequestCameraPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onPermissionGranted() else {
//            Toast.makeText(context, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
}

// Placeholder for QR code validation logic
fun validateQRCode(data: String): Boolean {
    return true // Replace with actual validation logic
}

@Preview
@Composable
fun PreviewQRScannerScreen() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme {
        Surface {
            QRScannerScreen(rememberNavController(), { null })
        }
    }
}