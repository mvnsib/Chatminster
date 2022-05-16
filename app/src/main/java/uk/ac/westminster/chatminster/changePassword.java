package uk.ac.westminster.chatminster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePassword extends AppCompatActivity {
    Button update;
    EditText oldPW,  newPW, confirmPW;
    String oldPWStr;
    String newPWStr = "";
    String confirmPWStr = "";
    FirebaseAuth fbAuth;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Change Password");
        setSupportActionBar(toolbar);

        update = findViewById(R.id.updatePassword);
        oldPW = findViewById(R.id.oldPassword);
        newPW = findViewById(R.id.newPassword);
        confirmPW = findViewById(R.id.confirmPassword);
        fbAuth = FirebaseAuth.getInstance();
        currentUser = fbAuth.getCurrentUser().getUid();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPWStr = oldPW.getText().toString();
                newPWStr = newPW.getText().toString();
                confirmPWStr = confirmPW.getText().toString();
                //checks if the old password text box is empty or not
                if(TextUtils.isEmpty(oldPWStr)){
                    oldPW.setError("Old Password is required");

                }
                //checks if the new password text box is empty or not
                else if (TextUtils.isEmpty(newPWStr)){
                    newPW.setError("New Password is required");
                }
                //checks if the confirm password text box is empty or not
                else if (TextUtils.isEmpty(confirmPWStr)){
                    confirmPW.setError("Please confirm your new password");
                }
                //checks if the password length is less than 6
                else if(oldPWStr.length() < 6 && newPWStr.length() < 6){
                    Toast.makeText(changePassword.this, "Password is too short", Toast.LENGTH_SHORT).show();
                }
                //checks if the new password matches the confirm password text box
                else if(!newPWStr.equals(confirmPWStr)){
                    Toast.makeText(changePassword.this, "Password do not match", Toast.LENGTH_SHORT).show();
                }
                //once the password match and the text boxes filled it
                else{
                    final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(fUser.getEmail(), oldPWStr);
                    //re authenticates the current logged in user
                    fUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // if the task is successful meaning that the old password match the database' password and the user confirmed the password
                            // it will notify  the user that the password has changed
                            if(task.isSuccessful()){
                                Toast.makeText(changePassword.this, "Password has changed", Toast.LENGTH_SHORT).show();
                                //this takes the current user and updates their password
                                fUser.updatePassword(newPWStr);

                                oldPW.setText("");
                                newPW.setText("");
                                confirmPW.setText("");

                            }else{
                                Toast.makeText(changePassword.this, "Password did not change", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });



    }
}
