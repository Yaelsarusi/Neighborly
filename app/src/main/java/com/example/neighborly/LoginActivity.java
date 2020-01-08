package com.example.neighborly;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    List<AuthUI.IdpConfig> providers;
    private boolean isNewUser;
    // todo remove when not needed!
    boolean debugFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Choose authentication providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        // todo remove debugFlag when not needed!
        if (auth.getCurrentUser() != null && !debugFlag) {
            // already signed in, go to main activity
            startActivity(new Intent(this, MainActivity.class));

        } else {
            // not signed in
            showSignInOptions();
        }
    }

    private void showSignInOptions() {
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Get UserModel
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Show Email on toast
                Toast.makeText(this, "" + user.getEmail(), Toast.LENGTH_SHORT).show();

                if (response != null) {
                    isNewUser = response.isNewUser();
                }

                // todo remove debugFlag when not needed!
                if (isNewUser || debugFlag) {
                    startActivity(new Intent(this, JoinBuildingActivity.class));
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
