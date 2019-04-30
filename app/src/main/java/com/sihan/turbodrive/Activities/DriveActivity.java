package com.sihan.turbodrive.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sihan.turbodrive.R;
import com.sihan.turbodrive.Utils.FileUtils;

import java.io.File;
import java.util.Objects;

public class DriveActivity extends AppCompatActivity {
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout mDrawer;
    @SuppressWarnings("FieldCanBeLocal")
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private FirebaseUser firebaseUser;
    private final String ROOT = "/sihan/";
    private static final String TAG = "DriveActivity";
    private String filename;
    private Uri fileUri;

    private static final int REQUEST_CODE = 6384;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        setTitle("Drive");
        bindWidgets();
        setupDrawerContent(nvDrawer);
    }

    private void bindWidgets() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer,toolbar ,  R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        //noinspection deprecation
        mDrawer.setDrawerListener(drawerToggle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        switch(menuItem.getItemId()) {
            case R.id.profile:

                break;
            case R.id.add_file:
                uploadFile();
                break;
            case R.id.download_file:

                break;
            case R.id.contacts:

                break;
            case R.id.log_out:

                break;
            default:
        }
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void uploadFile(){
        showChooser();
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference(ROOT);
//        StorageReference imageReference = storageReference.child(filename);
//        UploadTask uploadTask = imageReference.putFile(fileUri);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(DriveActivity.this,"Failed Uploading file",Toast.LENGTH_LONG).show();
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(DriveActivity.this,"File uploading successful",Toast.LENGTH_LONG).show();
//            }
//        });
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
                            Cursor cursor = this.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                            cursor.moveToFirst();
                            filename = cursor.getString(0);
                            cursor.close();
                            int slashIndex = filename.lastIndexOf('/');
                            filename = filename.substring(slashIndex+1);
                            Toast.makeText(DriveActivity.this,filename,Toast.LENGTH_LONG).show();
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
