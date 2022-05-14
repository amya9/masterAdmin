package com.example.testme2admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testme2admin.adapters.CategoriesAdapter;
import com.example.testme2admin.adapters.MasterAdapter;
import com.example.testme2admin.models.CategoriesModel;
import com.example.testme2admin.models.MasterModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MasterActivity extends AppCompatActivity {
    private Dialog loadingDialog , addMasterDialog;
    private RecyclerView recyclerView ;
    private CircleImageView addImage ;
    private Button addMasterBtn , getin_btn , add_master;
    private EditText addMasterName;
    private EditText addMasterPlace;
    private  String downloadUrl;
    private Uri image;
//    private TextView add_master;

    FirebaseDatabase database;
    DatabaseReference reference;
    public static List<MasterModel> list;
    private MasterAdapter masterAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        Toolbar toolbar = findViewById(R.id.toolbar);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Master");
        //loading Dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        //loading add_master  Dialog
        add_master = findViewById(R.id.add_master);
        setMasterDialog();

        recyclerView = findViewById(R.id.master_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        loadingDialog.show();
        list = new ArrayList<>();
        masterAdapter = new MasterAdapter(list, new MasterAdapter.DeleteListener() {
            @Override
            public void onDelete(final String key , final int position) {
                new AlertDialog.Builder(MasterActivity.this , R.style.Theme_AppCompat_Light_Dialog_Alert)
                        .setTitle("Delete Teacher")
                        .setMessage("Are you sure, you want to delete this Teacher?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                reference.child("masters").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            for (String setIds :list.get(position).getSets()){
                                                reference.child("SETS").child(setIds).removeValue();
                                            }
                                            list.remove(position);
                                            masterAdapter.notifyDataSetChanged();
                                            loadingDialog.dismiss();
                                        }else {
                                            Toast.makeText(MasterActivity.this , "Failed to delete" , Toast.LENGTH_LONG).show();
                                            loadingDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel" , null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        recyclerView.setAdapter(masterAdapter);
        loadingDialog.show();
        reference.child("masters").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    List<String> categories = new ArrayList<>();
                    List<String> sets = new ArrayList<>();
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("categories").getChildren()){
                        categories.add(dataSnapshot2.getKey());
                    }
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("sets").getChildren()){
                        sets.add(dataSnapshot2.getKey());
                    }
                    list.add(new MasterModel(dataSnapshot1.child("name").getValue().toString(),
                            dataSnapshot1.child("place").getValue().toString(),
                            categories,
                            sets,
                            dataSnapshot1.child("url").getValue().toString() ,
                            dataSnapshot1.getKey()));
                }
                masterAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MasterActivity.this , error.getMessage() , Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
                finish();
            }
        });

        add_master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMasterDialog.show();
            }
        });


//        main intent
        getin_btn = findViewById(R.id.login_btn);
        getin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MasterActivity.this , CategoriesActivity.class);
                startActivity(mainIntent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu , menu);
        return super.onCreateOptionsMenu(menu);
    }
    // function for menu items logout and add category
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.addItem){
            //dialog will show here
            addMasterDialog.show();
        }
        if (item.getItemId() == R.id.logout){
            new AlertDialog.Builder(MasterActivity.this , R.style.Theme_AppCompat_Light_Dialog_Alert)
                    .setTitle("Logout ")
                    .setMessage("Are you sure, you want to logout?")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingDialog.show();
                            FirebaseAuth.getInstance().signOut();
                            Intent logoutIntent = new Intent(MasterActivity.this,MainActivity.class);
                            startActivity(logoutIntent);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel" , null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void setMasterDialog(){
        addMasterDialog = new Dialog(this);
        addMasterDialog.setContentView(R.layout.add_master);
        addMasterDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        addMasterDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        addMasterDialog.setCancelable(true);

        addImage = addMasterDialog.findViewById(R.id.add_master_image);
        addMasterName = addMasterDialog.findViewById(R.id.add_master_name);
        addMasterPlace = addMasterDialog.findViewById(R.id.add_master_place);
        addMasterBtn = addMasterDialog.findViewById(R.id.add_btn);

        //image picker
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent , 101);
            }
        });

        addMasterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addMasterName.getText().toString().isEmpty() || addMasterName.getText() == null){
                    addMasterName.setError("Required");
                    return;
                }
//                for (MasterModel model : list){
//                    if (addMasterName.getText().toString().equals(model.getName())){
//                        addMasterName.setError("master name already exists");
//                        return;
//                    }
//                }
                //check for place
                if (addMasterPlace.getText().toString().isEmpty() || addMasterPlace.getText() == null){
                    addMasterPlace.setError("Required");
                    return;
                }

                if(image == null){
                    Toast.makeText(MasterActivity.this , "Please Select Master Image" , Toast.LENGTH_LONG).show();
                    return;
                }
                addMasterDialog.dismiss();
                // else we upload data to firebase
                uploadData();
            }
        });
    }

    // over write activity result to set downloadable image url
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101){
            if (resultCode == RESULT_OK){
                image = data.getData();
                addImage.setImageURI(image);
            }
        }
    }
    private void uploadData() {
        loadingDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference imageReference =  storageReference.child("master").child(image.getLastPathSegment());

        UploadTask uploadTask = imageReference.putFile(image);

        Task<Uri> urlTask =  uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadUrl = task.getResult().toString();
                            uploadMaster();
                        }else {
                            loadingDialog.dismiss();
                            Toast.makeText(MasterActivity.this , "Something Went Wrong" , Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    // Handle failures
                    loadingDialog.dismiss();
                    Toast.makeText(MasterActivity.this , "Something Went Wrong" , Toast.LENGTH_LONG).show();

                }
            }
        });

    }
    // method to upload  masters details  into firebase database
    private void uploadMaster() {
        Map<String , Object> map = new HashMap<>();
        map.put("categories" , 0);
        map.put("name" , addMasterName.getText().toString());
        map.put("place" , addMasterPlace.getText().toString());
        map.put("sets" , 0);
        map.put("url" , downloadUrl);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String id = UUID.randomUUID().toString();
        database.getReference().child("masters").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MasterActivity.this , "Added Successfully " , Toast.LENGTH_LONG).show();
                    list.add(new MasterModel(addMasterName.getText().toString() , addMasterPlace.getText().toString() , new ArrayList<String>() ,  new ArrayList<String>(), downloadUrl , id));
//                    categoriesAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(MasterActivity.this , "Something Went Wrong" , Toast.LENGTH_LONG).show();
                }
                loadingDialog.dismiss();
            }
        });
    }




}
