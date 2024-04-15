package ca.ulaval.ima.residencemanager.Connexion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ca.ulaval.ima.residencemanager.MainActivity
import ca.ulaval.ima.residencemanager.databinding.ActivityConnexionBinding
import com.google.firebase.auth.FirebaseAuth



class ConnexionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnexionBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnexionBinding.inflate(layoutInflater)
        setContentView(binding.root)


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

    override fun onStart() {
        super.onStart()

    }
}