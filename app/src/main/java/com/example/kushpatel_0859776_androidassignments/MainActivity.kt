package com.example.kushpatel_0859776_androidassignments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kushpatel_0859776_androidassignment6.R
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter
    private val expenseList = mutableListOf<Expense>()

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val editTextExpenseName = findViewById<EditText>(R.id.editTextExpenseName)
        val editTextExpenseAmount = findViewById<EditText>(R.id.editTextExpenseAmount)
        val buttonAddExpense = findViewById<Button>(R.id.buttonAddExpense)
        val textViewDate = findViewById<TextView>(R.id.textViewExpenseDate)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        expenseAdapter = ExpenseAdapter(expenseList) { position ->
            expenseList.removeAt(position)
            expenseAdapter.notifyItemRemoved(position)
        }
        recyclerView.adapter = expenseAdapter

        textViewDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                textViewDate.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            }, year, month, day)
            datePicker.show()
        }

        buttonAddExpense.setOnClickListener {
            val name = editTextExpenseName.text.toString().trim()
            val amount = editTextExpenseAmount.text.toString().trim().toDoubleOrNull()
            val date = textViewDate.text.toString()

            if(name.isEmpty()) {
                Toast.makeText(this, "Please enter an expense name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid expense amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (date == "Select Date" || date.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            expenseList.add(Expense(name, amount, date))
            expenseAdapter.notifyItemInserted(expenseList.size - 1)

            editTextExpenseName.text.clear()
            editTextExpenseAmount.text.clear()
        }
    }
}