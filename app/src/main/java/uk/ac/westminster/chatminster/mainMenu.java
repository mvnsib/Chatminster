package uk.ac.westminster.chatminster;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class mainMenu extends AppCompatActivity {

    UserList user = new UserList();
    DatabaseReference db;
    FirebaseFirestore fbStore;
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;
    String userID = "";
    private Button button1, button2, button3, button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        db = FirebaseDatabase.getInstance().getReference();
        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        fbStore = FirebaseFirestore.getInstance();

        button1 = (Button)findViewById(R.id.button_myProfile);
        button2 = (Button)findViewById(R.id.button_tutorChat);
        button3 = (Button)findViewById(R.id.button_CommunityChat);
        button4 = (Button)findViewById(R.id.button_courseChat);

        button4.setVisibility(View.GONE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(fbUser != null) {
            userID = fbAuth.getCurrentUser().getUid();
            DocumentReference reference = fbStore.collection("UserDetails").document(userID);

            reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    //depending on the user type, it will display a specific chat for the user
                    if (documentSnapshot.getString("userType").equals("Staff")) {
                        button2.setText("Student Chat");
                        user.setUserType("Student");
                        button4.setVisibility(View.VISIBLE);
                    }
                    if (documentSnapshot.getString("userType").equals("Student")) {
                        button2.setText("Tutor Chat");
                        user.setUserType("Staff");
                    }
                }
            });
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openTutor();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGroupChat();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCourse();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //logs the user out
            case R.id.menu_logout:
                Intent intent = new Intent(this, login.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    public void openProfile(){
        Intent intent = new Intent(this, myProfile.class);
        startActivity(intent);
    }
    public void openTutor(){
        Intent intent = new Intent(this, tutorStudentList.class);
        intent.putExtra("User",user);
        startActivity(intent);
    }
    public void openGroupChat(){
        Intent intent = new Intent(this, communityList.class);
        intent.putExtra("User",user);
        startActivity(intent);
    }
    public void openCourse(){
        Intent intent = new Intent(this, staffList.class);
        intent.putExtra("User",user);
        startActivity(intent);
    }

}
