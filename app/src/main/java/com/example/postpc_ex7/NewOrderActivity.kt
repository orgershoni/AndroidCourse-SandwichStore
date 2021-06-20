package com.example.postpc_ex7

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import android.widget.LinearLayout
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

    protected lateinit var db: OrdersDataBase

    protected lateinit var headlineView : TextView
    protected lateinit var picklesNumView: EditText
    protected lateinit var switchHummus: SwitchCompat
    protected lateinit var switchTahini: SwitchCompat
    protected lateinit var commentView: EditText
    protected lateinit var editNameView: EditText
    protected lateinit var saveButton : Button
    protected lateinit var deleteButton : Button
    protected lateinit var editNameFab : FloatingActionButton
    protected lateinit var finishEditFab : FloatingActionButton

    protected var isEditingName : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)

        // get DB
        if (!this::db.isInitialized)
        {
            db = SandwichStoreApp.getDB()
        }


        // load view
        picklesNumView = findViewById(R.id.pickles_number)
        switchHummus = findViewById(R.id.add_hummus)
        switchTahini = findViewById(R.id.add_tahini)
        commentView = findViewById(R.id.comment_view)
        editNameView = findViewById(R.id.name_headline)
        saveButton = findViewById(R.id.save_button)
        deleteButton = findViewById(R.id.deleteButton)
        editNameFab = findViewById(R.id.fab_edit_name)
        finishEditFab = findViewById(R.id.fab_edit_done)
        headlineView = findViewById(R.id.headline_new_order)



        setSaveDeleteLayout(addDeleteButton = false)

        // nothing to delete (to be used on EditOrderActivity)
        //deleteButton.visibility = GONE

        // handle name issue
        chooseNameEnviorment();



        // text listener for pickles
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

        // text listener for comment
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

        // click listeners for hummus and tahini switches
        switchHummus.setOnCheckedChangeListener { button : CompoundButton, _: Boolean ->
            addHummus = button.isChecked
        }

        switchTahini.setOnCheckedChangeListener { button: CompoundButton, _: Boolean ->
            addTahini = button.isChecked
        }

        // click listener for save button
        saveButton.setOnClickListener{

            saveButtonOnClickListener(startActivity = true)
        }

        editNameFab.setOnClickListener{

            isEditingName = true
            editNameView.visibility = VISIBLE
            finishEditFab.visibility = VISIBLE
            editNameFab.visibility = GONE
            headlineView.visibility = GONE

            editNameView.setText(this.name)

        }

        finishEditFab.setOnClickListener{

            isEditingName = false
            if (validateName(isNameUpdated = true))
            {
                editNameView.visibility = GONE
                finishEditFab.visibility = GONE
                editNameFab.visibility = VISIBLE
                headlineView.visibility = VISIBLE
                setNameTextView(name)
                db.saveNameToSP(name)
            }
        }
    }

    fun validatePicklesNum() : Boolean {

        val text = picklesNumView.text
        val errText = "Number of pickles need to be between 0 and 10"
        val userInput: Int?
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


    fun validateName(isNameUpdated : Boolean) : Boolean {

        val text = editNameView.text

        if (!isNameUpdated)
        {
            val nameFromSP = db.getNameFromSP()
            if (nameFromSP != null)
            {
                return true
            }
        }
        val errText = "Name can't be empty"
        return if (text.toString() == "" )
        {
            editNameView.error = errText
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

        if (!validateName(isNameUpdated = false) || !validatePicklesNum())
        {
            return
        }

        // update db
        val sandwichFireStore = OrderFireStore(id = getId(), costumerName = getCostumerName(),
                pickles = picklesNum, hummus = this.addHummus,
                tahini = this.addTahini, comment=this.comment)
        db.uploadOrder(sandwichFireStore)


        // start new activity
        if (startActivity)
        {
            val intent = Intent(this, EditOrderActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("value", OrderFireStore(hummus=this.addHummus,
                                                            tahini=this.addTahini,
                                                            pickles=this.picklesNum,
                                                            comment=this.comment))
        outState.putBoolean("isEditing", isEditingName);
        outState.putString("nameInserted", editNameView.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val newOrderParams = savedInstanceState.getSerializable("value") as OrderFireStore
        isEditingName = savedInstanceState.getBoolean("isEditing");
        editNameView.setText(savedInstanceState.getString("nameInserted"))

        restoreActivity(newOrderParams)

    }

    protected fun restoreActivity(orderFireStore: OrderFireStore){

        this.addHummus = orderFireStore.hummus
        this.addTahini = orderFireStore.tahini
        this.picklesNum = orderFireStore.pickles
        this.comment = orderFireStore.comment

        switchHummus.isChecked = this.addHummus
        switchTahini.isChecked = this.addTahini
        picklesNumView.setText(picklesNum.toString())
        commentView.setText(comment)

        if (isEditingName)
        {
            headlineView.visibility = GONE;
            editNameFab.visibility = GONE;
            editNameView.visibility = VISIBLE
            finishEditFab.visibility = VISIBLE
        }
        else
        {
            setNameTextView(editNameView.text.toString())
            headlineView.visibility = VISIBLE;
            editNameFab.visibility = VISIBLE;
            editNameView.visibility = GONE
            finishEditFab.visibility = GONE
        }


    }

    override fun onBackPressed() {
        // no back screen
    }

    fun getId() : String{

        val orderId = db.getIDFromSP()
        if (orderId == null)
        {
            val randomId = UUID.randomUUID().toString()
            db.saveIDToSP(randomId)
            return randomId
        }
        return orderId
    }

    fun getCostumerName(): String {

        return db.getNameFromSP() ?: return name
    }

    fun setSaveDeleteLayout(addDeleteButton: Boolean){

        saveButton.text = if (addDeleteButton) "Update Order" else "Order Sandwich"
        val saveWeight : Float = if (addDeleteButton) 1.0f else 2.0f
        val deleteWeight : Float = if (addDeleteButton) 1.0f else 0.0f

        val saveFabParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT)
        val deleteFabParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT)

        saveFabParams.weight = saveWeight
        saveFabParams.marginEnd = 8
        saveButton.layoutParams = saveFabParams

        deleteFabParams.weight = deleteWeight
        deleteButton.layoutParams = saveFabParams
    }

    fun chooseNameEnviorment(){

        val nameFromSP = db.getNameFromSP()
        if (nameFromSP != null && nameFromSP != "")
        {
            setNameTextView(nameFromSP)
            headlineView.visibility = VISIBLE;
            editNameFab.visibility = VISIBLE;
            editNameView.visibility = GONE
            finishEditFab.visibility = GONE
        }
        else
        {
            headlineView.visibility = GONE
            editNameFab.visibility = GONE
            editNameView.visibility = VISIBLE
            finishEditFab.visibility = VISIBLE
            editNameView.hint = "Fill your name here"
            editNameView.setText("")
        }
    }

    fun setNameTextView(name : String, isNewOrder : Boolean = true)
    {
        if (name != "")
        {
            val headlineTxt = "$name's NEW ORDER"
            val headlineTxtOfOrderWaiting = "$name's ORDER"
            headlineView.text = if (isNewOrder) headlineTxt else headlineTxtOfOrderWaiting
        }
    }
}