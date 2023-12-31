package com.example.mymusicapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView= findViewById(R.id.listview);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        Toast.makeText(MainActivity.this, "Runtime permisson Given", Toast.LENGTH_SHORT).show();
                        ArrayList<File> mysongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String [] items = new String[mysongs.size()];

                        for(int i = 0; i<mysongs.size();i++){
                            items[i] = mysongs.get(i).getName().replace("mp3","");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this,playsong.class);
                                String currentsong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("songlist",mysongs);
                                intent.putExtra("currentsong",currentsong);
                                intent.putExtra("position",position);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

public ArrayList<File> fetchSongs(File file) {
    ArrayList arraylist = new ArrayList();
    File[] songs = file.listFiles();
    if (songs != null) {
        for (File myFile : songs) {
            if (!myFile.isHidden() && myFile.isDirectory()) {
                arraylist.addAll(fetchSongs(myFile));
            }
            else {
                if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                    arraylist.add(myFile);
                }
            }
        }
    }

    return arraylist;
}


}