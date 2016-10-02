package testsample.altvr.com.testsample.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import testsample.altvr.com.testsample.Constants;
import testsample.altvr.com.testsample.vo.PhotoVo;

/**
 * Created by tejus on 4/14/2016.
 */
public class DatabaseUtil extends SQLiteOpenHelper {
    private LogUtil log = new LogUtil(DatabaseUtil.class);

    private static final int DATABASE_VERSION = 2;
    //DB and tables
    private static final String DATABASE_NAME = "imagesearcher";
    private static final String TABLE_PHOTOS = "photos";

    //Columns for Images table
    private static final String KEY_PHOTO_ID = "id";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_SAVED = "saved";
    private static final String KEY_FAVORITE = "favorite";

    private static String[] allColumns = {KEY_PHOTO_ID, KEY_PHOTO, KEY_SAVED, KEY_FAVORITE};

    SQLiteDatabase mDb;

    private Gson mGson;

    public DatabaseUtil(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDb = getWritableDatabase();
        mGson = new Gson();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_PHOTOS + " ("
                + KEY_PHOTO_ID + " STRING PRIMARY KEY, "
                + KEY_PHOTO + " TEXT, "
                + KEY_SAVED + " INTEGER DEFAULT 1, "
                + KEY_FAVORITE + " INTEGER DEFAULT 0"
        + ")";
        db.execSQL(CREATE_IMAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

    /**
     * YOUR CODE HERE
     *
     * For part 1b, you should fill in the various CRUD operations below to manipulate the db
     * returned by getWritableDatabase() to store/load photos.
     */

    public boolean insert(PhotoVo photoVo, Constants favoriteType) {
            ContentValues values = new ContentValues();
            values.put(KEY_PHOTO_ID, photoVo.id);
            values.put(KEY_PHOTO, mGson.toJson(photoVo));
            values.put(KEY_SAVED, "1");
            if (favoriteType != null && favoriteType == Constants.ADD_FAVORITE)
                values.put(KEY_FAVORITE, "1");
            return mDb.insert(TABLE_PHOTOS, null, values) != -1;
    }

    public boolean addOrRemoveFavorite(PhotoVo photoVo, Constants favoriteType) {

        if (!exists(photoVo.id)) {
            if (favoriteType == Constants.ADD_FAVORITE) return insert(photoVo, Constants.ADD_FAVORITE);
            else if (favoriteType == Constants.REMOVE_FAVORITE) {
            /*This condition should never occur
            Because whenever photo is saved it's removed from DB
            The pop up menu should be updated accordingly
             */
                return false;
            }
        }
        ContentValues values = new ContentValues();
        values.put(KEY_FAVORITE, favoriteType == Constants.ADD_FAVORITE ? "1" : "0");
        int updatedRows = mDb.update(TABLE_PHOTOS, values, KEY_PHOTO_ID+"=?", new String[]{photoVo.id});

        return (updatedRows > 0) ? true : false;

    }

    public boolean checkFavorite(String id) {
        if (!exists(id)) return false;
        Cursor cursor = mDb.query(TABLE_PHOTOS, new String[] {KEY_PHOTO}, KEY_PHOTO_ID+"=? AND " + KEY_FAVORITE+"=?", new String[]{id, "1"}, null, null, null);
        return cursor != null && cursor.getCount() > 0;
    }

    public void delete(String id) {
        mDb.delete(TABLE_PHOTOS, KEY_PHOTO_ID+"=?", new String[]{id});
    }

    public boolean exists(String id) {
        Cursor cursor = mDb.query(TABLE_PHOTOS, new String[] {KEY_PHOTO}, KEY_PHOTO_ID+"=?", new String[]{id}, null, null, null);
        return cursor != null && cursor.getCount() > 0;
    }

    public List<PhotoVo> getAllPhotos(Constants eventType) {
        List<PhotoVo> list = new ArrayList<>();
        //Instead of all columns string array, if we pass null we can get all columns
        // but just for readability created a string array
        Cursor cursor = mDb.query(TABLE_PHOTOS, allColumns, (eventType == Constants.ALL) ? null : (eventType == Constants.SAVE) ? KEY_SAVED : KEY_FAVORITE+"= 1", null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                list.add(mGson.fromJson(cursor.getString(cursor.getColumnIndex(KEY_PHOTO)), PhotoVo.class));
                cursor.moveToNext();
            }
        }

        cursor.close();
        return list;
    }
}
