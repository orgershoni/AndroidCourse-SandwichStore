package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ListenerRegistration

open class OrderInProgressActivity : AppCompatActivity() {



    lateinit var progressBar : ProgressBar
    lateinit var gotItFab : Button
    lateinit var db : OrdersDataBase
    var statusListenerRegistration : ListenerRegistration? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_in_progress)

        db = SandwichStoreApp.getInstance().ordersDataBase
        val orderId = db.getFromSP(NewOrderActivity.ORDER_ID_KEY, String::class.java)

        progressBar = findViewById(R.id.progressBar)
        gotItFab = findViewById(R.id.gotItButton)
        gotItFab.visibility = GONE

        if (orderId != null)
        {
            statusListenerRegistration = db.getStatusListener(orderId) { orderFireStore ->
                if (orderFireStore != null && orderFireStore.status == OrderStatus.READY) {
                    val intent = Intent(this, OrderReadyActivity::class.java)
                    startActivity(intent)
                }
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