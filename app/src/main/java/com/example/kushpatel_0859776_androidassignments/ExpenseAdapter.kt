package com.example.kushpatel_0859776_androidassignments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kushpatel_0859776_androidassignment6.R

// Adapter class for managing a list of expenses in a RecyclerView
class ExpenseAdapter(private val expenseList: MutableList<Expense>,
    private val onDeleteButton: (Int) -> Unit) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // ViewHolder class for holding the views of each expense item
    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextView for expense name
        val textViewExpenseName: TextView = itemView.findViewById(R.id.editTextExpenseName)
        // TextView for expense amount
        val textViewExpenseAmount: TextView = itemView.findViewById(R.id.editTextExpenseAmount)
        // Button for deleting the expense
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

    }

    // Inflates the item layout and creates the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    // Binds the data to the views in the ViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        // Set expense name
        holder.textViewExpenseName.text = expense.name
        // Set expense amount
        holder.textViewExpenseAmount.text = "$${expense.amount}"
        // Set delete button click listener
        holder.deleteButton.setOnClickListener { onDeleteButton(position) }
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = expenseList.size
}