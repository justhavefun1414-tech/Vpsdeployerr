package com.dmzx.vpsdeployer.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmzx.vpsdeployer.network.RetrofitClient
import com.dmzx.vpsdeployer.network.models.Droplet
import com.dmzx.vpsdeployer.network.models.DropletRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
sealed class VpsState {
    object Idle : VpsState(); object Loading : VpsState()
    data class Success(val message: String) : VpsState()
    data class Error(val message: String) : VpsState()
    data class ListLoaded(val droplets: List<Droplet>) : VpsState()
}
class VpsViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private var token: String = "Bearer ISI_TOKEN_DIGITALOCEAN_LO_DISINI"
    private val _state = MutableStateFlow<VpsState>(VpsState.Idle)
    val state: StateFlow<VpsState> = _state
    fun createVps(name: String, region: String, size: String, image: String) {
        _state.value = VpsState.Loading
        viewModelScope.launch {
            try {
                val res = api.createDroplet(token, DropletRequest(name, region, size, image))
                if (res.isSuccessful) _state.value = VpsState.Success("VPS $name berhasil dibuat!")
                else _state.value = VpsState.Error("Gagal: ${res.message()}")
            } catch (e: Exception) { _state.value = VpsState.Error("Error: ${e.message}") }
        }
    }
    fun fetchVpsList() {
        _state.value = VpsState.Loading
        viewModelScope.launch {
            try {
                val res = api.getDroplets(token)
                if (res.isSuccessful) _state.value = VpsState.ListLoaded(res.body()?.get("droplets") ?: emptyList())
                else _state.value = VpsState.Error("Gagal: ${res.message()}")
            } catch (e: Exception) { _state.value = VpsState.Error("Error: ${e.message}") }
        }
    }
}
