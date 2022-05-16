package uk.ac.westminster.chatminster;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    Button loginButton;
    EditText inputEmail, inputPassword;
    TextView forgotPassword, signUp;
    FirebaseAuth fbAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = (Button) findViewById(R.id.button_registered);

        inputEmail = findViewById(R.id.inp_email);
        inputPassword = findViewById(R.id.inp_Password);
        forgotPassword = findViewById(R.id.tv_ForgotPassword);
        signUp = findViewById(R.id.tv_SignUp);
        fbAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                //validation if the edit texts are empty or password is less than 6 characters
                if(TextUtils.isEmpty(email)){
                    inputEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    inputPassword.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    inputPassword.setError("Password must be at least 6 Characters");
                    return;
                }
                if(TextUtils.isEmpty(password) && TextUtils.isEmpty(email) ){
                    Toast.makeText(login.this, "Please input your credentials", Toast.LENGTH_SHORT).show();
                }
                //signs in the user
                fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //checks if the user has verified their email
                            if(fbAuth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(login.this, "Logged In", Toast.LENGTH_SHORT).show();
                                openMainMenu();
                            }
                            else {
                                Toast.makeText(login.this, "Please verify the link sent to your email", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(login.this, "Incorrect Login Details - " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEmail(v);
            }
        });



    }
    public void openRegister(){
        Intent intent = new Intent(this, registration.class);
        startActivity(intent);
    }
    public void openMainMenu(){
        Intent intent = new Intent(this, mainMenu.class);
        startActivity(intent);
        finish();
    }
    public void resetEmail(View v){
        final EditText email = new EditText(v.getContext());
        final AlertDialog.Builder resetPW = new AlertDialog.Builder(v.getContext(),R.style.AlertDialog);

        resetPW.setTitle("Reset Password?");
        resetPW.setMessage("Enter your email please");
        resetPW.setView(email);

        resetPW.setPositiveButton("submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String resetEMail = email.getText().toString();
                //Validation in case the text field is empty
                if(!TextUtils.isEmpty(resetEMail)){
                    fbAuth.sendPasswordResetEmail(resetEMail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(login.this, "A link has been sent to your email. ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(login.this, "Email is required ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        resetPW.create().show();
    }
}
