package com.example.postpc_ex7

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.google.gson.Gson


class OrdersDataBase() {

    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var context : Context
    var sp: SharedPreferences? = null

    constructor(context : Context) : this() {
        this.context = context
        sp = context.getSharedPreferences("todo_items_db", Context.MODE_PRIVATE)

    }


    fun clearSP(){
        val edit = sp?.edit()
        edit?.clear()
        edit?.apply()
    }

    fun <T> saveToSP(key: String, toSave: T) {
        val gsonDesc = Gson().toJson(toSave)
        val editor = sp?.edit()
        editor?.putString(key, gsonDesc)
        editor?.apply()
    }

    fun <T> getFromSP(key : String, className : Class<T>): T? {

        val gsonDesc : String? = this.sp?.getString(key, null)
        if (gsonDesc != null)
        {
            return Gson().fromJson(gsonDesc, className)
        }
        return null
    }

    fun removeFromSP(key : String) {
        val editor = sp?.edit()
        editor?.remove(key)
        editor?.apply()
    }

    fun uploadOrder(orderObj: OrderFireStore){
        db.collection("orders").document(orderObj.id).set(orderObj).
        addOnSuccessListener {  }
        .addOnFailureListener {  }
    }

    fun removeOrder(id : String) {
        db.collection("orders").document(id).delete().
        addOnSuccessListener {  }.
        addOnFailureListener{   }
    }

    fun getStatusListener(id: String, f : (OrderFireStore?) -> Unit): ListenerRegistration {
        val document = db.collection("orders").document(id)
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
        downloadOrder(id) { orderFireStore ->
            if (orderFireStore != null) {
                orderFireStore.status = newOrderStatus
                uploadOrder(orderFireStore)
            }

        }
    }

    fun updateOrder(id : String, orderObj : OrderFireStore){

        assert(id == orderObj.id)
        uploadOrder(orderObj)
    }


    fun downloadOrder(id : String, processFunc : (OrderFireStore?) -> Unit ) {

        db.collection("orders").document(id).get().
        addOnSuccessListener { result : DocumentSnapshot ->
            val orderObj = result.toObject(OrderFireStore::class.java)
            processFunc(orderObj)

        }.addOnFailureListener{it : Exception ->
            Log.e("OR_TAG", "downloadOrder FireBase error occurred $it")
        }.addOnCanceledListener {
            Log.e("OR_TAG", "downloadOrder FireBase canceled")
        }
    }

//    fun downloadOrders() : List<OrderFireStore>{
//        var sandwichList = ArrayList<OrderFireStore>()
//        db.collection("orders").get().addOnSuccessListener {
//            result : QuerySnapshot ->
//                for (document in result.documents) {
//
//                    var sandwichFireStore = document.toObject(OrderFireStore::class.java)
//                    if (sandwichFireStore != null) {
//                        sandwichList.add(sandwichFireStore)
//                }
//            }
//        }
//        return sandwichList
//    }

}

