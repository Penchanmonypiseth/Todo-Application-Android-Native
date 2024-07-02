package com.example.todoapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.io.Serializable

class AllTaskRecyclerViewAdapter(private val context: Context, private var items: MutableList<AllTaskDataModel>) : RecyclerView.Adapter<AllTaskRecyclerViewAdapter.ViewHolder>() {
    private var filteredItems: MutableList<AllTaskDataModel> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val updateButton: ImageView = itemView.findViewById(R.id.update_button)
        private val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)

        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(item: AllTaskDataModel) {
            itemView.findViewById<TextView>(R.id.task_title).text = item.taskName
            itemView.findViewById<TextView>(R.id.planDate).text = "Plan Date: ${item.planDate}"
            itemView.findViewById<TextView>(R.id.categoryLabel).text = item.category

            updateButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, FieldTaskScreen::class.java)
                intent.putExtra("data_key", item as Serializable)
                context.startActivity(intent)
            }

            deleteButton.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Question")
                    .setMessage("Are you sure to delete this task?")
                    .setPositiveButton("Yes") { _, _ ->
                        val database = Firebase.database
                        val taskRef = database.getReference("TaskList")
                        val taskIDToDelete = item.id
                        taskRef.child(taskIDToDelete).removeValue()
                            .addOnSuccessListener {
                                items.remove(item)
                                filteredItems.remove(item)
                                notifyDataSetChanged()

                                MaterialAlertDialogBuilder(context)
                                    .setTitle("Delete Task")
                                    .setMessage("Delete Task Success")
                                    .setPositiveButton("Okay") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .show()
                            }
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    fun getTotalCountForCategory(category: String): Int {
        return items.count { it.category == category }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        filteredItems = if (query.isEmpty()) {
            items
        } else {
            items.filter { it.taskName.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }
}

