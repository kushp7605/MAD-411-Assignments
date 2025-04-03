package com.example.kushpatel_0859776_androidassignments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kushpatel_0859776_androidassignment6.R

class FooterFragment : Fragment() {
    private var totalExpense = 0.00

    private lateinit var textViewTotalExpense: TextView

    @SuppressLint("SetTextI18n")
    fun updateTotalExpense(amount: Double) {
        totalExpense = amount
        if(this::textViewTotalExpense.isInitialized) {
            textViewTotalExpense.text = "Total Expense: $$totalExpense"
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_footer, container, false)
        textViewTotalExpense = view.findViewById(R.id.textViewTotalExpense)
        textViewTotalExpense.text = "Total Expense: $$totalExpense"
        return view
    }
}