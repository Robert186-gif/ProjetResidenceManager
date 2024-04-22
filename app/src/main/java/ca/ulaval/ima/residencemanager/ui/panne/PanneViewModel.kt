package ca.ulaval.ima.residencemanager.ui.panne

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PanneViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is panne Fragment"
    }
    val text: LiveData<String> = _text
}