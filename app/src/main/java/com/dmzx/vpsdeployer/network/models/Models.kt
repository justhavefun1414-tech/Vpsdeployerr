package com.dmzx.vpsdeployer.network.models
data class Droplet(val id: Int, val name: String, val status: String, val memory: Int, val vcpus: Int, val networks: Networks)
data class Networks(val v4: List<NetworkV4>)
data class NetworkV4(val ip_address: String)
data class DropletRequest(val name: String, val region: String, val size: String, val image: String)
data class DropletResponse(val droplet: Droplet)
