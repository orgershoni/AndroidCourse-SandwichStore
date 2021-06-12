package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = SandwichStoreApp.getInstance().ordersDataBase
        //db.clearSP()
        val orderFireStore = db.getFromSP(NewOrderActivity.LAST_EDIT_STATUS, OrderFireStore::class.java)
        var intent = Intent(this, MainActivity::class.java)
        if (orderFireStore == null || orderFireStore.status == OrderStatus.DONE)
        {
            intent = Intent(this, NewOrderActivity::class.java)
            if (orderFireStore != null)
            {
                db.removeFromSP(NewOrderActivity.LAST_EDIT_STATUS)
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