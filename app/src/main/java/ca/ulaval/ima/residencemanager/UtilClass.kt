package ca.ulaval.ima.residencemanager

import java.time.LocalDate

object DataManager {
    var userEmail: String? = null
}


data class Etudiant(
    val numChambre: Int = 0,
    val nom: String? = null,
    val prenom: String? = null,
    val email: String? = null,
    val urlPhotoEtudiant: String? = null,
    val contratBail: String? = null,
    val ChambreReserver: Int = 0,
    val mesAnnonces: List<String> = emptyList(),
    val mesPanne: List<String> = emptyList()
)


data class Annonce(
    val NomAnnonceur: String? = null,
    val idDemandeSelec: Int? = null,
    val idAnnonce: String? = null,
    val nomProduit: String? = null,
    val telephone: String? = null,
    val prix: Int = 0,
    val estDiscutable: String? = null,
    val imageProduit: String? = null,
    val dsecription: String? = null,
    val dateMiseEnLigne: String? = null
)


data class AnnonceActualite(
    val nomEven: String?,
    val imageEven: String?,
    val dsecriptionEven: String?,
    val  dateEven: String,
)

data class Panne(
    val idPanne: String?,
    val typePanne: String?,
    val niveauUrgence: String?,
    val imagePanne: String?,
    val dsecription: String?,
    val  datecreation: LocalDate,
)






































