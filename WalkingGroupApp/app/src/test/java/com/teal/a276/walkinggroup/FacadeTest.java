package com.teal.a276.walkinggroup;

import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Test for model facade
 */

public class FacadeTest {

    @Test
    public void testFacadeSetValidUser() {
        User user = new User();
        user.setEmail("Test@test.com");
        ModelFacade.getInstance().setCurrentUser(user);

        User currentUser = ModelFacade.getInstance().getCurrentUser();
        assertEquals(currentUser.getEmail(), user.getEmail());
    }

    @Test(expected = IllegalStateException.class)
    public void testFacadeInvalidUser() {
        ModelFacade.getInstance().getCurrentUser();
    }
}
