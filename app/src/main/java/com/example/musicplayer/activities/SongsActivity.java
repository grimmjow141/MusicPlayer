package com.example.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.example.musicplayer.R;
import com.example.musicplayer.activities.Adapter.JcSongsAdapter;
import com.example.musicplayer.activities.Model.GetSongs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SongsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    List<GetSongs> mupload;
    JcSongsAdapter adapter;
    DatabaseReference ref;
    ValueEventListener valueEventListener;
    JcPlayerView jcPlayerView;
    ArrayList<JcAudio> jcAudios=new ArrayList<>();
    MediaPlayer mediaPlayer;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        recyclerView=findViewById(R.id.recycleView);
        progressBar=findViewById(R.id.progressbarshowsong);
        jcPlayerView=findViewById(R.id.jcplayer);
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
        {
            jcPlayerView.setVisibility(View.GONE);
            LayoutParams lp=(LayoutParams) jcPlayerView.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            jcPlayerView.setLayoutParams(lp);
        }else if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
        {
            jcPlayerView.setVisibility(View.GONE);
            LayoutParams lp=(LayoutParams) jcPlayerView.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            jcPlayerView.setLayoutParams(lp);
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mupload=new ArrayList<>();
        adapter=new JcSongsAdapter(SongsActivity.this,mupload);
        recyclerView.setAdapter(adapter);
        ref=FirebaseDatabase.getInstance().getReference("songs");
        valueEventListener=ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mupload.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    GetSongs getSongs=ds.getValue(GetSongs.class);
                    getSongs.setmKey(ds.getKey());
                    mupload.add(getSongs);
                    jcAudios.add(JcAudio.createFromURL(getSongs.getSongTitle(),getSongs.getSongLink()));
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void playSong(List<GetSongs> getSongsList, int adapterPosition) throws IOException {
        GetSongs getSongs=getSongsList.get(adapterPosition);
        jcPlayerView.initPlaylist(jcAudios,null);
        jcPlayerView.playAudio(jcAudios.get(adapterPosition));
        jcPlayerView.setVisibility(View.VISIBLE);
        jcPlayerView.createNotification();
    }
}