package uk.ac.westminster.chatminster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registration extends AppCompatActivity{

    public static final String TAG = "TAG";
    Button signUpButton;
    FirebaseAuth fbAuth;
    FirebaseFirestore fbStore;
    EditText inpEmail, inpFullName, inpWestminsterID, inpPassword;

    RadioGroup userGroup;
    //RadioButton studentCheck, staffCheck;
    RadioButton userRadio;
    String userSelected = "";
    String emailStr = "";
    String fullnameStr = "";
    String westminsterIDStr = "";
    String passwordStr = "";
    String userID = "";

    String hasEmail = "westminster.ac.uk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        fbAuth = FirebaseAuth.getInstance();
        fbStore = FirebaseFirestore.getInstance();
        inpEmail = (EditText)findViewById(R.id.inp_email);
        inpFullName = (EditText)findViewById(R.id.inp_fullName);
        inpWestminsterID = (EditText) findViewById(R.id.inp_westminsterID);
        userGroup = (RadioGroup) findViewById(R.id.userTypeGroup);
        inpPassword = (EditText) findViewById(R.id.inp_Password);


        signUpButton =  findViewById(R.id.button_registered);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailStr = inpEmail.getText().toString();
                fullnameStr = inpFullName.getText().toString();
                westminsterIDStr = inpWestminsterID.getText().toString();
                passwordStr = inpPassword.getText().toString();
                int radioId = userGroup.getCheckedRadioButtonId();
                userRadio = findViewById(radioId);

                if (TextUtils.isEmpty(emailStr) && TextUtils.isEmpty(fullnameStr) && TextUtils.isEmpty(westminsterIDStr) && TextUtils.isEmpty(passwordStr)) {
                    Toast.makeText(registration.this, "Please enter the details correctly", Toast.LENGTH_SHORT).show();
                }
                else {
                    //checks if the email is a westminster domain
                    if(emailStr.contains(hasEmail)){
                        userSelected = userRadio.getText().toString();
                        //passes the email and password string and creates the user
                        fbAuth.createUserWithEmailAndPassword(emailStr,
                                passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    //determines the user type
                                    if (userSelected.equals("Student")) {
                                        UserRegistration type = new UserRegistration(emailStr, fullnameStr, westminsterIDStr);
                                        FirebaseDatabase.getInstance().getReference("Student")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(type);
                                    }
                                    if (userSelected.equals("Staff")) {
                                        UserRegistration type = new UserRegistration(emailStr, fullnameStr, westminsterIDStr);
                                        FirebaseDatabase.getInstance().getReference("Staff")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(type);
                                    }

                                    userID = fbAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fbStore.collection("UserDetails").document(userID);
                                    //stores the users detail in that format in the Firestore
                                    Map<String, Object> userHash = new HashMap<>();
                                    userHash.put("email", emailStr);
                                    userHash.put("fullName", fullnameStr);
                                    userHash.put("studentID", westminsterIDStr);
                                    userHash.put("userType", userSelected);

                                    documentReference.set(userHash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: Profile has been added to the database - " + userID);
                                        }
                                    });

                                    UserList user = new UserList(emailStr, fullnameStr, westminsterIDStr, userSelected);
                                    FirebaseDatabase.getInstance().getReference("UserProfile")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //sends user an email verification
                                                        fbAuth.getCurrentUser().sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(TAG, "Email sent.");
                                                                            Toast.makeText(registration.this, "Registered Successfully, please check your email to verify", Toast.LENGTH_LONG).show();
                                                                            inpEmail.setText("");
                                                                            inpFullName.setText("");
                                                                            inpWestminsterID.setText("");
                                                                            inpPassword.setText("");
                                                                        }
                                                                        else{
                                                                            Toast.makeText(registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });


                                    userSelected = "";
                                    emailStr = "";
                                    fullnameStr = "";
                                    westminsterIDStr = "";
                                    passwordStr = "";

                                } else {
                                    Toast.makeText(registration.this, "Registered not successful", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(registration.this, "Please enter a westminster domain", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
