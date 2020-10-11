package com.example.chaton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final int RC_LOG_IN = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null ){
            createSignInIntent();
        }
        else{
            Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }

    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]]
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                String userUID = mAuth.getCurrentUser().getUid();
                final String[] deviceToken = new String[1];
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful()){
                            deviceToken[0] = task.getResult().getToken();
                        }
                    }
                });
                HashMap<String,String> userData = new HashMap<>();
                userData.put("name", String.valueOf(mAuth.getCurrentUser().getDisplayName()));
                userData.put("status",getString(R.string.default_status));
                userData.put("image", "default");
                userData.put("thumbnail","default");
                userData.put("device_token",deviceToken[0]);

                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
                mDatabase.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(StartActivity.this, "Signed in Successfully", Toast.LENGTH_LONG).show();
                            Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                    }
                });



            }

            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_LONG).show();
                finish();
            }

        }
        if(requestCode == RC_LOG_IN){}
    }
}