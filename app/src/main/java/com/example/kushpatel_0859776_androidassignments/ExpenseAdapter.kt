package com.example.kushpatel_0859776_androidassignments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kushpatel_0859776_androidassignment6.R

class ExpenseAdapter(private val expenseList: MutableList<Expense>,
    private val onDeleteButton: (Int) -> Unit) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewExpenseName: TextView = itemView.findViewById(R.id.editTextExpenseName)
        val textViewExpenseAmount: TextView = itemView.findViewById(R.id.editTextExpenseAmount)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.textViewExpenseName.text = expense.name
        holder.textViewExpenseAmount.text = "$${expense.amount}"
        holder.deleteButton.setOnClickListener { onDeleteButton(position) }
    }

    override fun getItemCount(): Int = expenseList.size
}