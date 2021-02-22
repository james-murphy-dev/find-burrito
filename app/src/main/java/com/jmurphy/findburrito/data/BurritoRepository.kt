package com.jmurphy.findburrito.data

import GetRestaurantsQuery
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toDeferred
import com.jmurphy.findburrito.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class BurritoRepository(val context: Context) {

    val burritoLiveData : MutableLiveData<List<GetRestaurantsQuery.Business?>> = MutableLiveData()

    private class AuthorizationInterceptor(val context: Context): Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${context.getString(R.string.api_key)}" ?: "")
                .build()

            return chain.proceed(request)
        }
    }

    val apolloClient = ApolloClient.builder()
        .serverUrl(context.getString(R.string.api_yelp))
        .okHttpClient(
            OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor(context))
                .build()
        )
        .build()


    suspend fun getNearbyRestaurants(lat: Double, long: Double, radiusMiles: Int)  {
        val radiusMeters = milesToMeters(radiusMiles)
        val response = apolloClient.query(GetRestaurantsQuery(lat, long, radiusMeters)).await()
        val restaurants = response.data?.search?.business

        burritoLiveData.postValue(restaurants)
    }

    fun milesToMeters(miles: Int): Double {
        return miles.toDouble() / 0.0006213711
    }
}