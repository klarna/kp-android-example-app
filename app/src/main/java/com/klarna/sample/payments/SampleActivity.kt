package com.klarna.sample.payments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError
import com.klarna.mobile.sdk.api.payments.PAY_LATER
import com.klarna.sample.payments.api.OrderClient
import com.klarna.sample.payments.api.OrderPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SampleActivity : AppCompatActivity(), KlarnaPaymentViewCallback {
    private val paymentView by lazy { findViewById<KlarnaPaymentView>(R.id.paymentView) }


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

    override fun onInitialized(view: KlarnaPaymentView) {
        view.load(null)
    }

    override fun onLoaded(view: KlarnaPaymentView) {
        authorize.isEnabled = true
    }

    override fun onLoadPaymentReview(view: KlarnaPaymentView, showForm: Boolean) {}

    override fun onAuthorized(view: KlarnaPaymentView, approved: Boolean, authToken: String?, finalizedRequired: Boolean?) {
        finalize.isEnabled = finalizedRequired ?: false
        order.isEnabled = approved && !(finalizedRequired ?: false)
        order.tag = authToken
    }

    override fun onReauthorized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {}

    override fun onErrorOccurred(view: KlarnaPaymentView, error: KlarnaPaymentsSDKError) {
        println("An error occurred: ${error.name} - ${error.message}")
        if (error.isFatal) {
            paymentView.visibility = View.INVISIBLE
        }

    }

    override fun onFinalized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {
        order.isEnabled = approved
        order.tag = authToken
    }


}
