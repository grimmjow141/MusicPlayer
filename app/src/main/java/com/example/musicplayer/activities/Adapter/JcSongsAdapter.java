package com.example.musicplayer.activities.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.activities.Model.GetSongs;
import com.example.musicplayer.activities.Model.Utility;
import com.example.musicplayer.activities.SongsActivity;

import java.io.IOException;
import java.util.List;

public class JcSongsAdapter extends RecyclerView.Adapter<JcSongsAdapter.SongsAdapterViewHolder> {
    Context context;
    private int selectedPosition;
    List<GetSongs> getSongsList;

    public JcSongsAdapter(Context context, List<GetSongs> getSongsList) {
        this.context = context;
        this.getSongsList = getSongsList;
    }

    @NonNull
    @Override
    public SongsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.songs_row,parent,false);
        return new SongsAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsAdapterViewHolder holder, int position) {
        GetSongs getSongs=getSongsList.get(position);
        holder.tv_title.setText(getSongs.getSongTitle());
        String duration= Utility.convertDuration(Long.parseLong(getSongs.getSongDuration()));
        holder.tv_duration.setText(duration);
    }

    @Override
    public int getItemCount() {
        return getSongsList.size();
    }

    public class SongsAdapterViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_title,tv_duration;
        private ImageView iv_play_active;
        public SongsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title=itemView.findViewById(R.id.tv_title);
            tv_duration=itemView.findViewById(R.id.tv_duration);
            iv_play_active=itemView.findViewById(R.id.iv_play_active);
            //cand apas pe un cantec din lista incepe sa cante
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                ((SongsActivity)context).playSong(getSongsList,getAdapterPosition());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
