package com.klarna.sample.payments

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.klarna.mobile.sdk.payments.*
import com.klarna.sample.payments.api.OrderClient
import com.klarna.sample.payments.api.OrderPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SampleActivity : AppCompatActivity(), KlarnaPaymentViewCallBack {
    private val paymentView by lazy { findViewById<PaymentView>(R.id.paymentView) }


    private val authorize by lazy { findViewById<Button>(R.id.authorize) }
    private val finalize by lazy { findViewById<Button>(R.id.finalize) }
    private val order by lazy { findViewById<Button>(R.id.create_order) }

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sample)
        paymentView.category = PAY_LATER

        authorize.setOnClickListener {
            paymentView.authorize(true, null)
        }

        finalize.setOnClickListener {
            paymentView.finalize(null)
        }

        order.setOnClickListener {
            job = GlobalScope.launch {
                val successful =
                    OrderClient.instance.createOrder(it.tag as String, OrderPayload.defaultPayload).execute()
                        .isSuccessful

                if (successful) {
                    startActivity(Intent(this@SampleActivity, OrderCompletedActivity::class.java))
                    finish()
                }
            }
        }


        job = GlobalScope.launch {
            OrderClient.instance.createCreditSession(OrderPayload.defaultPayload)
                .execute()
                .body()
                ?.let {
                    GlobalScope.launch (Dispatchers.Main) {
                        paymentView.initialize(it.client_token, "kp-sample://kp")
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        paymentView.registerPaymentViewCallback(this)
    }

    override fun onPause() {
        super.onPause()
        paymentView.unregisterPaymentViewCallback(this)
        job?.cancel()
    }

    override fun onInitialized(view: PaymentView) {
        view.load(null)
    }

    override fun onLoaded(view: PaymentView) {
        authorize.isEnabled = true
    }

    override fun onAuthorized(view: PaymentView, approved: Boolean, authToken: String?, finalizedRequired: Boolean?) {
        finalize.isEnabled = finalizedRequired ?: false
        order.isEnabled = approved && !(finalizedRequired ?: false)
        order.tag = authToken
    }

    override fun onReauthorized(view: PaymentView, approved: Boolean, authToken: String?) {

    }

    override fun onErrorOccurred(view: PaymentView, error: KlarnaPaymentError) {
        println("An error occurred: ${error.name} - ${error.message}")
        if (error.isFatal) {
            paymentView.visibility = View.INVISIBLE
        }

    }

    override fun onFinalized(view: PaymentView, approved: Boolean, authToken: String?) {
        order.isEnabled = approved
        order.tag = authToken
    }


}
