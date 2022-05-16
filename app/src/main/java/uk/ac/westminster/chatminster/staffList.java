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
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;

public class staffList extends AppCompatActivity {

    private RecyclerView userListView;
    private DatabaseReference db;
    private String userID;
    private FirebaseAuth fAuth;

    uk.ac.westminster.chatminster.UserList userBlock;
    Query query;
    User user;
    String showUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Staff Chat");
        setSupportActionBar(toolbar);

        userListView = findViewById(R.id.userListView);
        userListView.setHasFixedSize(true);
        userListView.setLayoutManager(new LinearLayoutManager(this));
        userListView.addItemDecoration(new DividerItemDecoration(userListView.getContext(), DividerItemDecoration.VERTICAL));
    }
    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = getIntent();
        userBlock = intent.getParcelableExtra("User");
        showUserType = userBlock.getUserType();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        //this query displays the Staff List from the real-time database
        query = FirebaseDatabase.getInstance().getReference().child("Staff");
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        FirebaseRecyclerAdapter<User, UserList> fbAdapter = new FirebaseRecyclerAdapter<User, UserList>(options) {
            @Override
            protected void onBindViewHolder(UserList holder, int position, final User user) {
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
                        Intent intent = new Intent(staffList.this, privateChat.class);
                        intent.putExtra("selectedID", selectedID);
                        intent.putExtra("selectedUserName", selectedUserName);
                        startActivity(intent);
                        //Toast.makeText(tutorStudentChat.this, selectedID ,Toast.LENGTH_SHORT).show();

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


