package ca.ulaval.ima.residencemanager.ui.market

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import ca.ulaval.ima.residencemanager.Annonce
import ca.ulaval.ima.residencemanager.DataManager
import ca.ulaval.ima.residencemanager.Etudiant
import ca.ulaval.ima.residencemanager.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.random.Random

class AddArticleinActivity : AppCompatActivity() {
    private lateinit var firebaseRef : DatabaseReference
    private lateinit var firebaseRefAnonce : DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var viewModel: MarketViewModel


    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_articlein)
        viewModel = ViewModelProvider(this).get(MarketViewModel::class.java)

        firebaseRef = FirebaseDatabase.getInstance().getReference("Etudiant")
        firebaseRefAnonce = FirebaseDatabase.getInstance().getReference("Annonces")

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Etudiant")

        val monBouton = findViewById<Button>(R.id.button_soumettre)
        var estCocher = "Non"


        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val DateSoumisssion = dateFormat.format(currentDate)

        val nomPorduit = findViewById<TextView>(R.id.text_produit).text.toString() ;
        val nomVendeur = findViewById<TextView>(R.id.textView_nom).text.toString() ;
        val telephone = findViewById<TextView>(R.id.text_telephoneVendeur).text.toString() ;
        val description = findViewById<TextView>(R.id.text_dsecription).text.toString() ;
        val estDiscutable =  findViewById(R.id.switch1) as Switch ;


        val prix = findViewById<TextView>(R.id.text_prix).text.toString().toIntOrNull() ;
        var emailDeConnexion: String? = null;
        Log.w("MyAppTag", "ffffffffffffffffffffffffffffffffffffffffffffffffff")
        emailDeConnexion = DataManager.userEmail
        if (emailDeConnexion != null) {
            Log.w("MyAppTag", emailDeConnexion)
            Log.w("MyAppTag", "ffffffffffffffffffffffffffffffffffffffffffffffffff")
        }
        estDiscutable.setOnCheckedChangeListener { _, isChecked ->
            estCocher = if (isChecked) "Oui" else "Non"
        }

        val annonce =
                Annonce(
                    idDemandeSelec = Random.nextInt(),
                    idAnnonce = emailDeConnexion,
                    nomProduit = nomPorduit,
                    telephone = telephone,
                    prix = 7,
                    estDiscutable = estCocher,
                    imageProduit = "url_to_image.jpg",
                    dsecription = description,
                    dateMiseEnLigne =  DateSoumisssion   ,

                    )



        val etudiantList = mutableListOf<Etudiant>()
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (etudiantSnapshot in dataSnapshot.children) {
                    val etudiant = etudiantSnapshot.getValue(Etudiant::class.java)
                    etudiant?.let { etudiantList.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddArticleinActivity, "Failed to load students.", Toast.LENGTH_SHORT).show()
            }
        })


        monBouton.setOnClickListener {


        if (nomPorduit.isNullOrEmpty()) {
            Toast.makeText(this, "Veuillez remplir le champs Nom produit", Toast.LENGTH_LONG).show()
            return@setOnClickListener
        }

        if (nomVendeur.isNullOrEmpty()) {
            Toast.makeText(this, "Veuillez remplir le champs Nom vendeur", Toast.LENGTH_LONG).show()
            return@setOnClickListener
        }

        if (telephone.isNullOrEmpty()) {
            Toast.makeText(this, "Veuillez remplir le champs telephone", Toast.LENGTH_LONG).show()
            return@setOnClickListener
        }

        if (description.isNullOrEmpty()) {
            Toast.makeText(this, "Veuillez remplir le champs description", Toast.LENGTH_LONG).show()
            return@setOnClickListener
        }
            Log.w("MyAppTag", "sssssssssssssssssssssssssssssssssssssssssssss44444444")
        //if (prix == null) {
            //   Toast.makeText(this, "Veuillez remplir le champs Prix", Toast.LENGTH_LONG).show()
            //   return@setOnClickListener
            // }
            Log.w("MyAppTag", "sssssssssssssssssssssssssssssssssssssssssssss555555555555555")
            if (annonce != null) {
                Log.w("MyAppTag", "sssssssssssssssssssssssssssssssssssssssssssss6666666")
                saveDataEtudiant(annonce)
            }
        }
       // Fin de la définition du ValueEventListener
        }
        private fun saveDataEtudiant(annonce: Annonce) {
        Log.w("MyAppTag", "sssssssssssssssssssssssssssssssssssssssssssss1111111")
        val annonceId = firebaseRefAnonce.push().key!!
        val annoncecode = annonce.idAnnonce;
        firebaseRefAnonce.child(annoncecode.toString()).setValue(annonce)
              .addOnCompleteListener{
              }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}