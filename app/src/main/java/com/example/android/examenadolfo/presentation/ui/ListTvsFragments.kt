package com.example.android.examenadolfo.presentation.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import com.example.android.examenadolfo.databinding.FragmentListWordsBinding
import com.example.android.examenadolfo.presentation.common.viewBinding
import com.example.android.examenadolfo.utils.EventObserver


import com.example.android.examenadolfo.R
import com.example.android.examenadolfo.data.network.model.response.Tv
import com.example.android.examenadolfo.utils.CONSTANTES.*
import com.example.android.examenadolfo.utils.DialogPermissions
import com.example.android.examenadolfo.utils.spanedGridLayoutManager.SpanSize
import com.example.android.examenadolfo.utils.spanedGridLayoutManager.SpannedGridLayoutManager
import java.util.zip.GZIPOutputStream
import kotlin.collections.ArrayList


class ListTvsFragments  : Fragment() {



    private val authViewModel by activityViewModels<UsersViewModel>()
    private val binding by viewBinding(FragmentListWordsBinding::bind)
    lateinit var  adapter:TvAdapter
    lateinit var layoutManager : SpannedGridLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_words, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBinding()
        observeEvents()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun applyBinding() {
        with(binding) {
            adapter = TvAdapter(requireActivity(),object  : OpenUserListener{
                override fun open() { }
            })
            binding.recyclerview.adapter = adapter
            layoutManager =  SpannedGridLayoutManager(  orientation = SpannedGridLayoutManager.Orientation.VERTICAL,spans = 4)
            binding.recyclerview.layoutManager = layoutManager
            layoutManager.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
                when(position % 2)
                {
                    0->{
                        SpanSize(4, 2                                                                                                                                                                 )
                    }
                    else->{
                        SpanSize(2, 2)
                    }
                }

            }
            binding.fab.setOnClickListener {
                if(checkCameraPermission())
                    openGallery()
                else{
                    DialogPermissions().show(requireActivity().supportFragmentManager,"")
                }
            }
            authViewModel.getTvs()
        }
    }

    private fun checkCameraPermission():Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED&&
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            activity?.let {
                ActivityCompat.requestPermissions( it, arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE),  REQUEST_PERMISSION)
            }
            return false
        }else{
            return true
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(requireActivity().packageManager)?.also {
                activity?.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(requireActivity().packageManager)?.also {
                activity?.startActivityForResult(intent, REQUEST_PICK_IMAGE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        authViewModel.getTvs()
    }

    private fun observeEvents() {
        authViewModel.userresponse?.observe(viewLifecycleOwner,EventObserver{ tvs ->
            with(binding) {
                binding.tipesResults.setText(getString(R.string.list_online))
            }
            adapter.setTvsItems(tvs as ArrayList<Tv>)
        })
        authViewModel.mTvsLocallivedata?.observe(viewLifecycleOwner,EventObserver{ tvs ->
            with(binding) {
                binding.tipesResults.setText(getString(R.string.list_local))
                if(tvs.size>0) {
                    binding.recyclerview.visibility = VISIBLE
                    binding.noDataItem.root.visibility = View.GONE
                    adapter.setTvsItems(tvs as ArrayList<Tv>)
                }
                else{
                    binding.recyclerview.visibility =GONE
                    binding.noDataItem.root.visibility = View.VISIBLE
                }
            }

        })

    }






}



