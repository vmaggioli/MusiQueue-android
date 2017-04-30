package com.example.musiqueue;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.musiqueue.Activities.starting_activities.GettingStarted;
import com.example.musiqueue.Activities.users_only.SearchHub;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class StartJoinHubTest {
    @Rule
    public IntentsTestRule<GettingStarted> testStartJoinRule = new IntentsTestRule<>(GettingStarted.class);

    @Test
    public void testStartJoin() throws Exception {
        onView(withId(R.id.join_hub_button))
                .perform(click());
        intended(hasComponent(SearchHub.class.getName()));
    }
}
