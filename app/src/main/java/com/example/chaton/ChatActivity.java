package com.example.chaton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 0;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private ImageButton mAddBtn;
    private EditText mText;
    private ImageButton mSendBtn;
    private String mChatUser ="";
    private RecyclerView mChatField;
    private MessageAdapter mAdapter;
    private final List<Messages> mList = new ArrayList<>();

    private TextView mTitle;
    private TextView mLastSeen;
    private CircleImageView mTitleImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mAddBtn = findViewById(R.id.chat_add_btn);
        mText = findViewById(R.id.chat_edit_text);
        mSendBtn= findViewById(R.id.send_chat_btn);
        mChatField = findViewById(R.id.chat_field);
        mAdapter = new MessageAdapter(mList);
        
        mChatField.setLayoutManager(new LinearLayoutManager(this));
        mChatField.setHasFixedSize(true);
        mChatField.setAdapter(mAdapter);
        Intent intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.chat_actionbar_layout);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.chat_actionbar_layout,null);
        actionBar.setCustomView(view);
        mTitle = findViewById(R.id.title_textview);
        mLastSeen = findViewById(R.id.last_seen_textview);
        mTitleImage = findViewById(R.id.actionbar_profile_image);
         **/

        if(intent != null){
            mChatUser = intent.getStringExtra("USER_ID");
        }
        mRootRef.child("users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    getSupportActionBar().setTitle(snapshot.child("name").getValue().toString());
                    //Picasso.get().load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.avatar).into(mTitleImage);
                    //mLastSeen.setText("online");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessages();
            }
        });
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAttachments();
            }
        });

        populateChatField();
    }

    private void addAttachments() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"),GALLERY_PICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            final StorageReference imageMessage = FirebaseStorage.getInstance().getReference().child("messages_image")
                    .child(mCurrentUser.getUid()+".jpg");

            UploadTask uploadTask = imageMessage.putFile(imageUri);
            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageMessage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        String URI = task.getResult().toString();

                        String currentUser_message_ref = "messages/"+mCurrentUser.getUid()+"/"+mChatUser;
                        String chatUser_message_ref = "messages/"+mChatUser+"/"+mCurrentUser.getUid();
                        String time = DateFormat.getDateTimeInstance().format(new Date());

                        //String uriImage = uriForImageMessage[0];

                        Map<String, String> messageMap = new HashMap<>();
                        messageMap.put("message",URI);
                        messageMap.put("time", time);
                        messageMap.put("type","image");
                        messageMap.put("name",mCurrentUser.getDisplayName());

                        DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUser.getUid()).child(mChatUser).push();
                        String push_key = user_message_push.getKey();

                        Map<String, Object> mUserMessage = new HashMap<>();
                        mUserMessage.put(currentUser_message_ref+"/"+push_key,messageMap);
                        mUserMessage.put(chatUser_message_ref+"/"+push_key,messageMap);

                        mText.setText("");

                        mRootRef.updateChildren(mUserMessage, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if(error != null){
                                    Log.e("CHAT_LOG", error.getMessage());
                                }
                                else {/*populateChatField(); */}
                            }
                        });
                    }
                    else{
                        Log.e("CHAT_ACTIVITY", "onComplete: ", task.getException());
                    }
                }
            });


        }
    }

    private void sendMessages() {
        String message = mText.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String currentUser_message_ref = "messages/"+mCurrentUser.getUid()+"/"+mChatUser;
            String chatUser_message_ref = "messages/"+mChatUser+"/"+mCurrentUser.getUid();
            String time = DateFormat.getDateTimeInstance().format(new Date());

            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("message",message);
            messageMap.put("time", time);
            messageMap.put("type","text");
            messageMap.put("name",mCurrentUser.getDisplayName());

            DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUser.getUid()).child(mChatUser).push();
            String push_key = user_message_push.getKey();

            Map<String, Object> mUserMessage = new HashMap<>();
            mUserMessage.put(currentUser_message_ref+"/"+push_key,messageMap);
            mUserMessage.put(chatUser_message_ref+"/"+push_key,messageMap);

            mText.setText("");

            mRootRef.updateChildren(mUserMessage, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if(error != null){
                        Log.e("CHAT_LOG", error.getMessage());
                    }
                    else {/*populateChatField(); */}
                }
            });
        }
    }
    private void populateChatField() {

        mRootRef.child("messages").child(mCurrentUser.getUid()).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages m = snapshot.getValue(Messages.class);
                mList.add(m);
                mAdapter.notifyDataSetChanged();
                mChatField.scrollToPosition(mList.size()-1);
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
                Log.e("DATABASE_ERROR", "onCancelled: ChatActivity "+error.getMessage() );

            }
        });
    }

}