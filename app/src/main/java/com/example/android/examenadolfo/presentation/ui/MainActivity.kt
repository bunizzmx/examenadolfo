package com.example.android.examenadolfo.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

import javax.inject.Inject
import androidx.navigation.findNavController
import com.example.android.examenadolfo.App
import com.example.android.examenadolfo.R
import com.example.android.examenadolfo.databinding.ActivityMainBinding
import com.example.android.examenadolfo.domain.data.TvsRepository
import com.example.android.examenadolfo.presentation.BaseViewModelActivity
import com.example.android.examenadolfo.presentation.ui.tvs.UsersViewModel
import com.example.android.examenadolfo.utils.CONSTANTES.REQUEST_IMAGE_CAPTURE
import com.example.android.examenadolfo.utils.CONSTANTES.REQUEST_PICK_IMAGE
import com.example.android.examenadolfo.utils.DialogPermissions
import com.example.android.examenadolfo.utils.DialogPermissionsGPS
import com.example.android.examenadolfo.utils.DialogProgress
import com.example.android.examenadolfo.utils.NetworkConnection
import com.example.android.examenadolfo.utils.treking.TrackingService
import com.frentetecnologicoponce.elder.presentation.ViewModelFactory
import com.frentetecnologicoponce.elder.presentation.di.DaggerAuthenticationComponent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class MainActivity : BaseViewModelActivity<UsersViewModel>() {

    @Inject
    lateinit var loginRepository: TvsRepository
    val db = Firebase.firestore
    var dialog = DialogProgress()
    var locationPermissionGranted:Boolean=false

    lateinit var storage: FirebaseStorage
    private lateinit var navController: NavController
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1


    private val authViewModel by viewModels<UsersViewModel> {
        ViewModelFactory( loginRepository,this, intent.extras)
    }

    private lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        initInjection()
        super.onCreate(savedInstanceState)
        storage = FirebaseStorage.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.main_fragment)
        setupSmoothBottomMenu()
        if (Build.VERSION.SDK_INT >= 21) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.setStatusBarColor(this.resources.getColor(R.color.colorPrimary))
        }
        initServices()
        getLocationPermission()
        //Inicializa el TrackingService
        TrackingService.startLocationTracking(this)

    }

    fun uploadImage(uri:String){
        dialog.show(supportFragmentManager, "MyCustomFragment")
        val storageRef = storage.reference
        var file = Uri.fromFile(File(uri))
        val riversRef = storageRef.child("images/${file.lastPathSegment}")
        var uploadTask = riversRef.putFile(file)
        uploadTask.addOnFailureListener {
            dialog.dialog?.dismiss()
        }.addOnSuccessListener { taskSnapshot ->
            Toast.makeText(this,getString(R.string.sinc_success),Toast.LENGTH_LONG).show()
            dialog.dialog?.dismiss()
        }
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.menu)
        val menu = popupMenu.menu
        binding.bottomBar.setupWithNavController(menu, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override val baseViewModel: UsersViewModel
        get() = authViewModel

    private fun initInjection() {
        val component = DaggerAuthenticationComponent.builder()
            .applicationComponent(App.applicationComponent)
            .build()

        component.inject(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initServices() {
        if (NetworkConnection.hasInternetConnection(this)) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val bitmap = data?.extras?.get("data") as Bitmap
            }
            else if (requestCode == REQUEST_PICK_IMAGE) {
                uploadImage(data)
            }
        }
    }



    @SuppressLint("Range")
    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    @TargetApi(19)
    private fun uploadImage(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        if (DocumentsContract.isDocumentUri(this, uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri?.authority){
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selsetion)
            }
            else if ("com.android.providers.downloads.documents" == uri?.authority){
                val contentUri = ContentUris.withAppendedId(Uri.parse(
                    "content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null)
            }
        }
        else if ("content".equals(uri?.scheme, ignoreCase = true)){
            imagePath = getImagePath(uri, null)
        }
        else if ("file".equals(uri?.scheme, ignoreCase = true)){
            imagePath = uri?.path
        }
        if (NetworkConnection.hasInternetConnection(this))
            uploadImage(imagePath.toString())
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }else{
                    DialogPermissionsGPS().show(this.supportFragmentManager,"")
                }
            }
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }


}