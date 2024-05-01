package ca.ulaval.ima.residencemanager.ui.paiement

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import ca.ulaval.ima.residencemanager.R
import ca.ulaval.ima.residencemanager.databinding.FragmentBailBinding
import ca.ulaval.ima.residencemanager.databinding.FragmentPaiementBinding
import ca.ulaval.ima.residencemanager.ui.bail.BailViewModel
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import androidx.core.app.ActivityCompat
import java.util.Locale


class PaiementFragment : Fragment(),LocationListener  {
    private var _binding: FragmentPaiementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var locationManager: LocationManager? = null
    private lateinit var textViewLocalisation: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val PaiementViewModel =
            ViewModelProvider(this).get(PaiementViewModel::class.java)

        _binding = FragmentPaiementBinding.inflate(inflater, container, false)
        val root: View = binding.root

        textViewLocalisation = binding.textLocation
        val btnLocalistation: Button = binding.buttonLocation
        btnLocalistation.setOnClickListener {
            getLocation()
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkLocationPermission()
    }
    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        } else {
            Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    override fun onLocationChanged(location: Location) {
        Toast.makeText(context, "${location.latitude},${location.longitude}", Toast.LENGTH_SHORT).show()
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0].getAddressLine(0)
                    textViewLocalisation.text = address
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Deprecated in API 29 and above
    }

    override fun onProviderEnabled(provider: String) {
        // Called when the provider is enabled
    }

    override fun onProviderDisabled(provider: String) {
        // Called when the provider is disabled
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}