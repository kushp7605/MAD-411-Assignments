package com.example.kushpatel_0859776_androidassignments.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kushpatel_0859776_androidassignment6.R
import com.example.kushpatel_0859776_androidassignments.adapters.ExpenseAdapter
import com.example.kushpatel_0859776_androidassignments.network.RetrofitInstance
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar

// Data class representing an expense with a name and amount
data class Expense(val name: String, val amount: Double, val date: String, val currency: String, val convertedCost: Double)

private const val FILE_NAME = "expenses.txt"

class ExpenseListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter
    private val expenseList = mutableListOf<Expense>()

    private lateinit var editTextExpenseName: EditText
    private lateinit var editTextExpenseAmount: EditText
    private lateinit var buttonAddExpense: Button
    private lateinit var textViewDate: TextView
    private lateinit var btnFinancialTips: Button
    private lateinit var footerFragment: FooterFragment

    // Currency Conversion
    private lateinit var spinnerCurrency: Spinner
    private lateinit var textViewConvertedAmount: MaterialTextView
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchConvertCurrency: MaterialSwitch


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense_list, container, false)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.commit {
            replace(R.id.fragmentHeaderContainer, HeaderFragment())
        }

        // Added  footerfragment dynamically
        footerFragment = FooterFragment()
        requireActivity().supportFragmentManager.commit {
            replace(R.id.fragmentFooterContainer, footerFragment)
        }

        val navController = findNavController()

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        expenseAdapter = ExpenseAdapter(expenseList, { position -> deleteExpense(position) }, navController)
        recyclerView.adapter = expenseAdapter

        // Initialize UI Elements
        editTextExpenseName = view.findViewById(R.id.editTextExpenseName)
        editTextExpenseAmount = view.findViewById(R.id.editTextExpenseAmount)
        buttonAddExpense = view.findViewById(R.id.buttonAddExpense)
        textViewDate = view.findViewById(R.id.textViewExpenseDate)
        btnFinancialTips = view.findViewById(R.id.buttonFinancialTips)

        switchConvertCurrency = view.findViewById<MaterialSwitch>(R.id.switchCurrencyConversion)
        spinnerCurrency = view.findViewById(R.id.spinnerCurrency)
        textViewConvertedAmount = view.findViewById<MaterialTextView>(R.id.textViewConvertedCost)

        // Fetch currencies from the API
        fetchCurrencies()

        // Load previously saved expenses
        if (expenseList.isEmpty()) {
            expenseList.addAll(loadExpensesFromFile(requireContext()))
            expenseAdapter.notifyDataSetChanged()
        }

        updateFooterTotalExpense()

        // Date Picker Dialog
        textViewDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                textViewDate.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            }, year, month, day)
            datePicker.show()
        }

        // Add Expense
        buttonAddExpense.setOnClickListener {
            val name = editTextExpenseName.text.toString().trim()
            val amount = editTextExpenseAmount.text.toString().trim().toDoubleOrNull()
            val date = textViewDate.text.toString()

            if (name.isEmpty()) {
                editTextExpenseName.error = "Please enter an expense name"
                return@setOnClickListener
            }
            if (amount == null || amount <= 0) {
                editTextExpenseAmount.error = "Please enter a valid amount"
                return@setOnClickListener
            }

            if (date == "Select Date" || date.isEmpty()) {
                textViewDate.error = "Please select a date"
                return@setOnClickListener
            }

            expenseList.add(Expense(name, amount, date, "cad", amount))
            expenseAdapter.notifyItemInserted(expenseList.size - 1)

            // Save to file
            saveExpensesToFile(requireContext(), expenseList)

            editTextExpenseName.text.clear()
            editTextExpenseAmount.text.clear()

            updateFooterTotalExpense()
        }

        // Open Financial Tips Page
        btnFinancialTips.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cibc.com/en/imperial-service/insights.html"))
            startActivity(intent)
        }

        // Switch for Currency Conversion
        switchConvertCurrency.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val selectedCurrency = spinnerCurrency.selectedItem?.toString() ?: "cad"
                convertExpensesToCurrency(selectedCurrency)
            } else {
                // Reset to original amounts in CAD
                for (i in expenseList.indices) {
                    val expense = expenseList[i]
                    expenseList[i] = Expense(expense.name, expense.amount, expense.date, "cad", expense.amount)
                }
                expenseAdapter.notifyDataSetChanged()
                textViewConvertedAmount.text = "Converted Cost: ${expenseList.sumOf { it.amount }}"
            }
        }

        // Spinner for Currency
        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(switchConvertCurrency.isChecked) {
                    val selectedCurrency = spinnerCurrency.selectedItem.toString()
                    onCurrencySelected(selectedCurrency)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun convertExpensesToCurrency(selectedCurrency: String) {
        lifecycleScope.launch {
            try {
                val exchangeRatesResponse = withContext(Dispatchers.IO) {
                    // Base Currency is CAD
                    RetrofitInstance.api.getExchangeRates()
                }

                val conversionRates = exchangeRatesResponse.cad[selectedCurrency.lowercase()] ?: 1.0

                for(i in expenseList.indices) {
                    val expense = expenseList[i]
                    expenseList[i] = Expense(expense.name, expense.amount, expense.date, selectedCurrency, expense.amount * conversionRates)

                }

                expenseAdapter.notifyDataSetChanged()

                val totalAmountConverted = expenseList.sumOf { it.convertedCost }
                textViewConvertedAmount.text = "Converted Cost: $totalAmountConverted ${selectedCurrency.uppercase()}"
            } catch (e: Exception) {
                Snackbar.make(requireView(), "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCurrencies() {
        lifecycleScope.launch {
            try {
                val currencies = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getCurrencies()
                }

                Log.d("CurrencyFetch", "Fetched currencies: $currencies")

                if(currencies.isNotEmpty()) {
                    val currencyList = currencies.keys.toList()
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencyList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCurrency.adapter = adapter

                    val defaultPosition = currencyList.indexOf("cad")
                    if (defaultPosition != -1) {
                        spinnerCurrency.setSelection(defaultPosition)
                    }
                }
            } catch (e: Exception) {
                Log.e("CurrencyFetch", "Error fetching currencies: ${e.message}")
                Snackbar.make(requireView(), "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun onCurrencySelected(newCurrency: String) {
        lifecycleScope.launch {
            try {
                val exchangeRatesResponse = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getExchangeRates()
                }

                val conversionRate = exchangeRatesResponse.cad[newCurrency.lowercase()] ?: 1.0

                for (i in expenseList.indices) {
                    val expense = expenseList[i]
                    expenseList[i] = Expense(expense.name, expense.amount, expense.date, newCurrency, expense.amount * conversionRate)
                }

                expenseAdapter.notifyDataSetChanged()

                val convertedAmount = expenseList.sumOf { it.convertedCost }
                textViewConvertedAmount.text = "Converted Cost: $convertedAmount ${newCurrency.uppercase()}"
            } catch (e: Exception) {
                Snackbar.make(requireView(), "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    // Footer Fragment for total expense amount
    private fun updateFooterTotalExpense() {
        val total = expenseList.sumOf { it.amount }
        footerFragment.updateTotalExpense(total)
    }

    private fun deleteExpense(position: Int) {
        expenseList[position].amount
        expenseList.removeAt(position)
        expenseAdapter.notifyItemRemoved(position)
        expenseAdapter.notifyItemRangeChanged(position, expenseList.size)

        // Update the total expense in the footer
        if (expenseList.isEmpty()) {
            // Set total expense to 0
            Log.d("ExpenseDeletion", "All expenses deleted. Setting total to 0.")
            updateFooterTotalExpense()
        } else {
            // Subtract the deleted expense amount
            Log.d("ExpenseDeletion", "Expense deleted")
            updateFooterTotalExpense()
        }

        // Save update list to file
        saveExpensesToFile(requireContext(), expenseList)
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