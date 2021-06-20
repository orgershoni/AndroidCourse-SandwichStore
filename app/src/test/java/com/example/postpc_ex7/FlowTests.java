package com.example.postpc_ex7;

import android.app.Application;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 *
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)


public class FlowTests {

    private ActivityController<MainActivity> MainActivityController;
    private ActivityController<EditOrderActivity> editActivity;
    private ActivityController<NewOrderActivity> newOrderActivity;
    private ActivityController<OrderReadyActivity> orderReadyActivity;

    private OrdersDataBase ordersDataBase;
    private MainActivity mainActivity;


    @Before
    public void setup() {

        ordersDataBase = Mockito.mock(OrdersDataBase.class);
        mainActivity = Mockito.mock(MainActivity.class);
        MainActivityController = Robolectric.buildActivity(MainActivity.class);
        editActivity = Robolectric.buildActivity(EditOrderActivity.class);
        newOrderActivity = Robolectric.buildActivity(NewOrderActivity.class);
        orderReadyActivity = Robolectric.buildActivity(OrderReadyActivity.class);

        NewOrderActivity activityUnderTest = newOrderActivity.get();
        activityUnderTest.db = ordersDataBase;
        MainActivity activityUnderTest1 = MainActivityController.get();
        activityUnderTest1.db = ordersDataBase;
        EditOrderActivity activityUnderTest2 = editActivity.get();
        activityUnderTest2.db = ordersDataBase;
        OrderReadyActivity activityUnderTest3 = orderReadyActivity.get();
        activityUnderTest3.db = ordersDataBase;
    }

    @Test
    public void when_EnteringIllegalPickleNum_then_UploadOrderIsNotCalled() {

        newOrderActivity.create().visible();
        NewOrderActivity activityNewOrderTest = newOrderActivity.get();
        Button saveButton = activityNewOrderTest.findViewById(R.id.save_button);

        EditText name = activityNewOrderTest.findViewById(R.id.name_headline);
        EditText pickles = activityNewOrderTest.findViewById(R.id.pickles_number);

        // verify error on pickles field
        pickles.setText("11");
        assertEquals(pickles.getError().toString(), "Number of pickles need to be between 0 and 10");

        // verify that uploadOrder isn't called due to bad input
        saveButton.performClick();
        verify(ordersDataBase, never()).uploadOrder(any(OrderFireStore.class));

        // verify that after fixing the saveButton click causes uploadOrder to invoke
        pickles.setText("10");
        name.setText("name");
        saveButton.performClick();
        verify(ordersDataBase).uploadOrder(any(OrderFireStore.class));
    }

    @Test
    public void when_ClickingOnSaveButton_them_uploadOrderIsCalledWithRightArgs() {

        editActivity.create().visible();
        ArgumentCaptor<OrderFireStore> argument = ArgumentCaptor.forClass(OrderFireStore.class);

        // init views
        EditOrderActivity activityNewOrderTest = editActivity.get();
        activityNewOrderTest.setName("different name");
        activityNewOrderTest.setOrderId("id");

        EditText name = activityNewOrderTest.findViewById(R.id.name_headline);
        EditText pickles = activityNewOrderTest.findViewById(R.id.pickles_number);
        SwitchCompat addTahini = activityNewOrderTest.findViewById(R.id.add_tahini);
        SwitchCompat addHumus = activityNewOrderTest.findViewById(R.id.add_hummus);
        Button saveButton = activityNewOrderTest.findViewById(R.id.save_button);



        // play with views
        name.setText("name");
        pickles.setText("7");
        addHumus.setChecked(true);
        addTahini.setChecked(false);

        // save
        saveButton.performClick();

        // expect to uploadOrder to be called with OrderFireStore object with right args
        verify(ordersDataBase).uploadOrder(argument.capture());

        assertEquals(argument.getValue().getCostumerName(), "name");
        assertEquals(argument.getValue().getPickles(), 7);
        assertTrue(argument.getValue().getHummus());
        assertFalse(argument.getValue().getTahini());
    }

    @Test
    public void addition_isCorrect3() {

        // init id variable to not be null
        OrderReadyActivity orderReadyActivityObj = orderReadyActivity.get();
        orderReadyActivityObj.orderId = "id";

        // give screen to orderReadyActivity
        orderReadyActivity.create().visible();
        ArgumentCaptor<OrderStatus> argument = ArgumentCaptor.forClass(OrderStatus.class);

        // load views
        Button gotItButton = orderReadyActivityObj.findViewById(R.id.gotItButton);
        ProgressBar progressBar = orderReadyActivityObj.findViewById(R.id.progressBar);

        // assert that got it button is clickable
        assertEquals(gotItButton.getVisibility(), View.VISIBLE);

        // acknowledge got sandwich
        gotItButton.performClick();

        // assert that progress setOrderStatus is being called with right arg (status DONE)
        verify(ordersDataBase).setOrderStatus(any(String.class), argument.capture());
        assertEquals(argument.getValue(), OrderStatus.DONE);
    }
}