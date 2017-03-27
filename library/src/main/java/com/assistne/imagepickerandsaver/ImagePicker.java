package com.assistne.imagepickerandsaver;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.assistne.imagepickerandsaver.SelectionConfig.IMAGE_PROJECTION;

/**
 */
public class ImagePicker implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "#ImagePicker";
    private static final String BUNDLE_CONFIG = "CONFIG";

    private Activity mActivity;
    private OnImagesLoadedListener mOnImagesLoadedListener;                     //图片加载完成的回调接口
    private boolean mNeedArchive;

    public ImagePicker(Activity activity, SelectionConfig config, OnImagesLoadedListener loadedListener) {
        this(activity, config, loadedListener, false);
    }

    /**
     * 获取图片, 等待回调
     * @param activity 不能为null
     * @param config 筛选项, 用于构建where语句
     * @param loadedListener 回调接口
     * @param archive 是否划分出文件夹, false时回调中的Map<String, ImageFolder>为空
     */
    public ImagePicker(Activity activity, SelectionConfig config, OnImagesLoadedListener loadedListener, boolean archive) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity can not be null!");
        }
        // 调用前必须先申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
            throw new IllegalStateException("Get Manifest.permission.WRITE_EXTERNAL_STORAGE first!");
        }
        mOnImagesLoadedListener = loadedListener;
        mActivity = activity;
        mNeedArchive = archive;
        LoaderManager loaderManager = mActivity.getLoaderManager();
        if (config == null) {
            // 加载所有的图片
            loaderManager.initLoader(SelectionConfig.LOADER_ALL, null, this);
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(BUNDLE_CONFIG, config);
            loaderManager.initLoader(config.getIdentifier(), bundle, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;
        SelectionConfig config = args != null ? (SelectionConfig) args.getParcelable(BUNDLE_CONFIG) : null;
        if (config == null) {
            //扫描所有图片
            cursorLoader = new CursorLoader(
                    mActivity,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[4] + " DESC");
        } else {
            cursorLoader = new CursorLoader(
                    mActivity,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    getSelectionFromConfig(config), null, IMAGE_PROJECTION[4] + " DESC");
        }
        return cursorLoader;
    }

    // TODO: 17/3/27 根据Config的值构建where语句
    private String getSelectionFromConfig(SelectionConfig config) {
        return "";
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() <= 0) {
            mOnImagesLoadedListener.onImagesLoaded(null, null);
            return;
        }
        List<ImageItem> allImageGroup = new ArrayList<>(data.getCount());
        Map<String, ImageFolder> folderGroup = null;
        if (mNeedArchive) {
            folderGroup = new HashMap<>();
        }
        while (data.moveToNext()) {
            //封装实体
            ImageItem imageItem = Utils.createImageItemFromCursor(data);
            allImageGroup.add(imageItem);
            //根据父路径分类存放图片
            String parentPath = imageItem.getParentPath();
            if (mNeedArchive && parentPath != null) {
                ImageFolder imageFolder;
                if (folderGroup.containsKey(parentPath)) {
                    imageFolder = folderGroup.get(parentPath);
                } else {
                    imageFolder = new ImageFolder();
                    File dir = new File(parentPath);
                    imageFolder.name = dir.getName();
                    imageFolder.path = dir.getAbsolutePath();
                }
                imageFolder.addImageItem(imageItem);
            }
        }
        //回调接口，通知图片数据准备完成
        mOnImagesLoadedListener.onImagesLoaded(allImageGroup, folderGroup);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    /** 所有图片加载完成的回调接口 */
    public interface OnImagesLoadedListener {
        void onImagesLoaded(List<ImageItem> allImageItem, Map<String, ImageFolder> imageFolderMap);
    }

}