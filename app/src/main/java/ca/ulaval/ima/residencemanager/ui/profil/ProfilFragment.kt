
package ca.ulaval.ima.residencemanager.ui.profil

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.ulaval.ima.residencemanager.Annonce
import ca.ulaval.ima.residencemanager.DataManager
import ca.ulaval.ima.residencemanager.Etudiant
import ca.ulaval.ima.residencemanager.R
import ca.ulaval.ima.residencemanager.databinding.FragmentProfilBinding
import coil.load
import coil.transform.CircleCropTransformation
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView.Guidelines
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Task
import com.google.android.material.color.utilities.SchemeFidelity
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


class  ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val REQUEST_IMAGE_CAPTURE = 100
    private val GALLERY_REQUEST_CODE = 101
    private lateinit var profileImage: ImageView
    private lateinit var userName: TextView
    private var storagePermission: Array<String>? = null

    private lateinit var firebaseDatabaseRef: DatabaseReference
    private lateinit var storageRef : StorageReference
    private lateinit var etudiantList : ArrayList<Etudiant>
    private lateinit var dataList : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profilViewModel =
            ViewModelProvider(this).get(ProfilViewModel::class.java)

        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("Etudiant")
        storageRef = FirebaseStorage.getInstance().getReference("Images")
        etudiantList = arrayListOf()
        dataList = arrayListOf()

        getDataFromFirebase()

        userName = binding.textUser
//        if (etudiantList.size >= 1)
//        {
//            profilViewModel.getDataFromEtudiant(DataManager.etudiantCourant!!)
//        }
//        else{
//            profilViewModel.getNothing()
//        }
//
//        profilViewModel.text.observe(viewLifecycleOwner) {
//            userName.text = it
//        }

        val cameraBtn = binding.cameraBtn

        profileImage = binding.profileImage

        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)


        cameraBtn.setOnClickListener{
            val imageDialog = AlertDialog.Builder(requireContext())
            imageDialog.setTitle("Action")
            val imageDialogItem = arrayOf("A partir de la gallerie", "A partir de la camera", "enlever la photo")
            imageDialog.setItems(imageDialogItem){ _, which ->
                when(which){
                    0 -> galleryCheckPermission()
                    1 -> cameraCheckPermission()
                    2 -> removePhoto()
                }
            }
            imageDialog.show()
        }

        return root
    }

    private fun removePhoto(){
        profileImage.setImageResource(R.drawable.ic_profile)
        val desertRef = storageRef.child("${DataManager.userEmail}.jpg")
        // Delete the file
        desertRef.delete().addOnSuccessListener {
            Toast.makeText(requireContext(), "Images deleted",Toast.LENGTH_SHORT).show()
        }
        // File deleted successfully

        // Set the value of the URL to null to delete it
        DataManager.etudiantCourant?.urlPhotoEtudiant = "null"
        firebaseDatabaseRef.child(DataManager.etudiantCourant?.numCambre.toString()).setValue(DataManager.etudiantCourant)
            .addOnSuccessListener {
                // URL deleted
                Toast.makeText(requireContext(), "URL deleted", Toast.LENGTH_SHORT).show()
               // DataManager.etudiantCourant?.urlPhotoEtudiant = "null"
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }

    }

    private fun takePhoto()
    {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "Error " + e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun takeFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        try{
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "Error " + e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun galleryCheckPermission(){
        Dexter.withContext(requireContext())
            .withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    takeFromGallery()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(requireContext(), "Storage permission denied!!", Toast.LENGTH_SHORT).show()
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
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA).withListener(
                    object : MultiplePermissionsListener{
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
        AlertDialog.Builder(requireContext())
            .setMessage("Permissions not granted!!! Can be enabled under App settings!!")
            .setPositiveButton("Go to SETTINGS"){_,_->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
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

    private fun getImageFromFireBase(imageUrl: String) : Task<Bitmap>
    {
        // Get a reference to the image file
        val imageRef = storageRef.child("/$imageUrl")

        // Download the image file
        val ONE_MEGABYTE = 1024 * 1024.toLong()
        return imageRef.getBytes(ONE_MEGABYTE)
            .continueWith { task ->
                val data = task.result
                BitmapFactory.decodeByteArray(data, 0, data!!.size)
            }
    }
    private fun getDataFromFirebase()
    {
        //Recuperer le nom, prénom, numero de chambre et l'email
        firebaseDatabaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                DataManager.etudiantList.clear()
                if (dataSnapshot.exists()) {

                    for (etudiantSnap in dataSnapshot.children) {
                        // Get Etudiant object and use the values to update the UI
                        val etudiant = etudiantSnap.getValue(Etudiant::class.java)
                        DataManager.etudiantList.add(etudiant!!)

                        if (etudiant.email == DataManager.userEmail)
                        {
                            DataManager.etudiantCourant = etudiant
                            val numChambre : TextView = binding.numChambre
                            numChambre.text = "Chambre ${DataManager.etudiantCourant?.numCambre.toString()}"
                            val email : TextView = binding.emailText
                            email.text = DataManager.userEmail
                            userName.text = DataManager.etudiantCourant!!.nom+ " " + DataManager.etudiantCourant!!.prenom
                            val imageName = "${DataManager.userEmail}.jpg"
                            getImageFromFireBase(imageName)
                                .addOnSuccessListener { bitmap ->
                                    profileImage.load(bitmap)
                                }
                                .addOnFailureListener{
                                    Toast.makeText(requireContext(), "Images not", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("dsads", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    profileImage.load(bitmap){

                    }
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    var etudiant: Etudiant?
                    val imageName = "${DataManager.userEmail}.jpg"

                    // Create a reference to 'images/imageName.jpg'
                    val imageRef = storageRef.child("/$imageName")

                    // Upload file to Firebase Storage
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.addOnSuccessListener {
                        // Image uploaded successfully
                        // Get the download URL of the uploaded image
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            // Do something with the download URL (e.g., save it to a database)
                            DataManager.etudiantCourant?.urlPhotoEtudiant = imageUrl

                            firebaseDatabaseRef.child(DataManager.etudiantCourant?.numCambre.toString()).setValue(DataManager.etudiantCourant)
                                .addOnCompleteListener{
                                    Toast.makeText(requireContext(), "Images data", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{
                                    Toast.makeText(requireContext(), "Images not", Toast.LENGTH_SHORT).show()
                                }
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
                    profileImage.load(img) {
                    }
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, img)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    val imageName = "${DataManager.userEmail}.jpg"

                    // Create a reference to 'images/imageName.jpg'
                    val imageRef = storageRef.child("/$imageName")

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
                            DataManager.etudiantCourant?.urlPhotoEtudiant = imageUrl

                            firebaseDatabaseRef.child(DataManager.etudiantCourant?.numCambre.toString()).setValue(DataManager.etudiantCourant)
                                .addOnCompleteListener{
                                    Toast.makeText(requireContext(), "Images data", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{
                                    Toast.makeText(requireContext(), "Images not", Toast.LENGTH_SHORT).show()
                                }
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
            profileImage.setImageURI(data?.data)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
