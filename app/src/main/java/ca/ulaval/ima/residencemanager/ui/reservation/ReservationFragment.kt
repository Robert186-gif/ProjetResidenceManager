package ca.ulaval.ima.residencemanager.ui.reservation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import ca.ulaval.ima.residencemanager.DataManager
import ca.ulaval.ima.residencemanager.Etudiant
import ca.ulaval.ima.residencemanager.R
import ca.ulaval.ima.residencemanager.Reservation
import ca.ulaval.ima.residencemanager.databinding.FragmentPaiementBinding
import ca.ulaval.ima.residencemanager.databinding.FragmentReservationBinding
import ca.ulaval.ima.residencemanager.ui.bail.BailViewModel
import coil.load
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReservationFragment : Fragment() {

    private var _binding: FragmentReservationBinding? = null

    private val binding get() = _binding!!
    private lateinit var dateArrivee: EditText
    private lateinit var numChambreReserve: Spinner
    private lateinit var reservationDataBase: DatabaseReference
    private lateinit var firebaseDatabaseRef: DatabaseReference
    private var reservation: Reservation = Reservation(
        nomReserveur = null,
        prenomReserveur = null,
        email = null,
        numeroDeChambreReserve = 0,
        dateArrive = null
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val reservationViewModel =
            ViewModelProvider(this).get(ReservationViewModel::class.java)

        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("Etudiant")
        reservationDataBase = FirebaseDatabase.getInstance().getReference("Reservation")
        val textView: TextView = binding.textReservation
        reservationViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        //Spinner pour le numero de chambre
        numChambreReserve = binding.spinnerChambre
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.AvalaibleRooms,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            numChambreReserve.adapter = adapter
        }

        numChambreReserve.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                reservation.numeroDeChambreReserve = selectedItem.toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        getDataFromFirebase()

        //===== Choisir la date d'arrivée
        dateArrivee = binding.editArrive
        val dateArrive = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.MAY)
            set(Calendar.DAY_OF_MONTH, 2)
        }
        val datePicker = DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
            dateArrive.set(Calendar.YEAR, year)
            dateArrive.set(Calendar.MONTH, month)
            dateArrive.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            changerDate(dateArrive)
        }

        dateArrivee.setOnClickListener{
            DatePickerDialog(requireContext(), datePicker, dateArrive.get(Calendar.YEAR), dateArrive.get(Calendar.MONTH),
                dateArrive.get(Calendar.DAY_OF_MONTH)).show()
        }

        //bouton Reserver
        val boutonReserver : Button = binding.btnReserver
        boutonReserver.setOnClickListener{
            if (dateArrivee.text.isEmpty())
            {
                val message = "Champ date d'arrivée vide!!"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateArrivee.text.toString())
            if (sdf != null) {
                if(sdf.before(Date())) {
                    Toast.makeText(requireContext(), "La date d'arrivée ne peut pas être avant aujourd'hui!!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            val selectedSpinnerChambre = numChambreReserve.selectedItem.toString()
            reservation.numeroDeChambreReserve = selectedSpinnerChambre.toInt()
            val selectedDate = dateArrivee.text.toString()
            reservation.dateArrive = selectedDate
            updateReservationInFireBase(reservation)
        }

        return root
    }

    private fun changerDate(arriveDate: Calendar){
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        dateArrivee.setText(sdf.format(arriveDate.time))
    }

    private fun getDataFromFirebase()
    {
        //Recuperer le nom, prénom, numero de chambre et l'email
        firebaseDatabaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                DataManager.etudiantList.clear()
                if (dataSnapshot.exists()) {

                    for (etudiantSnap in dataSnapshot.children) {
                        // Get Etudiant object and use the values to update the UI
                        val etudiant = etudiantSnap.getValue(Etudiant::class.java)
                        DataManager.etudiantList.add(etudiant!!)

                        if (etudiant.email == DataManager.userEmail)
                        {
                            DataManager.etudiantCourant = etudiant
                            reservation.email = etudiant.email
                            reservation.nomReserveur = etudiant.nom
                            reservation.prenomReserveur = etudiant.prenom
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
    private fun updateReservationInFireBase(reservation: Reservation){
        reservationDataBase.child(reservation.numeroDeChambreReserve.toString()).setValue(reservation)
            .addOnCompleteListener{
                Toast.makeText(requireContext(), "Réservation complète!!", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}