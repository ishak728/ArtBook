package com.example.artbook.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.example.artbook.R
import com.example.artbook.adapter.ImageRecyclerAdapter
import com.example.artbook.databinding.FragmentImageApiBinding
import com.example.artbook.util.Status
import com.example.artbook.viewModel.ArtViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageApiFragment @Inject constructor(
    val imageRecyclerAdapter: ImageRecyclerAdapter
):Fragment(R.layout.fragment_image_api) {
    private var fragmentBinding : FragmentImageApiBinding? = null
    lateinit var viewModel : ArtViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ArtViewModel::class.java)

         fragmentBinding = FragmentImageApiBinding.bind(view)

        var job: Job? = null

        fragmentBinding!!.searchText.addTextChangedListener {
            //her text değiştiğinde önceki text için oluşturulan job iptal ediliyor.
            job?.cancel()
            job = lifecycleScope.launch {
                //kullanıcı her metin girişi yaptıktan 1 saniye sonra arama yaparaktan uygulamanın kitlenmesini delay sayesinde önleriz
                delay(1000)
                it?.let {
                    if (it.toString().isNotEmpty()) {
                        viewModel.searchForImage(it.toString())
                    }
                }
            }
        }

        subscribeToObservers()

        fragmentBinding!!.imageRecyclerView.adapter = imageRecyclerAdapter
        fragmentBinding!!.imageRecyclerView.layoutManager = GridLayoutManager(requireContext(),3)

        imageRecyclerAdapter.setOnItemClickListener {
            findNavController().popBackStack()
            viewModel.setSelectedImage(it)
        }

    }


    private fun subscribeToObservers() {
        viewModel.imageList.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    val urls = it.data?.hits?.map { imageResult ->  imageResult.previewURL }
                    imageRecyclerAdapter.images = urls ?: listOf()
                    fragmentBinding!!.progressBar.visibility = View.GONE

                }

                Status.ERROR -> {
                    Toast.makeText(requireContext(),it.message ?: "Error",Toast.LENGTH_LONG).show()
                    fragmentBinding?.progressBar?.visibility = View.GONE

                }

                Status.LOADING -> {
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE

                }
            }

        })
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}