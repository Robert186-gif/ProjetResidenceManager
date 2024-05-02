package ca.ulaval.ima.residencemanager

object DataManager {
    var userEmail: String? = null
    var etudiantList : ArrayList<Etudiant> = ArrayList()
    var annonceList : ArrayList<Annonce> = ArrayList()
    var panneList : ArrayList<Panne> = ArrayList()
    var etudiantCourant : Etudiant? = null
}

data class Etudiant(
    val numCambre: Int = 0,
    val nom: String? = null,
    val prenom: String? = null,
    val email: String? = null,
    var urlPhotoEtudiant: String? = null,
    var contratBail: String? = null,
    val ChambreReserver: Int = 0,
    val mesAnnonces : List<String> = emptyList(),
    val mesPanne : List<String> = emptyList(),

    )

data class Annonce(
    val nomAnnonceur: String? = null,
    val idDemandeSelec: Int? = null,
    val idAnnonce: String? = null,
    val nomProduit: String? = null,
    val telephone: String? = null,
    val prix: Int = 1,
    val estDiscutable: String? = null,
    val imageProduit: String? = null,
    val dsecription: String? = null,
    val dateMiseEnLigne: String? = null
    //val  dateMiseEnLigne: LocalDate,
)

data class Reservation(
    var nomReserveur: String? = null,
    var prenomReserveur: String? = null,
    var email: String? = null,
    var numeroDeChambreReserve: Int = 0,
    var dateArrive: String? = null
)

data class AnnonceActualite(
    val nomEven: String?,
    val imageEven: String?,
    val dsecriptionEven: String?,
    val  dateEven: String,
)

data class Panne(
    val idPanne: String? = null,
    val typePanne: String? = null,
    val description: String? = null,
    val dateCreation: String? = null,
    val initiateur: String? = null,
)







































