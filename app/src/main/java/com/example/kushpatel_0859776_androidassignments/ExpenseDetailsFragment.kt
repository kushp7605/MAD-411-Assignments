package com.example.kushpatel_0859776_androidassignments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kushpatel_0859776_androidassignment6.R


class ExpenseDetailsFragment : Fragment() {
    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense_details, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.textViewExpenseName)
        val tvAmount = view.findViewById<TextView>(R.id.textViewExpenseAmount)
        val tvDate = view.findViewById<TextView>(R.id.textViewExpenseDate)
        val btnBackHome = view.findViewById<Button>(R.id.buttonBackHome)

        // Retrieve data from arguments
        val name = arguments?.getString("expense_name")
        val amount = arguments?.getString("expense_amount")
        val date = arguments?.getString("expense_date")

        arguments?.clear()

        // Set Text Values
        tvName.text = "Expense Name: $name"
        tvAmount.text = "Expense Amount: $$amount"
        tvDate.text = "Expense Date: $date"

        // Navigate back
        btnBackHome.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}