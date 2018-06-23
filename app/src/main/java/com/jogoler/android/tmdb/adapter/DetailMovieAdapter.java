package com.jogoler.android.tmdb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jogoler.android.tmdb.DetailMovieContract;
import com.jogoler.android.tmdb.R;
import com.jogoler.android.tmdb.pojo.Review;
import com.jogoler.android.tmdb.pojo.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gilbert on 19/06/18.
 */

public class DetailMovieAdapter extends RecyclerView.Adapter<DetailMovieAdapter.BaseViewHolder> {
    private static final int TYPE_TITLE = 0;
    public static final int TYPE_TRAILER = 1;
    public static final int TYPE_REVIEW = 2;

    private Context context;
    private List<Object> data;
    private DetailMovieContract.Adapter fragment;

    public DetailMovieAdapter(Context context, DetailMovieContract.Adapter fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    public void add(List detailMovieMap) {
        if (data == null) {
            data = new ArrayList<>();
        }
        data.clear();
        data.addAll(detailMovieMap);
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TITLE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_list_content, parent, false);
                return new TitleViewAdapter(view);
            }
            case TYPE_TRAILER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_content, parent, false);
                return new TrailerViewAdapter(view);
            }
            case TYPE_REVIEW: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_content, parent, false);
                return new ReviewViewAdapter(view);
            }
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Object object = data.get(position);
        holder.bind(object);
    }

    @Override
    public int getItemViewType(int position) {
        Object object = data.get(position);
        if (object instanceof String) {
            return TYPE_TITLE;
        } else if (object instanceof Trailer) {
            return TYPE_TRAILER;
        } else if (object instanceof Review) {
            return TYPE_REVIEW;
        }

        throw new IllegalArgumentException("Invalid position " + position);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
        private BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public abstract void bind(T type);
    }

    public class TitleViewAdapter extends BaseViewHolder<String> {

        @BindView(R.id.title_text_view)
        TextView titleTextView;

        private TitleViewAdapter(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(String title) {
            titleTextView.setText(title);
        }
    }

    public class TrailerViewAdapter extends BaseViewHolder<Trailer> implements View.OnClickListener {

        @BindView(R.id.trailer_list_linear_layout)
        View trailerListView;

        @BindView(R.id.trailer_title_text_view)
        TextView titleTextView;

        @BindView(R.id.thumbnail_trailer_image_view)
        ImageView trailerImageView;

        private TrailerViewAdapter(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Trailer trailer) {
            String thumnailUrl = "http://img.youtube.com/vi/" + trailer.getKeyy() + "/0.jpg";
            Picasso.with(context)
                    .load(thumnailUrl)
                    .into(trailerImageView);
            titleTextView.setText(trailer.getName());
            trailerListView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            if (position < 0) {
                return;
            }

            Trailer trailer = (Trailer) data.get(position);
            fragment.watch(trailer, getAdapterPosition());
        }
    }

    public class ReviewViewAdapter extends BaseViewHolder<Review> implements View.OnClickListener {
        @BindView(R.id.review_list_linear_layout)
        View reviewListView;
        @BindView(R.id.author_text_view)
        TextView authorTextView;
        @BindView(R.id.review_text_view)
        TextView reviewTextView;

        public ReviewViewAdapter(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Review review) {
            authorTextView.setText(review.getAuthor());
            reviewTextView.setText(review.getContent());
            reviewListView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position < 0) {
                return;
            }

            Review review = (Review) data.get(position);
            fragment.read(review, getAdapterPosition());
        }
    }


}
