package ca.ulaval.ima.residencemanager.ui.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReservationViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Numero de chambre"
    }
    val text: LiveData<String> = _text
}