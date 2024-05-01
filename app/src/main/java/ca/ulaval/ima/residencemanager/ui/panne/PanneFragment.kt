package ca.ulaval.ima.residencemanager.ui.panne

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
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
import android.widget.ListView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import ca.ulaval.ima.residencemanager.Annonce
import ca.ulaval.ima.residencemanager.DataManager
import ca.ulaval.ima.residencemanager.Panne
import ca.ulaval.ima.residencemanager.R
import ca.ulaval.ima.residencemanager.databinding.FragmentPanneBinding
import ca.ulaval.ima.residencemanager.ui.market.AddArticleinActivity
import ca.ulaval.ima.residencemanager.ui.market.MarketAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PanneFragment : Fragment() {

    private var _binding: FragmentPanneBinding? = null
    private lateinit var listView: ListView
    private lateinit var firebaseDatabasePanne: DatabaseReference
    private var panneList: ArrayList<Panne>  = ArrayList()

    private val binding get() = _binding!!

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_panne, container, false)
        listView = view.findViewById(R.id.list_view_panne)
        val submitButton = view.findViewById<Button>(R.id.btn_ajouter_panne)
        firebaseDatabasePanne = FirebaseDatabase.getInstance().getReference("Pannes")

        fetchData()
        val panneAdapter = PanneAdapter(requireContext(), DataManager.panneList)
        listView.adapter = panneAdapter


        submitButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_panne_to_ajoutePanneFragment)

        }


        return view
    }

    private fun fetchData() {
        firebaseDatabasePanne.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                DataManager.panneList.clear()
                if (snapshot.exists()){
                    for (contactSnap in snapshot.children){
                        val panne = contactSnap.getValue(Panne::class.java)
                        DataManager.panneList.add(panne!!)
                    }
                    (listView.adapter as? PanneAdapter)?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


class PanneAdapter(private val context: Context, private val PanneList: ArrayList<Panne>) : BaseAdapter() {

    private class ViewHolder {
        lateinit var Text_typePanne: TextView
        lateinit var Text_DescriptionPanne: TextView
        lateinit var Text_DateSoumission: TextView
        lateinit var Text_Personne: TextView

    }

    override fun getCount(): Int = PanneList.size

    override fun getItem(position: Int): Any = PanneList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_panne, parent, false)
            viewHolder = ViewHolder()
            viewHolder.Text_typePanne = view.findViewById(R.id.text_item)
            viewHolder.Text_DateSoumission = view.findViewById(R.id.text_DateSoumission)
            viewHolder.Text_DescriptionPanne = view.findViewById(R.id.text_description)
            viewHolder.Text_Personne = view.findViewById(R.id.text_panneur)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val panne = getItem(position) as Panne
        viewHolder.Text_DescriptionPanne.text =
            panne.description ?.let { createStyledText("Description  Panne:     ", it) }
        viewHolder.Text_typePanne.text = panne.typePanne?.let { createStyledText(" Type Panne :   ", it) }
        viewHolder.Text_DateSoumission.text = panne.dateCreation?.let { createStyledText(" Date Soumission :   ", it) }
        viewHolder.Text_Personne.text = panne.initiateur?.let { createStyledText(" Initiateur :   ", it) }
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
