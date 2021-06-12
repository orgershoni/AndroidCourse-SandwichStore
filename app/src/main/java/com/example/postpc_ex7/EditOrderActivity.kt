package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.TextView
import com.google.firebase.firestore.ListenerRegistration

class EditOrderActivity : NewOrderActivity() {

    var statusListenerRegistration : ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        val db = SandwichStoreApp.getInstance().ordersDataBase
        super.onCreate(savedInstanceState)

        val stringExtra = intent.getStringExtra("order_id")
        if (stringExtra != null) {
            orderId = stringExtra
        }

        val headline = findViewById<TextView>(R.id.headline_new_order)
        headline.text = "EDIT ORDER"


        deleteButton.visibility = VISIBLE
        deleteButton.setOnClickListener{

            db.removeOrder(orderId)
            db.removeFromSP(LAST_EDIT_STATUS)
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

        saveButton.setOnClickListener{
            saveButtonOnClickListener()
        }

        statusListenerRegistration = db.getStatusListener(orderId) { orderStatus: String? ->
            if (orderStatus != null && orderStatus == OrderStatus.IN_PROGRESS.name) {
                val intent = Intent(this, OrderInProgressActivity::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        statusListenerRegistration?.remove()
    }


}