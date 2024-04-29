package ca.ulaval.ima.residencemanager.ui.market

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.residencemanager.Annonce
import ca.ulaval.ima.residencemanager.R
import ca.ulaval.ima.residencemanager.databinding.FragmentMarketBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class Market : Fragment() {
    private var _binding : FragmentMarketBinding? = null
    private  val binding get() = _binding!!

    private lateinit var AnnonceList: ArrayList<Annonce>
    private lateinit var  firebaseRef : DatabaseReference

    companion object {
        fun newInstance() = Market()
    }

    private lateinit var viewModel: MarketViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_market, container, false)
        val addButton: Button = view.findViewById(R.id.btn_ajouter_articl)

        firebaseRef = FirebaseDatabase.getInstance().getReference("Annonces")
        AnnonceList = arrayListOf()
        fetchData()

        addButton.setOnClickListener {
            val intent = Intent(activity, AddArticleinActivity::class.java)
            startActivity(intent)
        }

       // binding.rvContacts.apply {
          //  setHasFixedSize(true)
         //   layoutManager = LinearLayoutManager(this.context)
        //}

        return view
    }


    private fun fetchData() {
        firebaseRef.addValueEventListener(object : ValueEventListener {
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
                //val rvAdapter = RvContactsAdapter(AnnonceList)
               // binding.rvContacts.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context," error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MarketViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
