package com.sihan.turbodrive.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sihan.turbodrive.Domain.File;
import com.sihan.turbodrive.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DriveActivity extends AppCompatActivity {
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout mDrawer;
    @SuppressWarnings("FieldCanBeLocal")
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private FirebaseUser firebaseUser;
    private String ROOT = "/sihan";
    private String current_directory = "/sihan";
    private List<File> files = new ArrayList<>();
    private RecyclerView filesRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        setTitle("Drive");
        ROOT = "/"+FirebaseAuth.getInstance().getUid();
        current_directory = "/"+FirebaseAuth.getInstance().getUid();
        bindWidgets();
        setupDrawerContent(nvDrawer);
        prepareListView();
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
                startActivity(new Intent(DriveActivity.this,ProfileActivity.class));
                break;
            case R.id.add_file:
                startActivity(new Intent(DriveActivity.this,UploadFileActivity.class).putExtra("directory",current_directory));
                break;
            case R.id.download_file:

                break;
            case R.id.contacts:

                break;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DriveActivity.this,LoginActivity.class));
                break;
            default:
        }
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if(ROOT.equals(current_directory)){
            finishAffinity();
        }
        else{
            current_directory = current_directory.substring(current_directory.lastIndexOf('/')-1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveFiles();
    }

    private void retrieveFiles() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/user/path"+current_directory);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                files.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    File file = ds.getValue(File.class);
                    assert file != null;
                    //Log.e("----NOTIFICATION--->>>", notification.toString());
                    files.add(file);
                }
                FileListAdapter fileListAdapter = (FileListAdapter) filesRecyclerView.getAdapter();
                assert fileListAdapter != null;
                fileListAdapter.setNotifications(files);
                fileListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void prepareListView() {
        filesRecyclerView = findViewById(R.id.file_list);
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        retrieveFiles();
        filesRecyclerView.setAdapter(new FileListAdapter(files));
    }

    private class FileListAdapter extends RecyclerView.Adapter<FileListItemViewHolder>{
        private List<File> files;

        FileListAdapter(List<File> files){
            this.files = files;
        }
        public void setNotifications(List<File> files){
            this.files = files;
        }

        @Override
        public int getItemCount(){
            return files.size();
        }

        @NonNull
        @Override
        public FileListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(DriveActivity.this).inflate(R.layout.row_file_list,viewGroup,false);

            return new FileListItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileListItemViewHolder fileListItemViewHolder, int i) {

            final File file  = files.get(i);

            fileListItemViewHolder.filename.setText(file.getName());
            fileListItemViewHolder.image.setImageDrawable(getDrawable(draw(file)));

            fileListItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(file.getType().equals("folder")){
                        current_directory = current_directory+"/"+file.getName();
                        retrieveFiles();
                    }
                }
            });
        }
    }

    private int draw(File file){
        if(file.getType().equals("folder")) return R.drawable.folder_icon;
        else if(file.getName().endsWith(".java")) return R.drawable.java_icon;
        else if(file.getName().endsWith("js")) return R.drawable.javascript_icon;
        else if(file.getName().endsWith(".jpg")||file.getName().endsWith(".png")) return R.drawable.image_icon;
        else if(file.getName().endsWith(".docx")) return R.drawable.doc_icon;
        else if(file.getName().endsWith(".c")) return R.drawable.c_icon;
        else if(file.getName().endsWith(".mp3")) return R.drawable.mp3_icon;
        else if(file.getName().endsWith(".mp4")) return R.drawable.mp4_icon;
        else if(file.getName().endsWith(".pdf")) return R.drawable.pdf_icon;
        return R.drawable.unknown_icon;
    }

    private class FileListItemViewHolder extends RecyclerView.ViewHolder{
        private TextView filename;
        private ImageView image;

        FileListItemViewHolder(@NonNull View view){
            super(view);
            filename = view.findViewById(R.id.file_name);
            image = view.findViewById(R.id.image_view);
        }
    }
}
