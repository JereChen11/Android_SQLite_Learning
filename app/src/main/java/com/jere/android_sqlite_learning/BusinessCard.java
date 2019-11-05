package com.jere.android_sqlite_learning;

/**
 * @author jere
 */
public class BusinessCard {
    public static final String TABLE_NAME = "BusinessCard";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PORTRAIT = "portrait";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TELEPHONE = "telephone";
    public static final String COLUMN_ADDRESS = "address";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_PORTRAIT + " INTEGER,"
            + COLUMN_TELEPHONE + " TEXT,"
            + COLUMN_ADDRESS + " TEXT"
            + ")";

    private int portrait;
    private String name;
    private String telephone;
    private String address;

    public int getPortrait() {
        return portrait;
    }

    public void setPortrait(int portrait) {
        this.portrait = portrait;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephont(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
