package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.TextView
import com.google.firebase.firestore.ListenerRegistration

class EditOrderActivity : NewOrderActivity() {

    var statusListenerRegistration : ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val orderId : String
        val orderIdChecker = db.getFromSP(ORDER_ID_KEY, String::class.java)
        if (orderIdChecker == null)
        {
            Log.e("ERROR", "Edit Order screen can only be reached when id is saved to SP")
            return
        }
        else
        {
            orderId = orderIdChecker
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
            val newOrderIntent = Intent(this, NewOrderActivity::class.java)
            startActivity(newOrderIntent)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        statusListenerRegistration?.remove()
    }

    override fun onBackPressed() {
        // no back screen
    }

}