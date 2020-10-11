package com.example.chaton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class AccountSettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = AccountSettingsActivity.class.getSimpleName() ;
    private FirebaseAuth mAuth;

    private CircleImageView userImage;
    private TextView userName;
    private TextView userStatus;
    private Button mChangeName;
    private Button mChangeStatus;
    private FloatingActionButton mChangeImageFAB;

    private static final int GALLERY_PICK =1;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        userImage = (CircleImageView)findViewById(R.id.user_image);
        userName = findViewById(R.id.user_name);
        userStatus = findViewById(R.id.user_status);
        mChangeName = findViewById(R.id.changeName_button);
        mChangeStatus = findViewById(R.id.changeStatus_button);
        mChangeImageFAB = findViewById(R.id.fab_change_image);

        mAuth = FirebaseAuth.getInstance();
        String userUID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
        mDatabase.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = String.valueOf(snapshot.child("name").getValue());
                String status = String.valueOf(snapshot.child("status").getValue());
                final String image = String.valueOf(snapshot.child("image").getValue());
                String thumbnail = String.valueOf(snapshot.child("thumbnail").getValue());
                userName.setText(name);
                userStatus.setText(status);
                if(!image.equals("default")){
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar)
                            .into(userImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.avatar)
                                    .into(userImage);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(LOG_TAG, "onCancelled: " + error.getMessage() );

            }
        });

        mChangeImageFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"),GALLERY_PICK);

                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountSettingsActivity.this);*/
            }
        });

        mChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"),GALLERY_PICK);

                //CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountSettingsActivity.this);**/
                Intent nameIntent = new Intent(AccountSettingsActivity.this, NameActivity.class);
                startActivity(nameIntent);
            }
        });

        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeStatus = new Intent(AccountSettingsActivity.this,StatusActivity.class);
                startActivity(changeStatus);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode ==RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(AccountSettingsActivity.this);
                mProgressDialog.setTitle("Uploading Image ...");
                mProgressDialog.setMessage("Please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                Uri resultUri = result.getUri();
                File thumb_filepath = new File(resultUri.getPath());
                String userUID_pImage = mAuth.getCurrentUser().getUid();
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75).compressToBitmap(thumb_filepath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference imagePath = mStorageRef.child("profile_images").child(userUID_pImage+".jpg");
                final StorageReference thumb_imageRef = mStorageRef.child("profile_images").child("thumbnails")
                        .child(userUID_pImage+".jpg");
                UploadTask uploadTask = imagePath.putFile(resultUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return imagePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            final String downloadUri = task.getResult().toString();
                            UploadTask uploadTask1 = (UploadTask) thumb_imageRef.putBytes(thumb_byte)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    if(thumb_task.isSuccessful()){
                                        String download_thumb_path = thumb_task.getResult().toString();
                                        Map<String, Object> updateMap = new HashMap<>();
                                        updateMap.put("image",downloadUri);
                                        updateMap.put("thumbnail",download_thumb_path);

                                        mDatabase.updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(AccountSettingsActivity.this, "Profile Image uploaded successfully",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                            Log.d(LOG_TAG, "then: GETTING DOWNLOAD URL :-" +downloadUri);

                        } else {
                            mProgressDialog.dismiss();
                            // Handle failures
                            Log.e(LOG_TAG, "onComplete: ",task.getException());
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(LOG_TAG, "onActivityResult: ", error);
            }
        }
    }
}