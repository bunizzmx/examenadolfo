package com.example.android.examenadolfo.presentation.di;






import com.example.android.examenadolfo.data.network.NetworkModule;
import com.example.android.examenadolfo.domain.DataSourceModule;
import com.example.android.examenadolfo.domain.data.TvsRepository;
import com.example.android.examenadolfo.presentation.di.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DataSourceModule.class, NetworkModule.class})
public interface ApplicationComponent {
    TvsRepository tvsRepository();
}

