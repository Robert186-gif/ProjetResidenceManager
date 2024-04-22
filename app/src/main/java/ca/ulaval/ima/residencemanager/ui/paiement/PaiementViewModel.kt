package ca.ulaval.ima.residencemanager.ui.paiement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PaiementViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is paiement Fragment"
    }
    val text: LiveData<String> = _text
}