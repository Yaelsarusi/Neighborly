package com.example.neighborly;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "Neighborly_channel";
    private static final String CHANNEL_NAME = "Neighborly app channel";
    private static final String CHANNEL_DESC = "Channel for Neighborly app";
    private static final String NEW_REQ_TITLE = "%1s asked for %2s"; //todo - rephrase
    private static final String REQ_ANSWER_TITLE = "Someone replied to your request! check it out"; //todo - rephrase
    private static final String NEW_MSG_TITLE = "%1s send you a message"; //todo - rephrase
    private static final int RC_SIGN_IN = 1;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private List<AuthUI.IdpConfig> providers;
    private UserModel curUser;
    private boolean isNewUser;
    Button btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

        //The if statement is to prevent initializing when the device is rotated
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FeedFragment()).commit();
        }

        curUser = UserModelDataHolder.getInstance().getCurrentUser();
        DatabaseReference buildingRef = database.getReference().child(Constants.DB_BUILDINGS).child(curUser.getAddress());

        // update current building when changed in DB
        buildingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BuildingModel oldBuilding = null;
                if (BuildingModelDataHolder.getInstance().getCurrentBuilding() != null) {
                    oldBuilding = BuildingModelDataHolder.getInstance().getCurrentBuilding();
                }
                BuildingModel curBuilding = dataSnapshot.getValue(BuildingModel.class);
                BuildingModelDataHolder.getInstance().setCurrentBuilding(curBuilding);

                if (oldBuilding != null) {
                    RequestModel lastRequest = oldBuilding.getLastRequest();
                    RequestModel newRequest = curBuilding.getLastRequest();

                    if (newRequest != null && !newRequest.getRequestUserId().equals(curUser.getId())) {
                        if (lastRequest != null && !lastRequest.getRequestId().equals(newRequest.getRequestId())) {
                            String userName = curBuilding.getUserById(newRequest.getRequestUserId()).getPresentedName();
                            String itemName = newRequest.getItemRequested();
                            String text = newRequest.getRequestMsg();
                            sendNotification(String.format(NEW_REQ_TITLE, userName, itemName), text);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        setSignOutButton();
    }

    private void setSignOutButton() {
        providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setVisibility(View.INVISIBLE);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                btnSignOut.setEnabled(false);
                                showSignInOptions();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void showSignInOptions() {
        // Create and launch sign-in intent
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.custom_login_layout)
                .setGoogleButtonId(R.id.imageButtonGoogle)
                .setFacebookButtonId(R.id.imageButtonFacebook)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setAuthMethodPickerLayout(customLayout)
                        .setTheme(R.style.LoginTheme)
                        .build(),
                RC_SIGN_IN);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selected = null;

                    switch (menuItem.getItemId()) {
                        case R.id.nev_feed:
                            btnSignOut.setVisibility(View.INVISIBLE);
                            selected = new FeedFragment();
                            break;
                        case R.id.nev_profile:
                            btnSignOut.setVisibility(View.VISIBLE);
                            selected = new ProfileFragment();
                            break;
                        case R.id.nev_neighbors:
                            btnSignOut.setVisibility(View.INVISIBLE);
                            selected = new NeighborsFragment();
                            break;
                        default:
                            selected = new FeedFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selected).commit();
                    return true;
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Get UserModel
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Show Email on toast
                Toast.makeText(this, "" + (user != null ? user.getEmail() : ""),
                        Toast.LENGTH_SHORT).show();
                // Set Button signout
                btnSignOut.setEnabled(true);

                if (response != null) {
                    isNewUser = response.isNewUser();
                }

                if (isNewUser) {
                    startActivity(new Intent(this, JoinBuildingActivity.class));
                } else {
                    // todo bug in sign in flow
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotification(String title, String text) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.back_button)
                .setContentTitle(title).setContentText(text).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, notificationBuilder.build());
    }
}
