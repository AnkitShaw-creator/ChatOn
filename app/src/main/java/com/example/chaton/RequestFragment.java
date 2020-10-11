package com.example.chaton;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestFragment extends Fragment {

    private static final String TAG = "Request Fragment" ;

    private View mView;
    private String mCurrentUserUID;
    private RecyclerView mRequestsRecyclerView;
    private DatabaseReference mFriendReq;
    private DatabaseReference mUsers;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_request, container, false);

        mRequestsRecyclerView = mView.findViewById(R.id.request_RV);
        mRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCurrentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFriendReq = FirebaseDatabase.getInstance().getReference().child("friend_requests").child(mCurrentUserUID);
        mUsers = FirebaseDatabase.getInstance().getReference().child("users");
        // Inflate the layout for this fragment
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference().child("friend_requests").child(mCurrentUserUID);
        query.keepSynced(true);
        FirebaseRecyclerOptions<Requests> options  =new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(query,Requests.class)
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
        FirebaseRecyclerAdapter requestAdapter = new FirebaseRecyclerAdapter<Requests, RequestHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestHolder holder, final int position, @NonNull Requests model) {

                final String UID = model.getUID();
                final String req_status = model.getRequest_status();
                holder.request_status.setText(req_status);
                Log.d(TAG, "onBindViewHolder: Name: " +UID);
                mUsers.child(UID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            String name = snapshot.child("name").getValue().toString();

                            String image = snapshot.child("image").getValue().toString();
                            holder.senderName.setText(name);
                            Picasso.get().load(image).placeholder(R.drawable.avatar).into(holder.senderImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent requestIntent = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
                        requestIntent.putExtra("USER_ID",UID);
                        startActivity(requestIntent);
                    }
                });
            }
            @NonNull
            @Override
            public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_layout,parent,false);
                return new RequestHolder(view);
            }
        };
        requestAdapter.startListening();
        mRequestsRecyclerView.setAdapter(requestAdapter);
    }
    private static class RequestHolder extends RecyclerView.ViewHolder{
        CircleImageView senderImage;
        TextView senderName;
        TextView request_status;
        public RequestHolder(@NonNull View itemView) {
            super(itemView);
            senderImage = itemView.findViewById(R.id.sender_image);
            senderName = itemView.findViewById(R.id.sender_name);
            request_status = itemView.findViewById(R.id.request_status);
        }
    }
}