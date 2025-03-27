package com.example.kushpatel_0859776_androidassignments

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kushpatel_0859776_androidassignment6.R

// Adapter class for managing a list of expenses in a RecyclerView
class ExpenseAdapter(private val expenseList: MutableList<Expense>,
    private val onDeleteExpense: (Int) -> Unit) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

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
            val intent = Intent(holder.itemView.context, ExpenseDetailsActivity::class.java)
            intent.putExtra("expense_name", expense.name)
            intent.putExtra("expense_amount", expense.amount.toString())
            intent.putExtra("expense_date", expense.date)
            holder.itemView.context.startActivity(intent)
        }
        // Set delete button click listener
        holder.deleteButton.setOnClickListener {
            onDeleteExpense(position)
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = expenseList.size
}