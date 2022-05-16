package uk.ac.westminster.chatminster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class privateChat extends AppCompatActivity {

    private DatabaseReference db;
    private FirebaseListAdapter<sendMessage> adapter;

    Button sendButton;
    EditText input;


    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID, userName, receiverID, receiverName;
    private final List<message> chatBubbles = new ArrayList<>();
    private LinearLayoutManager layout;
    private RecyclerView messageFeed;
    private uk.ac.westminster.chatminster.chatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);


        fAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        sendButton = findViewById(R.id.btn_sendMessage);
        input = findViewById(R.id.typeText);

        chatAdapter = new chatAdapter(chatBubbles);

        messageFeed = (RecyclerView) findViewById(R.id.messageFeed);
        layout = new LinearLayoutManager(this);

        receiverName = getIntent().getStringExtra("selectedUserName");
        receiverID = getIntent().getStringExtra("selectedID");

        messageFeed.setHasFixedSize(true);
        messageFeed.setLayoutManager(layout);

        messageFeed.setAdapter(chatAdapter);
        toolbar.setTitle(receiverName);

        //Triggers the send message button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = input.getText().toString();
                //checks if the text isn't empty
                if(!TextUtils.isEmpty(message)){
                    DatabaseReference pushMessage = db.child("Messages").child(userID).child(receiverID).push();

                    String messageID = pushMessage.getKey();
                    //stores the Message ID in that format
                    Map msgMap = new HashMap();
                    msgMap.put("message",message);
                    msgMap.put("timeSent", ServerValue.TIMESTAMP);
                    msgMap.put("from", userID);
                    //directory where the message is being stored
                    Map msgUserMap = new HashMap();
                    msgUserMap.put("Messages/" + userID + "/" + receiverID + "/" + messageID, msgMap);
                    msgUserMap.put("Messages/" + receiverID + "/" + userID + "/" + messageID, msgMap);

                    db.updateChildren(msgUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });

                    input.setText("");

                }
            }
        });
        displayMessageFeed();
    }
    //displays the messages as it retrieves it from the Messages node underneath the receiver's ID and underneath that is the current User's ID
    private void displayMessageFeed(){
        db.child("Messages").child(receiverID).child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                message bubble = dataSnapshot.getValue(message.class);
                chatBubbles.add(bubble);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
