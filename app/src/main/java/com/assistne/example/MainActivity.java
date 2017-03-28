package com.assistne.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.assistne.imagepickerandsaver.ImageFolder;
import com.assistne.imagepickerandsaver.ImageItem;
import com.assistne.imagepickerandsaver.ImagePicker;
import com.assistne.imagepickerandsaver.SelectionConfig;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "#MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SelectionConfig config = new SelectionConfig.Builder()
                .includePath("Screenshots")
                .create();
        new ImagePicker(this, config, new ImagePicker.OnImagesLoadedListener() {
            @Override
            public void onImagesLoaded(List<ImageItem> allImageItem, Map<String, ImageFolder> imageFolderMap) {
                if (allImageItem != null) {
                    for (ImageItem i : allImageItem) {
                        Log.d(TAG, "" + i.path + "  " + i.width + "  " + i.height);
                    }
                }
                Log.d(TAG, "onImagesLoaded: " + imageFolderMap.size());
                if (imageFolderMap != null) {
                    for (Map.Entry<String, ImageFolder> entry : imageFolderMap.entrySet()) {
                        Log.d(TAG, entry.getValue().path);
                    }
                }
            }
        }, true);
    }
}
