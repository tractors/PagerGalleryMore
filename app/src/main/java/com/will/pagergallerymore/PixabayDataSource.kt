package com.will.pagergallerymore

import android.content.Context
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
private const val TAG = "PixabayDataSource"
class PixabayDataSource(private val context:Context) : PageKeyedDataSource<Int,PhotoItem>() {


    //随机一个关键字
    private val queryKey = arrayOf("cat","dog","car","beauty","phone","computer","flower","animal").random()



    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
        val url = "https://pixabay.com/api/?key=18052656-ccd8aa3aa1747309043e02819&q=${queryKey}&per_page=50&page=1"

        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener{
                val dataList = Gson().fromJson(it, Pixabay::class.java).hits.toList()

                callback.onResult(dataList,null,2)
            },
            Response.ErrorListener {
                Log.e(TAG, "loadInitial: $it" )
            }
        ).also{
            VolleySingleton.getInstance(context.applicationContext).requestQueue.add(it)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        val url = "https://pixabay.com/api/?key=18052656-ccd8aa3aa1747309043e02819&q=${queryKey}&per_page=50&page=${params.key}"

        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                val dataList = Gson().fromJson(it, Pixabay::class.java).hits.toList()
                callback.onResult(dataList,params.key +1)
            },
            Response.ErrorListener {
                Log.e(TAG, "loadInitial: $it" )
            }
        ).also {
            VolleySingleton.getInstance(context.applicationContext).requestQueue.add(it)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        TODO("Not yet implemented")
    }
}