package com.example.postpc_ex7

import com.google.gson.Gson
import java.io.Serializable


enum class OrderStatus {

    WAITING,
    IN_PROGRESS,
    READY,
    DONE
}

data class OrderFireStore(var hummus: Boolean = false,
                          var tahini: Boolean = false,
                          var pickles: Int = 0,
                          var comment: String = "",
                          var costumerName: String = "",
                          var id : String = "",
                          var status : OrderStatus = OrderStatus.WAITING) : Serializable
{}