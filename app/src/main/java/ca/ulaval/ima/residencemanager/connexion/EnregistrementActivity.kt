package ca.ulaval.ima.residencemanager.connexion

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ca.ulaval.ima.residencemanager.Etudiant
import ca.ulaval.ima.residencemanager.databinding.ActivityEnregistrementBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EnregistrementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnregistrementBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEnregistrementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance().getReference("Etudiant")

        binding.texteConnection.setOnClickListener {
            val intent = Intent(this, ConnexionActivity::class.java)
            startActivity(intent)
        }
        binding.buttonSoumettre.setOnClickListener {
            val email = binding.idemailtexte.text.toString()
            val pass = binding.passwordTexte.text.toString()
            val confirmPass = binding.confirmmMotDepass.text.toString()
            val numeroChambre = binding.idChambretexte.text.toString()
            val nom = binding.idNomtexte.text.toString()
            val prenom = binding.idPreNomText.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && numeroChambre.isNotEmpty()  && nom.isNotEmpty() && prenom.isNotEmpty()) {
                if (pass == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, ConnexionActivity::class.java)
                            saveDataEtudiant()
                            startActivity(intent)

                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "la confirmation du mot de passe nest pas correct" + pass + "confirmation"+ confirmPass, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "remplir tout les champs ", Toast.LENGTH_SHORT).show()

            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun saveDataEtudiant() {
        val name = binding.idPreNomText.text.toString()
        val numChambre = (binding.idChambretexte.text.toString()).toInt()
        val email = binding.idemailtexte.text.toString()
        val prenom = binding.idPreNomText.text.toString()
        //val etudiantId = firebaseRef.push().key!!
        val etudiantId = numChambre;
        val etudiant = Etudiant(numChambre,name , prenom , email,"","",0,emptyList(),emptyList());
                    firebaseRef.child(numChambre.toString()).setValue(etudiant)
                        .addOnCompleteListener{
                        }
                }
  }


