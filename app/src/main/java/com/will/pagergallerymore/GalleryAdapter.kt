package com.will.pagergallerymore


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*

class GalleryAdapter(private val galleryViewModel: GalleryViewModel) :
    PagedListAdapter<PhotoItem, RecyclerView.ViewHolder>(DiffCallback) {

    private var networkStatus: NetworkStatus? = null
    private var hasFooter = false

    init {
        //如果在Photo页面使回来后，有未加载成功到数据，可以继续重试加载数据
        galleryViewModel.retry()
    }

    fun updateNetworkStatus(networkStatus: NetworkStatus?) {
        this.networkStatus = networkStatus
        if (networkStatus == NetworkStatus.INITIAL_LOADING) hidFooter() else showFooter()
    }

    object DiffCallback : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }
    }

    private fun hidFooter() {
        if (hasFooter) {
            notifyItemRemoved(itemCount - 1)
        }

        hasFooter = false
    }

    private fun showFooter() {
        if (hasFooter) {
            notifyItemChanged(itemCount - 1)
        } else {
            hasFooter = true
            notifyItemInserted(itemCount -1)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasFooter && position == itemCount - 1) R.layout.gallery_footer else R.layout.gallery_cell
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            R.layout.gallery_cell -> PhotoViewHolder.newInstance(parent).also { holder ->
                holder.itemView.setOnClickListener {
                    Bundle().apply {
                        //通过View Model 的LiveData 传递currentList
                        //putParcelableArrayList("PHOTO_LIST", ArrayList(currentList))
                        putInt("PHOTO_POSITION", holder.adapterPosition)
                        holder.itemView.findNavController()
                            .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, this)
                    }
                }
            }
            else -> FooterViewHolder.newInstance(parent).also {
                it.itemView.setOnClickListener {
                    galleryViewModel.retry()
                }
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            R.layout.gallery_footer -> (holder as FooterViewHolder).bindWithNetworkStatus(
                networkStatus
            )
            else -> {
                val photoItem: PhotoItem = getItem(position) ?: return
                (holder as PhotoViewHolder).bindWithPhotoItem(photoItem)
            }
        }

    }


}

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup): PhotoViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false)
            return PhotoViewHolder(view)
        }
    }

    fun bindWithPhotoItem(photoItem: PhotoItem) {
        with(itemView) {
            shimmerLayoutCell.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()

            }
            imageView.layoutParams.height = photoItem.photoHeight

            textViewUser.text = photoItem.photoUser
            textViewLikes.text = photoItem.photoLikes.toString()
            textViewFavorites.text = photoItem.photoFavorites.toString()
        }


        Glide.with(itemView.context)
            .load(photoItem.previewURL)
            .placeholder(R.drawable.photo_placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { itemView.shimmerLayoutCell?.stopShimmerAnimation() }
                }
            })
            .into(itemView.imageView)
    }
}

class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup): FooterViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_footer, parent, false)
            //使footer这个item扩充到一整行
            (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            return FooterViewHolder(view)
        }
    }

    fun bindWithNetworkStatus(networkStatus: NetworkStatus?) {
        with(itemView) {
            when (networkStatus) {
                NetworkStatus.FAILED -> {
                    textView.text = "加载失败，点击重试"
                    progressBar.visibility = View.GONE
                    isClickable = true
                }

                NetworkStatus.COMPLETED -> {
                    textView.text = "加载完毕"
                    progressBar.visibility = View.GONE
                    isClickable = false
                }

                else -> {
                    textView.text = "正在加载..."
                    progressBar.visibility = View.VISIBLE
                    isClickable = false
                }
            }
        }
    }
}
