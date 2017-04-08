package com.example.vince.youtubeplayertest;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.users_only.SearchHub;
import com.example.vince.youtubeplayertest.Activities.users_only.ViewQueueActivity;

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
 * Created by Brian on 3/24/2017.
 */

@RunWith(AndroidJUnit4.class)

public class JoinHubPinCorrectTest {
    @Rule
    public IntentsTestRule<SearchHub> checkCorrectJoin = new IntentsTestRule<SearchHub>(SearchHub.class);

    @Test
    public void correctJoinTest() throws Exception {
        HubSingleton hubSingleton = HubSingleton.getInstance();
        if (hubSingleton.getUserID() == null) hubSingleton.setUserID("ba6e6ecebee2c145");

        onView(withId(R.id.hub_name_search))
                .perform(typeText("friday"), closeSoftKeyboard());
        onView(withId(R.id.hub_name_search_button))
                .perform(click());
        onView(withId(R.id.hubs_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        intended(hasComponent(ViewQueueActivity.class.getName()));
    }

}
