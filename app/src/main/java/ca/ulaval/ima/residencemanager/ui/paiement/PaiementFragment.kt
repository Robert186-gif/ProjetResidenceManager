package ca.ulaval.ima.residencemanager.ui.paiement

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.ulaval.ima.residencemanager.R
import ca.ulaval.ima.residencemanager.databinding.FragmentBailBinding
import ca.ulaval.ima.residencemanager.databinding.FragmentPaiementBinding
import ca.ulaval.ima.residencemanager.ui.bail.BailViewModel

class PaiementFragment : Fragment() {
    private var _binding: FragmentPaiementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val PaiementViewModel =
            ViewModelProvider(this).get(PaiementViewModel::class.java)

        _binding = FragmentPaiementBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPaiement
        PaiementViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}