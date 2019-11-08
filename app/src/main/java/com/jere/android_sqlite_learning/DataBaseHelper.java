package com.jere.android_sqlite_learning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * @author jere
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "business_card_db";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(BusinessCard.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //drop old table if is existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BusinessCard.TABLE_NAME);
        //create table again
        onCreate(sqLiteDatabase);
    }

    public long insertBusinessCard(BusinessCard businessCard) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BusinessCard.COLUMN_AVATAR, businessCard.getGender() ? R.drawable.male_avatar_icon : R.drawable.female_avatar_icon);
        values.put(BusinessCard.COLUMN_NAME, businessCard.getName());
        values.put(BusinessCard.COLUMN_GENDER, businessCard.getGender() ? 1 : 0);
        values.put(BusinessCard.COLUMN_TELEPHONE, businessCard.getTelephone());
        values.put(BusinessCard.COLUMN_ADDRESS, businessCard.getAddress());
        long id = db.insert(BusinessCard.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public BusinessCard getBusinessCard(long id) {
        BusinessCard businessCard = new BusinessCard();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columnArray = new String[]{
                BusinessCard.COLUMN_ID,
                BusinessCard.COLUMN_NAME,
                BusinessCard.COLUMN_GENDER,
                BusinessCard.COLUMN_AVATAR,
                BusinessCard.COLUMN_TELEPHONE,
                BusinessCard.COLUMN_ADDRESS};
        Cursor cursor = db.query(BusinessCard.TABLE_NAME,
                columnArray,
                BusinessCard.COLUMN_ID + "=? ",
                new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToNext()) {

            String name = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_NAME));
            int portrait = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_AVATAR));
            String telephone = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_TELEPHONE));
            String address = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_ADDRESS));
            int gender = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_GENDER));
            businessCard.setName(name);
            businessCard.setAvatar(portrait);
            businessCard.setTelephone(telephone);
            businessCard.setAddress(address);
            businessCard.setGender(gender == 1);

            cursor.close();
        }
        return businessCard;
    }

    public ArrayList<BusinessCard> getAllBusinessCards() {
        ArrayList<BusinessCard> businessCardsList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + BusinessCard.TABLE_NAME
                + " ORDER BY " + BusinessCard.COLUMN_ID + " ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                BusinessCard businessCard = new BusinessCard();
                String name = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_NAME));
                int portrait = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_AVATAR));
                String telephone = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_TELEPHONE));
                String address = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_ADDRESS));
                int gender = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_GENDER));
                businessCard.setName(name);
                businessCard.setAvatar(portrait);
                businessCard.setTelephone(telephone);
                businessCard.setAddress(address);
                businessCard.setGender(gender == 1);

                businessCardsList.add(businessCard);
            }
        }

        db.close();
        return businessCardsList;
    }

    public int getBusinessCardCount() {
        String countQuery = "SELECT * FROM " + BusinessCard.TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //todo update TABLE all filed, or only update TAble one filed
    public int updateBusinessCard(int id, BusinessCard businessCard) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BusinessCard.COLUMN_AVATAR, businessCard.getAvatar());
        values.put(BusinessCard.COLUMN_NAME, businessCard.getName());
        values.put(BusinessCard.COLUMN_TELEPHONE, businessCard.getTelephone());
        values.put(BusinessCard.COLUMN_ADDRESS, businessCard.getAddress());
        values.put(BusinessCard.COLUMN_GENDER, businessCard.getGender() ? 1 : 0);
        int idReturnByUpdate = db.update(BusinessCard.TABLE_NAME, values, BusinessCard.COLUMN_ID + " =? ", new String[]{String.valueOf(id)});
        db.close();
        return idReturnByUpdate;
    }

    public void deleteBusinessCard(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(BusinessCard.TABLE_NAME, BusinessCard.COLUMN_ID + "=? ", new String[]{String.valueOf(id)});
        db.close();
    }

    public int getBusinessCardId(BusinessCard businessCard) {
        int id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(BusinessCard.TABLE_NAME,
                null,
                BusinessCard.COLUMN_NAME + "=? ",
                new String[]{String.valueOf(businessCard.getName())},
                null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_ID));
            cursor.close();
        }
        return id;
    }
}
