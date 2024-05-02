package ca.ulaval.ima.residencemanager.ui.bail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.ulaval.ima.residencemanager.DataManager
import ca.ulaval.ima.residencemanager.databinding.FragmentBailBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BailFragment : Fragment() {

    private var _binding: FragmentBailBinding? = null

    private val binding get() = _binding!!

    private lateinit var firebaseDatabaseRef: DatabaseReference
    private lateinit var radioGroupBail: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bailViewModel =
            ViewModelProvider(this).get(BailViewModel::class.java)

        _binding = FragmentBailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("Etudiant")
        radioGroupBail = binding.radioGroupBail
        val textView: TextView = binding.textSlideshow
        bailViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val btnSoumettre = binding.btnSoumettre
        btnSoumettre.setOnClickListener{
            if (radioGroupBail.checkedRadioButtonId == -1)
            {
                val message = "Aucune option n'a été sélectionné!!"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadioButtonId = radioGroupBail.checkedRadioButtonId
            val selectedRadioButton = root.findViewById<RadioButton>(selectedRadioButtonId)
            val selectedOption = selectedRadioButton.text.toString()
            DataManager.etudiantCourant?.contratBail = selectedOption
            firebaseDatabaseRef.child(DataManager.etudiantCourant?.numCambre.toString()).setValue(DataManager.etudiantCourant)
                .addOnSuccessListener {
                    // URL deleted
                    Toast.makeText(requireContext(), "Formulaire envoyée", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}