package com.example.wanted.chatplus;
import java.util.Date;

public class Group {
    private String GroupName;
    private String GroupDescription;
    private String GroupMembers;


    public Group(String groupname, String groupdesc,String phone) {
        this.GroupName = groupname;
        this.GroupDescription = groupdesc;
        this.GroupMembers=phone;


    }

    public Group(){

    }



    public String getGroupname() {
        return GroupName;
    }

    public void setGroupname(String groupname) {
        this.GroupName = groupname;
    }

    public String getGroupdesc() {
        return GroupDescription;
    }

    public void setGroupdesc(String groupdesc) {
        this.GroupDescription = groupdesc;
    }

    public String getGroupMembers() {
        return GroupMembers;
    }

    public void setGroupMembers(String groupdesc) {
        this.GroupMembers = groupdesc;
    }




}
