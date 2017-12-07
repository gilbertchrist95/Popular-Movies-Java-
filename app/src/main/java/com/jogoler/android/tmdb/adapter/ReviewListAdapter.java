package com.jogoler.android.tmdb.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jogoler.android.tmdb.R;
import com.jogoler.android.tmdb.pojo.Review;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

    private final ArrayList<Review> mReviews;
    private final Callbacks mCallbacks;

    public ReviewListAdapter(ArrayList<Review> reviews, Callbacks mCallbacks) {
        this.mReviews = reviews;
        this.mCallbacks = mCallbacks;
    }

    public interface Callbacks{
        void read(Review review,int position);
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_content,parent,false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, int position) {
        final Review review = mReviews.get(position);
        holder.mReview = review;
        holder.authorTextView.setText(review.getAuthor());
        holder.reviewTextView.setText(review.getContent());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.read(review,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        public View mView;
        @BindView(R.id.author_text_view)
        TextView authorTextView;
        @BindView(R.id.review_text_view)
        TextView reviewTextView;
        public Review mReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.mView = itemView;
        }


    }

    public void add(List<Review>reviews){
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public ArrayList<Review> getReviews() {
        return mReviews;
    }
}
