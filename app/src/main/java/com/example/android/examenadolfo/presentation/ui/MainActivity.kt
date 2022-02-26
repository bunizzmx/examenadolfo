package com.example.android.examenadolfo.presentation.ui

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
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
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
import com.example.android.examenadolfo.domain.data.WordsRepository
import com.example.android.examenadolfo.presentation.BaseViewModelActivity
import com.example.android.examenadolfo.utils.CONSTANTES.REQUEST_IMAGE_CAPTURE
import com.example.android.examenadolfo.utils.CONSTANTES.REQUEST_PICK_IMAGE
import com.example.android.examenadolfo.utils.DialogProgress
import com.example.android.examenadolfo.utils.MyCustomDialog
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
    lateinit var loginRepository: WordsRepository
    val db = Firebase.firestore
    var dialog = DialogProgress()

    lateinit var storage: FirebaseStorage
    private lateinit var navController: NavController

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
        // setupActionBarWithNavController(navController)
        setupSmoothBottomMenu()
        if (Build.VERSION.SDK_INT >= 21) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.setStatusBarColor(this.resources.getColor(R.color.colorPrimary))
        }
        initServices()

        //Inicializa el TrackingService
        TrackingService.startLocationTracking(this)

    }

    fun uploadImage(uri:String){
        dialog.show(supportFragmentManager, "MyCustomFragment")
        val storageRef = storage.reference
        var file = Uri.fromFile(File(uri))
        val riversRef = storageRef.child("images/${file.lastPathSegment}")
        var uploadTask = riversRef.putFile(file)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            dialog.dialog?.dismiss()
            Log.e("UPLOAD_PHOTO","-->FAILURE")
        }.addOnSuccessListener { taskSnapshot ->
            dialog.dialog?.dismiss()
            Log.e("UPLOAD_PHOTO","-->succes")
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
                //ivImage.setImageBitmap(bitmap)
            }
            else if (requestCode == REQUEST_PICK_IMAGE) {
                Log.e("REQUEST_PICK_IMAGE","s")
                handleImageOnKitkat(data)
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
    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        //DocumentsContract defines the contract between a documents provider and the platform.
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


}