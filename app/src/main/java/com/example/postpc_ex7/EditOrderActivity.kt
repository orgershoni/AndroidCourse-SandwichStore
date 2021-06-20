package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.TextView
import com.google.firebase.firestore.ListenerRegistration

class EditOrderActivity : NewOrderActivity() {

    var statusListenerRegistration : ListenerRegistration? = null
    var orderId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // add a delete button
        setSaveDeleteLayout(addDeleteButton = true)
        if (name != "")
        {
            name = db.getNameFromSP()!!
        }
        setNameTextView(name, isNewOrder = false)

        // get orderId from SP (can't be null at this point)
        if (orderId != "")
        {
            orderId = db.getIDFromSP()!!
        }

        // load order from Firestore and fill activity fields with values
        db.downloadOrderAndDo(orderId) { orderFireStore ->
            if (orderFireStore != null) {
                restoreActivity(orderFireStore)
            }
        }

        finishEditFab.setOnClickListener{

            isEditingName = false
            if (validateName(isNameUpdated = true))
            {
                editNameView.visibility = View.GONE
                finishEditFab.visibility = View.GONE
                editNameFab.visibility = VISIBLE
                headlineView.visibility = VISIBLE
                setNameTextView(name, isNewOrder = false)
                db.saveNameToSP(name)
            }
        }

        deleteButton.setOnClickListener{
            db.removeOrder(orderId)
            db.removeIDFromSP()
            // if delete order button is clicked, go back to NewOrderActivity
            val newOrderIntent = Intent(this, NewOrderActivity::class.java)
            startActivity(newOrderIntent)
        }

        saveButton.setOnClickListener{
            saveButtonOnClickListener()
        }

        // install listener to check if order status was changed
        statusListenerRegistration = db.getStatusListener(orderId) { orderFireStore ->
            if (orderFireStore != null && orderFireStore.status == OrderStatus.IN_PROGRESS) {
                // if status in changed to IN_PROGRESS, open OrderInProgressActivity
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