package com.example.assignment

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var categoryInput: EditText
    private lateinit var addBtn: Button
    private lateinit var showAllBtn: Button
    private lateinit var filterBtn: Button
    private lateinit var listView: ListView
    private lateinit var spinner: Spinner

    private lateinit var db: ContactDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = ContactDatabase.getDatabase(this)

        nameInput = findViewById(R.id.nameInput)
        phoneInput = findViewById(R.id.phoneInput)
        categoryInput = findViewById(R.id.categoryInput)
        addBtn = findViewById(R.id.addBtn)
        showAllBtn = findViewById(R.id.showAllBtn)
        filterBtn = findViewById(R.id.filterBtn)
        listView = findViewById(R.id.listView)
        spinner = findViewById(R.id.categorySpinner)

        loadAllContacts()
        loadCategories()

        addBtn.setOnClickListener {
            val name = nameInput.text.toString()
            val phone = phoneInput.text.toString()
            val category = categoryInput.text.toString()

            if (name.isEmpty() || phone.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                db.contactDao().insert(Contact(name = name, phone = phone, category = category))
                loadAllContacts()
                loadCategories()
            }
        }

        showAllBtn.setOnClickListener {
            loadAllContacts()
        }

        filterBtn.setOnClickListener {
            val selected = spinner.selectedItem?.toString()
            if (selected != null) loadCategory(selected)
        }

        // Dialer on click
        listView.setOnItemClickListener { _, _, position, _ ->
            val contact = listView.adapter.getItem(position) as Contact
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${contact.phone}")
            startActivity(intent)
        }
    }

    private fun loadAllContacts() {
        lifecycleScope.launch {
            val contacts = db.contactDao().getAllContacts()
            listView.adapter = ContactAdapter(this@MainActivity, contacts)
        }
    }

    private fun loadCategory(cat: String) {
        lifecycleScope.launch {
            val contacts = db.contactDao().getContactsByCategory(cat)
            listView.adapter = ContactAdapter(this@MainActivity, contacts)
        }
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            val categories = db.contactDao().getCategories()
            val adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                categories
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }
}
