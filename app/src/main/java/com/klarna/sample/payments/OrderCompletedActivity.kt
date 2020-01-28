package com.klarna.sample.payments

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class OrderCompletedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_completed)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, SampleActivity::class.java))
    }

}