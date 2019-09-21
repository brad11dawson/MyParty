package com.partytime.myparty

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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

                }
            }.addOnFailureListener {
                Log.d(TAG, "failed with ", it)
            }
    }



    companion object {
        private const val TAG = "PartyViewActivity"
    }
}
