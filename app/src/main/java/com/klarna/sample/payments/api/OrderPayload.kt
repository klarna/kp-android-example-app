package com.klarna.sample.payments.api

data class OrderPayload(
    val purchase_country: String,
    val purchase_currency: String,
    val locale: String,
    val order_amount: Long,
    val order_tax_amount: Long,
    val order_lines: List<OrderLine>
) {


    companion object {


        val defaultPayload by lazy {

            val orderLIne = OrderLine(
                "https://demo.klarna.se/fashion/kp/media/wysiwyg/Accessoriesbagimg.jpg",
                "physical",
                "AccessoryBag-Ref-ID-0001",
                "Light Brown Accessory Bag",
                1,
                350,
                0,
                350,
                0
            )

            OrderPayload("SE", "SEK", "en-US", 350, 0, listOf(orderLIne))
        }
    }

}

data class OrderLine(
    val image_url: String,
    val type: String,
    val reference: String,
    val name: String,
    val quantity: Long,
    val unit_price: Long,
    val tax_rate: Long,
    val total_amount: Long,
    val total_tax_amount: Long
)
