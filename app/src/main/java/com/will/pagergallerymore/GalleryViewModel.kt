package com.will.pagergallerymore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData

class GalleryViewModel (application: Application): AndroidViewModel(application) {
    private val factory = PixabayDataSourceFactory(application)
    val pagedListLiveData = factory.toLiveData(1)

    //用一个LiveData 去观察另外一个LiveData,也就是从一个LiveData去生成另外一个LiveData
    val networkStatus = Transformations.switchMap(factory.pixabayDataSource) {it.networkStatus}

    fun resetQuery(){
        //使数据源，无效化，重新生成一个数据源，这样可以达到重新改变关键字
        pagedListLiveData.value?.dataSource?.invalidate()
    }

    fun retry(){
        //invoke重新呼叫,重新尝试
        factory.pixabayDataSource.value?.retry?.invoke()
    }
}