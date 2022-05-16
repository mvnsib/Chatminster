package uk.ac.westminster.chatminster;

public class community {
    public String communityName;
    public String timeCreated;
    public String createdBy;

    public community(){

    }

    public community(String communityName, String timeCreated, String createdBy) {
        this.communityName = communityName;
        this.timeCreated = timeCreated;
        this.createdBy = createdBy;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCommunityName() {
        return communityName;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
