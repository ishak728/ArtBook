package com.example.artbook.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.artbook.R
import com.example.artbook.adapter.ArtRecyclerAdapter
import com.example.artbook.adapter.ImageRecyclerAdapter
import com.example.artbook.databinding.FragmentArtsBinding
import com.example.artbook.viewModel.ArtViewModel
import javax.inject.Inject

class ArtFragment @Inject constructor(
    val artRecyclerAdapter: ArtRecyclerAdapter
):Fragment(R.layout.fragment_arts) {

    lateinit var viewModel : ArtViewModel
    private var fragmentBinding:FragmentArtsBinding?=null

    private val swipeCallBack = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //kaydırılan layoutun positionu verilir
            val layoutPosition = viewHolder.layoutPosition
            val selectedArt = artRecyclerAdapter.arts[layoutPosition]
            viewModel.deleteArt(selectedArt)

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ArtViewModel::class.java)
        fragmentBinding=FragmentArtsBinding.bind(view)

        subscribeToObservers()

        fragmentBinding!!.recyclerViewArt.adapter = artRecyclerAdapter
        fragmentBinding!!.recyclerViewArt.layoutManager = LinearLayoutManager(requireContext())

        ItemTouchHelper(swipeCallBack).attachToRecyclerView(fragmentBinding!!.recyclerViewArt)


        fragmentBinding!!.fab.setOnClickListener{
            val action=ArtFragmentDirections.actionArtFragmentToArtDetailsFragment()
            //findNavController().navigate(action)
            Navigation.findNavController(view).navigate(action)
        }
    }
    private fun subscribeToObservers() {
        viewModel.artList.observe(viewLifecycleOwner, Observer {
            artRecyclerAdapter.arts = it
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        fragmentBinding=null
    }
}