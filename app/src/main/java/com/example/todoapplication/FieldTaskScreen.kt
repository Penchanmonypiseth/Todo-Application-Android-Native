package com.example.todoapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FieldTaskScreen: AppCompatActivity(), DatePickerDialogListener {

    private lateinit var categoryRadioGroup: RadioGroup
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.field_task_screen)

        //======= Handle DatePicker ===========
        findViewById<TextView>(R.id.pickDate).setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(supportFragmentManager, "datePicker")
        }

        //======= Handle Radio Group ===========
        categoryRadioGroup = findViewById(R.id.category_radio_group)
        val defaultCheckedRadioButton = findViewById<RadioButton>(R.id.daily_radio_button)
        defaultCheckedRadioButton.isChecked = true
        categoryRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            checkedRadioButton.text?.let { text ->
                Log.d("RadioSelection", "Selected radio button: $text")
            }
        }
    }

   //======== Get Value From DatePicker after selected ========
    override fun onDateSet(year: Int, month: Int, day: Int) {
        val selectedDate = "$year/${month + 1}/$day"
        findViewById<TextView>(R.id.pickDate).text = selectedDate
    }

    fun handleBackToListingPage(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}