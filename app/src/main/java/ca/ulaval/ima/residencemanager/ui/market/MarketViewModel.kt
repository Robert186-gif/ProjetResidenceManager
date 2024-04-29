package ca.ulaval.ima.residencemanager.ui.market

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MarketViewModel : ViewModel() {
    val userEmail = MutableLiveData<String>()

    fun setEmail(email: String) {
        userEmail.value = email
    }
}