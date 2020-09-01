package com.will.pagergallerymore

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import androidx.lifecycle.Observer

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment() {

    private val galleryViewModel by activityViewModels<GalleryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val galleryAdapter = GalleryAdapter(galleryViewModel)
        recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }

        //pagedListLiveData 数据变化的观察
        galleryViewModel.pagedListLiveData.observe(viewLifecycleOwner, Observer {
            galleryAdapter.submitList(it)

        })

        //网络状态的变化观察
        galleryViewModel.networkStatus.observe(viewLifecycleOwner, Observer {
            galleryAdapter.updateNetworkStatus(it)

            swipeLayoutGallery.isRefreshing = it == NetworkStatus.INITIAL_LOADING
        })
//        galleryViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(GalleryViewModel::class.java)

//        galleryViewModel.photoListLive.observe(viewLifecycleOwner, Observer {
//            swipeLayoutGallery.isRefreshing = false
//            galleryAdapter.submitList(it)
//
//        })
//
//        galleryViewModel.photoListLive.value?:galleryViewModel.fetchData()
//
        swipeLayoutGallery.setOnRefreshListener {
            //刷新操作
            galleryViewModel.resetQuery()

        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.swipeIndicator ->{
                swipeLayoutGallery.isRefreshing = true
                Handler().postDelayed({ galleryViewModel.resetQuery() },1000)
            }
            R.id.retry ->{
                galleryViewModel.retry()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}