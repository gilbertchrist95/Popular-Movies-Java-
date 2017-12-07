package com.jogoler.android.tmdb.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jogoler.android.tmdb.R;
import com.jogoler.android.tmdb.pojo.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewHolder>{

    private ArrayList<Trailer> mTrailers;
    private Callbacks mCallbacks;

    public interface Callbacks{
        void watch(Trailer trailer, int position);
    }

    public TrailerListAdapter(ArrayList<Trailer> mTrailers, Callbacks mCallbacks) {
        this.mTrailers = mTrailers;
        this.mCallbacks = mCallbacks;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_content,parent,false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerViewHolder holder, final int position) {
        final Trailer trailer = mTrailers.get(position);
        final Context context = holder.mView.getContext();
        holder.trailer = mTrailers.get(position);
        String thumnailUrl = "http://img.youtube.com/vi/" + trailer.getKeyy() + "/0.jpg";
        Log.d("thumbnail traler", "thumbnailUrl -> " + thumnailUrl);
        Picasso.with(context)
                .load(thumnailUrl)
                .into(holder.trailerImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.watch(trailer,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder{
        public final View mView;

        @BindView(R.id.thumbnail_trailer_image_view)
        ImageView trailerImageView;
        public Trailer trailer;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.mView = itemView;
        }


    }

    public void add(List<Trailer> trailers){
        mTrailers.clear();
        mTrailers.addAll(trailers);
        notifyDataSetChanged();
    }

    public ArrayList<Trailer> getTrailers() {
        return mTrailers;
    }
}
