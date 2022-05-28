package com.appvaze.studentsdetailapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.appvaze.studentsdetailapp.MyApplication;
import com.appvaze.studentsdetailapp.sqlite.OpenHelper;
import com.appvaze.studentsdetailapp.ui.weather.OpenWeatherActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constant {

    //SQLite+Firebase Table
    public static final String DB_NAME="db_std";
    public static final String TABLE_USER = "tbl_std";

    public static final String STD_ID = "stdId";
    public static final String STD_NAME = "stdName";
    public static final String SURNAME = "surName";
    public static final String FATHER_NAME = "fatherName";
    public static final String NATIONAL_ID = "nationalId";
    public static final String DOB = "dob";
    public static final String GENDER = "gender";

    public static final OpenHelper helper = new OpenHelper(MyApplication.getAppContext());
    public static final SQLiteDatabase database = helper.getReadableDatabase();

    public static final DatabaseReference stdReference = FirebaseDatabase.getInstance("https://sarahalshaghroudse328-default-rtdb.firebaseio.com/").getReference(TABLE_USER);

    //API URL+KEY
    public static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    public static final String API_KEY = "&appid=1f1d6588819771e3d082ba37cbf28916";

    //connectivity
    public static ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    public static final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

    public static final void makeToast(String value){
        Toast.makeText(MyApplication.getAppContext(), value, Toast.LENGTH_SHORT).show();
    }

    public static void setLog(String value){
        Log.d("Sarah", value);
    }
}