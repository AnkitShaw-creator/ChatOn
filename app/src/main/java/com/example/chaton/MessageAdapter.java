package com.example.chaton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    //Context context;
    private List<Messages> messageList;

    public MessageAdapter(List<Messages> messages){
        //this.context = context;
        messageList = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Messages ob = messageList.get(position);
        String type = ob.getType();
        holder.userName.setText(ob.getName());
        holder.time.setText(ob.getTime());
        if(type.equals("text")){
            holder.imageMessage.setVisibility(View.INVISIBLE);
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.messageTextView.setText(ob.getMessage());
        }
        if(type.equals("image")){
            holder.messageTextView.setVisibility(View.INVISIBLE);
            holder.imageMessage.setVisibility(View.VISIBLE);
            Picasso.get().load(ob.getMessage()).into(holder.imageMessage);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView messageTextView;
        TextView userName;
        TextView time;
        ImageView imageMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_textView);
            userName = itemView.findViewById(R.id.message_user_name);
            time = itemView.findViewById(R.id.message_timeStamp);
            imageMessage = itemView.findViewById(R.id.chat_image_view);
        }
    }
}
