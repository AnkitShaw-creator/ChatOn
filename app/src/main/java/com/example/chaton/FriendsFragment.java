package com.example.chaton;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View mView;
    private DatabaseReference mUsers;
    private RecyclerView mFriendsRecyclerView;
    private String mCurrentUser;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       mView =  inflater.inflate(R.layout.fragment_friends, container, false);
       mFriendsRecyclerView = mView.findViewById(R.id.friends_RV);
       mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
       mUsers = FirebaseDatabase.getInstance().getReference().child("users");
       mFriendsRecyclerView.setHasFixedSize(true);
       mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        final Query query = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrentUser);
        final FirebaseRecyclerOptions<Friends> options  =new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(query,Friends.class)
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
        FirebaseRecyclerAdapter friendsAdapter   = new FirebaseRecyclerAdapter<Friends, FriendsHolder>(options) {

            @NonNull
            @Override
            public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_layout,parent, false);
                return new FriendsHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendsHolder holder, int position, @NonNull final Friends model) {

                String UID = model.getUID();
                mUsers.child(UID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            holder.friendName.setText(snapshot.child("name").getValue().toString());
                            holder.friendStatus.setText(snapshot.child("status").getValue().toString());
                            Picasso.get().load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.avatar)
                                    .into(holder.friendImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chatIntent = new Intent(getActivity().getApplicationContext(),ChatActivity.class);
                        chatIntent.putExtra("USER_ID",model.getUID());
                        startActivity(chatIntent);
                    }
                });

            }
        };
        friendsAdapter.startListening();
        mFriendsRecyclerView.setAdapter(friendsAdapter);
    }

    private class FriendsHolder extends RecyclerView.ViewHolder{

        CircleImageView friendImage;
        TextView friendName;
        TextView friendStatus;
        public FriendsHolder(@NonNull View itemView) {
            super(itemView);
            friendImage = itemView.findViewById(R.id.friends_thumb_image);
            friendName =itemView.findViewById(R.id.friends_name_tv);
            friendStatus = itemView.findViewById(R.id.friends_status_tv);
        }
    }
}