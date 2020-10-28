package com.klarna.sample.payments.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object OrderClient {

    private const val user = "" // please enter user here
    private const val password = "" // please enter password here
    private const val baseUrl = "https://api.playground.klarna.com/" // please update the url according to your location and desired environment (playground, production)

    val instance: OrderService by lazy {

        val builder = OkHttpClient.Builder().addInterceptor(BasicAuthInterceptor(user, password))
        val okHttpClient = builder.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(OrderService::class.java)
    }

    fun hasSetCredentials() = user.isNotBlank() && password.isNotBlank()
}