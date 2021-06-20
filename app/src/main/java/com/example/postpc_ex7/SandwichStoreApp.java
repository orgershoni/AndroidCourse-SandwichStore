package com.example.postpc_ex7;
import android.app.Application;

import com.google.firebase.FirebaseApp;

public class SandwichStoreApp extends Application {

    OrdersDataBase ordersDataBase = null;
    static SandwichStoreApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        ordersDataBase = new OrdersDataBase(this);
        instance = this;
    }

    //private static SandwichStoreApp getInstance() { return  instance; }
    public static OrdersDataBase getDB() {
        return instance.ordersDataBase;
    }
}
