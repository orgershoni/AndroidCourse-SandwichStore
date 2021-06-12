package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


open class NewOrderActivity : AppCompatActivity() {
    
    var addHummus : Boolean = false
    var addTahini : Boolean = false
    var picklesNum : Int = 0
    var comment : String = ""
    var name : String = ""
    var orderId : String = ""


    protected lateinit var picklesNumView: EditText
    protected lateinit var switchHummus: SwitchCompat
    protected lateinit var switchTahini: SwitchCompat
    protected lateinit var commentView: EditText
    protected lateinit var nameView: EditText
    protected lateinit var saveButton : FloatingActionButton
    protected lateinit var deleteButton : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)

        picklesNumView = findViewById(R.id.pickles_number)
        switchHummus = findViewById(R.id.add_hummus)
        switchTahini = findViewById(R.id.add_tahini)
        commentView = findViewById(R.id.comment_view)
        nameView = findViewById(R.id.name)
        saveButton = findViewById(R.id.save_button)
        deleteButton = findViewById(R.id.deleteButton)

        orderId = UUID.randomUUID().toString()

        deleteButton.visibility = GONE;

        picklesNumView.addTextChangedListener( object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun  afterTextChanged(text : Editable?) {
                validatePicklesNum()
            }
        })

        nameView.addTextChangedListener( object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun  afterTextChanged(text : Editable?) {

                validateName()
            }
        })

        commentView.addTextChangedListener( object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun  afterTextChanged(text : Editable?) {
                comment =  text.toString();
            }
        })

        switchHummus.setOnCheckedChangeListener { button : CompoundButton, _: Boolean ->
            addHummus = button.isChecked
        }

        switchTahini.setOnCheckedChangeListener { button: CompoundButton, _: Boolean ->
            addTahini = button.isChecked
        }

        saveButton.setOnClickListener{

            saveButtonOnClickListener(startActivity = true)
        }
    }

    fun validatePicklesNum() : Boolean {

        val text = picklesNumView.text
        val errText = "Number of pickles need to be between 0 and 10"
        var userInput : Int? = null
        if (text != null)
        {
            userInput = text.toString().toIntOrNull()
        }
        else
        {
            return false
        }
        if (userInput == null || 0 > userInput || userInput  > 10 )
        {
            picklesNumView.error = errText
            Toast.makeText(this, errText, Toast.LENGTH_SHORT).show()
            return false
        }
        else
        {
            picklesNum = userInput;
            return true
        }
    }

    fun validateName() : Boolean {

        val text = nameView.text
        val errText = "Name can't be empty"
        return if (text.toString() == "" )
        {
            nameView.error = errText
            Toast.makeText(this, errText, Toast.LENGTH_SHORT).show()
            false
        }
        else
        {
            name =  text.toString();
            true
        }
    }

    fun saveButtonOnClickListener(startActivity : Boolean =  false){

        if (!validateName() || !validatePicklesNum())
        {
            return
        }


        val sandwichFireStore = OrderFireStore(id = this.orderId, costumerName = this.name, pickles = picklesNum,
                hummus = this.addHummus, tahini = this.addTahini, comment=this.comment)
        val db = SandwichStoreApp.getInstance().ordersDataBase
        db.uploadOrder(sandwichFireStore)

        val intent = Intent(this, EditOrderActivity::class.java)
        intent.putExtra("order_id", this.orderId)

        db.saveToSP(LAST_EDIT_STATUS, OrderFireStore(hummus=this.addHummus,
                tahini=this.addTahini,
                pickles=this.picklesNum,
                costumerName=this.name,
                comment=this.comment,
                id=this.orderId))

        if (startActivity)
        {
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("value", OrderFireStore(hummus=this.addHummus,
                                                            tahini=this.addTahini,
                                                            pickles=this.picklesNum,
                                                            costumerName=this.name,
                                                            comment=this.comment,
                                                            id=this.orderId))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val newOrderParams = savedInstanceState.getSerializable("value") as OrderFireStore
        restoreActivity(newOrderParams)

    }

    override fun onResume() {
        super.onResume()
        val db = SandwichStoreApp.getInstance().ordersDataBase
        val newOrderParams = db.getFromSP(LAST_EDIT_STATUS, OrderFireStore::class.java) ?: return

        restoreActivity(newOrderParams)

    }

    override fun onDestroy() {
        super.onDestroy()

        val db = SandwichStoreApp.getInstance().ordersDataBase
        db.saveToSP(LAST_EDIT_STATUS, OrderFireStore(hummus=this.addHummus,
                                                        tahini=this.addTahini,
                                                        pickles=this.picklesNum,
                                                        costumerName=this.name,
                                                        comment=this.comment,
                                                        id=this.orderId))

    }
    fun restoreActivity(orderFireStore: OrderFireStore){

        this.addHummus = orderFireStore.hummus
        this.addTahini = orderFireStore.tahini
        this.name = orderFireStore.costumerName
        this.picklesNum = orderFireStore.pickles
        this.comment = orderFireStore.comment

        switchHummus.isChecked = this.addHummus
        switchTahini.isChecked = this.addTahini
        nameView.setText(name)
        picklesNumView.setText(picklesNum.toString())
        commentView.setText(comment)
    }

    companion object {
        const val LAST_EDIT_STATUS = "last_edit_status"
    }
}