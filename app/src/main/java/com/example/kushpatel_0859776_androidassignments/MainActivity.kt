package com.example.kushpatel_0859776_androidassignments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileNotFoundException
import java.util.Calendar

// Data class representing an expense with a name and amount
data class Expense(val name: String, val amount: Double, val date: String)

private const val FILE_NAME = "expenses.txt"

class MainActivity : AppCompatActivity() {
    private lateinit var headerFragment: HeaderFragment
    private lateinit var footerFragment: FooterFragment
    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter
    private val expenseList = mutableListOf<Expense>()

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged", "SetTextI18n", "CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        Log.d("MainActivityLifecycle", "onCreate called")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        expenseAdapter = ExpenseAdapter(expenseList) { position ->
            deleteExpense(position)
        }
        recyclerView.adapter = expenseAdapter

        // Load previously saved expenses
        expenseList.addAll(loadExpensesFromFile(this))

        // Notify the adapter about data change
        expenseAdapter.notifyDataSetChanged()

        headerFragment = HeaderFragment()
        footerFragment = FooterFragment()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentHeaderContainer, headerFragment)
        fragmentTransaction.replace(R.id.fragmentFooterContainer, footerFragment)
        fragmentTransaction.commit()

        val editTextExpenseName = findViewById<EditText>(R.id.editTextExpenseName)
        val editTextExpenseAmount = findViewById<EditText>(R.id.editTextExpenseAmount)
        val buttonAddExpense = findViewById<Button>(R.id.buttonAddExpense)
        val textViewDate = findViewById<TextView>(R.id.textViewExpenseDate)
        val btnFinancialTips = findViewById<Button>(R.id.buttonFinancialTips)

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
                editTextExpenseName.error = "Please enter an expense name"
                return@setOnClickListener
            }

            if(amount == null || amount <= 0) {
                editTextExpenseAmount.error = "Please enter a valid amount"
                return@setOnClickListener
            }

            if (date == "Select Date" || date.isEmpty()) {
                textViewDate.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                return@setOnClickListener
            }

            expenseList.add(Expense(name, amount, date))
            expenseAdapter.notifyItemInserted(expenseList.size - 1)

            // Save to file after adding
            saveExpensesToFile(this, expenseList)

            editTextExpenseName.text.clear()
            editTextExpenseAmount.text.clear()

            footerFragment.updateTotalExpense(amount)
        }

        btnFinancialTips.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.cibc.com/en/imperial-service/insights.html")
            startActivity(intent)
        }
    }

    private fun deleteExpense(position: Int) {
        val expenseAmount = expenseList[position].amount
        expenseList.removeAt(position)
        expenseAdapter.notifyItemRemoved(position)
        expenseAdapter.notifyItemRangeChanged(position, expenseList.size)

        // Update the total expense in the footer
        if (expenseList.isEmpty()) {
            // Set total expense to 0
            Log.d("ExpenseDeletion", "All expenses deleted. Setting total to 0.")
            footerFragment.updateTotalExpense(-expenseAmount)
        } else {
            // Subtract the deleted expense amount
            Log.d("ExpenseDeletion", "Expense deleted")
            footerFragment.updateTotalExpense(-expenseAmount)
        }

        // Save update list to file
        saveExpensesToFile(this, expenseList)
        Log.d("ExpenseDeletion", "Expenses saved to file successfully")
    }

    // Save expenses to a JSON file
    private fun saveExpensesToFile(context: Context, expenseList: List<Expense>) {
        try {
            val json = Gson().toJson(expenseList)
            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { output ->
                output.write(json.toByteArray())
            }
            Log.d("FileIOStorage", "Expenses saved to file successfully")
        } catch (e: Exception) {
            Log.e("FileIOStorage", "Error saving expenses to file: ${e.message}")
        }
    }

    // Load Expenses from the file
    private fun loadExpensesFromFile(context: Context): List<Expense> {
        val expenseList = mutableListOf<Expense>()
        try {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists()) {
                // File doesn't exist, so create it
                file.createNewFile()
                Log.d("FileIOStorage", "File created: $FILE_NAME")
            } else {
                // Read from the file if it exists
                val json = file.readText()
                val type = object : TypeToken<List<Expense>>() {}.type
                val loadedExpenses: List<Expense> = Gson().fromJson(json, type)
                expenseList.addAll(loadedExpenses)
                Log.d("FileIOStorage", "Expenses loaded from file successfully")
            }
        } catch (e: Exception) {
            Log.e("FileIOStorage", "Error loading expenses from file: ${e.message}")
        }
        return expenseList
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivityLifecycle", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivityLifecycle", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivityLifecycle", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivityLifecycle", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivityLifecycle", "onDestroy called")
    }
}