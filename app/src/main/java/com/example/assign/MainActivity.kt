package com.example.assignment

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var inputName: EditText
    private lateinit var inputPhone: EditText
    private lateinit var inputCategory: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnShowAll: Button
    private lateinit var btnFilter: Button
    private lateinit var contactsList: ListView
    private lateinit var categorySelector: Spinner

    private lateinit var database: ContactDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = ContactDatabase.getDatabase(this)

        initializeViews()
        populateContacts()
        refreshCategorySpinner()

        btnAdd.setOnClickListener { handleAddContact() }
        btnShowAll.setOnClickListener { populateContacts() }
        btnFilter.setOnClickListener { filterBySelectedCategory() }

        contactsList.setOnItemClickListener { _, _, position, _ ->
            val chosen = contactsList.adapter.getItem(position) as Contact
            openDialer(chosen.phone)
        }
    }

    private fun initializeViews() {
        inputName = findViewById(R.id.nameInput)
        inputPhone = findViewById(R.id.phoneInput)
        inputCategory = findViewById(R.id.categoryInput)
        btnAdd = findViewById(R.id.addBtn)
        btnShowAll = findViewById(R.id.showAllBtn)
        btnFilter = findViewById(R.id.filterBtn)
        contactsList = findViewById(R.id.listView)
        categorySelector = findViewById(R.id.categorySpinner)
    }

    private fun handleAddContact() {
        val nameValue = inputName.text.toString().trim()
        val phoneValue = inputPhone.text.toString().trim()
        val categoryValue = inputCategory.text.toString().trim()

        if (nameValue.isBlank() || phoneValue.isBlank() || categoryValue.isBlank()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            database.contactDao().insert(
                Contact(
                    name = nameValue,
                    phone = phoneValue,
                    category = categoryValue
                )
            )
            populateContacts()
            refreshCategorySpinner()
        }
    }

    private fun populateContacts() {
        lifecycleScope.launch {
            val allContacts = database.contactDao().getAllContacts()
            contactsList.adapter = ContactAdapter(this@MainActivity, allContacts)
        }
    }

    private fun filterBySelectedCategory() {
        val selectedCategory = categorySelector.selectedItem?.toString() ?: return

        lifecycleScope.launch {
            val filtered = database.contactDao().getContactsByCategory(selectedCategory)
            contactsList.adapter = ContactAdapter(this@MainActivity, filtered)
        }
    }

    private fun refreshCategorySpinner() {
        lifecycleScope.launch {
            val cats = database.contactDao().getCategories()

            val spinAdapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                cats
            )
            spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySelector.adapter = spinAdapter
        }
    }

    private fun openDialer(number: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }
        startActivity(dialIntent)
    }
}
