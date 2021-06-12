package com.example.postpc_ex7;
import android.app.Application;

public class SandwichStoreApp extends Application {

    OrdersDataBase ordersDataBase = null;
    static SandwichStoreApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        ordersDataBase = new OrdersDataBase(this);
        instance = this;
    }

    public static SandwichStoreApp getInstance() { return  instance; }
}
