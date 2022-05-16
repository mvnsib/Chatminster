package uk.ac.westminster.chatminster;

public class message {
    private String message, from;
    private long timeSent;

    public message(){

    }
    public message(String from, String message, long timeSent) {
        this.from = from;
        this.message = message;
        this.timeSent = timeSent;


    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public String getFrom() {
        return from;
    }
}
