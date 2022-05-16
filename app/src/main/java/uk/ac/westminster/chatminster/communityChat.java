package uk.ac.westminster.chatminster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class communityChat extends AppCompatActivity {
    String selectedCom;
    EditText textField;
    String chatField;
    String userID, userName, receiverID, userFullName;
    String currentDate, currentTime;
    Button sendPin;
    FirebaseAuth fAuth;
    DatabaseReference db, userDB, communityDB, messageDB;
    TextView userIDName, userMessage, userDateTime;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_chat);

        sendPin = (Button) findViewById(R.id.btn_sendCommunityMessage);
        textField = (EditText) findViewById(R.id.communityText);
        selectedCom = getIntent().getStringExtra("selectedCommunity");
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        userMessage = (TextView) findViewById(R.id.textDisplay) ;
        userMessage.setBackgroundColor(Color.YELLOW);


        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference();
        userDB = FirebaseDatabase.getInstance().getReference().child("UserProfile");
        communityDB = FirebaseDatabase.getInstance().getReference().child("Community").child(selectedCom);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(selectedCom);
        setSupportActionBar(toolbar);

        getUserInfo();
        //whenever the user presses 'pin' it retrieves the message from the EditText and uses a HashMap to store the sent data in the database in that format
        sendPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinmessage = textField.getText().toString();
                String pinmessageKey = communityDB.push().getKey();

                if(!TextUtils.isEmpty(pinmessage)){
                    Calendar calendarDate = Calendar.getInstance();
                    SimpleDateFormat date = new SimpleDateFormat("dd MMM, yyyy");
                    currentDate = date.format(calendarDate.getTime());

                    Calendar calendarTime = Calendar.getInstance();
                    SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
                    currentTime = time.format(calendarTime.getTime());

                    HashMap<String, Object> communityMessage = new HashMap<>();
                    communityDB.updateChildren(communityMessage);

                    messageDB = communityDB.child("PinMessages").child(pinmessageKey);

                    HashMap<String, Object> messageInfo = new HashMap<>();
                    messageInfo.put("name", userName);
                    messageInfo.put("fullname", userFullName);
                    messageInfo.put("message", pinmessage);
                    messageInfo.put("date", currentDate);
                    messageInfo.put("time", currentTime);

                    messageDB.updateChildren(messageInfo);

                }
                textField.setText("");

            }
        });

    }

    //This displays the messages and always updated when the user sends a pin message
    @Override
    protected void onStart(){
        super.onStart();
        communityDB.child("PinMessages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) {
                    displayMessages(dataSnapshot);
                }
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

    //I'm using an iterator to retrieve all the value and using a while loop to display the stored messages
    private void displayMessages(DataSnapshot dataSnapshot){
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String date =  (String)((DataSnapshot)iterator.next()).getValue();
            String fullname = (String)((DataSnapshot)iterator.next()).getValue();
            String message =  (String)((DataSnapshot)iterator.next()).getValue();
            String user =  (String)((DataSnapshot)iterator.next()).getValue();
            String time =  (String)((DataSnapshot)iterator.next()).getValue();


            userMessage.append(fullname + " (" + user + ")"  + ":\n" + message + "\n" + time +" on " + date + "\n\n__________________\n\n");
        }
    }
    //retrieves the user's full name and their ID to be displayed in the messages
    private void getUserInfo() {
        userDB.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userName = dataSnapshot.child("studentID").getValue().toString();
                    userFullName = dataSnapshot.child("fullname").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
