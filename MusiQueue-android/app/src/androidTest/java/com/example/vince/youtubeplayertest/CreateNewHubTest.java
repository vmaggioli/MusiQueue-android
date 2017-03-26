package com.example.vince.youtubeplayertest;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.vince.youtubeplayertest.Activities.hub_admin_only.CreateHub;
import com.example.vince.youtubeplayertest.Activities.starting_activities.GettingStarted;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Brian on 3/23/2017.
 */
@RunWith(AndroidJUnit4.class)

public class CreateNewHubTest {
    @Rule
    public IntentsTestRule<CreateHub> createValidHubTest = new IntentsTestRule<CreateHub>(CreateHub.class);

    @Test
    public void createHubTest() throws Exception {
        onView(withId(R.id.hub_name))
            .perform(typeText("uniqueName"), closeSoftKeyboard());
        onView(withId(R.id.pass_pin))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.create_hub_button))
                .perform(click());
        intended(hasComponent(GettingStarted.class.getName()));
    }
}
