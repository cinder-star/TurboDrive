package com.sihan.turbodrive.Activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sihan.turbodrive.R;
import com.sihan.turbodrive.Utils.FileUtils;

public class UploadFileActivity extends AppCompatActivity {
    private Button upload, choose;
    private TextView location;
    private static final String TAG = "DriveActivity";
    private String filename;
    private Uri fileUri;
    private String directory;
    private ProgressDialog progressDialog;
    private String dum;

    private static final int REQUEST_CODE = 6384;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);
        if(getIntent().getExtras() != null){
            Object directory = getIntent().getExtras().get("directory");
            this.directory = (String) directory;
        }
        bindWidgets();
        bindListeners();
    }

    private void bindListeners() {
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooser();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }

    private void bindWidgets() {
        location = findViewById(R.id.location);
        upload = findViewById(R.id.upload_file);
        choose = findViewById(R.id.choose_file);
    }

    private void uploadFile(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading file...");
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(directory+"/");
        StorageReference imageReference = storageReference.child(filename);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/user/path"+directory+"/"+dum+"/");
        databaseReference.child("name").setValue(filename);
        databaseReference.child("type").setValue("file");
        UploadTask uploadTask = imageReference.putFile(fileUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadFileActivity.this,"Failed Uploading file",Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                finish();
            }
        });
    }

    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        assert uri != null;
                        Log.i(TAG, "Uri = " + uri.toString());
                        try {
                            // Get the file path from the URI
                            fileUri =uri;
                            dum = uri.getLastPathSegment();
                            Cursor cursor = this.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                            cursor.moveToFirst();
                            filename = cursor.getString(0);
                            cursor.close();
                            location.setText(filename);
                            int slashIndex = filename.lastIndexOf('/');
                            filename = filename.substring(slashIndex+1);
                        } catch (Exception e) {
                            Log.e("FileSelectorTest", "File select error", e);
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
