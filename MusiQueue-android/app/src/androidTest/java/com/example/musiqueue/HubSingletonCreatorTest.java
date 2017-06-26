package com.example.musiqueue;

import android.provider.Settings;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.example.musiqueue.Activities.hub_admin_only.CreateHub;
import com.example.musiqueue.Activities.hub_admin_only.QueueActivity;
import com.example.musiqueue.Activities.starting_activities.GettingStarted;
import com.example.musiqueue.HelperClasses.HubSingleton;

import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class HubSingletonCreatorTest {

    @Rule
    public IntentsTestRule<GettingStarted> checkHubSingletonTest = new IntentsTestRule<>(GettingStarted.class);
    @Test
    public void hubSingletonTest() throws Exception {
        onView(withId(R.id.create_hub_button))
                .perform(click());
        intended(hasComponent(CreateHub.class.getName()));

        onView(withId(R.id.hub_name))
                .perform(typeText("abcdef" + String.valueOf(Calendar.getInstance().get(Calendar.SECOND))), closeSoftKeyboard());
        onView(withId(R.id.pass_pin))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.create_hub_button))
                .perform(click());

        intended(hasComponent(QueueActivity.class.getName()));
        HubSingleton hubSingleton = HubSingleton.getInstance();
        if (hubSingleton.getUserID() == null || hubSingleton.getUsername() == null) {
            hubSingleton.setUserID(Settings.Secure.getString(checkHubSingletonTest.getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
            hubSingleton.setUsername("Richard");
        }
        if (hubSingleton.getEntireList() == null) {
            System.out.println("List Is Null");
        } else if (hubSingleton.getEntireList().size() > 0 && hubSingleton.getSongAt(0) == null) {
            System.out.println("Corrupted List");
        } else if (hubSingleton.getHubId() <= 0) {
            System.out.println("Corrupted HubId");
        } else if (hubSingleton.getHubName() == null || hubSingleton.getHubName().length() <= 0) {
            System.out.println("Corrupted Hub Name");
//        } else if (hubSingleton.getPassPin().length() != 4) {
//            System.out.println("Corrupted Hub Pin");
        } else if (hubSingleton.getUserID() == null || !hubSingleton.getUserID().equals(Settings.Secure.getString(checkHubSingletonTest.getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID))) {
            System.out.println("User Id null or Incorrect");
        } else if (hubSingleton.getUsername() == null || hubSingleton.getUsername().length() == 0) {
            System.out.println("User Name Not Recorded");
        }
    }
}
