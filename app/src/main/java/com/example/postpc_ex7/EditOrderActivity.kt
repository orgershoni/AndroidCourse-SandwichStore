package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ListenerRegistration

class EditOrderActivity : NewOrderActivity() {

    var statusListenerRegistration : ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val orderIdFromIntent = db.getFromSP(ORDER_ID_KEY, String::class.java)
        if (orderIdFromIntent != null) {
            orderId = orderIdFromIntent
        }

        db.downloadOrder(orderId) { orderFireStore ->
            if (orderFireStore != null) {
                restoreActivity(orderFireStore)
            }
        }

        val headline = findViewById<TextView>(R.id.headline_new_order)
        headline.text = "EDIT ORDER"


        deleteButton.visibility = VISIBLE
        deleteButton.setOnClickListener{

            db.removeOrder(orderId)
            db.removeFromSP(ORDER_ID_KEY)
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

        saveButton.setOnClickListener{
            saveButtonOnClickListener()
        }


        statusListenerRegistration = db.getStatusListener(orderId) { orderFireStore ->
            if (orderFireStore != null && orderFireStore.status == OrderStatus.IN_PROGRESS) {
                val intent = Intent(this, OrderInProgressActivity::class.java)
                startActivity(intent)
            }

        }
//        orderLiveData.observe(this, {orderFireStore ->
//            if (orderFireStore.status == OrderStatus.IN_PROGRESS)
//            {
//                val intent = Intent(this, OrderInProgressActivity::class.java)
//                startActivity(intent)
//            }
//        })

    }

    override fun onDestroy() {
        super.onDestroy()
        statusListenerRegistration?.remove()
    }


}