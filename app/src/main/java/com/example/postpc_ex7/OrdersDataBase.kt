package com.example.postpc_ex7

import android.content.Context
import android.content.SharedPreferences
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

        val gsonDesc : String? = this.sp?.getString(key, "")
        if (gsonDesc != null && gsonDesc != "")
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

    fun removeOrder(id : String) : Unit{
        db.collection("orders").document(id).delete().
        addOnSuccessListener {  }.
        addOnFailureListener{   }
    }

    fun getStatusListener(id: String, func: (orderStatus: String?) -> Unit): ListenerRegistration {
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
                func(snapshot.getString("status"))
                return@addSnapshotListener
            }
        }


    }

    fun getOrderStatus(id : String) : OrderStatus? {
        val downloadOrder = downloadOrder(id)
        if (downloadOrder != null) {
            return downloadOrder.status
        }
        return null
    }

    fun updateOrder(id : String, orderObj : OrderFireStore){

        assert(id == orderObj.id)
        uploadOrder(orderObj)
    }


    fun downloadOrder(id : String) : OrderFireStore? {

        var orderFireStore : OrderFireStore? = null;
        db.collection("orders").document(id).get().
        addOnSuccessListener { result : DocumentSnapshot ->
            orderFireStore = result.toObject(OrderFireStore::class.java)
        }

        return orderFireStore
    }

    fun downloadOrders() : List<OrderFireStore>{
        var sandwichList = ArrayList<OrderFireStore>()
        db.collection("orders").get().addOnSuccessListener {
            result : QuerySnapshot ->
                for (document in result.documents) {

                    var sandwichFireStore = document.toObject(OrderFireStore::class.java)
                    if (sandwichFireStore != null) {
                        sandwichList.add(sandwichFireStore)
                }
            }
        }
        return sandwichList
    }

}

