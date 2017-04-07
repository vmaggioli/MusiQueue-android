package com.example.vince.youtubeplayertest;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.starting_activities.GettingStarted;
import com.example.vince.youtubeplayertest.Activities.users_only.SearchHub;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Sam on 4/5/2017.
 */
@RunWith(AndroidJUnit4.class)

public class voteTest {
    @Rule
    public IntentsTestRule<GettingStarted> checkHubSingletonTest = new IntentsTestRule<GettingStarted>(GettingStarted.class);
    @Test
    public void VoteTest() throws Exception {
        HubSingleton hubSingleton = HubSingleton.getInstance();
        hubSingleton.setUserID("ba6e6ecebee2c145");
        hubSingleton.setUsername("bd");
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
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        //intended(hasComponent(ViewQueueActivity.class.getName()));
        if (hubSingleton.getEntireList() != null && hubSingleton.getEntireList().size() != 0 && hubSingleton.getEntireList().get(0).getDownVotes() == 2) {
            System.out.println("Correct");
        }
        else {
            System.out.println("Maybecorrect/idkwhatrightansweris/Incorrect");
        }
    }
}
