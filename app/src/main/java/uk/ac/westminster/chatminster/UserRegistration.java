package uk.ac.westminster.chatminster;

public class UserRegistration {
    public String email, fullname, studentID;

    public UserRegistration(){

    }

    public UserRegistration(String email, String fullname, String studentID){
        this.email = email;
        this.fullname = fullname;
        this.studentID = studentID;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setFullname(String fullname){
        this.fullname = fullname;
    }
    public void setStudentID(String studentID){
        this.studentID = studentID;
    }
    public String getEmail(){
        return this.email;
    }
    public String getFullname(){
        return  this.fullname;
    }
    public String getStudentID() {
        return this.studentID;
    }
}
