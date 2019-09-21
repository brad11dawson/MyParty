package com.partytime.myparty

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class PartyViewActivity : AppCompatActivity() {
    // [START declare_database]
    private lateinit var db: FirebaseFirestore
    // [END declare_database]

    var title: String = ""
    var description: String = ""
    var address: String = ""
    var partyType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party_view)
        db = FirebaseFirestore.getInstance()

        val partyId: String = intent.getStringExtra("partyId")
        setPartyInfo(partyId)
        Log.d(TAG, "viewing party with id: $partyId")
    }

    private fun setPartyInfo(partyId: String) {
        db.collection("parties").document(partyId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    title = document.data?.get("title").toString()
                    description = document.data?.get("description").toString()
                    address = document.data?.get("address").toString()
                    partyType = document.data?.get("type").toString()

                    findViewById<TextView>(R.id.title).text = title
                    findViewById<TextView>(R.id.partyDescriptionText).text = description
                    findViewById<TextView>(R.id.addressText).text = address
                    findViewById<TextView>(R.id.partyTypeText).text = partyType
                    val hostEmail: String = document.data?.get("hostEmail").toString()
                    Log.d(TAG, "host email is: $hostEmail")
                    db.collection("users").document(hostEmail).get()
                        .addOnSuccessListener { document ->
                            val fName: String = document.data?.get("firstName").toString()
                            val lName: String = document.data?.get("lastName").toString()
                            val host = "Hosted by: $fName $lName"
                            findViewById<TextView>(R.id.hostNameText).text = host
                        }
                }
            }.addOnFailureListener {
                Log.d(TAG, "failed with ", it)
            }
    }

    fun gotoHome(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "PartyViewActivity"
    }

    fun rsvpToast(view: View) {
        val text = "RSVP Successfull"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
