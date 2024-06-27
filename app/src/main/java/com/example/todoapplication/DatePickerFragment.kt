package com.example.todoapplication

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var listener: DatePickerDialogListener
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DatePickerDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DatePickerDialogListener")
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        listener.onDateSet(year, month, day)
    }
}

interface DatePickerDialogListener {
    fun onDateSet(year: Int, month: Int, day: Int)
}
