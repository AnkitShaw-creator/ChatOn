package com.example.chaton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.label_all_users);
        userRecyclerView = findViewById(R.id.users_list);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseRecyclerOptions<Users>options  =new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(query,Users.class)
                .build();
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addChildEventListener(eventListener);
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersHolder>(options) {
            @NonNull
            @Override
            public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout,parent,false);
                return new UsersHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersHolder holder, int position, @NonNull Users model) {
                    holder.mName.setText(model.getName());
                    holder.mStatus.setText(model.getStatus());
                    Picasso.get().load(model.getImage()).placeholder(R.drawable.avatar).into(holder.mImage);
                    final String userID = getRef(position).getKey();

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                            profileIntent.putExtra("USER_ID",userID);
                            startActivity(profileIntent);
                        }
                    });


            }
        };
            firebaseRecyclerAdapter.startListening();
            userRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    private static class UsersHolder extends RecyclerView.ViewHolder{
        CircleImageView mImage;
        TextView mName;
        TextView mStatus;
        public UsersHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.userName);
            mStatus = itemView.findViewById(R.id.userStatus);
            mImage = itemView.findViewById(R.id.userImage);
        }
    }
}