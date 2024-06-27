package com.example.todoapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AllTaskRecyclerViewAdapter
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view_tasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val items = listOf(
            AllTaskDataModel("Do Project"),
            AllTaskDataModel("Node Js"),
            AllTaskDataModel("Practice"),
            AllTaskDataModel("Android Native"),
            AllTaskDataModel("Work"),
            AllTaskDataModel("Fix Bugs"),
            AllTaskDataModel("Reviews Doc"),
            AllTaskDataModel("Workout"),
            AllTaskDataModel("Play Game"),
            )

        adapter = AllTaskRecyclerViewAdapter(items)
        val totalItems = adapter.itemCount;
        findViewById<TextView>(R.id.total_all_task_count).text = "($totalItems)";
        recyclerView.adapter = adapter;
        val createNewTask = findViewById<View>(R.id.create_new_task_btn)
        createNewTask.setOnClickListener(){
            val intent = Intent(this@MainActivity, FieldTaskScreen::class.java)
            startActivity(intent);
        }
    }
}
