package com.klarna.sample.payments.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {

    @POST("sessions")
    fun createCreditSession(@Body payload: OrderPayload): Call<Session>


    @POST("authorizations/{token}/order")
    fun createOrder(@Path("token") authorizationToken: String, @Body payload: OrderPayload): Call<Unit>
}