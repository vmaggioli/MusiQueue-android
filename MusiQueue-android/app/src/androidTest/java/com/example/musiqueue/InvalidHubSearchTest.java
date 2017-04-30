package com.example.musiqueue;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.example.musiqueue.Activities.starting_activities.GettingStarted;
import com.example.musiqueue.Activities.users_only.SearchHub;
import com.example.musiqueue.HelperClasses.HubSingleton;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.lessThan;

public class InvalidHubSearchTest {
    @Rule
    public IntentsTestRule<GettingStarted> checkInvalidHubSearchTest = new IntentsTestRule<>(GettingStarted.class);
    @Test
    public void invalidHubSearchTest() throws Exception {
        HubSingleton hubSingleton = HubSingleton.getInstance();
        hubSingleton.setUserID("ba6e6ecebee2c145");
        hubSingleton.setUsername("bd");
        onView(withId(R.id.join_hub_button))
                .perform(click());
        intended(hasComponent(SearchHub.class.getName()));
        onView(withId(R.id.hub_name_search))
                .perform(typeText("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"), closeSoftKeyboard());
        intended(hasComponent(SearchHub.class.getName()));
        onView(withId(R.id.hub_name_search_button))
                .perform(click());
        intended(hasComponent(SearchHub.class.getName()));
        onView(withId(R.id.hub_name_search))
                .check(matches(withText("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz")));

        onView(withId(R.id.hub_name_search))
                .perform(clearText());
        onView(withId(R.id.hub_name_search))
                .perform(typeText("hello OR 1=1"));
        onView(withId(R.id.hubs_list))
                .check(new RecyclerViewItemCountAssertion(lessThan(30)));

        onView(withId(R.id.hub_name_search))
                .perform(clearText());
        onView(withId(R.id.hub_name_search))
                .perform(typeText("\"or\"\"=\""));
        onView(withId(R.id.hubs_list))
                .check(new RecyclerViewItemCountAssertion(lessThan(30)));

        onView(withId(R.id.hub_name_search))
                .perform(clearText());
        onView(withId(R.id.hub_name_search))
                .perform(typeText("hello; DROP TABLE Songs"));
        onView(withId(R.id.hubs_list))
                .check(new RecyclerViewItemCountAssertion(lessThan(1)));

        onView(withId(R.id.hub_name_search))
                .perform(clearText());
        onView(withId(R.id.hub_name_search))
                .perform(typeText("%$result%"));
        onView(withId(R.id.hubs_list))
                .check(new RecyclerViewItemCountAssertion(lessThan(1)));
    }
}
