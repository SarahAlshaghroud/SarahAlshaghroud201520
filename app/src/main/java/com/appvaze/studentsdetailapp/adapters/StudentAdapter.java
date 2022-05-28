package com.appvaze.studentsdetailapp.adapters;

import static com.appvaze.studentsdetailapp.util.Constant.DOB;
import static com.appvaze.studentsdetailapp.util.Constant.FATHER_NAME;
import static com.appvaze.studentsdetailapp.util.Constant.GENDER;
import static com.appvaze.studentsdetailapp.util.Constant.NATIONAL_ID;
import static com.appvaze.studentsdetailapp.util.Constant.STD_ID;
import static com.appvaze.studentsdetailapp.util.Constant.STD_NAME;
import static com.appvaze.studentsdetailapp.util.Constant.SURNAME;
import static com.appvaze.studentsdetailapp.util.Constant.stdReference;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appvaze.studentsdetailapp.R;
import com.appvaze.studentsdetailapp.models.Student;
import com.appvaze.studentsdetailapp.sqlite.OpenHelper;
import com.appvaze.studentsdetailapp.ui.AddStudentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.viewHolder> implements Filterable {

    private List<Student> dataHolder;
    private final List<Student> myList;
    Context context;

    public StudentAdapter(List<Student> dataHolder, Context context) {
        this.dataHolder = dataHolder;
        this.myList = new ArrayList<>(dataHolder);
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.std_detail_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {

        String stdId=dataHolder.get(position).getStdId();
        String name=dataHolder.get(position).getStdName();
        String surname=dataHolder.get(position).getSurName();
        String father=dataHolder.get(position).getFatherName();
        String nationalId=dataHolder.get(position).getNationalId();
        String dob=dataHolder.get(position).getDob();
        String gender=dataHolder.get(position).getDob();
        boolean isLocal = dataHolder.get(position).isLocal();

        holder.stdName.setText(name);
        holder.stdNationalId.setText(nationalId);
        holder.stdID.setText(stdId);

        OpenHelper helper = new OpenHelper(context);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, name+" "+surname, Toast.LENGTH_LONG).show();
            }
        });

        holder.imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), AddStudentActivity.class);
                intent.putExtra(STD_ID,stdId);
                intent.putExtra(STD_NAME,name);
                intent.putExtra(SURNAME,surname);
                intent.putExtra(FATHER_NAME,father);
                intent.putExtra(NATIONAL_ID,nationalId);
                intent.putExtra(DOB,dob);
                intent.putExtra(GENDER,gender);
                intent.putExtra("isLocal",String.valueOf(isLocal));
                view.getContext().startActivity(intent);
            }
        });

        holder.imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
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
                            Toast.makeText(view.getContext(), "Successfully Deleted", Toast.LENGTH_LONG).show();
                            dataHolder.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, dataHolder.size());
                            dialog.cancel();

                        } else {

                            stdReference.child(stdId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(view.getContext(), "Successfully Deleted", Toast.LENGTH_LONG).show();
                                            dialog.cancel();
                                        }
                                    });
                            dataHolder.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, dataHolder.size());

                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }

    public void filterList(ArrayList<Student> filteredList) {
        dataHolder = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Student> filteredList = new ArrayList<>();
            if (constraint.length() == 0 || constraint == null) {
                filteredList.addAll(myList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Student item : myList) {
                    if (item.getStdId().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            dataHolder.clear();
            dataHolder.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class viewHolder extends RecyclerView.ViewHolder{

        TextView stdName, stdNationalId, stdID;
        ImageView imgDel, imgUpdate;
        View view;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            stdName=itemView.findViewById(R.id.tv_stdNmae);
            stdNationalId=itemView.findViewById(R.id.tv_nationalId);
            stdID=itemView.findViewById(R.id.tv_stdID);
            imgDel=itemView.findViewById(R.id.img_delete);
            imgUpdate=itemView.findViewById(R.id.img_update);
            view=itemView;
        }
    }
}



