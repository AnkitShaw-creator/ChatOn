package com.example.chaton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference userRef;

    private TextView mName;
    private TextView mStatus;
    private ImageView mImage;
    private FloatingActionButton mFab;
    private Button mAcceptBtn;
    private Button mDeclineBtn;

    private DatabaseReference mFriendsRequests;
    private DatabaseReference mFriends;
    private DatabaseReference mNotification;
    private FirebaseUser mCurrentUser;
    private String currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        String UID = "";
        String req_stats = "";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = findViewById(R.id.profile_name);
        mStatus = findViewById(R.id.profile_status);
        mImage = findViewById(R.id.profile_imageView);
        mFab = findViewById(R.id.fab_add_friend);
        mAcceptBtn = findViewById(R.id.btn_accept_req);
        mDeclineBtn =findViewById(R.id.btn_decline_req);

        if(intent != null){
            UID = intent.getStringExtra("USER_ID");
            /**
            if(intent.hasExtra("request_status")){
                req_stats = String.valueOf(intent.getExtras());
                if(req_stats == "received"){
                    mFab.setVisibility(View.INVISIBLE);
                    mAcceptBtn.setVisibility(View.VISIBLE);
                    mDeclineBtn.setVisibility(View.VISIBLE);
                }
            }
            else{

                mFab.setVisibility(View.VISIBLE);
                mAcceptBtn.setVisibility(View.INVISIBLE);
                mDeclineBtn.setVisibility(View.INVISIBLE);
            }
             **/
        }


        final String finalUID = UID;


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendsRequests = FirebaseDatabase.getInstance().getReference().child("friend_requests");
        mFriends = FirebaseDatabase.getInstance().getReference().child("friends");
        mNotification = FirebaseDatabase.getInstance().getReference().child("notification");
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(UID);
        currentStatus = "not_friend";


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String profile_pic = snapshot.child("image").getValue().toString();

                getSupportActionBar().setTitle(name);

                mName.setText(name);
                mStatus.setText(status);
                Picasso.get().load(profile_pic).placeholder(R.drawable.avatar).into(mImage);


                mFriendsRequests.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(finalUID)){
                            String request_status = snapshot.child(finalUID).child("request_status").getValue().toString();

                            //--------Friend requests/ Received------//
                            if(request_status.equals("received")){
                                currentStatus = "received";
                                mFab.setVisibility(View.INVISIBLE);
                                mAcceptBtn.setVisibility(View.VISIBLE);
                                mAcceptBtn.setClickable(true);
                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setClickable(true);
                                //mFab.setImageDrawable(getResources().getDrawable(R.drawable.add_friends));
                                //mFab.setBackgroundColor(getResources().getColor(R.color.fui_transparent));

                            }
                            //--------Friend requests/ Sent------//
                            else if( request_status.equals("sent")){
                                currentStatus = "sent";
                                mFab.setVisibility(View.VISIBLE);
                                mAcceptBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mFab.setImageDrawable(getResources().getDrawable(R.drawable.icon_cancel));
                            }
                        }
                        else{
                            mAcceptBtn.setVisibility(View.INVISIBLE);
                            mDeclineBtn.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentStatus.equals("not_friend")){
                    HashMap<String, String> userData = new HashMap<>();
                    userData.put("UID",finalUID);
                    userData.put("request_status","sent");
                    mFriendsRequests.child(mCurrentUser.getUid()).child(finalUID)
                            .setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        HashMap<String, String> senderData = new HashMap<>();
                                        senderData.put("UID",mCurrentUser.getUid());
                                        senderData.put("request_status","received");
                                        mFriendsRequests.child(finalUID).child(mCurrentUser.getUid())
                                                .setValue(senderData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                HashMap<String, String> notification = new HashMap<>();
                                                notification.put("from", mCurrentUser.getUid());
                                                notification.put("type", "request");

                                                mNotification.child(finalUID).push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            currentStatus = "sent";
                                                            Toast.makeText(ProfileActivity.this,"Request sent successfully",Toast.LENGTH_SHORT).show();
                                                            mFab.setImageDrawable(getResources().getDrawable(R.drawable.icon_cancel));
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(ProfileActivity.this,"Request was not sent",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                if(currentStatus.equals("request_sent")){
                    mFriendsRequests.child(mCurrentUser.getUid()).child(finalUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendsRequests.child(finalUID).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mNotification.child(finalUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(ProfileActivity.this,"Request deleted",Toast.LENGTH_SHORT).show();
                                                        mFab.setImageDrawable(getResources().getDrawable(R.drawable.add_friends));
                                                        currentStatus ="not_friend";
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(ProfileActivity.this,"Request was not deleted",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        mAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentStatus.equals("received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    String uid = finalUID;

                    Map<String, String> map = new HashMap<>();
                    map.put("UID",uid);
                    map.put("timeStamp", currentDate);
                    mFriends.child(mCurrentUser.getUid()).child(finalUID).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Map<String, String> friendsMap = new HashMap<>();
                                friendsMap.put("UID", mCurrentUser.getUid());
                                friendsMap.put("timeStamp", currentDate);
                                mFriends.child(finalUID).child(mCurrentUser.getUid()).setValue(friendsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            currentStatus = "friends";
                                            mFriendsRequests.child(mCurrentUser.getUid()).child(finalUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mFriendsRequests.child(finalUID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mFab.setVisibility(View.INVISIBLE);
                                                            mAcceptBtn.setVisibility(View.INVISIBLE);
                                                            mDeclineBtn.setVisibility(View.INVISIBLE);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });

        mDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentStatus.equals("received")){
                    mFriendsRequests.child(mCurrentUser.getUid()).child(finalUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendsRequests.child(finalUID).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mAcceptBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mFab.setVisibility(View.VISIBLE);
                                        mFab.setImageDrawable(getResources().getDrawable(R.drawable.add_friends));
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}