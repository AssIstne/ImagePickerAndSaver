package com.assistne.imagepickerandsaver;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by assistne on 17/3/27.
 */
class SelectionConfig implements Parcelable {
    public static final int LOADER_ALL = 111;         //加载所有图片
    static final String PATH = "path";
    static final String TYPE = "type";
    static final String WIDTH = "width";
    static final String HEIGHT = "height";

    public static String[] IMAGE_PROJECTION;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            IMAGE_PROJECTION = new String[]{     //查询图片需要的数据列
                    MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  aaa.jpg
                    MediaStore.Images.Media.DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
                    MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
                    MediaStore.Images.Media.MIME_TYPE,      //图片的类型     image/jpeg
                    MediaStore.Images.Media.DATE_ADDED,    //图片被添加的时间，long型  1450518608
                    MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
                    MediaStore.Images.Media.HEIGHT};      //图片的高度，int型  1080
        } else {// SDK16以下没有width和height参数
            IMAGE_PROJECTION = new String[]{
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.MIME_TYPE,
                    MediaStore.Images.Media.DATE_ADDED};
        }
    }

    ArrayList<String> pathSelection;
    ArrayList<String> typeSelection;

    public int getIdentifier() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.pathSelection);
        dest.writeStringList(this.typeSelection);
    }

    public SelectionConfig() {
    }

    protected SelectionConfig(Parcel in) {
        this.pathSelection = in.createStringArrayList();
        this.typeSelection = in.createStringArrayList();
    }

    public static final Creator<SelectionConfig> CREATOR = new Creator<SelectionConfig>() {
        @Override
        public SelectionConfig createFromParcel(Parcel source) {
            return new SelectionConfig(source);
        }

        @Override
        public SelectionConfig[] newArray(int size) {
            return new SelectionConfig[size];
        }
    };
}
