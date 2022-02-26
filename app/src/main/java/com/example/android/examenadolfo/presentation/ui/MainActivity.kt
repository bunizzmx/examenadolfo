package com.example.android.examenadolfo.presentation.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.NavController

import javax.inject.Inject
import androidx.navigation.findNavController
import com.example.android.examenadolfo.App
import com.example.android.examenadolfo.R
import com.example.android.examenadolfo.databinding.ActivityMainBinding
import com.example.android.examenadolfo.domain.data.WordsRepository
import com.example.android.examenadolfo.presentation.BaseViewModelActivity
import com.example.android.examenadolfo.utils.NetworkConnection
import com.example.android.examenadolfo.utils.treking.TrackingService
import com.frentetecnologicoponce.elder.presentation.ViewModelFactory
import com.frentetecnologicoponce.elder.presentation.di.DaggerAuthenticationComponent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : BaseViewModelActivity<UsersViewModel>() {

    @Inject
    lateinit var loginRepository: WordsRepository
    val db = Firebase.firestore


    private lateinit var navController: NavController

    private val authViewModel by viewModels<UsersViewModel> {
        ViewModelFactory( loginRepository,this, intent.extras)
    }

    private lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        initInjection()
        super.onCreate(savedInstanceState)
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
        if (NetworkConnection.hasInternetConnection(this)) {
            //accountDetailViewModel.getAccountStatusPeriods()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

    }



}