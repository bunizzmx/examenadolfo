package  com.example.android.examenadolfo

import android.app.Application
import com.example.android.examenadolfo.data.network.NetworkModule
import com.example.android.examenadolfo.domain.DataSourceModule
import com.example.android.examenadolfo.presentation.di.ApplicationComponent
import com.example.android.examenadolfo.presentation.di.DaggerApplicationComponent

import com.example.android.examenadolfo.presentation.di.module.ApplicationModule
import com.example.android.examenadolfo.utils.CONSTANTES.URI_API


class App : Application() {
    companion object {
        var applicationComponent: ApplicationComponent? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        configureDagger()
    }

    private fun configureDagger() {
       applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(applicationContext))
            .dataSourceModule(DataSourceModule())
            .networkModule(NetworkModule(URI_API))
            .build()
    }

}