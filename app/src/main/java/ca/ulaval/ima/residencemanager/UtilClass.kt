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
    val mesAnnonces : List<String>,
    val mesPanne : List<String>,

)

data class Annonce(
    val idAnnonce: String?,
    val nomProduit: String?,
    val telephone: String?,
    val prix: Int,
    val estDiscutable: String?,
    val imageProduit: String?,
    val dsecription: String?,
    //val  dateMiseEnLigne: LocalDate,
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

val annoncesList = listOf(
    Annonce(
        idAnnonce = "A123",
        nomProduit = "Vélo de montagne",
        telephone = "0123456789",
        prix = 300,
        estDiscutable = "oui",
        imageProduit = "url_de_l_image_du_produit",
        dsecription = "Vélo en très bon état, idéal pour les parcours en montagne."
    ),
    Annonce(
        idAnnonce = "B456",
        nomProduit = "Ordinateur Portable",
        telephone = "0987654321",
        prix = 500,
        estDiscutable = "non",
        imageProduit = "url_de_l_image_de_l_ordinateur",
        dsecription = "Ordinateur dernier cri, parfait pour le gaming."
    ),
    // Ajoutez autant d'annonces que nécessaire
)

val annonce1 = Annonce(
    idAnnonce = "gtt",
    nomProduit = "Ordinateur Portable",
    telephone = "0123456789",
    prix = 750,
    estDiscutable = "Non",
    imageProduit = "http://exemple.com/image_ordi.jpg",
    dsecription = "Ordinateur portable en bon état, utilisé pendant 1 an.",
    // dateMiseEnLigne = null// Remplacez par la date réelle
)






































