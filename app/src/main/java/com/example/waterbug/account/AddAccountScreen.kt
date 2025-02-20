package com.example.waterbug.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.waterbug.R
import com.example.waterbug.appstate.Account
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.ui.theme.WaterbugTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShowMnemonic(mnemonic: String, step: Int, setStep: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Mnemonic Display
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    mnemonic.split(" ").forEachIndexed { index, word ->
                        Box(
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    MaterialTheme.shapes.small
                                )
                                .padding(8.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "${index + 1}.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = word, style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                TextButton(
                    onClick = { /* Add copy to clipboard logic later */ },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Copy to Clipboard")
                }
            }
        }

        // Warning Section
        Column(
            modifier = Modifier.padding(top = 16.dp), horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Important:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "1. Keep your mnemonic safe. Never share it with anyone.",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "2. Losing your mnemonic means losing access to your funds.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Next Button
        Button(
            onClick = { setStep(step + 1) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
        ) {
            Text("Next")
        }
    }
}

@Composable
fun VerifyMnemonic(
    mnemonic: String,
    step: Int,
    setStep: (Int) -> Unit,
    setIsProcessing: (Boolean) -> Unit,
    alias: String,
    onAliasChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    password2: String,
    onPassword2Change: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var password2Visible by remember { mutableStateOf(false) }

    // Split the mnemonic into a list of words
    val mnemonicWords = remember(mnemonic) { mnemonic.split(" ") }

    // Randomly select two unique word indices each time the component re-renders
    val randomIndices = remember(mnemonic) { mnemonicWords.indices.shuffled().take(2) }

    var word1Input by remember { mutableStateOf("") }
    var word2Input by remember { mutableStateOf("") }

    var word1BorderColor by remember { mutableStateOf(Color.Gray) }
    var word2BorderColor by remember { mutableStateOf(Color.Gray) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // First word input
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Word ${randomIndices[0] + 1}:", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = word1Input,
                onValueChange = {
                    word1Input = it
                    word1BorderColor = Color.Gray
                },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, word1BorderColor, MaterialTheme.shapes.small),
                singleLine = true
            )
        }

        // Second word input
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Word ${randomIndices[1] + 1}:", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = word2Input,
                onValueChange = {
                    word2Input = it
                    word2BorderColor = Color.Gray
                },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, word2BorderColor, MaterialTheme.shapes.small),
                singleLine = true
            )
        }

        Text("Account name", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = alias,
            onValueChange = onAliasChange,
            label = { Text("e.g., Trading Account") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Create wallet password", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(value = password,
            onValueChange = onPasswordChange,
            label = { Text("At least 8 characters") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Image(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                        ),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            })

        Text("Confirm wallet password", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(value = password2,
            onValueChange = onPassword2Change,
            label = { Text("Re-enter your password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (password2Visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { password2Visible = !password2Visible }) {
                    Image(
                        painter = painterResource(
                            id = if (password2Visible) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                        ),
                        contentDescription = if (password2Visible) "Hide password" else "Show password",
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            })

        if (password.isNotEmpty() && password.length < 8) {
            Text(
                "Password must be at least 8 characters.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (password.isNotEmpty() && password2.isNotEmpty() && password != password2) {
            Text(
                "Passwords do not match.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        val btnEnabled =
            password.isNotEmpty() && password.length >= 8 && password2.isNotEmpty() && password == password2 && alias.isNotEmpty()


        Button(
            onClick = {
                val word1Correct = word1Input.trim() == mnemonicWords[randomIndices[0]]
                val word2Correct = word2Input.trim() == mnemonicWords[randomIndices[1]]

                if (!word1Correct) word1BorderColor = Color.Yellow
                if (!word2Correct) word2BorderColor = Color.Yellow

                if (word1Correct && word2Correct) {
                    setIsProcessing(true)
                    setStep(step + 1)
                }
            },
            enabled = btnEnabled,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
        ) {
            Text("Create Account")
        }
    }
}

@Composable
fun InitAccount(mnemonic: String, step: Int, setStep: (Int) -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000L)
        setStep(step + 1)
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.create_animation))
    val progress by animateLottieCompositionAsState(
        composition, iterations = LottieConstants.IterateForever, isPlaying = true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(200.dp).border(width = 1.dp, color = MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            // Display the Lottie animation
            LottieAnimation(
                composition = composition, progress = progress, modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Progress bar
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp), color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Initializing account...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SetupComplete(onSave: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 128.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.network),
            contentDescription = "Icon",
            modifier = Modifier
                .size(256.dp),
        )
        Spacer(modifier = Modifier.height(48.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Setup Complete!",
                style = MaterialTheme.typography.bodyLarge)
            Button(onClick = {
                onSave()
            },
                shape = MaterialTheme.shapes.small) {
                Text("Done")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    viewModel: AppViewModel,
    navController: NavController,
    returnRoute: String,
    step: Int = 1,
    snackbarHostState: SnackbarHostState
) {
    var step by remember { mutableIntStateOf(step) }
    var alias by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    val mnemonic = getTestMnemonic()

    BackHandler {
        if (isProcessing) return@BackHandler
        when (step) {
            2 -> step -= 1
            else -> {
                // consider just navController.popBackStack() to avoid needing returnRoute
                navController.navigate(returnRoute) {
                    popUpTo("add_account?returnRoute=$returnRoute") { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    val titleText = when (step) {
        1 -> "Create a new wallet"
        2 -> "Verify your recovery phrase"
        3 -> "Initializing your account"
        else -> ""
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Column {
                Text(titleText)
                if (step <= 3) Text(
                    "Step $step of 3", style = MaterialTheme.typography.labelMedium
                )
            }

        }, navigationIcon = {
            if (isProcessing) {
                null
            } else {
                IconButton(onClick = {
                    if (step > 1) {
                        step -= 1
                    } else {
                        navController.popBackStack()
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back"
                    )
                }
            }
        })
    }) {
        Surface(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            when (step) {
                1 -> ShowMnemonic(mnemonic = mnemonic, step = step, setStep = { step = it })

                2 -> VerifyMnemonic(
                    mnemonic = mnemonic,
                    step = step,
                    setStep = { step = it },
                    setIsProcessing = { isProcessing = it },
                    alias = alias,
                    onAliasChange = { alias = it },
                    password = password,
                    onPasswordChange = { password = it },
                    password2 = password2,
                    onPassword2Change = { password2 = it },
                )

                3 -> InitAccount(mnemonic = mnemonic, step = step, setStep = { step = it })

                4 -> SetupComplete(onSave = {
                    viewModel.upsertAccount(
                        editIndex = null,
                        account = Account(
                            alias = alias,
                            address = "tnam?",
                            defaultPayAddr = "znam?",
                        ),
                        snackbarHostState = snackbarHostState,
                    )
                    navController.navigate(returnRoute) {
                        popUpTo("add_account?returnRoute=$returnRoute") { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
            }
        }
    }
}

fun getTestMnemonic(): String {
    return "park remain person kitchen mule spell knee armed position rail grid ankle"
}

@Preview(showBackground = true)
@Composable
fun ShowMnemonicPreview() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme(darkTheme = true) {
        Surface {
            AddAccountScreen(viewModel, rememberNavController(), "", 1, SnackbarHostState())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyMnemonicPreview() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme(darkTheme = true) {
        Surface {
            AddAccountScreen(viewModel, rememberNavController(), "", 2, SnackbarHostState())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitAccountPreview() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme(darkTheme = true) {
        Surface {
            AddAccountScreen(viewModel, rememberNavController(), "", 3, SnackbarHostState())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetupCompletePreview() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme(darkTheme = true) {
        Surface {
            AddAccountScreen(viewModel, rememberNavController(), "", 4, SnackbarHostState())
        }
    }
}