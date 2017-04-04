package com.example.vince.youtubeplayertest;

import android.provider.Settings;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.starting_activities.GettingStarted;
import com.example.vince.youtubeplayertest.Activities.users_only.SearchHub;
import com.example.vince.youtubeplayertest.Activities.users_only.ViewQueueActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Brian on 4/4/2017.
 */
@RunWith(AndroidJUnit4.class)


public class HubSingletonTest  {
    @Rule
    public IntentsTestRule<GettingStarted> checkHubSingletonTest = new IntentsTestRule<GettingStarted>(GettingStarted.class);
    @Test
    public void hubSingletonTest() throws Exception {
        onView(withId(R.id.join_hub_button))
                .perform(click());
        intended(hasComponent(SearchHub.class.getName()));
        try {
            synchronized (this) {
                this.wait(3000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.hubs_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        intended(hasComponent(ViewQueueActivity.class.getName()));
        HubSingleton hubSingleton = HubSingleton.getInstance();
        if (hubSingleton.getUserID() == null) hubSingleton.setUserID(Settings.Secure.getString(checkHubSingletonTest.getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        if (hubSingleton.getEntireList() == null) {
            System.out.println("List Is Null");
            double i = 0/10;
        } else if (hubSingleton.getEntireList().size() > 0 && hubSingleton.getSongAt(0) == null) {
            System.out.println("Corrupted List");
            double i = 0/10;
        } else if (hubSingleton.getHubId() <= 0) {
            System.out.println("Corrupted HubId");
            double i = 0/10;
        } else if (hubSingleton.getHubName() == null || hubSingleton.getHubName().length() <= 0) {
            System.out.println("Corrupted Hub Name");
            double i = 0/10;
//        } else if (hubSingleton.getPassPin().length() != 4) {
//            System.out.println("Corrupted Hub Pin");
//            double i = 0/10;
        } else if (hubSingleton.getUserID() == null || !hubSingleton.getUserID().equals(Settings.Secure.getString(checkHubSingletonTest.getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID))) {
            System.out.println("User Id null or Incorrect");
            double i = 0/10;
        } else if (hubSingleton.getUsername() == null) {
            System.out.println("Hub Creator Name Not Recorded");
            double i = 0/10;
        }
    }
}
