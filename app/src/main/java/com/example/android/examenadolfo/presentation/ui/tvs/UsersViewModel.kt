package  com.example.android.examenadolfo.presentation.ui.tvs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.android.examenadolfo.R
import com.example.android.examenadolfo.domain.data.TvsRepository
import com.example.android.examenadolfo.presentation.BaseViewModel
import com.example.android.examenadolfo.utils.Event


import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


import javax.inject.Inject
import com.example.android.examenadolfo.data.network.model.response.Tv
import com.example.android.examenadolfo.utils.CONSTANTES
import com.example.android.examenadolfo.utils.treking.LocationsFirestore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UsersViewModel
@Inject constructor(
    private val loginRepository: TvsRepository
) : BaseViewModel() {

    private val disposable = CompositeDisposable()

    private val _userresponse = MutableLiveData<Event<ArrayList<Tv>>>()
    val userresponse get() = _userresponse

    private val _save_succes = MutableLiveData<Event<Boolean>>()
    val save_succes get() = _save_succes

    var mTvsLocal: List<Tv>? = null

    private val _mpins = MutableLiveData<Event<Task<QuerySnapshot>>>()
    val mpins get() = _mpins

    private val _mTvsLocallivedata = MutableLiveData<Event<List<Tv>>>()
    val mTvsLocallivedata get() = _mTvsLocallivedata


    private val _is_local= MutableLiveData<Event<String>>()
    val is_local get() = _is_local



    internal fun saveTvs(tv:ArrayList<Tv>) {
        showLoading()
        CoroutineScope(Dispatchers.IO).launch {
           loginRepository.saveAllTvs(tv)
        }
    }

    internal fun getLocalTVS() {
        showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            mTvsLocal = loginRepository.getTvsOffline()
            _mTvsLocallivedata.postValue(Event(mTvsLocal) as Event<List<Tv>>?)
        }
    }


    internal fun getTvs() {
        showLoading()
        val task = loginRepository.getListTvs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        val subscriber = task.subscribe({
            if(it.reluts!=null) {
                if (it.reluts.size > 0) {
                    saveTvs(it.reluts)
                }
            }
            _userresponse.postValue(Event(it.reluts) )
        }, {
            getLocalTVS()
            serviceError(it.message!!)
        })
        disposable.add(subscriber)
    }


    fun getCurrent_pin() {
        val firebase = FirebaseFirestore.getInstance()
         firebase.collection(CONSTANTES.COLLECTION_GPS).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var counter = 0
                    var total = 0
                    total = task.result.size()
                    is_local.postValue(Event(total.toString()))
                    mpins.postValue(Event(task))
                }
            }


    }




}