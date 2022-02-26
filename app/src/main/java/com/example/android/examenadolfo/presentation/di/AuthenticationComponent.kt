package com.frentetecnologicoponce.elder.presentation.di





import com.example.android.examenadolfo.presentation.di.ApplicationComponent
import com.example.android.examenadolfo.presentation.di.scope.FragmentScope
import com.example.android.examenadolfo.presentation.ui.MainActivity
import dagger.Component



@FragmentScope
@Component(dependencies = [ApplicationComponent::class])
interface AuthenticationComponent {
    fun inject(target: MainActivity)

}