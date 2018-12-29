package com.teal.a276.walkinggroup.model.dataobjects;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simple User class to store the data the server expects and returns.
 */
@SuppressWarnings("WeakerAccess")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Integer birthYear;
    private Integer birthMonth;
    private String address;
    private String cellPhone;
    private String homePhone;
    private String grade;
    private String teacherName;
    private String emergencyContactInfo;
    private Integer currentPoints = 0;
    private Integer totalPointsEarned = 0;
    private String customJson;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Group> memberOfGroups = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Group> leadsGroups = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<User> monitoredByUsers = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<User> monitorsUsers = new ArrayList<>();

    @JsonIgnore
    private UserLocation location;

    private String href;

    @JsonIgnore
    private boolean isLeader;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getEmergencyContactInfo() {
        return emergencyContactInfo;
    }

    public void setEmergencyContactInfo(String emergencyContactInfo) {
        this.emergencyContactInfo = emergencyContactInfo;
    }

    public List<User> getMonitoredByUsers() {
        return monitoredByUsers;
    }

    public void setMonitoredByUsers(List<User> monitoredByUsers) {
        this.monitoredByUsers = monitoredByUsers;
    }

    public List<User> getMonitorsUsers() {
        return monitorsUsers;
    }

    public void setMonitorsUsers(List<User> monitorsUsers) {
        this.monitorsUsers = monitorsUsers;
    }

    public List<Group> getMemberOfGroups() {
        return memberOfGroups;
    }

    public void setMemberOfGroups(List<Group> memberOfGroups) {
        this.memberOfGroups = memberOfGroups;
    }

    public List<Group> getLeadsGroups() {
        return leadsGroups;
    }

    public void setLeadsGroups(List<Group> leadsGroups) {
        this.leadsGroups = leadsGroups;
    }

    public UserLocation getLocation() {
        return location;
    }

    public void setLocation(UserLocation location) {
        this.location = location;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public Integer getTotalPointsEarned() {
        return totalPointsEarned;
    }

    public void setTotalPointsEarned(Integer totalPointsEarned) {
        this.totalPointsEarned = totalPointsEarned;
    }

    public String getCustomJson() {
        return customJson;
    }

    public void setCustomJson(String customJson) {
        this.customJson = customJson;
    }

    public void updateExistingGroup(@NonNull Group newGroup) {
        boolean updatedGroup = false;

        Group[] groupsArray = getMemberOfGroups().toArray(new Group[this.memberOfGroups.size()]);
        for(int i = 0; i < groupsArray.length; i++) {
            Group group = groupsArray[i];
            if(group.getId().equals(newGroup.getId())) {
                groupsArray[i] = newGroup;
                updatedGroup = true;
                break;
            }
        }

        if (!updatedGroup) {
            throw new IllegalArgumentException(String.format("Group %s not found in memberOfGroups %s", newGroup, memberOfGroups));
        }

        this.memberOfGroups = Arrays.asList(groupsArray);
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", currentPoints=" + currentPoints +
                ", totalPointsEarned=" + totalPointsEarned +
                ", customJson='" + customJson + '\'' +
                ", monitoredByUsers=" + monitoredByUsers + '\'' +
                ", monitorsUsers=" + monitorsUsers + '\'' +
                ", leadsGroups=" + leadsGroups + '\'' +
                "memberOfGroups=" + memberOfGroups + '\'' +
                "href=" + href + '\'' +
                '}';
    }

    public static boolean validateEmail(String email) {
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return email != null && p.matcher(email).matches();
    }
    public static boolean validatePhoneNumber(String phone){
        Pattern p = Pattern.compile("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$", Pattern.CASE_INSENSITIVE);
        return phone != null && p.matcher(phone).matches();
    }

    public void copyUser(@NonNull User user) {
        setId(user.getId());
        setName(user.getName());
        setEmail(user.getEmail());
        setPassword(user.getPassword());
        setBirthYear(user.birthYear);
        setBirthMonth(user.birthMonth);
        setAddress(user.address);
        setCellPhone(user.cellPhone);
        setHomePhone(user.homePhone);
        setGrade(user.grade);
        setTeacherName(user.teacherName);
        setEmergencyContactInfo(user.emergencyContactInfo);
        setMemberOfGroups(user.getMemberOfGroups());
        setLeadsGroups(user.getLeadsGroups());
        setMonitoredByUsers(user.getMonitoredByUsers());
        setMonitorsUsers(user.getMonitorsUsers());
        setCurrentPoints(user.getCurrentPoints());
        setTotalPointsEarned(user.getTotalPointsEarned());
        setCustomJson(user.getCustomJson());
        setHref(user.getHref());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!User.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final User other = (User) obj;
        return this.name.equals(other.name) && this.id.equals(other.id);
    }
}