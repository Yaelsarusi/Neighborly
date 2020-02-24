package com.example.neighborly;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestActivity extends AppCompatActivity {

    static public final String REQUEST_PRIVATE_CHAT = "private chat";
    static public final String REQUEST_ITEM = "request item";
    static private final int NON_SELECTED = -5;

    private FirebaseRecyclerAdapter<MessageModel, MessageAdapter.MessageHolder> adapter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Set<UserModel> commentedUsers = new ArraySet<>();
    private TextView privateChatTitle;
    private CircleImageView profilePicture;
    private Dialog popupRequestDialog;
    private EditText input;
    private UserModel curUser;
    private BuildingModel curBuilding;
    private UserModelFacade neighbor;
    private String msgPath;
    private String chosenNeighbor;
    private int chosenBadge = NON_SELECTED;
    private RequestModel curRequest;

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
        input.requestFocus();

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

                UserModel user = message.getSender();
                if (user != null && !user.getId().equals(curUser.getId())) {
                    commentedUsers.add(user);
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

                if (!neighbor.getId().equals(curUser.getId())) {
                    if (curRequest != null){
                        showAskIfAddNewItemPopup(curRequest.getItemPresentedName());
                    }
                }
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
        msgPath = String.format("Messages/%s", requestId);
        curRequest = curBuilding.getRequestById(requestId);
        TextView requestTitle = findViewById(R.id.requestDetailsTitle);
        neighbor = BuildingModelDataHolder.getInstance().getCurrentBuilding().getUserById(curRequest.getRequestUserId());
        Switch isResolved = findViewById(R.id.isResolved);
        if (neighbor.getId().equals(curUser.getId())) {
            requestTitle.setText(getString(R.string.isResolvedTitle));
            isResolved.setVisibility(View.VISIBLE);
            isResolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    curBuilding.setIsResolvedByRequestId(curRequest.getRequestId(), isChecked);
                    BuildingModelDataHolder.getInstance().setCurrentBuilding(curBuilding);

                    if (isChecked) {
                        showResolvedPopup();
                    }
                }
            });
        } else {
            isResolved.setVisibility(View.INVISIBLE);
            requestTitle.setText(String.format(getString(R.string.public_request_title), neighbor.getPresentedName(), curRequest.getItemRequested()));
            input.setText(this.getString(R.string.neighbor_offering_help_msg));
        }

        TextView originalMsg = findViewById(R.id.requestDetailsOriginalMessage);
        originalMsg.setText(curRequest.getRequestMsg());


    }

    private void handlePrivateChat(String otherUser, String itemName) {
        setContentView(R.layout.activity_request);
        input = findViewById(R.id.editRequestMessage);
        privateChatTitle = findViewById(R.id.privateChatTitle);
        profilePicture = findViewById(R.id.profilePicture);
        neighbor = curBuilding.getUserById(otherUser);
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
                curBuilding.addBadgeToUserById(chosenBadge, chosenNeighbor);
                BuildingModelDataHolder.getInstance().setCurrentBuilding(curBuilding);
                popupRequestDialog.dismiss();
            }
        });

        final FlexboxLayout neighborsLayout = popupRequestDialog.findViewById(R.id.neighborOptions);
        final FlexboxLayout badgesLayout = popupRequestDialog.findViewById(R.id.neighborBadgesOptions);

        for (final UserModel neighbor : commentedUsers) {
            final ImageView neighborImage = new ImageView(this);
            Glide.with(this).load(Uri.parse(neighbor.getImageUriString())).into(neighborImage);
            neighborImage.setPadding(8, 20, 8, 20);
            neighborsLayout.addView(neighborImage);
            neighborImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // deselect the rest
                    for (int i = 0; i < neighborsLayout.getFlexItemCount(); i++) {
                        neighborsLayout.getFlexItemAt(i).setBackgroundColor(Color.WHITE);
                    }

                    neighborImage.setBackgroundColor(Color.BLUE);
                    chosenNeighbor = neighbor.getId();
                    if (chosenBadge != NON_SELECTED) {
                        sendButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        for (final int badge : UserModel.BADGES) {
            final ImageView badgeImage = new ImageView(this);
            badgeImage.setImageResource(badge);
            badgeImage.setPadding(0, 20, 0, 20);
            badgesLayout.addView(badgeImage);

            badgeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // deselect the rest
                    for (int i = 0; i < badgesLayout.getFlexItemCount(); i++) {
                        badgesLayout.getFlexItemAt(i).setBackgroundColor(Color.WHITE);
                    }

                    badgeImage.setBackgroundColor(Color.BLUE);
                    chosenBadge = badge;
                    if (chosenNeighbor != null) {
                        sendButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        popupRequestDialog.show();
    }

    private void showAskIfAddNewItemPopup(final String requestedItem) {
        popupRequestDialog = new Dialog(this);
        popupRequestDialog.setContentView(R.layout.popup_ask_if_add_new_item);

        Button closeButton = popupRequestDialog.findViewById(R.id.exit);
        final Button addItem = popupRequestDialog.findViewById(R.id.addItem);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupRequestDialog.dismiss();
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(RequestActivity.this, AddItemActivity.class);
                activityIntent.putExtra("activityType", AddItemActivity.EDIT_EXISTING_ITEM);
                activityIntent.putExtra("item", new ItemModel(requestedItem, curUser.getId()));
                startActivity(activityIntent);
                popupRequestDialog.dismiss();
            }
        });

        popupRequestDialog.show();
    }

}
