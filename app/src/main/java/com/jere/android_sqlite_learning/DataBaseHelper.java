package com.jere.android_sqlite_learning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jere.android_sqlite_learning.model.BusinessCard;

import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * @author jere
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "business_card_db";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(BusinessCard.CREATE_TABLE);
        sqLiteDatabase.execSQL(BusinessCard.CREATE_COMPNAY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        /**在很多Demo中为了简单，直接删除重建表
        //drop old table if is existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BusinessCard.TABLE_NAME);
        //create table again
        onCreate(sqLiteDatabase); **/

        //但在实际开发中需要严谨的对待数据库migration，一旦造成用户数据丢失，是很差的用户体验。
        if (oldVersion < 2) {
            upgradeVersion2(sqLiteDatabase);
        }
    }

    private void upgradeVersion2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + BusinessCard.TABLE_NAME + " ADD COLUMN " + BusinessCard.COLUMN_COMMENT + " TEXT");
    }

    //需要测试一下回滚DB, 数据库降级，数据库版本从3降到2
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion > 2) { //DATABASE_VERSION == 3
            db.execSQL("DROP TABLE IF EXISTS company");
        }
//        super.onDowngrade(db, oldVersion, newVersion);
    }

    /**
     *
     * @param businessCard
     * @return 返回新插入的行的ID，发生错误，插入不成功，则返回-1
     */
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

    /**
     *
     * @param searchName query database by name
     * @return BusinessCard
     */
    public BusinessCard getBusinessCardQueryByName(String searchName) {
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
                BusinessCard.COLUMN_NAME + "=? ",
                new String[]{searchName},
                null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_NAME));
            int portrait = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_AVATAR));
            String telephone = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_TELEPHONE));
            String address = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_ADDRESS));
            int gender = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_GENDER));
            businessCard.setId(id);
            businessCard.setName(name);
            businessCard.setAvatar(portrait);
            businessCard.setTelephone(telephone);
            businessCard.setAddress(address);
            businessCard.setGender(gender == 1);

            cursor.close();
            return businessCard;
        }
        return null;
    }

    /**
     *
     * @return 读取数据库，返回一个 BusinessCard 类型的 ArrayList
     */
    public ArrayList<BusinessCard> getAllBusinessCards() {
        ArrayList<BusinessCard> businessCardsList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + BusinessCard.TABLE_NAME
                + " ORDER BY " + BusinessCard.COLUMN_ID + " ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                BusinessCard businessCard = new BusinessCard();
                int id = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_NAME));
                int portrait = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_AVATAR));
                String telephone = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_TELEPHONE));
                String address = cursor.getString(cursor.getColumnIndex(BusinessCard.COLUMN_ADDRESS));
                int gender = cursor.getInt(cursor.getColumnIndex(BusinessCard.COLUMN_GENDER));
                businessCard.setId(id);
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

    /**
     *
     * @return 返回数据库行数
     */
    public int getBusinessCardCount() {
        String countQuery = "SELECT * FROM " + BusinessCard.TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     *
     * @param id update row id （需要更新的ID）
     * @param businessCard update value （去更新数据库的内容）
     * @return the number of rows affected (影响到的行数，如果没更新成功，返回0。所以当return 0时，需要告诉用户更新不成功)
     */
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

    /**
     *
     * @param id the database table row id need to delete(需要删除的数据库表中行的ID)
     * @return 返回影响到的行数，如果在 whereClause 有传入条件，返回该条件下影响到的行数，否则返回0。
     * 想要删除所有行，只要在 whereClause 传入 String "1"，并返回删除掉的行数总数（比如：删除了四行就返回4）
     */
    public int deleteBusinessCard(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int idReturnByDelete = db.delete(BusinessCard.TABLE_NAME, BusinessCard.COLUMN_ID + "=? ", new String[]{String.valueOf(id)});
        db.close();
        return idReturnByDelete;
    }

    /**
     * 删除所有行，whereClause 传入 String "1"
     * @return 返回删除掉的行数总数（比如：删除了四行就返回4）
     */
    public int deleteAllBusinessCard() {
        SQLiteDatabase db = getWritableDatabase();
        int idReturnByDelete = db.delete(BusinessCard.TABLE_NAME, String.valueOf(1), null);
        db.close();
        return idReturnByDelete;
    }
}
