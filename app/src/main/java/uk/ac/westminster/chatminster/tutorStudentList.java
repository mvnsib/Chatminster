package uk.ac.westminster.chatminster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class tutorStudentList extends AppCompatActivity {

    private RecyclerView userListView;
    DatabaseReference db, userDB;

    uk.ac.westminster.chatminster.UserList userBlock;
    Toolbar toolbar;
    Query query;
    User user;
    String showUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_student_chat);

        toolbar = findViewById(R.id.toolbar);

        userListView = findViewById(R.id.userListView);
        userListView.setHasFixedSize(true);
        userListView.setLayoutManager(new LinearLayoutManager(this));
        userListView.addItemDecoration(new DividerItemDecoration(userListView.getContext(), DividerItemDecoration.VERTICAL));
    }
    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = getIntent();
        //getIntent passed on from the main menu with the ParcelableExtra class
        userBlock = intent.getParcelableExtra("User");
        showUserType = userBlock.getUserType();
        //if the user is a student it will show up as Tutor Chat
        if(showUserType.equals("Staff")){
            setTitle("Tutor Chat");

        }
        //of the user is a staff title will be set as student chat
        if(showUserType.equals("Student")){
            setTitle("Student Chat");
        }
        setSupportActionBar(toolbar);
        //this query displays the user type List from the real-time database
        //this depends on the getIntent which is passed on from the main menu

        query = FirebaseDatabase.getInstance().getReference().child(showUserType);
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        FirebaseRecyclerAdapter<User, UserList> fbAdapter = new FirebaseRecyclerAdapter<User, UserList>(options) {
            @Override
            protected void onBindViewHolder(UserList holder, int position, User user) {
                //this sets the name and user ID for each block in the recycler list
                holder.tvUserID.setText(user.getStudentID());
                holder.tvFullName.setText(user.getFullname());
                //gets the position and triggers a specific recycler block if its selected
                final String selectedID = getRef(position).getKey();
                final String selectedUserName = user.getStudentID();
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //creates an intent where the receiver ID is passed onto the privateChat
                        Intent intent = new Intent(tutorStudentList.this, privateChat.class);
                        intent.putExtra("selectedID", selectedID);
                        intent.putExtra("selectedUserName", selectedUserName);
                        startActivity(intent);
                    }
                });

            }
            @Override
            public UserList onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_list, parent, false);

                return new UserList(view);
            }
        };
        fbAdapter.startListening();
        userListView.setAdapter(fbAdapter);
    }
    public static class UserList extends RecyclerView.ViewHolder{
        View view;
        TextView tvUserID, tvFullName;
        public UserList (View userView){
            super(userView);
            view = userView;
            tvUserID = (TextView) view.findViewById(R.id.tv_sCommunity);
            tvFullName = (TextView) view.findViewById(R.id.tv_sFullName);
        }
    }
}


