package ca.ulaval.ima.residencemanager

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.util.Date

data class Etudiant(
    val numCambre: Int,
    val nom: String?,
    val prenom: String?,
    val email: String?,
    val urlPhotoEtudiant: String?,
    val contratBail: String?,
    val ChambreReserver: Int,
    val mesAnnonces : List<Annonce>,
    val mesPanne : List<Panne>,

)

data class Annonce(
    val nomProduit: String?,
    val telephone: String?,
    val prix: Int,
    val estDiscutable: String?,
    val imageProduit: String?,
    val dsecription: String?,
    val  dateMiseEnLigne: LocalDate,
)

data class Panne(
    val typePanne: String?,
    val niveauUrgence: String?,
    val imagePanne: String?,
    val dsecription: String?,
    val  datecreation: LocalDate,
)







































