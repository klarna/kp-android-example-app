package com.klarna.sample.payments.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object OrderClient {

    private val user = "<please enter here>"
    private val password = "<please enter here>"

    val instance by lazy {

        val builder = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(user, password))
        val okHttpClient = builder.build()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.playground.klarna.com/payments/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(OrderService::class.java)
    }


}