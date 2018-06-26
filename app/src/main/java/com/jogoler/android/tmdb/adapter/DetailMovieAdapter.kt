package com.jogoler.android.tmdb.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jogoler.android.tmdb.DetailMovieContract
import com.jogoler.android.tmdb.R
import com.jogoler.android.tmdb.pojo.Review
import com.jogoler.android.tmdb.pojo.Trailer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.review_list_content.view.*
import kotlinx.android.synthetic.main.title_list_content.view.*
import kotlinx.android.synthetic.main.trailer_list_content.view.*
import java.util.*


/**
 * Created by Gilbert on 19/06/18.
 */

class DetailMovieAdapter(private val listener: DetailMovieContract.MovieListener) : RecyclerView.Adapter<DetailMovieAdapter.BaseViewHolder<*>>() {

    private val data: MutableList<Comparable<*>>

    companion object {
        private val TYPE_TITLE = 0
        private val TYPE_TRAILER = 1
        private val TYPE_REVIEW = 2
    }

    init {
        data = ArrayList()
    }

    fun swapData(newData: List<Comparable<*>>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val context = parent.context
        return when (viewType) {
            TYPE_TITLE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.title_list_content, parent, false)
                TitleViewHolder(view)
            }
            TYPE_TRAILER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.trailer_list_content, parent, false)
                TrailerViewHolder(view, listener, data)
            }
            TYPE_REVIEW -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.review_list_content, parent, false)
                ReviewViewHolder(view, listener, data)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = data[position]
        when(holder){
            is TitleViewHolder -> holder.bind(element as String)
            is TrailerViewHolder -> holder.bind(element as Trailer)
            is ReviewViewHolder -> holder.bind(element as Review)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val comparable = data[position]
        return when (comparable) {
            is String -> TYPE_TITLE
            is Trailer -> TYPE_TRAILER
            is Review -> TYPE_REVIEW
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    class TitleViewHolder(val view: View) : BaseViewHolder<String>(view) {

        private val titleTextView = view.title_text_view_

        override fun bind(title: String) {
            titleTextView.text = title
        }
    }

    class TrailerViewHolder(val view: View, val listener: DetailMovieContract.MovieListener,
                            val data: List<Comparable<*>>) : BaseViewHolder<Trailer>(view), View.OnClickListener {

        val trailerListView = view.trailer_list_linear_layout
        val titleTextView = view.trailer_title_text_view
        val trailerImageView = view.thumbnail_trailer_image_view

        override fun bind(trailer: Trailer) {
            val thumbnailUrl = "http://img.youtube.com/vi/" + trailer.key + "/0.jpg" // getKeyyy
            Picasso.with(view.context)
                    .load(thumbnailUrl)
                    .into(trailerImageView)
            titleTextView.text = trailer.name
            trailerListView!!.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition

            if (position < 0) {
                return
            }

            val trailer = data[position] as Trailer
            listener.onWatch(trailer, adapterPosition)
        }
    }

    class ReviewViewHolder(val view: View, val listener: DetailMovieContract.MovieListener,
                           val data: List<Comparable<*>>) : BaseViewHolder<Review>(view), View.OnClickListener {
        val reviewListView = view.review_list_linear_layout
        val authorTextView = view.author_text_view
        val reviewTextView = view.review_text_view

        override fun bind(review: Review) {
            authorTextView.text = review.author
            reviewTextView.text = review.content
            reviewListView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition

            if (position < 0) {
                return
            }

            val review = data[position] as Review
            listener.onRead(review, adapterPosition)
        }
    }
}
