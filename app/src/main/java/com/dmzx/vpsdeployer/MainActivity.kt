package com.dmzx.vpsdeployer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dmzx.vpsdeployer.viewmodel.VpsState
import com.dmzx.vpsdeployer.viewmodel.VpsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val viewModel: VpsViewModel = viewModel()
                val state by viewModel.state.collectAsState()
                var tab by remember { mutableStateOf(0) }

                Scaffold(
                    bottomBar = {
                        TabRow(selectedTabIndex = tab) {
                            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Buat VPS") })
                            Tab(selected = tab == 1, onClick = { tab = 1; viewModel.fetchVpsList() }, text = { Text("Daftar VPS") })
                        }
                    }
                ) { padding ->
                    Column(Modifier.padding(padding).padding(16.dp)) {
                        if (tab == 0) {
                            var name by remember { mutableStateOf("my-vps") }
                            var region by remember { mutableStateOf("sgp1") }
                            var size by remember { mutableStateOf("s-1vcpu-1gb") }
                            var image by remember { mutableStateOf("ubuntu-22-04-x64") }
                            Text("Buat VPS DigitalOcean", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = region, onValueChange = { region = it }, label = { Text("Region") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = size, onValueChange = { size = it }, label = { Text("Size") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = image, onValueChange = { image = it }, label = { Text("Image") }, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(10.dp))
                            Button(onClick = { viewModel.createVps(name, region, size, image) }, modifier = Modifier.fillMaxWidth()) { Text("Deploy") }
                            Spacer(Modifier.height(10.dp))
                            when (val s = state) {
                                is VpsState.Loading -> CircularProgressIndicator()
                                is VpsState.Success -> Text(s.message, color = MaterialTheme.colorScheme.primary)
                                is VpsState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
                                else -> {}
                            }
                        } else {
                            Text("Daftar VPS", style = MaterialTheme.typography.titleLarge)
                            when (val s = state) {
                                is VpsState.Loading -> CircularProgressIndicator()
                                is VpsState.ListLoaded -> LazyColumn {
                                    items(s.droplets) { d ->
                                        Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                            Column(Modifier.padding(16.dp)) {
                                                Text(d.name, style = MaterialTheme.typography.titleMedium)
                                                Text("IP: ${d.networks.v4.firstOrNull()?.ip_address ?: "Pending"}")
                                                Text("Status: ${d.status}")
                                            }
                                        }
                                    }
                                }
                                is VpsState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}
