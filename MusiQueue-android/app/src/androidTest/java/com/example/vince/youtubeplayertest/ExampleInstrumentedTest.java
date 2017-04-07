package com.example.vince.youtubeplayertest;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.vince.youtubeplayertest.Activities.users_only.SearchHub;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    // the rule allows you to jump directly to a specific activity
    @Rule
    public ActivityTestRule<SearchHub> searchHubActivityTestRule = new ActivityTestRule<SearchHub>(SearchHub.class);

    @Test
    public void typeAcceptableSearch() throws Exception {

        onView(withId(R.id.hub_name_search))                    // choose view to manipulate (search box)
                .perform(typeText("friday"), closeSoftKeyboard()); // type "hub" into search bar and close keyboard

        onView(withId(R.id.hub_name_search_button))             // choose view to manipulate (search button)
                .perform(click());                              // click on search button

        onView(withId(R.id.hubs_list))                          // choose view to manipulate (recycler view)
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));   // click third item on list
    }

}
