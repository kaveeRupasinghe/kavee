package com.example.mynewhope;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="StartMart.db";
    private static final String TABLE_NAME="RECORD";
    private static final String COL_1="ID";
    private static final String COL_2="name";
    private static final String COL_3="phone";
    private static final String COL_4="email";
    private static final String COL_5="category";
    private static final String COL_6="image";
    //constructor
    SQLiteHelper(Context context,
                 String name,
                 SQLiteDatabase.CursorFactory factory,
                 int version){
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone INTEGER, email TEXT, category TEXT,image BYTE)");

    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //insertData
    public void insertData(String name, String phone, String address, String email, String category, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        //query to insert record in database table
        String sql = "INSERT INTO RECORD VALUES(NULL,?,?,?,?,?,?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, phone);
        statement.bindString(3, address);
        statement.bindString(4, email);
        statement.bindString(5, category);
        statement.bindBlob(6, image);

        statement.executeInsert();

    }

    //updateData
    public void updateData(String name, String phone, String address, String email, String category, byte[] image, int id){
        SQLiteDatabase database= getWritableDatabase();
        //query to update record
        String sql = "UPDATE RECORD SET name=?, phone=?, address=?, email=?, category=?, image=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, name);
        statement.bindString(2, phone);
        statement.bindString(3, address);
        statement.bindString(4, email);
        statement.bindString(5, category);
        statement.bindBlob(6, image);
        statement.bindDouble(7,(double)id);

        statement.execute();
        database.close();
    }

    //deleteData
    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();
        //query to delete record using id
        String sql = "DELETE FROM RECORD WHERE id=?";

        SQLiteStatement statement= database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1,(double)id);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
