package com.example.android.examenadolfo.presentation.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import com.example.android.examenadolfo.databinding.FragmentListWordsBinding
import com.example.android.examenadolfo.presentation.common.viewBinding
import com.example.android.examenadolfo.utils.EventObserver


import com.example.android.examenadolfo.R
import com.example.android.examenadolfo.data.network.model.response.Tv
import com.example.android.examenadolfo.utils.spanedGridLayoutManager.SpanSize
import com.example.android.examenadolfo.utils.spanedGridLayoutManager.SpannedGridLayoutManager
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
               // val intent = createImagePickerIntent(context, ImagePickerConfig())
                //startActivityForResult(intent, RC_IMAGE_PICKER)
            }
            authViewModel.getTvs()
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
            }
            adapter.setTvsItems(tvs as ArrayList<Tv>)
        })

    }






}



