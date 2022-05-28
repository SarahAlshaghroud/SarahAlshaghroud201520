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
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.appvaze.studentsdetailapp.MyApplication;
import com.appvaze.studentsdetailapp.R;
import com.appvaze.studentsdetailapp.databinding.ActivityAddStudentBinding;
import com.appvaze.studentsdetailapp.sqlite.OpenHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.appvaze.studentsdetailapp.models.Student;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class AddStudentActivity extends AppCompatActivity {

    ActivityAddStudentBinding binding;

    String stdId;
    Boolean isLocal = false, isUpdate = false;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddStudentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.addStdToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        //receive intent from adapter
        if(getIntent().getStringExtra("isLocal").equals("true"))
            isLocal = true;

        if(networkInfo==null)
            binding.btnAddStd.setEnabled(false);

        stdId = getIntent().getStringExtra("stdId");
        if(stdId!=null) {
            binding.addStdToolbar.setTitle("Update Student");
            binding.btnAddStd.setText("Update Now");
            isUpdate = true;

            binding.etStdID.setText(stdId);
            binding.etStdName.setText(getIntent().getStringExtra(STD_NAME));
            binding.etSurName.setText(getIntent().getStringExtra(SURNAME));
            binding.etFatherName.setText(getIntent().getStringExtra(FATHER_NAME));
            binding.etNationalID.setText(getIntent().getStringExtra(NATIONAL_ID));
            binding.etDob.setText(getIntent().getStringExtra(DOB));

            binding.etStdID.setEnabled(false);

            if(getIntent().getStringExtra(GENDER).equals("Male"))
                binding.rbMale.setChecked(true);
            else
                binding.rbFemale.setChecked(true);
        }

        binding.rbMale.setChecked(true);

        binding.etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDob();
            }
        });

        binding.btnAddStd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStdInfo();
            }
        });
    }

    private void chooseDob() {

        Calendar calendar=Calendar.getInstance();
        final int yy= calendar.get(Calendar.YEAR);
        final int mm= calendar.get(Calendar.MONTH);
        final int dd= calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog=new DatePickerDialog(
                AddStudentActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1=i1+1;
                binding.etDob.setText(i2+"-"+i1+"-"+i);
            }
        },yy,mm,dd);
        datePickerDialog.show();
    }

    private void addStdInfo() {

        String id = binding.etStdID.getText().toString().trim();
        String name = binding.etStdName.getText().toString().trim();
        String surname = binding.etSurName.getText().toString().trim();
        String father = binding.etFatherName.getText().toString().trim();
        String nationId = binding.etNationalID.getText().toString().trim();
        String dob = binding.etDob.getText().toString().trim();

        int genderValue=binding.radioGroup.getCheckedRadioButtonId();
        RadioButton genderBtn=findViewById(genderValue);

        if(id.isEmpty()){
            binding.etStdID.setError("Enter ID First");
            binding.etStdID.requestFocus();
            binding.etStdID.performClick();
        } else if(name.isEmpty()){
            binding.etStdName.setError("Enter Name First");
            binding.etStdName.requestFocus();
            binding.etStdName.performClick();
        } else if(surname.isEmpty()){
            binding.etSurName.setError("Enter Surname First");
            binding.etSurName.requestFocus();
            binding.etSurName.performClick();
        } else if(father.isEmpty()){
            binding.etFatherName.setError("Enter Father Name First");
            binding.etFatherName.requestFocus();
            binding.etFatherName.performClick();
        } else if(nationId.isEmpty()){
            binding.etNationalID.setError("Enter National Id First");
            binding.etNationalID.requestFocus();
            binding.etNationalID.performClick();
        } else if(dob.isEmpty()){
            binding.etDob.setError("Choose Date of Birth First");
            binding.etDob.requestFocus();
            binding.etDob.performClick();
        } else {

            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();

            setLog(isLocal+"");

            if (isUpdate)
                updateStdInfoToFirebase(name,surname,father,nationId,dob,genderBtn.getText().toString());
            else
                addStdInfoToFirebase(id,name,surname,father,nationId,dob,genderBtn.getText().toString());
        }
    }

    private void updateStdInfoToFirebase(String name, String surname, String father, String nationId, String dob, String gender) {

        if(isLocal){
            helper.deleteSpecificStdInfo(stdId);
            boolean res = helper.storeStdInfo(stdId,name,surname,father,nationId,dob,gender);
            if(res){
                makeToast("Student Info Successfully updated");
                setLog("Local Update");
                Intent intent = new Intent(getApplicationContext(), StudentDetailActivity.class);
                intent.putExtra("activityType", "1");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } else {

            HashMap<String, Object> updateMap = new HashMap<>();
            updateMap.put(STD_NAME, name);
            updateMap.put(SURNAME, surname);
            updateMap.put(FATHER_NAME, father);
            updateMap.put(NATIONAL_ID, nationId);
            updateMap.put(DOB, dob);
            updateMap.put(GENDER, gender);

            stdReference.child(stdId)
                    .updateChildren(updateMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            makeToast("Student Information Successfully Updated");
                            Intent intent = new Intent(getApplicationContext(), StudentDetailActivity.class);
                            intent.putExtra("activityType", "0");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            setLog("Firebase Update");
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            makeToast(e.toString());
                        }
                    });
        }
    }

    private void addStdInfoToFirebase(String id,String name, String surname, String father, String nationId, String dob, String gender) {

        if(isLocal){
            boolean res = helper.storeStdInfo(id,name,surname,father,nationId,dob,gender);
            //boolean res = helper.storeStdInfo("1","2","3","5","6","7","8");
            if(res){
                makeToast("Student Info Successfully Added");
                setLog("Local added");
                Intent intent = new Intent(getApplicationContext(), StudentDetailActivity.class);
                intent.putExtra("activityType", "1");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                progressDialog.dismiss();
                finish();
            } else {
                makeToast("failed");
                progressDialog.dismiss();
            }
        } else {

            Student student = new Student(id, name, surname, father, nationId, dob, gender, false);
            stdReference.child(id)
                    .setValue(student)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Student Information Successfully Stored", Toast.LENGTH_LONG).show();
                            makeToast("Student Information Successfully Stored");
                            Intent intent = new Intent(getApplicationContext(), StudentDetailActivity.class);
                            intent.putExtra("activityType", "0");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            setLog("firebase added");
                            progressDialog.dismiss();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            makeToast(e.toString());
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.del_menu, menu);
        if(isUpdate==false)
            menu.findItem(R.id.toolbarDel).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.toolbarDel:
                deleteStdInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteStdInfo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete it?");
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if(isLocal){

                    helper.deleteSpecificStdInfo(stdId);
                    makeToast("Successfully Deleted");
                    Intent intent = new Intent(getApplicationContext(), StudentDetailActivity.class);
                    intent.putExtra("activityType", "1");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    setLog("Local delete");
                    finish();

                } else {

                    stdReference.child(stdId)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    makeToast("Successfully Deleted");
                                    Intent intent = new Intent(getApplicationContext(), StudentDetailActivity.class);
                                    intent.putExtra("activityType", "0");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    setLog("Firebase delete");
                                    dialog.cancel();
                                    finish();
                                }
                            });
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), StudentDetailActivity.class);

        if(isLocal)
            intent.putExtra("activityType", "1");
        else
            intent.putExtra("activityType", "0");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return false;
    }
}