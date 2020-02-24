package com.example.neighborly;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.SnapshotParser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestActivity extends AppCompatActivity {

    static public final String REQUEST_PRIVATE_CHAT = "private chat";
    static public final String REQUEST_ITEM = "request item";

    private FirebaseRecyclerAdapter<MessageModel, MessageAdapter.MessageHolder> adapter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private TextView privateChatTitle;
    private CircleImageView profilePicture;
    private Dialog popupRequestDialog;
    private EditText input;
    private UserModel curUser;
    private BuildingModel curBuilding;
    private String msgPath;
    private String lastUserToAnswer;
    private int chosenBadge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        curUser = UserModelDataHolder.getInstance().getCurrentUser();
        curBuilding = BuildingModelDataHolder.getInstance().getCurrentBuilding();

        Intent intent = getIntent();
        String requestType = intent.getStringExtra("requestType");

        if (requestType.equals(RequestActivity.REQUEST_PRIVATE_CHAT)) {
            String otherUser = intent.getStringExtra("neighbor");
            String itemName = intent.getStringExtra("itemName");
            handlePrivateChat(otherUser, itemName);
        }
        if (requestType.equals(RequestActivity.REQUEST_ITEM)) {
            String requestId = intent.getStringExtra("requestId");
            handleItemRequest(requestId);
        }

        final RecyclerView recyclerView = findViewById(R.id.recycleView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        final DatabaseReference messagesRef = database.getReference(msgPath);

        // New child entries
        SnapshotParser<MessageModel> parser = new SnapshotParser<MessageModel>() {
            @NonNull
            @Override
            public MessageModel parseSnapshot(DataSnapshot dataSnapshot) {
                MessageModel message = dataSnapshot.getValue(MessageModel.class);
                if (message != null) {
                    message.setMessageId(dataSnapshot.getKey());
                }
                String senderId = message.getSender().getId();
                if (senderId != null && (lastUserToAnswer == null || !senderId.equals(curUser.getId()))){
                    lastUserToAnswer = senderId;
                }
                return message;
            }
        };

        FirebaseRecyclerOptions<MessageModel> options =
                new FirebaseRecyclerOptions.Builder<MessageModel>()
                        .setQuery(messagesRef, parser)
                        .build();

        adapter = new MessageAdapter(RequestActivity.this, options, curUser.getId(), requestType);
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
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        ImageButton btnSend = findViewById(R.id.buttonSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = input.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(RequestActivity.this, "Posted", Toast.LENGTH_LONG).show();
                    return;
                }

                messagesRef.push().setValue(new MessageModel(curUser, message));
                input.setText("");
            }
        });

        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_button);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void handleItemRequest(String requestId) {
        setContentView(R.layout.activity_request_public);
        input = findViewById(R.id.editRequestMessage);
        msgPath = String.format("messages/%s", requestId);
        final RequestModel curRequest = curBuilding.getRequestById(requestId);
        TextView requestTitle = findViewById(R.id.requestDetailsTitle);
        UserModelFacade curNeighbor = BuildingModelDataHolder.getInstance().getCurrentBuilding().getUserById(curRequest.getRequestUserId());
        Switch isResolved = findViewById(R.id.isResolved);
        if (curNeighbor.getId().equals(curUser.getId())) {
            requestTitle.setText(getString(R.string.isResolvedTitle));
            isResolved.setVisibility(View.VISIBLE);
            isResolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    curBuilding.setIsResolvedByRequestId(curRequest.getRequestId(), isChecked);
                    BuildingModelDataHolder.getInstance().setCurrentBuilding(curBuilding);

                    if (lastUserToAnswer != null && isChecked && !lastUserToAnswer.equals(curUser.getId())) {
                        showResolvedPopup();
                    }
                }
            });
        } else {
            isResolved.setVisibility(View.INVISIBLE);
            requestTitle.setText(String.format(getString(R.string.public_request_title), curNeighbor.getPresentedName(), curRequest.getItemRequested()));
        }

        TextView originalMsg = findViewById(R.id.requestDetailsOriginalMessage);
        originalMsg.setText(curRequest.getRequestMsg());


    }

    private void handlePrivateChat(String otherUser, String itemName) {
        setContentView(R.layout.activity_request);
        input = findViewById(R.id.editRequestMessage);
        privateChatTitle = findViewById(R.id.privateChatTitle);
        profilePicture = findViewById(R.id.profilePicture);
        UserModelFacade neighbor = curBuilding.getUserById(otherUser);
        privateChatTitle.setText(String.format(this.getString(R.string.private_chat_title), neighbor.getPresentedName()));
        Glide.with(this).load(neighbor.getImageUriString()).into(profilePicture);

        if (itemName != null) {
            input.setText(String.format(this.getString(R.string.request_item_from_neighbor_message), neighbor.getPresentedName(), itemName));
        }

        if (otherUser.compareTo(curUser.getId()) > 0) {
            msgPath = String.format(this.getString(R.string.private_messages_db_path), otherUser, curUser.getId());
        } else {
            msgPath = String.format(this.getString(R.string.private_messages_db_path), curUser.getId(), otherUser);
        }

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

    // Todo: implement this
    int getImage() {
        return 0;
    }

    private void showResolvedPopup() {
        popupRequestDialog = new Dialog(this);
        popupRequestDialog.setContentView(R.layout.popup_resolved_request);

        Button closeButton = popupRequestDialog.findViewById(R.id.exit);
        final Button sendButton = popupRequestDialog.findViewById(R.id.send);

        sendButton.setVisibility(View.INVISIBLE);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupRequestDialog.dismiss();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curBuilding.addBadgeToUserById(chosenBadge, lastUserToAnswer);
                BuildingModelDataHolder.getInstance().setCurrentBuilding(curBuilding);
                popupRequestDialog.dismiss();
            }
        });

        FlexboxLayout badgesLayout = popupRequestDialog.findViewById(R.id.neighborBadgesOptions);

        for (final int badge : UserModel.BADGES) {
            ImageView badgeImage = new ImageView(this);
            badgeImage.setImageResource(badge);
            badgeImage.setPadding(0, 20, 0, 20);
            badgesLayout.addView(badgeImage);
            badgeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chosenBadge = badge;
                    sendButton.setVisibility(View.VISIBLE);
                }
            });
        }
        popupRequestDialog.show();
    }

    private void showAskIfAddNewItemPopup(final String requestedItem) {
        popupRequestDialog = new Dialog(this);
        popupRequestDialog.setContentView(R.layout.popup_ask_if_add_new_item);

        Button closeButton = popupRequestDialog.findViewById(R.id.exit);
        final Button sendButton = popupRequestDialog.findViewById(R.id.send);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupRequestDialog.dismiss();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(RequestActivity.this, AddItemActivity.class);
                activityIntent.putExtra("item name", requestedItem);
                startActivity(activityIntent);
                popupRequestDialog.dismiss();
            }
        });



        popupRequestDialog.show();
    }

}
