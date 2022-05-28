package com.appvaze.studentsdetailapp.sqlite;

import static com.appvaze.studentsdetailapp.util.Constant.DB_NAME;
import static com.appvaze.studentsdetailapp.util.Constant.DOB;
import static com.appvaze.studentsdetailapp.util.Constant.FATHER_NAME;
import static com.appvaze.studentsdetailapp.util.Constant.GENDER;
import static com.appvaze.studentsdetailapp.util.Constant.NATIONAL_ID;
import static com.appvaze.studentsdetailapp.util.Constant.STD_ID;
import static com.appvaze.studentsdetailapp.util.Constant.STD_NAME;
import static com.appvaze.studentsdetailapp.util.Constant.SURNAME;
import static com.appvaze.studentsdetailapp.util.Constant.TABLE_USER;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class OpenHelper extends SQLiteOpenHelper {

    String CREATE_USERS_TABLE =" CREATE TABLE "+TABLE_USER+"(stdId TEXT PRIMARY KEY, stdName TEXT, " +
            "surName TEXT, fatherName TEXT, nationalId TEXT, dob TEXT, gender TEXT)";

    public OpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
        onCreate(db);
    }

    public boolean storeStdInfo(String stdId, String stdName, String surName, String fatherName,
                                String nationalId, String dob, String gender){
        SQLiteDatabase db=this.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put(STD_ID,stdId);
        values.put(STD_NAME,stdName);
        values.put(SURNAME,surName);
        values.put(FATHER_NAME,fatherName);
        values.put(NATIONAL_ID,nationalId);
        values.put(DOB,dob);
        values.put(GENDER,gender);

        long res=db.insert(TABLE_USER,null,values);
        if(res==-1)
            return false;
        else
            return true;
    }

    public Integer deleteStdInfo(){
        SQLiteDatabase database=this.getWritableDatabase();
        return database.delete(TABLE_USER, null,null);
    }

    public Integer deleteSpecificStdInfo(String stdId){
        SQLiteDatabase database=this.getWritableDatabase();
        return database.delete(TABLE_USER, STD_ID+"=?",new String[]{stdId});
    }


}
