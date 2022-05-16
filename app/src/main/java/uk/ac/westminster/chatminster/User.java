package uk.ac.westminster.chatminster;

public class User {
    public String email;
    public String fullname;
    public String studentID;
    public User() {

    }
    public User(String email, String fullname, String studentID) {
        this.email = email;
        this.fullname = fullname;
        this.studentID = studentID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getStudentID() {
        return studentID;
    }
}
