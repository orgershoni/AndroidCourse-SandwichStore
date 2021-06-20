package com.example.postpc_ex7

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.*
import com.google.gson.Gson


class OrdersDataBase() {

    private lateinit var firestoreApp : FirebaseFirestore
    private lateinit var context : Context
    var sp: SharedPreferences? = null


    constructor(context : Context) : this() {
        this.context = context
        firestoreApp = FirebaseFirestore.getInstance();
        sp = context.getSharedPreferences("todo_items_db", Context.MODE_PRIVATE)

    }

    fun removeOrder(id : String) {
        firestoreApp.collection("orders").document(id).delete().
        addOnSuccessListener {  }.
        addOnFailureListener{   }
    }

    fun getStatusListener(id: String, f : (OrderFireStore?) -> Unit): ListenerRegistration {
        val document = firestoreApp.collection("orders").document(id)
        return document.addSnapshotListener { snapshot, e ->

            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot == null) {
                return@addSnapshotListener
            }
            if (!snapshot.exists()) {
                return@addSnapshotListener
            } else {
                val orderFireStore = snapshot.toObject(OrderFireStore::class.java)
                f(orderFireStore)
                return@addSnapshotListener
            }
        }


    }

    fun setOrderStatus(id : String, newOrderStatus: OrderStatus)
    {
        downloadOrderAndDo(id) { orderFireStore ->
            if (orderFireStore != null) {
                orderFireStore.status = newOrderStatus
                uploadOrder(orderFireStore)
            }

        }
    }

    fun uploadOrder(orderObj: OrderFireStore){
        firestoreApp.collection("orders").document(orderObj.id).set(orderObj).
        addOnSuccessListener {  }
        .addOnFailureListener {  }
    }


    fun downloadOrderAndDo(id : String, processFunc : (OrderFireStore?) -> Unit ) {

        firestoreApp.collection("orders").document(id).get().
        addOnSuccessListener { result : DocumentSnapshot ->
            val orderObj = result.toObject(OrderFireStore::class.java)

            // upon download success apply processFunc
            processFunc(orderObj)

        }.addOnFailureListener{it : Exception ->
            Log.e("OR_TAG", "downloadOrder FireBase error occurred $it")
        }.addOnCanceledListener {
            Log.e("OR_TAG", "downloadOrder FireBase canceled")
        }
    }


    // SP functions //
    fun getIDFromSP() : String?{
        return getFromSP(ORDER_ID_KEY, String::class.java)
    }

    fun removeIDFromSP(){
        removeFromSP(ORDER_ID_KEY)
    }

    fun saveIDToSP(id : String){
        saveToSP(ORDER_ID_KEY, id)
    }


    fun getNameFromSP() : String?{
        return getFromSP(NAME_KEY, String::class.java)
    }

    fun removeNameFromSP(){
        removeFromSP(NAME_KEY)
    }

    fun saveNameToSP(id : String){
        saveToSP(NAME_KEY, id)
    }


    // this is only for debug
    fun clearSP(){
        val edit = sp?.edit()
        edit?.clear()
        edit?.apply()
    }

    // generic SP functions - now only used for id of order, but can be extended to many more
    private fun <T> saveToSP(key: String, toSave: T) {
        val gsonDesc = Gson().toJson(toSave)
        val editor = sp?.edit()
        editor?.putString(key, gsonDesc)
        editor?.apply()
    }

    private fun <T> getFromSP(key : String, className : Class<T>): T? {

        val gsonDesc : String? = this.sp?.getString(key, null)
        if (gsonDesc != null)
        {
            return Gson().fromJson(gsonDesc, className)
        }
        return null
    }

    private fun removeFromSP(key : String) {
        val editor = sp?.edit()
        editor?.remove(key)
        editor?.apply()
    }

    companion object {
        const val ORDER_ID_KEY = "last_edit_status"
        const val NAME_KEY = "user_name"
    }


}

