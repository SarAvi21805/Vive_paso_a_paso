import com.example.vivepasoapaso.data.remote.dto.EdamamResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EdamamApiService {
    @GET("/api/nutrition-details")
    suspend fun getNutritionDetails(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("ingr") food: String
    ): EdamamResponse
}