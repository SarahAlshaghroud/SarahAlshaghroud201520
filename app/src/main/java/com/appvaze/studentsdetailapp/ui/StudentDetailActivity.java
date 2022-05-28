package com.appvaze.studentsdetailapp.ui;

import static com.appvaze.studentsdetailapp.util.Constant.DOB;
import static com.appvaze.studentsdetailapp.util.Constant.FATHER_NAME;
import static com.appvaze.studentsdetailapp.util.Constant.GENDER;
import static com.appvaze.studentsdetailapp.util.Constant.NATIONAL_ID;
import static com.appvaze.studentsdetailapp.util.Constant.STD_ID;
import static com.appvaze.studentsdetailapp.util.Constant.STD_NAME;
import static com.appvaze.studentsdetailapp.util.Constant.SURNAME;
import static com.appvaze.studentsdetailapp.util.Constant.TABLE_USER;
import static com.appvaze.studentsdetailapp.util.Constant.database;
import static com.appvaze.studentsdetailapp.util.Constant.helper;
import static com.appvaze.studentsdetailapp.util.Constant.makeToast;
import static com.appvaze.studentsdetailapp.util.Constant.networkInfo;
import static com.appvaze.studentsdetailapp.util.Constant.setLog;
import static com.appvaze.studentsdetailapp.util.Constant.stdReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.appvaze.studentsdetailapp.R;
import com.appvaze.studentsdetailapp.adapters.StudentAdapter;
import com.appvaze.studentsdetailapp.databinding.ActivityStudentDetailBinding;
import com.appvaze.studentsdetailapp.models.Student;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentDetailActivity extends AppCompatActivity {

    ActivityStudentDetailBinding binding;

    StudentAdapter adapter;
    List<Student> list;

    String isLocal = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStudentDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.stdListToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String activityTpe = getIntent().getStringExtra("activityType");

        binding.firebaseRecyclerview.setHasFixedSize(true);
        binding.firebaseRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        if(activityTpe.equals("0")) {
            binding.linearLayout3.setVisibility(View.GONE);
            if (networkInfo != null) {
                fetchStdInfoFromFirebase();
                setLog("firebase DB called");
            }
        } else if(activityTpe.equals("1")) {
            fetchStdFromLocalDb();
            isLocal = "true";
            setLog("Local DB called");
        }

        binding.fabAddStd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddStudentActivity.class);
                intent.putExtra("isLocal", isLocal);
                startActivity(intent);
            }
        });

        binding.btnLocalSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stdId = binding.etLocalStdId.getText().toString().trim();

                if(stdId.isEmpty()) {
                    binding.etLocalStdId.setError("Enter Student ID");
                    binding.etLocalStdId.requestFocus();
                    binding.etLocalStdId.performClick();
                } else {
                    binding.progressBar2.setVisibility(View.VISIBLE);
                    fetchSingleStdInfoFromFirebase(stdId);
                }
            }
        });
    }

    private void fetchSingleStdInfoFromFirebase(String stdId) {

        stdReference.child(stdId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                if (snapshot1.exists()) {
                    String stdId = snapshot1.child(STD_ID).getValue().toString();
                    String stdName = snapshot1.child(STD_NAME).getValue().toString();
                    String surName = snapshot1.child(SURNAME).getValue().toString();
                    String fatherName = snapshot1.child(FATHER_NAME).getValue().toString();
                    String nationalId = snapshot1.child(NATIONAL_ID).getValue().toString();
                    String dob = snapshot1.child(DOB).getValue().toString();
                    String gender = snapshot1.child(GENDER).getValue().toString();

                    helper.deleteSpecificStdInfo(stdId);
                    Boolean res = helper.storeStdInfo(stdId,stdName,surName,fatherName,nationalId,dob,gender);
                    if(res) {
                        makeToast("Searched Information successfully stored");
                        binding.progressBar2.setVisibility(View.GONE);
                        fetchStdFromLocalDb();
                    }
                } else {
                    makeToast("No record found");
                    binding.progressBar2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setLog(error.toString());
            }
        });
    }

    private void fetchStdInfoFromFirebase() {

        stdReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String stdId = snapshot1.child(STD_ID).getValue().toString();
                    String stdName = snapshot1.child(STD_NAME).getValue().toString();
                    String surName = snapshot1.child(SURNAME).getValue().toString();
                    String fatherName = snapshot1.child(FATHER_NAME).getValue().toString();
                    String nationalId = snapshot1.child(NATIONAL_ID).getValue().toString();
                    String dob = snapshot1.child(DOB).getValue().toString();
                    String gender = snapshot1.child(GENDER).getValue().toString();

                    list.add(new Student(stdId,stdName,surName,fatherName,nationalId,dob,gender, false));
                }

                setListOnAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setLog( error.toString());
            }
        });
    }

    private void fetchStdFromLocalDb() {
        list.clear();
        Cursor cursor = database.rawQuery("select * from " + TABLE_USER, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String stdId = cursor.getString(0);
                String stdName = cursor.getString(1);
                String surName = cursor.getString(2);
                String fatherName = cursor.getString(3);
                String nationalId = cursor.getString(4);
                String dob = cursor.getString(5);
                String gender = cursor.getString(6);

                list.add(new Student(stdId, stdName, surName, fatherName, nationalId, dob, gender, true));
            }
        }

        setListOnAdapter();
    }

    private void setListOnAdapter() {
        adapter = new StudentAdapter(list, StudentDetailActivity.this);
        if(adapter.getItemCount() > 0) {
            binding.firebaseRecyclerview.setAdapter(adapter);
            binding.emptyList.setVisibility(View.GONE);
            binding.progressBar2.setVisibility(View.GONE);
        } else {
            binding.firebaseRecyclerview.setVisibility(View.GONE);
            binding.emptyList.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item=menu.findItem(R.id.searchMenu);
        SearchView searchView= (SearchView) item.getActionView();
        searchView.setQueryHint("Search Student Info");

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

}