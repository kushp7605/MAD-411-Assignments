package com.example.kushpatel_0859776_androidassignments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.kushpatel_0859776_androidassignment6.R

// Adapter class for managing a list of expenses in a RecyclerView
class ExpenseAdapter(
    private val expenseList: MutableList<Expense>,
    private val onDeleteExpense: (Int) -> Unit,
    private val navController: NavController
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // ViewHolder class for holding the views of each expense item
    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextView for expense name
        val textViewExpenseName: TextView = itemView.findViewById(R.id.textViewExpenseName)
        // TextView for expense amount
        val textViewExpenseAmount: TextView = itemView.findViewById(R.id.textViewExpenseAmount)
        // TextView for expense date
        val textViewExpenseDate: TextView = itemView.findViewById(R.id.textViewExpenseDate)
        // Button for showing expense details
        val showDetailsButton: Button = itemView.findViewById(R.id.showDetailsButton)
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
        // Set expense date
        holder.textViewExpenseDate.text = expense.date

        // Set show details button click listener
        holder.showDetailsButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("expense_name", expense.name)
            bundle.putString("expense_amount", expense.amount.toString())
            bundle.putString("expense_date", expense.date)
            // Navigate using the bundle
            navController.navigate(R.id.action_expenseListFragment_to_expenseDetailsFragment, bundle)
        }
        // Set delete button click listener
        holder.deleteButton.setOnClickListener {
            Log.d("ExpenseAdapter", "Delete button clicked for position: $position")
            onDeleteExpense(position)
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = expenseList.size
}