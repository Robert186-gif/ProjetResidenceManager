package ca.ulaval.ima.residencemanager.ui.acceuil

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.ulaval.ima.residencemanager.AnnonceActualite
import ca.ulaval.ima.residencemanager.R
import ca.ulaval.ima.residencemanager.databinding.FragmentAcceuilBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AcceuilFragment : Fragment() {

    private var _binding: FragmentAcceuilBinding? = null
    private lateinit var listView: ListView


    val descriptionsEvenements = arrayOf(
        "Gala de la vie étudiante à l’Université Laval, mettant en avant l'engagement étudiant.",
        "Conférence sur le développement durable avec des experts en écologie.",
        "Tournoi de football inter-universitaire, réunissant les meilleures équipes locales.",
        "Atelier de photographie urbaine et naturelle pour amateurs avancés.",
        "Foire de l'emploi pour explorer des opportunités de carrière dans divers secteurs.",
        "Concert de musique classique présentant des œuvres célèbres par l'orchestre local.",
        "Semaine de la mode avec des défilés et ateliers par de jeunes créateurs.",
        "Festival du film étudiant, exposition de talents et visions cinématographiques.",
        "Marché artisanal local, offrant des œuvres uniques comme céramiques et bijoux.",
        "Compétition de débat inter-universitaire sur des sujets d'actualité."
    )

    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      //  val acceuilViewModel =
        //    ViewModelProvider(this).get(AcceuilViewModel::class.java)

        _binding = FragmentAcceuilBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val calendar: Calendar = Calendar.getInstance()
        calendar.set(2025, Calendar.JANUARY, 5).toString()
 //       val date: Date = calendar.time
        listView = binding.listViewAnnonces
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val annoncesList = listOf(
            AnnonceActualite(
                nomEven = "Le Gala de la vie étudiante",
                imageEven = "drawable/gala.jpg",
                dsecriptionEven = descriptionsEvenements[0],
                dateEven = dateFormat.format(Calendar.getInstance().apply { set(2024, Calendar.MAY, 10) }.time)
            ),
            AnnonceActualite(
                nomEven = "La conférence annuelle",
                imageEven = "url_de_l_image_du_produit",
                dsecriptionEven = descriptionsEvenements[1],
                dateEven = dateFormat.format(Calendar.getInstance().apply { set(2024, Calendar.MAY, 9) }.time)
            ),
            AnnonceActualite(
                nomEven = "Le tournoi de football",
                imageEven = "url_de_l_image_du_produit",
                dsecriptionEven = descriptionsEvenements[2],
                dateEven = dateFormat.format(Calendar.getInstance().apply { set(2024, Calendar.MAY, 8) }.time)
            ),
            AnnonceActualite(
                nomEven = "L'atelier de photographie",
                imageEven = "url_de_l_image_du_produit",
                dsecriptionEven = descriptionsEvenements[3],
                dateEven = dateFormat.format(Calendar.getInstance().apply { set(2024, Calendar.MAY, 7) }.time)
            ),
            AnnonceActualite(
                nomEven = "La foire de l'emploi",
                imageEven = "url_de_l_image_du_produit",
                dsecriptionEven = descriptionsEvenements[4],
                dateEven = dateFormat.format(Calendar.getInstance().apply { set(2024, Calendar.MAY, 6) }.time)
            ),
            AnnonceActualite(
                nomEven = "Le concert de musique",
                imageEven = "url_de_l_image_du_produit",
                dsecriptionEven =descriptionsEvenements[5],
                dateEven = dateFormat.format(Calendar.getInstance().apply { set(2024, Calendar.MAY, 5) }.time)
            ),
            AnnonceActualite(
                nomEven = "La semaine de la mode",
                imageEven = "url_de_l_image_du_produit",
                dsecriptionEven = descriptionsEvenements[6],
                dateEven = dateFormat.format(Calendar.getInstance().apply { set(2024, Calendar.MAY, 4) }.time)
            )
        )

        val adapter = OffreAdapter(requireContext(), annoncesList)
        listView.adapter = adapter



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class OffreAdapter(private val context: Context, private val annonces: List<AnnonceActualite>) : BaseAdapter(),
    ListAdapter {

    private class ViewHolder {
        lateinit var imageView: ImageView
        lateinit var titreView: TextView
        lateinit var dateView: TextView
        lateinit var descriptionEvenView: TextView
    }
    override fun getCount(): Int = annonces.size

    override fun getItem(position: Int): AnnonceActualite = annonces[position]

    override fun getItemId(position: Int): Long = position.toLong()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.actualite, parent, false)
            holder = ViewHolder()
            holder.imageView = view.findViewById(R.id.imageView3)
            holder.titreView = view.findViewById(R.id.texte_titre)
            holder.descriptionEvenView  = view.findViewById(R.id.text_description)
            holder.dateView  = view.findViewById(R.id.text_dateEven)

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val annonceselec = getItem(position)
        setImageBasedOnInt(holder.imageView, position+1)

        holder.titreView.text = annonceselec.nomEven?.let { createStyledText("    ", it) }
        holder.dateView.text = createStyledText(" Date: ", annonceselec.dateEven)
        holder.descriptionEvenView.text = createStyledText(" Description:", annonceselec.dsecriptionEven.toString())

        return view
    }
    private fun createStyledText(label: String, value: String): SpannableString {
        val fullText = label + value
        val spannableText = SpannableString(fullText)
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableText.setSpan(boldSpan, 0, label.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableText
    }
    fun setImageBasedOnInt(imageView: ImageView, selectedInt: Int) {
        val drawableResId = when (selectedInt) {
            1 -> R.drawable.gala
            2 -> R.drawable.conf
            3 -> R.drawable.foot
            4 -> R.drawable.ph
            5 -> R.drawable.foireemploie
            6 -> R.drawable.concert
            7 -> R.drawable.mode
            else -> R.drawable.concert
        }
        imageView.setImageResource(drawableResId)
    }
}