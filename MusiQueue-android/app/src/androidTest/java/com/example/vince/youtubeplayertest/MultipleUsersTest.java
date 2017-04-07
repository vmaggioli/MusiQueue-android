package com.example.vince.youtubeplayertest;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.vince.youtubeplayertest.Activities.SearchActivity;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.hub_admin_only.CreateHub;
import com.example.vince.youtubeplayertest.Activities.hub_admin_only.QueueActivity;
import com.example.vince.youtubeplayertest.Activities.starting_activities.GettingStarted;
import com.example.vince.youtubeplayertest.Activities.users_only.JoinHub;
import com.example.vince.youtubeplayertest.Activities.users_only.ViewQueueActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Created by Brian on 4/5/2017.
 */
@RunWith(AndroidJUnit4.class)

public class MultipleUsersTest {
    @Rule
    public IntentsTestRule<GettingStarted> testMultipleUsersRule = new IntentsTestRule<GettingStarted>(GettingStarted.class);

    @Test
    public void multipleUsersTest() throws Exception {
        HubSingleton hubSingleton = HubSingleton.getInstance();
        hubSingleton.setUserID("ba6e6ecebee2c145");
        hubSingleton.setUsername("bd");
        onView(withId(R.id.create_hub_button))
                .perform(click());
        intended(hasComponent(CreateHub.class.getName()));
        String hub = String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND));
        onView(withId(R.id.hub_name))
                .perform(typeText("a;lskdfj" + hub), closeSoftKeyboard());
        onView(withId(R.id.pass_pin))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.create_hub_button))
                .perform(click());
        intended(hasComponent(QueueActivity.class.getName()));
        onView(withId(R.id.search_edit))
                .perform(typeText("shortest video"), closeSoftKeyboard());
        onView(withId(R.id.search_button))
                .perform(click());

        Thread.sleep(2000);
        onData(anything()).inAdapterView(withId(R.id.videos_found)).atPosition(0)
                .perform(click());

        Intent intent = new Intent(testMultipleUsersRule.getActivity().getApplicationContext(), GettingStarted.class);
        testMultipleUsersRule.getActivity().startActivity(intent);
        intended(hasComponent(GettingStarted.class.getName()));

        hubSingleton.setUserID("de873375dc0515f2");
        hubSingleton.setUsername("skuhns");

        onView(withId(R.id.join_hub_button))
                .perform(click());
        onView(withId(R.id.hub_name_search))
                .perform(typeText("a;lskdfj" + hub), closeSoftKeyboard());
        onView(withId(R.id.hub_name_search_button))
                .perform(click());
        onView(withId(R.id.hubs_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(hasComponent(JoinHub.class.getName()));

        onView(withId(R.id.pass_pin))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.join_hub_button))
                .perform(click());
        intended(hasComponent(ViewQueueActivity.class.getName()));

        onView(withId(R.id.search_button))
                .perform(click());
        intended(hasComponent(SearchActivity.class.getName()));

        onView(withId(R.id.search_input))
                .perform(typeText("shortest video"), closeSoftKeyboard(), ViewActions.pressImeActionButton());

        Thread.sleep(2000);
        onData(anything()).inAdapterView(withId(R.id.videos_found)).atPosition(1)
                .perform(click());

        testMultipleUsersRule.getActivity().startActivity(new Intent(testMultipleUsersRule.getActivity().getApplicationContext(), GettingStarted.class));

        hubSingleton = HubSingleton.getInstance();
        hubSingleton.setUserID("ba6e6ecebee2c145");
        hubSingleton.setUsername("bd");

        onView(withId(R.id.join_hub_button))
                .perform(click());
        onView(withId(R.id.hub_name_search))
                .perform(typeText("a;lskdfj" + hub), closeSoftKeyboard());
        onView(withId(R.id.hub_name_search_button))
                .perform(click());
        onView(withId(R.id.hubs_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(7000);

        intent = new Intent(testMultipleUsersRule.getActivity().getApplicationContext(), GettingStarted.class);
        testMultipleUsersRule.getActivity().startActivity(intent);

        hubSingleton.setUserID("de873375dc0515f2");
        hubSingleton.setUsername("skuhns");

        onView(withId(R.id.join_hub_button))
                .perform(click());
        onView(withId(R.id.hub_name_search))
                .perform(typeText("a;lskdfj" + hub), closeSoftKeyboard());
        onView(withId(R.id.hub_name_search_button))
                .perform(click());
        onView(withId(R.id.hubs_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }
}
