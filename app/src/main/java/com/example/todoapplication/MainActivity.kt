package com.example.todoapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noDataLabel: TextView
    private lateinit var adapter: AllTaskRecyclerViewAdapter
    private var tasks = mutableListOf<AllTaskDataModel>()

    @SuppressLint("SetTextI18n", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        noDataLabel = findViewById(R.id.no_data_label)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        val database = FirebaseDatabase.getInstance()
        val taskRef = database.getReference().child("TaskList")

        recyclerView = findViewById(R.id.recycler_view_tasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AllTaskRecyclerViewAdapter(this, tasks)
        recyclerView.adapter = adapter

        taskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tasks.clear() // Clear the list before adding new items
                for (userSnapshot in snapshot.children) {
                    val task = userSnapshot.getValue(AllTaskDataModel::class.java)
                    task?.let {
                        tasks.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
                updateTaskCounts()
                showNoDataContainer()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        val createNewTask = findViewById<View>(R.id.create_new_task_btn)
        createNewTask.setOnClickListener {
            val intent = Intent(this@MainActivity, FieldTaskScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_activity_from_right, R.anim.slide_activity_to_left)
        }

        val searchView = findViewById<EditText>(R.id.search_bar)
        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                adapter.filter(query)
                showNoDataContainer()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateTaskCounts() {
        val totalItems = adapter.itemCount
        findViewById<TextView>(R.id.total_all_task_count).text = "($totalItems)"

        val dailyCount = adapter.getTotalCountForCategory("Daily")
        val projectCount = adapter.getTotalCountForCategory("Project")
        val workCount = adapter.getTotalCountForCategory("Work")
        val groceriesCount = adapter.getTotalCountForCategory("Groceries")
        findViewById<TextView>(R.id.daily_totalcounts).text = "($dailyCount)"
        findViewById<TextView>(R.id.work_totalcounts).text = "($workCount)"
        findViewById<TextView>(R.id.project_totalcounts).text = "($projectCount)"
        findViewById<TextView>(R.id.groceries_totalcounts).text = "($groceriesCount)"
    }

    private fun showNoDataContainer() {
        if (adapter.itemCount == 0) {
            recyclerView.visibility = View.GONE
            noDataLabel.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noDataLabel.visibility = View.GONE
        }
    }
}

