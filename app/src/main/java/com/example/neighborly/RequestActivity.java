package com.example.neighborly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestActivity extends AppCompatActivity {
    private FirebaseRecyclerAdapter<MessageModel, MessageAdapter.MessageHolder> adapter;
    private EditText input;
    private ProgressBar progressBar;
    private String userId;
    private String userName;
    private CircleImageView profileImage;

    private FirebaseUser user;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        //todo add intent logic for getting the request context
        Intent intent = getIntent();
        user = FirebaseAuth.getInstance().getCurrentUser();

        input = findViewById(R.id.editTextInput);
        progressBar = findViewById(R.id.progressBar);
        final RecyclerView recyclerView = findViewById(R.id.recycleView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        userName = user.getDisplayName();
        userId = user.getUid();

        // todo change to request context from intent
        String path = "messages/" + "requestedItemId";
        database = FirebaseDatabase.getInstance();
        DatabaseReference messagesRef = database.getReference("messages");

        // New child entries
        SnapshotParser<MessageModel> parser = new SnapshotParser<MessageModel>() {
            @NonNull
            @Override
            public MessageModel parseSnapshot(DataSnapshot dataSnapshot) {
                MessageModel message = dataSnapshot.getValue(MessageModel.class);
                if (message != null) {
                    message.setId(dataSnapshot.getKey());
                }
                return message;
            }
        };

        FirebaseRecyclerOptions<MessageModel> options =
                new FirebaseRecyclerOptions.Builder<MessageModel>()
                        .setQuery(messagesRef, parser)
                        .build();

        adapter = new MessageAdapter(RequestActivity.this, options, userId);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = adapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setAdapter(adapter);

        ImageButton btnSend = findViewById(R.id.buttonSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = input.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(RequestActivity.this, "Posted", Toast.LENGTH_LONG).show();
                    return;
                }

                // todo use path
                // todo change to reference
                database.getReference().child("messages").push().setValue(new MessageModel(userName, message, user.getUid()));
                input.setText("");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }
}
