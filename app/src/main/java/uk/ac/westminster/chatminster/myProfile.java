package uk.ac.westminster.chatminster;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class myProfile extends AppCompatActivity {
    TextView emailText, fullnameText, studentIDText;
    Button logoutButton, changePWButton;
    DatabaseReference reference;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser fUser;

    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_myprofile);

            emailText = (TextView) findViewById(R.id.tv_Email);
            fullnameText = (TextView) findViewById(R.id.tv_fullName);
            studentIDText = (TextView) findViewById(R.id.tv_studentID);

            changePWButton = (Button) findViewById(R.id.btn_changePassword);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setTitle("My Profile");
            setSupportActionBar(toolbar);

            fAuth = FirebaseAuth.getInstance();
            fUser = fAuth.getCurrentUser();

            fStore = FirebaseFirestore.getInstance();

            userID = fAuth.getCurrentUser().getUid();
            if(fUser != null) {
                //retrieves the user details they have entered from registering
                DocumentReference documentReference = fStore.collection("UserDetails").document(userID);
                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        emailText.setText(documentSnapshot.getString("email"));
                        fullnameText.setText(documentSnapshot.getString("fullName"));
                        studentIDText.setText(documentSnapshot.getString("studentID"));
                    }
                });
            }

        changePWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                Intent intent = new Intent(this, login.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changePassword(){
        Intent intent = new Intent(this, changePassword.class);
        startActivity(intent);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
