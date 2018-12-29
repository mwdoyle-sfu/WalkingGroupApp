package com.teal.a276.walkinggroup.model.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;


/**
 * Holds Group information
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {

    private Long id;
    private User leader;
    private String groupDescription;
    private List<Double> routeLatArray = new ArrayList<>();
    private List<Double> routeLngArray = new ArrayList<>();
    private List<User> memberUsers = new ArrayList<>();
    private String href;

//added to match Dr Brian's Json retrofit

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getLeader(){
        return leader;
    }
    public void setLeader(User user){
        this.leader = user;
    }

    public String getGroupDescription() {
        return this.groupDescription;
    }
    public void setGroupDescription(String descriptions){
        this.groupDescription = descriptions;
    }

    public List<User> getMemberUsers(){
        return memberUsers;
    }
    public void setMemberUsers(List<User> memberUsers){
        this.memberUsers = memberUsers;
    }

    public List<Double> getRouteLatArray(){
        return routeLatArray;
    }
    public void setRouteLatArray(List<Double> routeLatArray){
        this.routeLatArray = routeLatArray;
    }

    public List<Double> getRouteLngArray(){
       return routeLngArray;
    }
    public void setRouteLngArray(List<Double> routeLngArray){
        this.routeLngArray = routeLngArray;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString(){
        return "groupDescription=" + groupDescription +
               ", leader='" + leader + '\'' +

               ", routeLatArray='" + routeLatArray + '\'' +
               ", routeLngArray='" + routeLngArray + '\'' +
               ", memberUsers='" + memberUsers + '\'' +
               '}';
    }
}
