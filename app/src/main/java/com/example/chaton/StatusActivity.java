package com.example.chaton;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private TextInputLayout mStatusInput;
    private Button mSaveBtn;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        String userUID = mAuth.getCurrentUser().getUid();

        mStatusInput = findViewById(R.id.status_text_input_layout);
        mSaveBtn = findViewById(R.id.change_status_button);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newStatus = mStatusInput.getEditText().getText().toString();
                mDatabaseRef.child("status").setValue(newStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(StatusActivity.this, "Status updated successfully",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(StatusActivity.this,"Status update unsuccessful", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }
        });
    }
}
