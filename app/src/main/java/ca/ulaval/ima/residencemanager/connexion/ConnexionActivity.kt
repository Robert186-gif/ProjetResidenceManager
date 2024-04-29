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
import ca.ulaval.ima.residencemanager.connexion.EnregistrementActivity
import ca.ulaval.ima.residencemanager.MainActivity
import ca.ulaval.ima.residencemanager.databinding.ActivityConnexionBinding
import ca.ulaval.ima.residencemanager.ui.market.MarketViewModel
import com.google.firebase.auth.FirebaseAuth



class ConnexionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnexionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userViewModel: MarketViewModel

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

    override fun onStart() {
        super.onStart()
    }
}