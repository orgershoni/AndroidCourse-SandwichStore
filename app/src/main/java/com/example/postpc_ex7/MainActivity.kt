package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData

class MainActivity : AppCompatActivity() {

    protected lateinit var db: OrdersDataBase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = SandwichStoreApp.getInstance().ordersDataBase
        //db.clearSP()
        val orderId = db.getFromSP(NewOrderActivity.ORDER_ID_KEY, String::class.java)
        if (orderId == null) {
            var intent = Intent(this, NewOrderActivity::class.java)
            startActivity(intent)
            return
        }
        db.downloadOrder(orderId) { orderObj ->
            chooseActivity(orderObj)
        }

    }


    private fun chooseActivity(orderFireStore: OrderFireStore?){

        var intent = Intent(this, MainActivity::class.java)
        if (orderFireStore == null || orderFireStore.status == OrderStatus.DONE)
        {
            intent = Intent(this, NewOrderActivity::class.java)
            if (orderFireStore != null)
            {
                db.removeFromSP(NewOrderActivity.ORDER_ID_KEY)
            }
        }
        else
        {
            if (orderFireStore.status ==  OrderStatus.WAITING)
            {
                intent = Intent(this, EditOrderActivity::class.java)
            }
            else if (orderFireStore.status == OrderStatus.IN_PROGRESS)
            {
                intent = Intent(this, OrderInProgressActivity::class.java)
            }
            else if (orderFireStore.status == OrderStatus.READY)
            {
                intent = Intent(this, OrderReadyActivity::class.java)
            }
        }
        startActivity(intent)
    }

}