package com.example.todoapplication
import java.io.Serializable
data class AllTaskDataModel(
    val id: String ="",
    val taskName: String ="",
    val category: String ="",
    val planDate: String ="",
    val description: String =""
) : Serializable
