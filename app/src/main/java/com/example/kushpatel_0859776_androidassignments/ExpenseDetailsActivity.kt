package com.example.kushpatel_0859776_androidassignments

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kushpatel_0859776_androidassignment6.R

class ExpenseDetailsActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_details)

        val tvName = findViewById<TextView>(R.id.textViewExpenseName)
        val tvAmount = findViewById<TextView>(R.id.textViewExpenseAmount)
        val tvDate = findViewById<TextView>(R.id.textViewExpenseDate)
        val btnBackHome = findViewById<Button>(R.id.buttonBackHome)

        val name = intent.getStringExtra("expense_name")
        val amount = intent.getStringExtra("expense_amount")
        val date = intent.getStringExtra("expense_date")

        tvName.text = "Expense Name: $name"
        tvAmount.text = "ExpenseAmount: $amount"
        tvDate.text = "Expense Date: $date"

        btnBackHome.setOnClickListener {
            finish()
        }
    }
}