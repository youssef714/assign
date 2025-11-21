package com.example.assignment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ContactAdapter(
    val context: Context,
    val contacts: List<Contact>
) : BaseAdapter() {

    override fun getCount(): Int = contacts.size
    override fun getItem(i: Int): Any = contacts[i]
    override fun getItemId(i: Int): Long = contacts[i].id.toLong()

    override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)

        val contact = contacts[i]

        val name = view.findViewById<TextView>(android.R.id.text1)
        val category = view.findViewById<TextView>(android.R.id.text2)

        name.text = contact.name
        category.text = contact.category

        return view
    }
}
