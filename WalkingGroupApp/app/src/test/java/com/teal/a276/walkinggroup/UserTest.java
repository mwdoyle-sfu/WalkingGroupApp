package com.teal.a276.walkinggroup;

import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * JUnit Testing for the User Data Object
 */

public class UserTest extends BaseTest {
    private User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test
    public void setAndGetName() {
        user.setName("bill");
        assertEquals("bill",user.getName());
    }
    @Test
    public void getAndSetId(){
        Long l =(long) 10;
        user.setId(l);
        assertEquals(l,user.getId());
    }

    @Test
    public void getAndSetEmail() throws Exception{
        user.setEmail("asdf@test.com");
        assertEquals("asdf@test.com", user.getEmail());
    }

    @Test
    public void getAndSetPassword() throws Exception{
        user.setPassword("1234");
        assertEquals("1234", user.getPassword());
    }

    @Test
    public void testValidEmailValidation() throws Exception {
        assertEquals(User.validateEmail("valid@valid.com"), true);
    }

    @Test
    public void testInvalidEmailValidation() throws Exception {
        assertEquals(User.validateEmail("***@valid.com"), false);
        assertEquals(User.validateEmail("***"), false);
        assertEquals(User.validateEmail("valid@"), false);
        assertEquals(User.validateEmail(""), false);
        assertEquals(User.validateEmail(null), false);
    }

    @Test
    public void testInvalidPhoneNumberValidation() throws Exception {
        assertEquals(User.validateEmail("604iii5555"), false);
        assertEquals(User.validateEmail("***unnnnn"), false);
        assertEquals(User.validateEmail("iii8888888"), false);
        assertEquals(User.validateEmail(""), false);
        assertEquals(User.validateEmail(null), false);
    }

    @Test
    public void testValidPhoneNumberValidation() throws Exception {
        assertEquals(User.validateEmail("6048888888"), true);
        assertEquals(User.validateEmail("(604) 888-8888"), true);
    }

    @Test
    public void testGetSetHref() throws Exception {
        user.setHref("/1234");
        assertEquals(user.getHref(), "/1234");

        user.setHref("");
        assertEquals(user.getHref(), "");

        user.setHref(null);
        assertEquals(user.getHref(), null);
    }

    @Test
    public void testGetSetMonitoredByUsers() {
        List<User> users = generateUserList();
        user.setMonitoredByUsers(users);
        assertEquals(user.getMonitoredByUsers(), users);

        user.setMonitoredByUsers(null);
        assertEquals(user.getMonitoredByUsers(), null);
    }

    @Test
    public void testGetSetMonitorUsers() {
        List<User> users = generateUserList();
        user.setMonitorsUsers(users);
        assertEquals(user.getMonitorsUsers(), users);

        user.setMonitorsUsers(null);
        assertEquals(user.getMonitorsUsers(), null);
    }

    @Test
    public void testGetSetMemberOfGroups() {
        List<Group> groups = generateGroupList();
        user.setMemberOfGroups(groups);
        assertEquals(user.getMemberOfGroups(), groups);

        user.setMonitorsUsers(null);
        assertEquals(user.getMonitorsUsers(), null);
    }

    @Test
    public void testGetSetLeadsGroups() {
        List<Group> groups = generateGroupList();
        user.setLeadsGroups(groups);
        assertEquals(user.getLeadsGroups(), groups);

        user.setMonitorsUsers(null);
        assertEquals(user.getMonitorsUsers(), null);
    }


    private List<Group> generateGroupList() {
        Group group = generateGroup("TestGroup");
        ArrayList<Group> groups = new ArrayList<>();
        groups.add(group);

        return groups;
    }


