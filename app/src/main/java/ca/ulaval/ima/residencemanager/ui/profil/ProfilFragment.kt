
package ca.ulaval.ima.residencemanager.ui.profil

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.ulaval.ima.residencemanager.R
import ca.ulaval.ima.residencemanager.databinding.FragmentProfilBinding
import coil.load
import coil.transform.CircleCropTransformation
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView.Guidelines
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


class  ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val REQUEST_IMAGE_CAPTURE = 100
    private val GALLERY_REQUEST_CODE = 101
    private lateinit var profileImage: ImageView
    private var storagePermission: Array<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profilViewModel =
            ViewModelProvider(this).get(ProfilViewModel::class.java)

        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textUser
        profilViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        profileImage = binding.profileImage

        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //Tu appuies sur le bouton prendre une photo
        val btnPrendrePhoto = binding.btnTakePhoto
        btnPrendrePhoto.setOnClickListener{
            cameraCheckPermission()
        }

        val btnGallery = binding.btnGallery
        btnGallery.setOnClickListener{
            galleryCheckPermission()
        }

        profileImage.setOnClickListener{
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    profileImage.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                }

                GALLERY_REQUEST_CODE -> {
                    val bitmap = data?.data
                    profileImage.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                }

            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
