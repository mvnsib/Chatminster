package uk.ac.westminster.chatminster;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ChatViewHolder> {
    private List<message> chatFeed;
    private FirebaseAuth fAuth =  FirebaseAuth.getInstance();

    public chatAdapter(List<message> chatFeed) {
        this.chatFeed = chatFeed;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText, timeText, messageUser, timeUser;

        public ChatViewHolder(View view) {
            super(view);
            messageText = (TextView) view.findViewById(R.id.chatMessage);
            timeText = (TextView) view.findViewById(R.id.chatTime);

            messageUser = (TextView) view.findViewById(R.id.Message);
            timeUser = (TextView) view.findViewById(R.id.Time);
        }
    }


    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receiver_bubble, parent, false);

        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        message bubble = chatFeed.get(position);
        String userID = fAuth.getCurrentUser().getUid();
        // if message is sent from the current logged in user
        // it will display their message in a yellow bubble
        // and on the right hand side of the display
        String fromUser = bubble.getFrom();
        if(fromUser.equals(userID)){
            //this hides the left hand side bubble which is visible when the receiver replies

            holder.messageText.setBackgroundColor(Color.YELLOW);
            holder.messageText.setVisibility(View.INVISIBLE);
            holder.timeText.setVisibility(View.INVISIBLE);

            holder.messageUser.setVisibility(View.VISIBLE);
            holder.timeUser.setVisibility(View.VISIBLE);
            holder.messageUser.setText(bubble.getMessage());
            holder.timeUser.setText(getDate(bubble.getTimeSent()));

        }else{
            //displayed in grey bubble
            holder.messageText.setBackgroundResource(R.drawable.bubble);
            holder.messageText.setVisibility(View.VISIBLE);
            holder.timeText.setVisibility(View.VISIBLE);

            holder.messageUser.setVisibility(View.INVISIBLE);
            holder.timeUser.setVisibility(View.INVISIBLE);

            holder.messageText.setText(bubble.getMessage());
            holder.timeText.setText(getDate(bubble.getTimeSent()));
        }

    }
    //this is for formatting the date underneath the bubble
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm , dd/MM/yyyy", cal).toString();
        return date;
    }


    @Override
    public int getItemCount() {
        return chatFeed.size();
    }
}


