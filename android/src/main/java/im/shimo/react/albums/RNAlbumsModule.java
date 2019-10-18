package im.shimo.react.albums;

import android.database.Cursor;
import android.provider.MediaStore;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RNAlbumsModule extends ReactContextBaseJavaModule {

    public RNAlbumsModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNAlbumsModule";
    }


    @ReactMethod
    public void getImageList(ReadableMap options, Promise promise) {

    String[] PROJECTION_BUCKET = {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATA,
                "count(" +  MediaStore.Images.ImageColumns.BUCKET_ID + ") as count"
        };

        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(" + MediaStore.Images.ImageColumns.DATE_TAKEN + ") DESC";


        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE 
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + " AND ";
    
        Cursor cursor = getReactApplicationContext().getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                PROJECTION_BUCKET,
                selection + BUCKET_GROUP_BY,
                null,
                BUCKET_ORDER_BY
        );

        WritableArray list = Arguments.createArray();
        if (cursor != null && cursor.moveToFirst()) {
            
            String bucket;
            String date;
            String data;
            String count;
            int bucketColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dateColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);
            int dataColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATA);
            int countColumn = cursor.getColumnIndex("count");
            do {
                // Get the field values
                bucket = cursor.getString(bucketColumn);
                date = cursor.getString(dateColumn);
                data = cursor.getString(dataColumn);
                count = cursor.getString(countColumn);


                ArrayList<String> projection = new ArrayList<>();
                ArrayList<ReadableMap> columns = new ArrayList<>();
                // ArrayList<ReadableMap> columnsImage = new ArrayList<>();

                setColumn("uri", MediaStore.Images.Media.DATA, projection, columns);

                // if (shouldSetField(options, "dimensions")) {
                setColumn("width", MediaStore.Images.Media.WIDTH, projection, columns);
                setColumn("height", MediaStore.Images.Media.HEIGHT, projection, columns);
                // }
                setColumn("playableDuration", MediaStore.Video.VideoColumns.DURATION, projection, columns);
                // setColumn("image", projectionImage, projection, columns);
                setColumn("name", MediaStore.Images.Media.DISPLAY_NAME, projection, columns);
                setColumn("size", MediaStore.Images.Media.SIZE, projection, columns);
                setColumn("type", MediaStore.Images.Media.MIME_TYPE, projection, columns);
                setColumn("timestamp", MediaStore.Images.Media.DATE_ADDED, projection, columns);
                setColumn("album", MediaStore.Images.Media.BUCKET_DISPLAY_NAME, projection, columns);


                setColumn("uri", MediaStore.Video.Media.DATA, projection, columns);

                // if (shouldSetField(options, "dimensions")) {
                setColumn("width", MediaStore.Video.Media.WIDTH, projection, columns);
                setColumn("height", MediaStore.Video.Media.HEIGHT, projection, columns);
                // }
                setColumn("playableDuration", MediaStore.Video.VideoColumns.DURATION, projection, columns);
                // setColumn("image", projectionImage, projection, columns);
                setColumn("name", MediaStore.Video.Media.DISPLAY_NAME, projection, columns);
                setColumn("size", MediaStore.Video.Media.SIZE, projection, columns);
                setColumn("type", MediaStore.Video.Media.MIME_TYPE, projection, columns);
                setColumn("timestamp", MediaStore.Video.Media.DATE_ADDED, projection, columns);

                String selectionImage = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE 
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

                Cursor cursorImage = getReactApplicationContext().getContentResolver().query(
                        MediaStore.Files.getContentUri("external"),
                        projection.toArray(new String[projection.size()]),
                        selectionImage,
                        null,
                        null
                );


                Map<String, Integer> columnIndexMap = new HashMap<>();
                WritableArray listImage = Arguments.createArray();

                if (cursorImage != null && cursorImage.getCount() > 0 && cursorImage.moveToFirst()) {
                    for (int i = 0; i < projection.size(); i++) {
                        String field = projection.get(i);
                        columnIndexMap.put(field, cursorImage.getColumnIndex(field));
                    }

                    do {
                        Iterator<ReadableMap> columnIterator = columns.iterator();

                        WritableMap imageWapper = Arguments.createMap();
                        WritableMap node = Arguments.createMap();
                        WritableMap image = Arguments.createMap();

                        while (columnIterator.hasNext()) {
                            ReadableMap column = columnIterator.next();
                            setWritableMap(imageWapper, column.getString("name"), cursorImage.getString(columnIndexMap.get(column.getString("columnName"))));
                            setWritableMap(image, column.getString("name"), cursorImage.getString(columnIndexMap.get(column.getString("columnName"))));

                        }
                        if (image.getString("album").equals(bucket)) {
                            image.putString("type", image.getString("type").split("/")[0]);
                            imageWapper.putString("type", image.getString("type").split("/")[0]);
                            image.putString("uri", "file://" + image.getString("uri"));
                            imageWapper.putString("uri", "file://" + image.getString("uri"));
                            imageWapper.putMap("image", image);
                            node.putMap("node", imageWapper);

                            listImage.pushMap(node);
                        }
                    } while (cursorImage.moveToNext());
                    cursorImage.close();
                }


                WritableMap album = Arguments.createMap();
                album.putArray("list", listImage);
                setWritableMap(album, "name", bucket);

                list.pushMap(album);
            } while (cursor.moveToNext());

            cursor.close();
        }

        promise.resolve(list);

    }

    @ReactMethod
    public void getAlbumList(ReadableMap options, Promise promise) {
        // which image properties are we querying
        String[] PROJECTION_BUCKET = {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATA,
                "count(" +  MediaStore.Images.ImageColumns.BUCKET_ID + ") as count"
        };

        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(" + MediaStore.Images.ImageColumns.DATE_TAKEN + ") DESC";


        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE 
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + " AND ";
    
        Cursor cursor = getReactApplicationContext().getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                PROJECTION_BUCKET,
                selection + BUCKET_GROUP_BY,
                null,
                BUCKET_ORDER_BY
        );

        WritableArray list = Arguments.createArray();
        if (cursor != null && cursor.moveToFirst()) {
            String bucket;
            String date;
            String data;
            String count;
            int bucketColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dateColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);
            int dataColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATA);
            int countColumn = cursor.getColumnIndex("count");
            do {
                // Get the field values
                bucket = cursor.getString(bucketColumn);
                date = cursor.getString(dateColumn);
                data = cursor.getString(dataColumn);
                count = cursor.getString(countColumn);


                WritableMap image = Arguments.createMap();
                setWritableMap(image, "count", count);
                setWritableMap(image, "date", date);
                setWritableMap(image, "cover", "file://" + data);
                setWritableMap(image, "name", bucket);

                list.pushMap(image);
            } while (cursor.moveToNext());

            cursor.close();
        }

        promise.resolve(list);
    }

    private boolean shouldSetField(ReadableMap options, String name) {
        return options.hasKey(name) && options.getBoolean(name);
    }

    private void setWritableMap(WritableMap map, String key, String value) {
        if (value == null) {
            map.putNull(key);
        } else {
            map.putString(key, value);
        }
    }

    private void setColumn(String name, String columnName, ArrayList<String> projection, ArrayList<ReadableMap> columns) {
        projection.add(columnName);
        WritableMap column = Arguments.createMap();
        column.putString("name", name);
        column.putString("columnName", columnName);
        columns.add(column);
    }
}
