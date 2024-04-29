package ca.ulaval.ima.residencemanager.connexion

import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import ca.ulaval.ima.residencemanager.DataManager
import ca.ulaval.ima.residencemanager.Etudiant
import ca.ulaval.ima.residencemanager.connexion.EnregistrementActivity
import ca.ulaval.ima.residencemanager.MainActivity
import ca.ulaval.ima.residencemanager.databinding.ActivityConnexionBinding
import ca.ulaval.ima.residencemanager.ui.market.MarketViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class ConnexionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnexionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userViewModel: MarketViewModel
    private lateinit var firebaseDatabaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnexionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel = ViewModelProvider(this).get(MarketViewModel::class.java)


        firebaseAuth = FirebaseAuth.getInstance()
        binding.seConnecter.setOnClickListener {
            val intent = Intent(this, EnregistrementActivity::class.java)
            startActivity(intent)
        }

        binding.buttonConnexion.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        userViewModel.setEmail(email)
                        DataManager.userEmail = email // Sauvegarde de l'email
                        Log.w("MyAppTag", email)
                        Log.w("MyAppTag", "email maintenantmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm")
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "mot de passe ou email incorrect", Toast.LENGTH_SHORT).show()

                    }
                }
            } else {

                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDataFromFirebase()
    {
        //Recuperer le nom, prénom, numero de chambre et l'email
        firebaseDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                DataManager.etudiantList.clear()
                if (dataSnapshot.exists()) {

                    for (etudiantSnap in dataSnapshot.children) {
                        // Get Etudiant object and use the values to update the UI
                        val etudiant = etudiantSnap.getValue(Etudiant::class.java)
                        etudiant?.nom?.let { Log.w("dadsa", it) }
                        DataManager.etudiantList.add(etudiant!!)
                        if (etudiant.email == DataManager.userEmail)
                        {
                            DataManager.etudiantCourant = etudiant

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("dsads", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onStart() {
        super.onStart()
    }
}