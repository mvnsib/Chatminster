package uk.ac.westminster.chatminster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class communityList extends AppCompatActivity {
    Query query;
    Button createCommunity;
    DatabaseReference db;
    RecyclerView communityRecycler;
    FirebaseAuth fAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_list);
        db = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Community");
        setSupportActionBar(toolbar);

        createCommunity = findViewById(R.id.btn_createCommunity);

        communityRecycler = findViewById(R.id.communityList);
        communityRecycler.setHasFixedSize(true);
        communityRecycler.setLayoutManager(new LinearLayoutManager(this));
        communityRecycler.addItemDecoration(new DividerItemDecoration(communityRecycler.getContext(), DividerItemDecoration.VERTICAL));
        //triggers the create community button
        createCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alert dialog for the user to input the community name
                AlertDialog.Builder builder =  new AlertDialog.Builder(communityList.this, R.style.AlertDialog);
                builder.setTitle("Enter the Community Name");

                final EditText communityText = new EditText(communityList.this);
                builder.setView(communityText);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String community = communityText.getText().toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(TextUtils.isEmpty(community)){
                                    Toast.makeText(communityList.this,  "Community Name Required", Toast.LENGTH_SHORT).show();
                                }
                                //this checks if the community exists
                                else if(dataSnapshot.child("Community").hasChild(community)){
                                    Toast.makeText(communityList.this,  "Community already exists", Toast.LENGTH_SHORT).show();
                                }else{
                                    //if it doesnt and the edittext isnt empty, it will create the community and pass these values
                                    Map comMap = new HashMap();
                                    comMap.put("communityName",community);
                                    comMap.put("timeCreated", ServerValue.TIMESTAMP);
                                    comMap.put("createdBy", userID);

                                    community com = new community(community, ServerValue.TIMESTAMP.toString(), userID );

                                    FirebaseDatabase.getInstance().getReference("Community")
                                            .child(community).setValue(com).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(communityList.this,  "Community has been created", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                builder.show();
            }
        });
    }

    protected void onStart() {
        super.onStart();
        //this query retrieves the community list and displays it with FirebaseRecyclerOptions
        query = FirebaseDatabase.getInstance().getReference().child("Community");
        FirebaseRecyclerOptions<community> options =
                new FirebaseRecyclerOptions.Builder<community>().setQuery(query, community.class).build();

        FirebaseRecyclerAdapter<community, communityView> fbAdapter = new FirebaseRecyclerAdapter<community, communityView>(options) {
            @Override
            protected void onBindViewHolder( communityView holder, int position,  community model) {
                holder.communityName.setText(model.getCommunityName());

                final String selectedCommunity = getRef(position).getKey();
                //if the user clicks on a community it will direct them to that selected community activity
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(communityList.this,communityChat.class);
                        intent.putExtra("selectedCommunity", selectedCommunity);
                        startActivity(intent);

                    }
                });

            }

            @Override
            public communityView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_community_list, parent, false);
                return new communityView(view);
            }
        };
        //set adapter for the recycler list
        fbAdapter.startListening();
        communityRecycler.setAdapter(fbAdapter);

    }

    public static class communityView extends RecyclerView.ViewHolder{

        View view;
        TextView communityName;
        public communityView (View userView){
            super(userView);
            view = userView;
            communityName = (TextView) view.findViewById(R.id.tv_sCommunity);

        }
    }

}

