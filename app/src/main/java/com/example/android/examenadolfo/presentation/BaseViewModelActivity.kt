package com.example.android.examenadolfo.presentation
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.android.examenadolfo.utils.EventObserver
import com.example.android.examenadolfo.utils.MyCustomDialog
import com.google.android.material.textfield.TextInputEditText



abstract class BaseViewModelActivity<T : BaseViewModel> : AppCompatActivity() {

    private var dialog: AlertDialog? = null
    private var isOnBackPressed: Boolean? = false
    private val displayMetrics = DisplayMetrics()

    protected abstract val baseViewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeEvents()
    }

    private fun observeEvents() {
        baseViewModel.showLoading.observe(this, EventObserver {
            showLoadingDialog()
        })

        baseViewModel.hideLoading.observe(this, EventObserver {
            hideLoadingDialog()
        })

        baseViewModel.isOnBackPressed.observe(this, EventObserver {
            isOnBackPressed = it
        })

        baseViewModel.serviceError.observe(this, EventObserver { message ->
            MyCustomDialog().show(this.supportFragmentManager, "MyCustomFragment")
        })

        baseViewModel.connectionError.observe(this, EventObserver {
            MyCustomDialog().show(this.supportFragmentManager, "MyCustomFragment")
        })
    }

    private fun showLoadingDialog() {
        /*dialog = LoadingDialog.Builder(this)
            .create()
        dialog?.show()*/
    }

    private fun hideLoadingDialog() {
        dialog?.dismiss()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        val ret = super.dispatchTouchEvent(event)
        if (currentFocus is TextInputEditText) {
            val view = currentFocus
            view?.let {
                val coordinates = IntArray(2)
                view.getLocationOnScreen(coordinates)
                val x = event.rawX + view.left - coordinates[0]
                val y = event.rawY + view.top - coordinates[1]
                if (event.action == MotionEvent.ACTION_UP &&
                    (x < view.left || x >= view.right || y < view.top || y > view.bottom)
                ) {
                   // hideKeyboard(view)
                }
            }
        }
        return ret
    }

    override fun attachBaseContext(newBase: Context?) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val var10000 = newBase?.getSystemService(WINDOW_SERVICE)
                if (var10000 == null) {
                    throw ClassCastException("null cannot be cast to non-null type android.view.WindowManager")
                } else {
                    val windowManager = var10000 as WindowManager
                    windowManager.defaultDisplay.getMetrics(this.displayMetrics)
                    if (this.displayMetrics.densityDpi != this.displayMetrics.xdpi.toInt()) {
                        val var10002 = newBase.resources
                        val newConfiguration = Configuration(var10002?.configuration)
                        newConfiguration.densityDpi = this.displayMetrics.xdpi.toInt()
                        newConfiguration.fontScale = 1.1f
                        applyOverrideConfiguration(newConfiguration)
                    }
                }
            }
        } catch (ignore: Exception) {
        }
        super.attachBaseContext(newBase)
    }
}