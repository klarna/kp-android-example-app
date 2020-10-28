package com.klarna.sample.payments.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {

    @POST("payments/v1/sessions")
    fun createCreditSession(@Body payload: OrderPayload): Call<Session>

    @POST("payments/v1/authorizations/{token}/order")
    fun createOrder(@Path("token") authorizationToken: String, @Body payload: OrderPayload): Call<Unit>
}