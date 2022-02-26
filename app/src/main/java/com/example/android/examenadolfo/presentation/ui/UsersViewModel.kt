package  com.example.android.examenadolfo.presentation.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.android.examenadolfo.data.network.model.response.User
import com.example.android.examenadolfo.domain.data.WordsRepository
import com.example.android.examenadolfo.presentation.BaseViewModel
import com.example.android.examenadolfo.utils.Event


import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


import javax.inject.Inject
import com.example.android.examenadolfo.data.network.model.response.DetailUsersResponse
import com.example.android.examenadolfo.data.network.model.response.Tv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UsersViewModel
@Inject constructor(
    private val loginRepository: WordsRepository
) : BaseViewModel() {

    private val disposable = CompositeDisposable()

    private val _login = MutableLiveData<Event<Int>>()
    val login get() = _login

    private val _register = MutableLiveData<Event<User>>()
    val register get() = _register


    private val _userresponse = MutableLiveData<Event<ArrayList<Tv>>>()
    val userresponse get() = _userresponse

    private val _userdetail = MutableLiveData<Event<DetailUsersResponse>>()
    val userdetail get() = _userdetail


    private val _save_succes = MutableLiveData<Event<Boolean>>()
    val save_succes get() = _save_succes

    public  var mTvsLocal: List<Tv>? = null

    private val _mTvsLocallivedata = MutableLiveData<Event<List<Tv>>>()
    val mTvsLocallivedata get() = _mTvsLocallivedata



    internal fun saveTvs(tv:ArrayList<Tv>) {
        showLoading()
        CoroutineScope(Dispatchers.IO).launch {
           loginRepository.saveAllTvs(tv)
        }
    }

    internal fun getLocalTVS() {
        showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("SAVE_LOCAL","s")
            mTvsLocal = loginRepository.getTvsOffline()
            Log.e("SAVE_LOCAL","aaaa"+mTvsLocal!!.size)
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




}