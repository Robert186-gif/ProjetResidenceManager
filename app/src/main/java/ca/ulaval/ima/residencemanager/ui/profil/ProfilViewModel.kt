package ca.ulaval.ima.residencemanager.ui.profil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.ulaval.ima.residencemanager.Annonce
import ca.ulaval.ima.residencemanager.Etudiant
import java.util.ArrayList

class ProfilViewModel : ViewModel() {

    val text: MutableLiveData<String> =  MutableLiveData()
    fun getDataFromEtudiant(etudiant: Etudiant){
        val nom  = etudiant.nom
        val prenom = etudiant.prenom
        val data = nom + prenom
        text.postValue(data)
    }

    fun getNothing()
    {
        text.postValue("jORDAN")
    }
}
