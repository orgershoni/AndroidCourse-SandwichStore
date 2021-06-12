package com.example.postpc_ex7

import com.google.gson.Gson
import java.io.Serializable


enum class OrderStatus {

    WAITING,
    IN_PROGRESS,
    READY,
    DONE
}

data class OrderFireStore(var hummus: Boolean, var tahini: Boolean, var pickles: Int,
                          var comment: String, var costumerName: String,
                          var id : String,
                          var status : OrderStatus = OrderStatus.WAITING) : Serializable
{
    fun serialize() : String{
        return Gson().toJson(this)
    }

    companion object{
        fun deserialize(json: String?) : OrderFireStore? {
            if (json == null)
            {
                return null;
            }
            return Gson().fromJson(json, OrderFireStore::class.java)
        }
    }

}