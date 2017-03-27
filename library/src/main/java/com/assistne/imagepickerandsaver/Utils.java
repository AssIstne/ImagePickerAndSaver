package com.assistne.imagepickerandsaver;

import android.database.Cursor;

import static com.assistne.imagepickerandsaver.SelectionConfig.IMAGE_PROJECTION;

/**
 * Created by assistne on 17/3/27.
 */

class Utils {
    static ImageItem createImageItemFromCursor(Cursor cursor) {
        ImageItem imageItem = new ImageItem();
        imageItem.name = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
        imageItem.path = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
        imageItem.size = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
        imageItem.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
        imageItem.addTime = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
        if (IMAGE_PROJECTION.length == 7) {
            imageItem.width = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
            imageItem.height = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
        }
        return imageItem;
    }
}
