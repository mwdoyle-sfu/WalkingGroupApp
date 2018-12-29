package com.teal.a276.walkinggroup;

import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GroupTest extends BaseTest {

    Group group;

    @Before
    public void setUp() {
        group = new Group();
    }

    @Test
    public void testGetSetGroupDescription() {
        String des = "TestGroupDes";
        group.setGroupDescription(des);
        assertEquals(des, group.getGroupDescription());

        group.setGroupDescription("");
        assertEquals("", group.getGroupDescription());

        group.setGroupDescription(null);
        assertEquals(null, group.getGroupDescription());
    }

    @Test
    public void getAndSetId(){
        Long id = 10L;
        group.setId(id);
        assertEquals(id, group.getId());

        group.setId(null);
        assertEquals(null, group.getId());
    }

    @Test
    public void getAndSetLeader() throws Exception {
        User user = new User("test", "test@test.com", "password");
        group.setLeader(user);
        assertEquals(user, group.getLeader());

        group.setLeader(null);
        assertEquals(null, group.getLeader());
    }

    @Test
    public void testGetSetHref() throws Exception {
        String href = "/1234";
        group.setHref(href);
        Assert.assertEquals(href, group.getHref());

        group.setHref("");
        Assert.assertEquals("", group.getHref());

        group.setHref(null);
        Assert.assertEquals(null, group.getHref());
    }

    @Test
    public void testGetSetLatArray() {
        Double[] coordinates = new Double[] {1.23, 4.56, 7.11};
        List<Double> coordinatesList = Arrays.asList(coordinates);
        group.setRouteLatArray(coordinatesList);
        assertEquals(coordinatesList, group.getRouteLatArray());

        group.setRouteLatArray(null);
        Assert.assertEquals(null, group.getRouteLatArray());
    }

    @Test
    public void testGetSetLngArray() {
        Double[] coordinates = new Double[] {1.23, 4.56, 7.11};
        List<Double> coordinatesList = Arrays.asList(coordinates);
        group.setRouteLngArray(coordinatesList);
        assertEquals(coordinatesList, group.getRouteLngArray());

        group.setRouteLngArray(null);
        Assert.assertEquals(null, group.getRouteLngArray());
    }

    @Test
    public void testGetSetMemberOfGroups() {
        List<User> users = generateUserList();
        group.setMemberUsers(users);
        assertEquals(users, group.getMemberUsers());

        group.setMemberUsers(null);
        assertEquals(null, group.getMemberUsers());
    }
}