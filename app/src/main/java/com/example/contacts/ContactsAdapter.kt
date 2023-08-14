package com.example.contacts

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(var contacts: List<Contact>, private val listener: RecyclerViewClickListener) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {


    class ViewHolder(itemView: View, private val listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val name: TextView = itemView.findViewById(R.id.name)
        val phoneNumber : ImageView = itemView.findViewById(R.id.phone_number)
//        init {
//            phoneNumber.setOnClickListener(this)
//        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            // Call your listener callback
            Log.d("ContactsAdapter", adapterPosition.toString())
            listener(v!!, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.name.text = contact.name
//        holder.phoneNumber.text = contact.phoneNumber
    }

    override fun getItemCount() = contacts.size
}

typealias RecyclerViewClickListener = (view: View, position: Int) -> Unit
