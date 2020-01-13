package com.example.neighborly;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends FirebaseRecyclerAdapter<MessageModel, MessageAdapter.MessageHolder> {
    private final String TAG = "MessageAdapter";
    private Context context;
    private String userId;
    private StorageReference storageReference;
    private RequestOptions requestOptions = new RequestOptions();
    private final int MESSAGE_IN_VIEW_TYPE = 1;
    private final int MESSAGE_OUT_VIEW_TYPE = 2;

    public MessageAdapter(@NonNull Context context, FirebaseRecyclerOptions<MessageModel> options, String userID) {
        /*
        Configure recycler adapter options:
        query defines the request made to Firestore
        Message.class instructs the adapter to convert each DocumentSnapshot to a Message object
        */
        super(options);
        this.context = context;
        this.userId = userID;
        requestOptions.placeholder(R.mipmap.ic_launcher);
        storageReference = FirebaseStorage.getInstance().getReference()
                .child("profile_images");
    }

    @Override
    public int getItemViewType(int position) {
        //if message userId matches current userid, set view type 1 else set view type 2
        if (getItem(position).getSenderUid().equals(userId)) {
            return MESSAGE_OUT_VIEW_TYPE;
        }
        return MESSAGE_IN_VIEW_TYPE;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull MessageModel model) {
        //Bind values from Message to the viewHolder

        final TextView mText = holder.mText;
        final TextView mUsername = holder.mUsername;
        final TextView mTime = holder.mTime;
        final CircleImageView imgProfile = holder.imgProfile;

        mUsername.setText(model.getSender());
        mText.setText(model.getText());
        //mTime.setText(DateFormat.format("dd MMM  (h:mm a)", model.getSentTime()));
//        Glide.with(context)
//                .setDefaultRequestOptions(requestOptions)
//                .load(storageReference.child(model.getSenderUid()))
//                .into(imgProfile);
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        We're using two different layouts. One for messages from others and the other for user's messages
         */
        View view = null;
        if (viewType == MESSAGE_IN_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_right_list_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_left_list_item, parent, false);
        }
        return new MessageHolder(view);
    }


    public class MessageHolder extends RecyclerView.ViewHolder {
        TextView mText;
        TextView mUsername;
        TextView mTime;
        CircleImageView imgProfile;

        public MessageHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.textViewMessage);
            mUsername = itemView.findViewById(R.id.textViewUserName);
//            mTime = itemView.findViewById(R.id.message_time);
            imgProfile = itemView.findViewById(R.id.profileImage);
        }
    }
}
