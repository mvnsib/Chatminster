package uk.ac.westminster.chatminster;

import android.os.Parcel;
import android.os.Parcelable;
//This class is used from the main menu which passes the user type to tutorStudentList
//this aids the recycler view with what user type to display
public class UserList implements Parcelable {
    public String email;
    public String fullname;
    public String studentID;
    public  String userType;

    public UserList(){

    }
    public UserList(String email, String fullname, String studentID, String userType){
        this.email = email;
        this.fullname = fullname;
        this.studentID = studentID;
        this.userType = userType;
    }

    protected UserList(Parcel in) {
        email = in.readString();
        fullname = in.readString();
        studentID = in.readString();
        userType = in.readString();
    }

    public static final Creator<UserList> CREATOR = new Creator<UserList>() {
        @Override
        public UserList createFromParcel(Parcel in) {
            return new UserList(in);
        }

        @Override
        public UserList[] newArray(int size) {
            return new UserList[size];
        }
    };

    public void setEmail(String email){
        this.email = email;
    }
    public void setFullname(String fullname){
        this.fullname = fullname;
    }
    public void setStudentID(String studentID){
        this.studentID = studentID;
    }
    public void setUserType(String userType){
        this.userType = userType;
    }

    public String getEmail(){
        return email;
    }
    public String getFName(){
        return fullname;
    }
    public String getStudentID(){
        return studentID;
    }
    public String getUserType(){
        return userType;
    }
    public String toString(){
        return this.email + " | " + this.fullname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(fullname);
        dest.writeString(studentID);
        dest.writeString(userType);
    }
}