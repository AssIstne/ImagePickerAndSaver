package com.assistne.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.assistne.imagepickerandsaver.ImageFolder;
import com.assistne.imagepickerandsaver.ImageItem;
import com.assistne.imagepickerandsaver.ImagePicker;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "#MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ImagePicker(this, null, new ImagePicker.OnImagesLoadedListener() {
            @Override
            public void onImagesLoaded(List<ImageItem> allImageItem, Map<String, ImageFolder> imageFolderMap) {
                if (allImageItem != null) {
                    for (ImageItem i : allImageItem) {
                        Log.d(TAG, "onImagesLoaded: " + i.path);
                    }
                }
            }
        });
    }
}
