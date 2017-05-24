package com.ajaysinghdewari.testproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

@SuppressWarnings("VisibleForTests")
public class MainActivity extends AppCompatActivity {

    private final static int PICK_IMAGE=121;
    private StorageReference mStorageRef;
    private Uri file;
    private Button mBtnUpload;
    private FirebaseAuth mFireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFireBaseAuth=FirebaseAuth.getInstance();
        mFireBaseAuth.createUserWithEmailAndPassword("ajay@gmail.com", "123ajay");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mBtnUpload= (Button) findViewById(R.id.btn_upload);
        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK && data.getData()!=null){
            file=data.getData();
            uploadFile();
        }
    }

    private void uploadFile(){

        if(file!=null){
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference riversRef = mStorageRef.child("images/photo.jpg");

            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "SUCESS", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage((int)progress+"% uploaed");
                }
            });
        }else{
            Toast.makeText(MainActivity.this, "Please select an image first", Toast.LENGTH_LONG).show();
        }

    }
}
