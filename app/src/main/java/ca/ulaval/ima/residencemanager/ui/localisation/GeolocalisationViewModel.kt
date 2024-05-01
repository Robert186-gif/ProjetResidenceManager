package ca.ulaval.ima.residencemanager.ui.localisation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GeolocalisationViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is paiement Fragment"
    }
    val text: LiveData<String> = _text
}