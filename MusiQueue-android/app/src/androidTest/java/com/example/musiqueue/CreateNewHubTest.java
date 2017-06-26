package com.example.musiqueue;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.musiqueue.Activities.hub_admin_only.CreateHub;
import com.example.musiqueue.Activities.hub_admin_only.QueueActivity;
import com.example.musiqueue.HelperClasses.HubSingleton;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)

public class CreateNewHubTest {
    @Rule
    public IntentsTestRule<CreateHub> createValidHubTest = new IntentsTestRule<>(CreateHub.class);

    @Test
    public void createHubTest() throws Exception {
        HubSingleton hubSingleton = HubSingleton.getInstance();
        hubSingleton.setUserID("ba6e6ecebee2c145");
        hubSingleton.setUsername("bd");
        onView(withId(R.id.hub_name))
            .perform(typeText("uniqueName"+ String.valueOf(Calendar.getInstance().get(Calendar.SECOND))), closeSoftKeyboard());
        onView(withId(R.id.pass_pin))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.create_hub_button))
                .perform(click());
        intended(hasComponent(QueueActivity.class.getName()));
    }
}
