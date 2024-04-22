package ca.ulaval.ima.residencemanager.ui.panne

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.ulaval.ima.residencemanager.R
import ca.ulaval.ima.residencemanager.databinding.FragmentPaiementBinding
import ca.ulaval.ima.residencemanager.databinding.FragmentPanneBinding
import ca.ulaval.ima.residencemanager.ui.bail.BailViewModel

class PanneFragment : Fragment() {

    private var _binding: FragmentPanneBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val PanneViewModel =
            ViewModelProvider(this).get(PanneViewModel::class.java)

        _binding = FragmentPanneBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPanne
        PanneViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}