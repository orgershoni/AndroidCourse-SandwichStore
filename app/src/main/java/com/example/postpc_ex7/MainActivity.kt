package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var db: OrdersDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!this::db.isInitialized)
        {
            db = SandwichStoreApp.getDB()
        }
        // orderId would be null if no order is currently running
        val orderId = db.getIDFromSP()
        if (orderId == null) {
            val intent = Intent(this, NewOrderActivity::class.java)
            startActivity(intent)
            return
        }
        db.downloadOrderAndDo(orderId) { orderObj ->
            chooseActivity(orderObj)
        }

    }


    private fun chooseActivity(orderFireStore: OrderFireStore?){

        val intent : Intent
        val status = orderFireStore?.status

        if (orderFireStore == null || orderFireStore.status == OrderStatus.DONE)
        {
            intent = Intent(this, NewOrderActivity::class.java)
            if (orderFireStore != null)
            {
                db.removeIDFromSP()
            }
        }
        else {
            intent = when (status) {
                OrderStatus.WAITING -> Intent(this, EditOrderActivity::class.java)
                OrderStatus.IN_PROGRESS -> Intent(this, OrderInProgressActivity::class.java)
                OrderStatus.READY -> Intent(this, OrderReadyActivity::class.java)
                else -> Intent(this, NewOrderActivity::class.java)
            }
        }

        startActivity(intent)
    }

}