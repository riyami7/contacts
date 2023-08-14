package com.example.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var contactsAdapter: ContactsAdapter
    lateinit var contacts : List<Contact>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize adapter and attach it to RecyclerView
        contactsAdapter = ContactsAdapter(listOf()) { view, position ->
//            Toast.makeText(this,"${contacts[position]} $position clicked", Toast.LENGTH_SHORT).show()
//            val intent = Intent(Intent.ACTION_DIAL).apply {
//                data = Uri.parse("tel:${contacts[position].phoneNumber}")
//            }
//            if (intent.resolveActivity(packageManager) != null) {
//                startActivity(intent)
//            }
            Toast.makeText(this,"${contacts[position]} $position clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_CALL)
            intent.setData(Uri.parse("tel:${contacts[position].phoneNumber}"));

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)

            }

        }
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactsAdapter

        // This allows main thread accessing Network
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                //.detectDiskReads()
                .detectDiskWrites()
                // .detectNetwork()  // remove this line to allow network access
                .penaltyLog()  // Log detected violations to the system log
                .build()
        )


        makeCall()
        // Fetch contacts and update adapter data
        var button = findViewById<Button>(R.id.button_refresh)
        button.setOnClickListener {
            makeCall()
        }



    }

    private fun makeCall() {
        lifecycleScope.launch(Dispatchers.Main) {
            contacts = fetchContacts()
            if (contacts != null) {
                contactsAdapter.contacts = contacts
            }
            contactsAdapter.notifyDataSetChanged()
        }
    }
}


internal interface ContactService {
    @GET("/contacts")
    fun getContacts(): Call<List<Contact>>
}

suspend fun fetchContacts(): List<Contact> {

//    val logging = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }

//    val client = OkHttpClient.Builder()
//        .addInterceptor(logging)
//        .build()

//    val gson = GsonBuilder()
//        .registerTypeAdapterFactory(LenientTypeAdapterFactory())
//        .create()

    val retrofit = Retrofit.Builder()
        //.client(client)
        .baseUrl("https://contactsapp.brytvu.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Create a Call object for the GET request

    // Create a Call object for the GET request
    val call: Call<List<Contact>> = retrofit.create(ContactService::class.java).getContacts()

    // Execute the HTTP request and get the response

    // Execute the HTTP request and get the response
    var jsonObj = call.execute().body()
    return jsonObj!!
}