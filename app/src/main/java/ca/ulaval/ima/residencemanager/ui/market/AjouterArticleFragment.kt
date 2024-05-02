package ca.ulaval.ima.residencemanager.ui.market

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.ulaval.ima.residencemanager.R

class AjouterArticleFragment : Fragment() {

    companion object {
        fun newInstance() = AjouterArticleFragment()
    }

    private lateinit var viewModel: AjouterArticleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ajouter_article, container, false)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AjouterArticleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}