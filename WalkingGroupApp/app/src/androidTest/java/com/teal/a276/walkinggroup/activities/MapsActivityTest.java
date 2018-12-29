package com.teal.a276.walkinggroup.activities;

import android.support.test.rule.ActivityTestRule;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.map.MapsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class MapsActivityTest {

    @Rule
    public ActivityTestRule<MapsActivity> activityTestRule = new ActivityTestRule<>(MapsActivity.class);

    private MapsActivity mapsActivity = null;

    @Before
    public void setUp() throws Exception {
        mapsActivity = activityTestRule.getActivity();
    }

    @Test
    public void testVisibility() {
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        mapsActivity = null;
    }
}