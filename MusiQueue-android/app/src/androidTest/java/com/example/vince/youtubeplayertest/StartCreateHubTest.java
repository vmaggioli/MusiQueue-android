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
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class StartCreateHubTest {
    @Rule
    public IntentsTestRule<GettingStarted> testMainRule = new IntentsTestRule<GettingStarted>(GettingStarted.class);

    @Test
    public void testMain() throws Exception {
        onView(withId(R.id.create_hub_button))
                .perform(click());
        intended(hasComponent(CreateHub.class.getName()));
    }
}
