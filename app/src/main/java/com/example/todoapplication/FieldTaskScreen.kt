package com.example.todoapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.android.material.dialog.MaterialAlertDialogBuilder as MaterialAlertDialogBuilder1


class FieldTaskScreen: AppCompatActivity(), DatePickerDialogListener {

    private lateinit var categoryRadioGroup: RadioGroup;
    private lateinit var createTaskButton: TextView;
    private lateinit var taskNameInputField: EditText;
    private lateinit var planDateInputField: String;
    private lateinit var descriptionInputField: EditText;
    private lateinit var categoryField: String;
    private lateinit var labelTitle: String
    private lateinit var backButton: ImageView;
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.field_task_screen)
        categoryRadioGroup = findViewById(R.id.category_radio_group)
        taskNameInputField = findViewById(R.id.task_name_input_field)
        descriptionInputField = findViewById(R.id.description_input_field)
        planDateInputField = findViewById<TextView>(R.id.pickDate).text.toString()
        backButton = findViewById(R.id.back_btn)

        //====== Handle Click Back to Listing Page =====
        backButton.setOnClickListener{
            handleBackToListingPage();
        }

        //======= Handle DatePicker ===========
        findViewById<TextView>(R.id.pickDate).setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(supportFragmentManager, "datePicker")
        }

        //======= Handle Radio Group ===========
        val defaultCheckedRadioButton = findViewById<RadioButton>(R.id.daily_radio_button)
        defaultCheckedRadioButton.isChecked = true
        categoryField = defaultCheckedRadioButton.text.toString();
        categoryRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            categoryField = checkedRadioButton.text.toString()
        }

        createTaskButton = findViewById(R.id.create_new_task_btn)
        createTaskButton.setOnClickListener {
            validateInputField()
        }



        //==== Watch When Text is Change ====
        taskNameInputField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val isTaskNameField = s.toString().trim { it <= ' ' }
                if(isTaskNameField.isEmpty()){
                    taskNameInputField.error = "field is require."
                    taskNameInputField.setBackgroundResource(R.drawable.invalid_input_field)
                }else{
                    taskNameInputField.setBackgroundResource(R.drawable.valid_input_field)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        // render data detail for update
        if(intent.getSerializableExtra("data_key") != null) {
            val data = intent.getSerializableExtra("data_key") as AllTaskDataModel
            taskNameInputField.setText(data.taskName);
            when (data.category) {
                "Daily" -> findViewById<RadioButton>(R.id.daily_radio_button).isChecked = true
                "Work" -> findViewById<RadioButton>(R.id.work_radio_button).isChecked = true
                "Project" -> findViewById<RadioButton>(R.id.project_radio_button).isChecked = true
                "Groceries" -> findViewById<RadioButton>(R.id.groceries_radio_button).isChecked = true
                }
            findViewById<TextView?>(R.id.pickDate).text = data.planDate
            planDateInputField = data.planDate;
            descriptionInputField.setText(data.description)
        }

        // render label title
        if(intent.getSerializableExtra("data_key") != null){
            findViewById<TextView>(R.id.task_header_label).text = "Update Task"
            findViewById<TextView>(R.id.create_new_task_btn).text = "Update"
            labelTitle = "Update Task"
        }else{
            findViewById<TextView>(R.id.task_header_label).text = "Create Task"
            findViewById<TextView>(R.id.create_new_task_btn).text = "Create new Task"
            labelTitle = "Create new Task"
        }
    }

    //======== Get Value From DatePicker after selected ========
    override fun onDateSet(year: Int, month: Int, day: Int) {
        val selectedDate = "$year/${month + 1}/$day"
        findViewById<TextView>(R.id.pickDate).text = selectedDate
        planDateInputField = selectedDate
    }

    // ======= Handle Back to All Listing Page ============
    private fun handleBackToListingPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_activity_from_left, R.anim.slide_activity_to_right);
    }

    // ===== Validate Input Field =====
    private fun validateInputField(){
        val isTaskNameField = taskNameInputField.text.toString();
        if(isTaskNameField.isEmpty()){
            taskNameInputField.error = "field is require."
            taskNameInputField.setBackgroundResource(R.drawable.invalid_input_field)
        }else{
            showMaterialDialog();
            taskNameInputField.setBackgroundResource(R.drawable.valid_input_field)
        }
    }
    private fun showMaterialDialog() {
        MaterialAlertDialogBuilder1(this)
            .setTitle("Question")
            .setMessage("Would you like to $labelTitle task?")
            .setPositiveButton("Yes") { _, _ ->
                if(intent.getSerializableExtra("data_key") != null){
                    updateTask()
                }else{
                    createTask()
            }

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createTask(){
        val database = Firebase.database
        val taskRef = database.getReference("TaskList")
        val taskID = taskRef.push().key
        val taskItem = AllTaskDataModel(
            taskID.toString(),
            taskNameInputField.text.toString()
            ,categoryField
            ,planDateInputField
            ,descriptionInputField.text.toString());
        println("createItem $taskItem")
        if (taskID != null) {
            taskRef.child(taskID).setValue(taskItem)
        }
        MaterialAlertDialogBuilder1(this)
            .setTitle("Create Task")
            .setMessage("Created Successful")
            .setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss();
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent);
            }
            .show();
        println("Create Screen")
    }

    private fun updateTask() {
        val database = Firebase.database
        val taskRef = database.getReference("TaskList")
        val data = intent.getSerializableExtra("data_key") as AllTaskDataModel
        val updatedTask = AllTaskDataModel(
            data.id,
            taskNameInputField.text.toString(),
            categoryField,
            planDateInputField,
            descriptionInputField.text.toString()
        )
        taskRef.child(data.id).setValue(updatedTask)
            .addOnSuccessListener {
                MaterialAlertDialogBuilder1(this)
                    .setTitle("Update Task")
                    .setMessage("Update Successful")
                    .setPositiveButton("Okay") { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .show()
            }
            .addOnFailureListener { e ->
                MaterialAlertDialogBuilder1(this)
                    .setTitle("Update Task")
                    .setMessage("Failed to update task: ${e.message}")
                    .setPositiveButton("Okay") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
    }


}