    @Test
    public void testCopyUser() throws Exception {
        User newUser = new User("Name", "test@test.com", "password");
        newUser.setId(1234L);
        newUser.setHref("/1234");
        newUser.setBirthMonth(4);
        newUser.setBirthYear(1998);
        newUser.setAddress("42 Towel St");
        newUser.setCellPhone("888-888-8888");
        newUser.setHomePhone("888-888-8888");
        newUser.setGrade("4");
        newUser.setTeacherName("Mrs. Lizzie");
        newUser.setEmergencyContactInfo("oh no, call my mom");

        List<Group> groups = generateGroupList();
        newUser.setLeadsGroups(groups);
        newUser.setMemberOfGroups(groups);

        List<User> users = generateUserList();
        newUser.setMonitorsUsers(users);
        newUser.setMonitoredByUsers(users);

        user.copyUser(newUser);

        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getHref(), newUser.getHref());
        assertEquals(user.getId(), newUser.getId());
        assertEquals(user.getPassword(), newUser.getPassword());
        assertEquals(user.getLeadsGroups(), newUser.getLeadsGroups());
        assertEquals(user.getMemberOfGroups(), newUser.getMemberOfGroups());
        assertEquals(user.getMonitorsUsers(), newUser.getMonitorsUsers());
        assertEquals(user.getMonitoredByUsers(), newUser.getMonitoredByUsers());
        assertEquals(user.getBirthYear(), newUser.getBirthYear());
        assertEquals(user.getBirthMonth(), newUser.getBirthMonth());
        assertEquals(user.getAddress(), newUser.getAddress());
        assertEquals(user.getCellPhone(), newUser.getCellPhone());
        assertEquals(user.getHomePhone(), newUser.getHomePhone());
        assertEquals(user.getGrade(), newUser.getGrade());
        assertEquals(user.getTeacherName(), newUser.getTeacherName());
        assertEquals(user.getEmergencyContactInfo(), newUser.getEmergencyContactInfo());
    }

    @Test
    public void updateGroupTest() throws Exception {
        user.getMemberOfGroups().add(generateGroup("TestGroup 1"));

        Group testGroup = generateGroup("UpdatedGroup");
        user.updateExistingGroup(testGroup);
        assertTrue(user.getMemberOfGroups().contains(testGroup));
    }

    private Group generateGroup(String groupDescription) {
        Group group = new Group();
        group.setGroupDescription(groupDescription);
        group.setId(2L);

        List<User> users = generateUserList();
        group.setMemberUsers(users);

        return group;
    }

    //empty tests
    @Test
    public void setAndGetNameEmpty() throws Exception{
        user.setName("");
        assertEquals("",user.getName());
    }

    @Test
    public void getAndSetEmailEmpty() throws Exception{
        user.setEmail("");
        assertEquals("", user.getEmail());
    }

    @Test
    public void getAndSetPasswordEmpty() throws Exception{
        user.setPassword("");
        assertEquals("", user.getPassword());
    }


    @Test (expected = IllegalArgumentException.class)
    public void updateGroupTestEmptyGroups() throws Exception {
        Group testGroup = generateGroup("UpdatedGroup");
        user.updateExistingGroup(testGroup);
    }

    //null Tests
    @Test
    public void setAndGetNameNull() throws Exception{
        assertEquals(null,user.getName());
    }

    @Test
    public void getAndSetIdNull() throws Exception {
        assertEquals(null,user.getId());
    }

    @Test
    public void getAndSetEmailNull() throws Exception{
        assertEquals(null, user.getEmail());
    }

    @Test
    public void getAndSetPasswordNull() throws Exception{
        assertEquals(null, user.getPassword());
    }

    //testing Constructors
    @Test
    public void testBaseConstructor() throws Exception{
        User user = new User();
        assertEquals(null,user.getName());
        assertEquals(null,user.getPassword());
        assertEquals(null,user.getEmail());
        assertEquals(null,user.getHref());
        assertEquals(null,user.getId());
    }

    @Test
    public void testConstructor() throws Exception{
        User user = new User("Bob Lee", "email@email.com", "Password1234");
        assertEquals("Bob Lee",user.getName());
        assertEquals("Password1234",user.getPassword());
        assertEquals("email@email.com",user.getEmail());
        assertEquals(null,user.getHref());
        assertEquals(null,user.getId());
    }

    @Test
    public void testNotEqual() {
        User user = new User("Bob Lee", "email@email.com", "Password1234");
        user.setId(1L);
        User userTwo = new User("Lee Bob", "email1@email.com", "Password12345");
        userTwo.setId(2L);
        assertFalse(user.equals(userTwo));
    }

    @Test
    public void testEqual() {
        User user = new User("Bob Lee", "email@email.com", "Password1234");
        User userTwo = new User("Bob Lee", "email@email.com", "Password1234");
        user.setId(1L);
        userTwo.setId(1L);
        assertTrue(user.equals(userTwo));
    }

    @Test
    public void getAndSetCurrentPoints() throws Exception{
        Integer points = 4000;
        user.setCurrentPoints(points);
        assertEquals(points, user.getCurrentPoints());
    }

    @Test
    public void getAndSetTotalPointsEarned() throws Exception{
        Integer totalPoints = 100000;
        user.setTotalPointsEarned(totalPoints);
        assertEquals(totalPoints, user.getTotalPointsEarned());
    }
}
