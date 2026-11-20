package com.dmzx.vpsdeployer.network
import com.dmzx.vpsdeployer.network.models.*
import retrofit2.Response
import retrofit2.http.*
interface ApiService {
    @GET("droplets")
    suspend fun getDroplets(@Header("Authorization") token: String): Response<Map<String, List<Droplet>>>
    @POST("droplets")
    suspend fun createDroplet(@Header("Authorization") token: String, @Body request: DropletRequest): Response<DropletResponse>
}
