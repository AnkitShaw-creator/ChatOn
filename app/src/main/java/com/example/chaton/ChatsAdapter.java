package com.example.chaton;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolders> {

    private List<String> chatUserUIDs;
    private DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child("users");
    private OnItemClickListener onItemClicked;
    public ChatsAdapter(List<String> list, OnItemClickListener onItemClicked){
        chatUserUIDs = list;
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public ChatsViewHolders onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_layout, parent, false);

        return new ChatsViewHolders(view, onItemClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolders holder, int position) {
        Log.d("ChatAdapter", "onBindViewHolder: USER_ID: "+chatUserUIDs.get(position));
        mUsers.child(chatUserUIDs.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    holder.chatUserName.setText(snapshot.child("name").getValue().toString());
                    Log.d("ChatAdapter", "onDataChange: NAME - "+snapshot.child("name").getValue().toString());
                    Picasso.get().load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.avatar).into(holder.chatUserImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatUserUIDs.size();
    }

    public static class ChatsViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView chatUserImage;
        TextView chatUserName;
        OnItemClickListener onItemClickListener;

        public ChatsViewHolders(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            chatUserImage = itemView.findViewById(R.id.chat_user_profile_image);
            chatUserName = itemView.findViewById(R.id.chat_user_name);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener{
        public void onItemClick(int position);
    }
}
