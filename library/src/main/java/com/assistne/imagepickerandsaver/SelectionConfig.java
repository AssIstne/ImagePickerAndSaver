package com.assistne.imagepickerandsaver;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by assistne on 17/3/27.
 */
public class SelectionConfig implements Parcelable {
    private static final String TAG = "#SelectionConfig";
    public static final int LOADER_ALL = 111;         //加载所有图片
    static final String GREATER = " > ";
    static final String EQUAL = " = ";
    static final String NOT_EQUAL = " != ";
    static final String LESS = " < ";
    static final String GREATER_AND_EQUAL = " >= ";
    static final String LESS_AND_EQUAL = " <= ";
    static final String AND = " and ";
    static final String OR = " or ";
    static final String LIKE = " like ";
    static final String NOT_LIKE = " not like ";


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

    private ArrayList<String> pathSelection;
    private ArrayList<String> pathExclude;
    private ArrayList<String> typeSelection;
    private ArrayList<String> typeExclude;
    private String mWidthOperator;
    private int mWidthLimit;
    private String mHeightOperator;
    private int mHeightLimit;
    private String mSelection;

    public String getSelection() {
        if (mSelection == null) {
            mSelection = assembleSelection();
        }
        return mSelection;
    }

    private String wrapForLike(String value) {
        return "'%" + value + "%'";
    }

    private String assembleSelection() {
        StringBuilder builder = new StringBuilder();
        if (pathSelection != null && !pathSelection.isEmpty()) {
            for (int i = 0; i < pathSelection.size(); i ++) {
                builder.append(IMAGE_PROJECTION[1])
                        .append(LIKE).append(wrapForLike(pathSelection.get(i)));
                if (i != 0 && i != pathSelection.size() - 1) {
                    builder.append(AND);
                }
            }
        }
        if (pathExclude != null && !pathExclude.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(AND);
            }
            for (int i = 0; i < pathExclude.size(); i ++) {
                builder.append(IMAGE_PROJECTION[1])
                        .append(NOT_LIKE).append(wrapForLike(pathExclude.get(i)));
                if (i != 0 && i != pathSelection.size() - 1) {
                    builder.append(AND);
                }
            }
        }
        if (typeSelection != null && !typeSelection.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(AND);
            }
            for (int i = 0; i < typeSelection.size(); i ++) {
                builder.append(IMAGE_PROJECTION[3])
                        .append(LIKE).append(wrapForLike(typeSelection.get(i)));
                if (i != 0 && i != typeSelection.size() - 1) {
                    builder.append(AND);
                }
            }
        }
        if (typeExclude != null && !typeExclude.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(AND);
            }
            for (int i = 0; i < typeExclude.size(); i ++) {
                builder.append(IMAGE_PROJECTION[3])
                        .append(NOT_LIKE).append(wrapForLike(typeExclude.get(i)));
                if (i != 0 && i != typeExclude.size() - 1) {
                    builder.append(AND);
                }
            }
        }
        if (IMAGE_PROJECTION.length == 7) {
            if (mWidthLimit > 0) {
                if (builder.length() > 0) {
                    builder.append(AND);
                }
                builder.append(IMAGE_PROJECTION[5])
                        .append(mWidthOperator).append(mWidthLimit);
            }
            if (mHeightLimit > 0) {
                if (builder.length() > 0) {
                    builder.append(AND);
                }
                builder.append(IMAGE_PROJECTION[6])
                        .append(mHeightOperator).append(mHeightLimit);
            }
        }
        return builder.toString();
    }

    public int getIdentifier() {
        return getSelection() == null || getSelection().isEmpty() ? LOADER_ALL : getSelection().hashCode();
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

    public static class Builder {
        private SelectionConfig mConfig;

        public Builder() {
            mConfig = new SelectionConfig();
        }

        public SelectionConfig create() {
            return mConfig;
        }

        public Builder includePath(String path) {
            if (path == null || path.isEmpty()) {
                return this;
            }
            if (mConfig.pathSelection == null) {
                mConfig.pathSelection = new ArrayList<>();
            }
            mConfig.pathSelection.add(path);
            return this;
        }

        public Builder excludePath(String path) {
            if (path == null || path.isEmpty()) {
                return this;
            }
            if (mConfig.pathExclude == null) {
                mConfig.pathExclude = new ArrayList<>();
            }
            mConfig.pathExclude.add(path);
            return this;
        }

        public Builder excludeType(String type) {
            if (type == null || type.isEmpty()) {
                return this;
            }
            if (mConfig.typeExclude == null) {
                mConfig.typeExclude = new ArrayList<>();
            }
            mConfig.typeExclude.add(type);
            return this;
        }

        public Builder includeType(String type) {
            if (type == null || type.isEmpty()) {
                return this;
            }
            if (mConfig.typeSelection == null) {
                mConfig.typeSelection = new ArrayList<>();
            }
            mConfig.typeSelection.add(type);
            return this;
        }

        public Builder widthGreater(int px) {
            if (px <= 0) {
                return this;
            }
            mConfig.mWidthOperator = GREATER;
            mConfig.mWidthLimit = px;
            return this;
        }

        public Builder widthLess(int px) {
            if (px <= 0) {
                return this;
            }
            mConfig.mWidthOperator = LESS;
            mConfig.mWidthLimit = px;
            return this;
        }

        public Builder widthEqual(int px) {
            if (px <= 0) {
                return this;
            }
            mConfig.mWidthOperator = EQUAL;
            mConfig.mWidthLimit = px;
            return this;
        }

        public Builder widthLessAndEqual(int px) {
            if (px <= 0) {
                return this;
            }
            mConfig.mWidthOperator = LESS_AND_EQUAL;
            mConfig.mWidthLimit = px;
            return this;
        }

        public Builder widthGreaterAndEqual(int px) {
            if (px <= 0) {
                return this;
            }
            mConfig.mWidthOperator = GREATER_AND_EQUAL;
            mConfig.mWidthLimit = px;
            return this;
        }

        public Builder heightGreater(int px) {
            if (px <= 0) {
                return this;
            }
            mConfig.mHeightOperator = GREATER;
            mConfig.mHeightLimit = px;
            return this;
        }

        public Builder heightLess(int px) {
            if (px <= 0) {
                return this;
            }
            mConfig.mHeightOperator = LESS;
            mConfig.mHeightLimit = px;
            return this;
        }

        public Builder heightEqual(int px) {
            if (px <= 0) {
                return this;
            }
            mConfig.mHeightOperator = EQUAL;
            mConfig.mHeightLimit = px;
            return this;
        }

        public Builder heightLessAndEqual(int px) {
            if (px <= 0) {
                return this;
            }
            mConfig.mHeightOperator = LESS_AND_EQUAL;
            mConfig.mHeightLimit = px;
            return this;
        }

        public Builder heightGreaterAndEqual(int px) {
            if (px <= 0) {
                return this;
            }
            mConfig.mHeightOperator = GREATER_AND_EQUAL;
            mConfig.mHeightLimit = px;
            return this;
        }
    }
}
