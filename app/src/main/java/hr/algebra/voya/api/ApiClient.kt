package hr.algebra.voya.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Volatile
    private var apiService: ApiService? = null

    fun getApiService(context: Context): ApiService {
        return apiService ?: synchronized(this) {
            apiService ?: buildService(context.applicationContext).also { apiService = it }
        }
    }

    private fun buildService(context: Context): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val token = TokenManager.getToken(context)
            val originalRequest = chain.request()
            val shouldAttachAuthHeader = shouldAttachAuthHeader(originalRequest.url.encodedPath)
            val request = if (token.isNullOrBlank() || !shouldAttachAuthHeader) {
                originalRequest
            } else {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            }
            chain.proceed(request)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    internal fun shouldAttachAuthHeader(encodedPath: String): Boolean {
        return !encodedPath.startsWith("/voya/api/auth/")
    }
}
