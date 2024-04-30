package ca.ulaval.ima.residencemanager.ui.market

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.residencemanager.Annonce
import ca.ulaval.ima.residencemanager.DataManager
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
    private lateinit var listView: ListView

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

        listView = view.findViewById(R.id.list_view_annonces)
        val marquesAdapter = MarketAdapter(requireContext(), DataManager.annonceList)
        listView.adapter = marquesAdapter

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



class MarketAdapter(private val context: Context, private val annonceList: List<Annonce>) : BaseAdapter() {

    private class ViewHolder {
        lateinit var UrlImage: ImageView
        lateinit var NomVendeur: TextView
        lateinit var Prix_article: TextView
        lateinit var Telephone_vendeur: TextView
        lateinit var estDiscutable: TextView
        lateinit var Nom_Produit: TextView

    }

    override fun getCount(): Int = annonceList.size

    override fun getItem(position: Int): Any = annonceList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_market, parent, false)
            viewHolder = ViewHolder()
            viewHolder.UrlImage = view.findViewById(R.id.imageView3)
            viewHolder.NomVendeur = view.findViewById(R.id.text_nomVendeur)
            viewHolder.Nom_Produit = view.findViewById(R.id.texte_titre)
            viewHolder.Prix_article  = view.findViewById(R.id.text_Prix_Vend)
            viewHolder.Telephone_vendeur  = view.findViewById(R.id.text_telephone_vend)
            viewHolder.estDiscutable  = view.findViewById(R.id.text_discutable_vend)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        var annonce = getItem(position) as Annonce



        viewHolder.UrlImage.setImageResource(R.drawable.bucati)
        viewHolder.NomVendeur.text = annonce.NomAnnonceur?.let { createStyledText("  Nom Vendeur :   ", it) }
        viewHolder.Prix_article.text = createStyledText("  Prix:           ", annonce.prix.toString() + "$")
        viewHolder.Nom_Produit.text = createStyledText("    ", annonce.nomProduit.toString())
        viewHolder.Telephone_vendeur.text =
            annonce.telephone?.let { createStyledText("  Telephone:   ", it) }
        viewHolder.estDiscutable.text =
            annonce.estDiscutable?.let { createStyledText("  Discutable :                ", it) }

        return view
    }

    private fun createStyledText(label: String, value: String): SpannableString {
        val fullText = label + value
        val spannableText = SpannableString(fullText)
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableText.setSpan(boldSpan, 0, label.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableText
    }
}

