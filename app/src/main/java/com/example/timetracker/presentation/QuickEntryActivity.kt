// presentation/QuickEntryActivity.kt
package com.example.timetracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.timetracker.ui.theme.TimeTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuickEntryActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            TimeTrackerTheme {
//                var showDialog by remember { mutableStateOf(true) }
//
//                if (showDialog) {
//                    QuickEntryDialog(
//                        viewModel = viewModel,
//                        onDismiss = {
//                            showDialog = false
//                            finish()
//                        }
//                    )
//                } else {
//                    finish()
//                }
//            }
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        android.util.Log.d("QuickEntry", "onCreate started")

        try {
            setContent {
                android.util.Log.d("QuickEntry", "setContent started")
                TimeTrackerTheme {
                    android.util.Log.d("QuickEntry", "Theme applied")
                    var showDialog by remember { mutableStateOf(true) }

                    android.util.Log.d("QuickEntry", "showDialog = $showDialog")

                    if (showDialog) {
                        QuickEntryDialog(
                            viewModel = viewModel,
                            onDismiss = {
                                android.util.Log.d("QuickEntry", "onDismiss called")
                                showDialog = false
                                finish()
                            }
                        )
                    }
                }
            }
            android.util.Log.d("QuickEntry", "onCreate finished successfully")
        } catch (e: Exception) {
            android.util.Log.e("QuickEntry", "onCreate crashed", e)
            finish()
        }
    }
}

@Composable
fun QuickEntryDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var activityName by remember { mutableStateOf("") }
    val activities by viewModel.activities.collectAsState()
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "New Activity",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                TextField(
                    value = activityName,
                    onValueChange = { activityName = it },
                    label = { Text("Activity name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))

//                LazyColumn(
//                    modifier = Modifier.weight(1f, fill = false)
//                ) {
//                    items(
//                        activities.filter {
//                            it.name.contains(activityName, ignoreCase = true)
//                        }.take(5)
//                    ) { activity ->
//                        ListItem(
//                            headlineContent = { Text(activity.name) },
//                            modifier = Modifier.clickable {
//                                viewModel.toggleActivity(activity.id)
//                                onDismiss()
//                            }
//                        )
//                    }
//                }
                
                if (activityName.isNotBlank()) {
                    Button(
                        onClick = {
                            viewModel.createAndStartActivity(activityName)
//                            viewModel.updateWidget(context)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create & Start")
                    }
                }
            }
        }
    }
}
