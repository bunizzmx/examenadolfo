package com.example.android.examenadolfo.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.android.examenadolfo.R
import com.google.android.material.button.MaterialButton

class DialogPreviewImage : DialogFragment() {
    lateinit var dismiss:MaterialButton
    lateinit var image_previw: AppCompatImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.background_trans);
        return inflater.inflate(R.layout.dialog_preview_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dismiss = view.findViewById(R.id.btn_accept)
        dismiss.setOnClickListener {
            dialog!!.dismiss()
        }
      /*  Glide.with(requireActivity())
            .load("https://image.tmdb.org/t/p/original"+item.backdrop_path)
            .into(image_previw)*/
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}