package ca.ulaval.ima.residencemanager.ui.market

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
    var emailDeConnexion: String? = null;

    private var AnnonceList: ArrayList<Annonce>  = ArrayList()
    private lateinit var  firebaseRef2 : DatabaseReference
    private lateinit var firebaseDatabaseRefAnnonce: DatabaseReference



    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_articlein)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this,R.color.red)))
        supportActionBar?.title = "Ajouter Votre Article A vendre "
        viewModel = ViewModelProvider(this).get(MarketViewModel::class.java)

        firebaseRef = FirebaseDatabase.getInstance().getReference("Etudiant")
        firebaseRefAnonce = FirebaseDatabase.getInstance().getReference("Annonces")
        firebaseRef2 = FirebaseDatabase.getInstance().getReference("Annonces")

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Etudiant")





        val monBouton = findViewById<Button>(R.id.btnSoumettre)
        var estCocher = "Non"


        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val DateSoumisssion = dateFormat.format(currentDate)
        val nomVendeur = ""
        val description = ""

        emailDeConnexion = DataManager.userEmail
        if (emailDeConnexion != null) {
        }

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


            val nomPorduit = findViewById<EditText>(R.id.text_produit).text.toString() ;
            val telephone = findViewById<EditText>(R.id.text_telephoneVendeur).text.toString() ;
            val prixString = findViewById<EditText>(R.id.text_prix).text.toString().trim() ;
            val prix = prixString.toIntOrNull()

            val radioGroup = findViewById<RadioGroup>(R.id.radioGroupSexe)
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                 estCocher = when (checkedId) {
                    R.id.radioHomme -> "NON"
                    R.id.radioFemme -> "OUI"
                    else -> "NON"
                }
            }



        if (nomPorduit.isNullOrEmpty()) {
               Toast.makeText(this, "Veuillez remplir le champs Nom produit", Toast.LENGTH_LONG).show()
            return@setOnClickListener }

        if (telephone.isNullOrEmpty()) {
            Toast.makeText(this, "Veuillez remplir le champs telephone", Toast.LENGTH_LONG).show()
            return@setOnClickListener
        }


        if (prix == null) {
               Toast.makeText(this, "Veuillez remplir le champs Prix", Toast.LENGTH_LONG).show()
               return@setOnClickListener
             }


            val annonce =
                Annonce(
                    nomAnnonceur = DataManager.etudiantCourant?.nom,
                    idDemandeSelec = Random.nextInt(),
                    idAnnonce = emailDeConnexion,
                    nomProduit = nomPorduit,
                    telephone = telephone,
                    prix = prix,
                    estDiscutable = estCocher,
                    imageProduit = "url_to_image.jpg",
                    dsecription = description,
                    dateMiseEnLigne =  DateSoumisssion   ,

                    )


            if (annonce != null) {
                saveDataEtudiant(annonce)
            }
            fetchData()
            Toast.makeText(this, "VOtre Article a bien été Soumis", Toast.LENGTH_LONG).show()

        }
        }
        private fun saveDataEtudiant(annonce: Annonce) {
        val annonceId = firebaseRefAnonce.push().key!!
        val annoncecode = emailDeConnexion;
        firebaseRefAnonce.child(annonceId).setValue(annonce)
              .addOnCompleteListener{
              }
    }

    private fun fetchData() {
        firebaseRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                AnnonceList.clear()
                if (snapshot.exists()){
                    for (contactSnap in snapshot.children){
                        val contacts = contactSnap.getValue(Annonce::class.java)
                        AnnonceList.add(contacts!!)

                        Log.w("MyAppTag", "recuperationReussiBIENNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN")
                        contacts.dsecription?.let { Log.w("MyAppTag", it) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }




    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}