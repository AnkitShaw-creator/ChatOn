package com.example.chaton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NameActivity extends AppCompatActivity {

    private Button mSaveBtn;
    private TextInputLayout mNameInput;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        mNameInput = findViewById(R.id.name_input_layout);
        mSaveBtn = findViewById(R.id.change_name_button);
        mDatabaseRef  = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = mNameInput.getEditText().getText().toString();
                mDatabaseRef.child("name").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(NameActivity.this, "Name Changed Succesfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(NameActivity.this, "Name was not changed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}