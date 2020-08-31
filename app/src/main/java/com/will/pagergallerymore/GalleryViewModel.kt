package com.will.pagergallerymore

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.toLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

class GalleryViewModel (application: Application): AndroidViewModel(application) {

    val dataListPhoto = PixabayDataSourceFactory(application).toLiveData(1)

    fun resetQuery(){
        //使数据源，无效化，重新生成一个数据源，这样可以达到重新改变关键字
        dataListPhoto.value?.dataSource?.invalidate()
    }
}