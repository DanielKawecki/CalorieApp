package pl.edu.uwr.pam.calorieapp

import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class NutritionResponse(
    val items: List<NutritionItem>
)

data class NutritionItem(
    val sugar_g: Double,
    val fiber_g: Double,
    val serving_size_g: Double,
    val sodium_mg: Int,
    val name: String,
    val potassium_mg: Int,
    val fat_saturated_g: Double,
    val fat_total_g: Double,
    val calories: Double,
    val cholesterol_mg: Int,
    val protein_g: Double,
    val carbohydrates_total_g: Double
)

interface NutritionApi {
    @GET("nutrition")
    suspend fun getNutrition(
        @Query("query") query: String
    ): NutritionResponse
}

object RetrofitInstance {

    private const val BASE_URL = "https://api.calorieninjas.com/v1/"
    private const val API_KEY = "H5Kr+uY0gAtUcHbACYgP5A==TNzZMj5Xgwe4Ej7u"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Api-Key", API_KEY)
                    .build()
                chain.proceed(request)
            })
            .build()
    }

    val api: NutritionApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NutritionApi::class.java)
    }
}