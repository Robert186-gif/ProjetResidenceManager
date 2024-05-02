package ca.ulaval.ima.residencemanager.ui.market

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ca.ulaval.ima.residencemanager.Annonce
import ca.ulaval.ima.residencemanager.DataManager
import ca.ulaval.ima.residencemanager.Etudiant
import ca.ulaval.ima.residencemanager.R
import coil.load
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.random.Random

class AddArticleinActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 100
    private val GALLERY_REQUEST_CODE = 101

    private lateinit var firebaseRef : DatabaseReference
    private lateinit var firebaseRefAnonce : DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageRef : StorageReference
    private lateinit var viewModel: MarketViewModel
    private lateinit var image: ImageView
    private var imageAnnonce: String? = null
    private var emailDeConnexion: String? = null

    private var AnnonceList: ArrayList<Annonce>  = ArrayList()
    private lateinit var  firebaseRef2 : DatabaseReference




    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_articlein)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this,R.color.red)))
        supportActionBar?.title = "Ajouter Votre Article A vendre "
        viewModel = ViewModelProvider(this).get(MarketViewModel::class.java)

        firebaseRef = FirebaseDatabase.getInstance().getReference("Etudiant")
        firebaseRefAnonce = FirebaseDatabase.getInstance().getReference("Annonces")
        firebaseRef2 = FirebaseDatabase.getInstance().getReference("Annonces")

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Etudiant")

        storageRef = FirebaseStorage.getInstance().getReference("ImageAnnonces")

        image = findViewById(R.id.imageView4)
        image.setOnClickListener{
            val imageDialog = AlertDialog.Builder(this)
            imageDialog.setTitle("Action")
            val imageDialogItem = arrayOf("A partir de la gallerie", "A partir de la camera")
            imageDialog.setItems(imageDialogItem){ _, which ->
                when(which){
                    0 -> galleryCheckPermission()
                    1 -> cameraCheckPermission()
                }
            }
            imageDialog.show()
        }

        val monBouton = findViewById<Button>(R.id.btnSoumettre)
        var estCocher = "Non"


        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val DateSoumisssion = dateFormat.format(currentDate)
        //val nomVendeur = ""
        val description = ""

        emailDeConnexion = DataManager.userEmail
        if (emailDeConnexion != null) {
        }

        val etudiantList = mutableListOf<Etudiant>()
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (etudiantSnapshot in dataSnapshot.children) {
                    val etudiant = etudiantSnapshot.getValue(Etudiant::class.java)
                    etudiant?.let { etudiantList.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddArticleinActivity, "Failed to load students.", Toast.LENGTH_SHORT).show()
            }
        })


        monBouton.setOnClickListener {


            val nomPorduit = findViewById<EditText>(R.id.text_produit).text.toString()
            val telephone = findViewById<EditText>(R.id.text_telephoneVendeur).text.toString()
            val prixString = findViewById<EditText>(R.id.text_prix).text.toString().trim()
            val prix = prixString.toIntOrNull()

            val radioGroup = findViewById<RadioGroup>(R.id.radioGroupSexe)
            val check = radioGroup.checkedRadioButtonId
                estCocher = if (check != -1) findViewById<RadioButton>(check)?.text.toString() else ""



        if (nomPorduit.isEmpty()) {
               Toast.makeText(this, "Veuillez remplir le champs Nom produit", Toast.LENGTH_LONG).show()
            return@setOnClickListener }

        if (telephone.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir le champs telephone", Toast.LENGTH_LONG).show()
            return@setOnClickListener
        }


        if (prix == null) {
               Toast.makeText(this, "Veuillez remplir le champs Prix", Toast.LENGTH_LONG).show()
               return@setOnClickListener
             }


            val annonce =
                Annonce(
                    nomAnnonceur = DataManager.etudiantCourant?.nom,
                    idDemandeSelec = Random.nextInt(),
                    idAnnonce = emailDeConnexion,
                    nomProduit = nomPorduit,
                    telephone = telephone,
                    prix = prix,
                    estDiscutable = estCocher,
                    imageProduit = imageAnnonce,
                    dsecription = description,
                    dateMiseEnLigne =  DateSoumisssion,

                    )


            saveDataEtudiant(annonce)
            fetchData()
            Toast.makeText(this, "VOtre Article a bien été Soumis", Toast.LENGTH_LONG).show()

        }
        }
        private fun saveDataEtudiant(annonce: Annonce) {
        val annonceId = firebaseRefAnonce.push().key!!
        //val annoncecode = emailDeConnexion
        firebaseRefAnonce.child(annonceId).setValue(annonce)
              .addOnCompleteListener{
              }
    }

    private fun fetchData() {
        firebaseRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                AnnonceList.clear()
                if (snapshot.exists()){
                    for (contactSnap in snapshot.children){
                        val contacts = contactSnap.getValue(Annonce::class.java)
                        AnnonceList.add(contacts!!)

                        Log.w("MyAppTag", "recuperationReussiBIENNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN")
                        contacts.dsecription?.let { Log.w("MyAppTag", it) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun takePhoto()
    {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, "Error " + e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun takeFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        try{
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, "Error " + e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun galleryCheckPermission(){
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    takeFromGallery()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
//                    Toast.makeText(this, "Storage permission denied!!", Toast.LENGTH_SHORT).show()
                    showRotationForDialogPermission()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRotationForDialogPermission()
                }
            }).onSameThread().check()
    }

    private fun cameraCheckPermission(){
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA).withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        p0?.let{
                            if(p0.areAllPermissionsGranted()) {
                                takePhoto()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRotationForDialogPermission()
                    }

                }

            ).onSameThread().check()
    }

    private fun showRotationForDialogPermission(){
        AlertDialog.Builder(this)
            .setMessage("Permissions not granted!!! Can be enabled under App settings!!")
            .setPositiveButton("Go to SETTINGS"){_,_->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("CANCEL"){dialog, _->
                dialog.dismiss()
            }.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    image.load(bitmap){

                    }
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    imageAnnonce = "${DataManager.userEmail}.jpg"

                    // Create a reference to 'images/imageName.jpg'
                    val imageRef = storageRef.child("/$imageAnnonce")

                    // Upload file to Firebase Storage
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.addOnSuccessListener {
                        // Image uploaded successfully
                        // Get the download URL of the uploaded image
                        imageRef.downloadUrl.addOnSuccessListener { uri ->

                        }
                            .addOnFailureListener {
                                // Handle failures
                            }
                    }
                        .addOnFailureListener {
                            // Handle unsuccessful uploads
                        }
                }

                GALLERY_REQUEST_CODE -> {
                    val img = data?.data
                    image.load(img) {
                    }
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, img)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    imageAnnonce = "${DataManager.userEmail}.jpg"

                    // Create a reference to 'images/imageName.jpg'
                    val imageRef = storageRef.child("/$imageAnnonce")

                    // Upload file to Firebase Storage
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.addOnSuccessListener {
                        // Image uploaded successfully
                        // Get the download URL of the uploaded image
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            // Do something with the download URL (e.g., save it to a database)
//                            etudiant = Etudiant(9612, "JORDAN", "JORDAN", "jordankamakwee4@gmail.com",
//                                imageUrl, "", 0, emptyList(), emptyList())
                           // DataManager.etudiantCourant?.urlPhotoEtudiant = imageUrl
                        }
                            .addOnFailureListener {
                                // Handle failures
                            }
                    }
                        .addOnFailureListener {
                            // Handle unsuccessful uploads
                        }
                }

            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
            image.setImageURI(data?.data)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}