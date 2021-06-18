package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.example.postpc_ex7.NewOrderActivity.Companion.ORDER_ID_KEY

class OrderReadyActivity : OrderInProgressActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        statusListenerRegistration?.remove()
        progressBar.visibility = GONE
        gotItFab.visibility = VISIBLE
        val orderId = db.getFromSP(ORDER_ID_KEY, String::class.java)

        val headline = findViewById<TextView>(R.id.headline_order_in_progress)
        headline.text = "ORDER READY !"


        gotItFab.setOnClickListener {
            progressBar.visibility = GONE
            if (orderId != null) {
                db.setOrderStatus(orderId, OrderStatus.DONE)
                db.removeFromSP(ORDER_ID_KEY)
                val newActivity = Intent(this, NewOrderActivity::class.java)
                startActivity(newActivity);
            }
        }
    }

    override fun onBackPressed() {
        // no back screen
    }
}