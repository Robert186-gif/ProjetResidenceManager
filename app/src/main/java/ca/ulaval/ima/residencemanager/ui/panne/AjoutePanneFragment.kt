package ca.ulaval.ima.residencemanager.ui.panne

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import androidx.navigation.fragment.findNavController
import ca.ulaval.ima.residencemanager.DataManager
import ca.ulaval.ima.residencemanager.Panne
import ca.ulaval.ima.residencemanager.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar

class AjoutePanneFragment : Fragment() {


    private var PanneList: ArrayList<Panne>  = ArrayList()
    private lateinit var firebaseRef : DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajoute_panne, container, false)

        firebaseRef = FirebaseDatabase.getInstance().getReference("Pannes")



        val typeEditText = view.findViewById<EditText>(R.id.editTextText2)
        val descriptionMultiAutoCompleteTextView = view.findViewById<MultiAutoCompleteTextView>(R.id.multiAutoCompleteTextView)
        val submitButton = view.findViewById<Button>(R.id.btnSoumettre)


        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val DateSoumisssion = dateFormat.format(currentDate)


        submitButton.setOnClickListener {
            val type = typeEditText.text.toString()
            val description = descriptionMultiAutoCompleteTextView.text.toString()

            val panne = Panne(
                idPanne = DataManager.etudiantCourant?.email,
                typePanne = type,
                description = description,
                initiateur = DataManager.etudiantCourant?.nom,
                dateCreation = DateSoumisssion
            )

            if (panne != null) {
                saveDataEtudiant(panne)
            }

            findNavController().navigate(R.id.action_ajoutePanneFragment_to_nav_panne)

        }
        return view
    }

    private fun saveDataEtudiant(panne: Panne) {
        val panneId = firebaseRef.push().key!!
        firebaseRef.child(panneId).setValue(panne)
            .addOnCompleteListener{
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}