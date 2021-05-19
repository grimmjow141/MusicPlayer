package com.example.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.activities.Model.UploadSong;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    TextView textViewImage;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference mStorageRef;
    StorageTask mUploadsTask;
    DatabaseReference ref;
    String songsCategory;
    byte[] art;
    String title1,artist1,album_art1="",duration1;
    TextView title,artist,durations,album,dataa;
    ImageView album_art;
    MediaMetadataRetriever metadataRetriever;
    Spinner spinner;
    Button openAudioFiles,uploadBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        textViewImage=findViewById(R.id.textViewSongsFilesSelected);
        progressBar=findViewById(R.id.progressBar);
        title=findViewById(R.id.title);
        artist=findViewById(R.id.artist);
        durations=findViewById(R.id.duration);
        album=findViewById(R.id.album);
        dataa=findViewById(R.id.dataa);
        album_art=findViewById(R.id.imageview);
        openAudioFiles=findViewById(R.id.openAudioFiles);
        uploadBtn=findViewById(R.id.uploadBtn);
        metadataRetriever= new MediaMetadataRetriever();
        ref=FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageRef= FirebaseStorage.getInstance().getReference().child("songs");
        spinner=findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        List<String> categories=new ArrayList<>();
        categories.add("Love Songs");
        categories.add("Sad Songs");
        categories.add("Party Songs");
        categories.add("Birthday Songs");
        categories.add("God Songs");
        ArrayAdapter<String> dataAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        openAudioFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("audio/*");
                startActivityForResult(i,101);
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFileToFirebase();
            }
        });
    }

    private void uploadFileToFirebase() {
        if(textViewImage.equals("No file Selected"))
        {
            Toast.makeText(getApplicationContext(),"Please selected an image!",Toast.LENGTH_SHORT).show();
        }else{
            if(mUploadsTask!=null&&mUploadsTask.isInProgress())
            {
                Toast.makeText(getApplicationContext(),"songs uploads in allready progress!",Toast.LENGTH_SHORT).show();
            }else{
                uploadFile();
            }
        }
    }

    private void uploadFile() {
        if(audioUri!=null)
        {
            Toast.makeText(getApplicationContext(),"upload please wait!",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference=mStorageRef.child(System.currentTimeMillis()+"."+getFileNameExtension(audioUri));
            mUploadsTask=storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UploadSong uploadSong=new UploadSong(songsCategory,title1,artist1,album_art1,duration1,uri.toString());
                            String uploadId=ref.push().getKey();
                            ref.child(uploadId).setValue(uploadSong);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    // calculeaza progresul in functie de rata de transfer
                    // si de dimensiunea totala a fisierului pe care dorim sa-l uploadam
                    double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);
                    if(snapshot.getTotalByteCount()==snapshot.getBytesTransferred())
                        Toast.makeText(getApplicationContext(),"Uploadet!",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private String getFileNameExtension(Uri audioUri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101&&resultCode==RESULT_OK&&data.getData()!=null)
        {
            audioUri=data.getData();
            metadataRetriever.setDataSource(this,audioUri);
            art=metadataRetriever.getEmbeddedPicture();
            album.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            artist.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            dataa.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
            durations.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            title.setText(getFileName(audioUri));
            artist1=metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            title1=getFileName(audioUri);
            duration1=metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            textViewImage.setText(getFileName(audioUri));
        }
    }
    private String getFileName(Uri uri)
    {
        String result=null;
        if(uri.getScheme().equals("content"))
        {
            Cursor cursor=getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if(result==null)
        {
            result=uri.getPath();
            int cut=result.lastIndexOf('/');
            if(cut!=-1)
            {
                result=result.substring(cut+1);
            }
        }
        return result;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        songsCategory=parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}