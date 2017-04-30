package com.example.musiqueue;

import android.provider.Settings;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.musiqueue.Activities.users_only.JoinHub;
import com.example.musiqueue.Activities.users_only.SearchHub;
import com.example.musiqueue.HelperClasses.HubSingleton;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)

public class JoinHubPinIncorrectTest {
    @Rule
    public IntentsTestRule<SearchHub> checkCorrectJoin = new IntentsTestRule<>(SearchHub.class);

    @Test
    public void correctJoinTest() throws Exception {
        HubSingleton hubSingleton = HubSingleton.getInstance();
        if (hubSingleton.getUserID() == null) hubSingleton.setUserID(Settings.Secure.getString(checkCorrectJoin.getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));

        onView(withId(R.id.hub_name_search))
                .perform(typeText("test"), closeSoftKeyboard());
        onView(withId(R.id.hub_name_search_button))
                .perform(click());
        onView(withId(R.id.hubs_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(22,click()));
        onView(withId(R.id.pass_pin))
                .perform(typeText("4327"), closeSoftKeyboard());
        onView(withId(R.id.join_hub_button))
                .perform(click());
        onView(withText("HUB_PIN_WRONG - The pin provided for this hub is not correct.")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
        intended(hasComponent(JoinHub.class.getName()));

    }

}
