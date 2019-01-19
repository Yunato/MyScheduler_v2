package io.github.yunato.myscheduler.ui.activity;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.ui.fragment.CalendarFragment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

public class MainDrawerActivityTest {
    @Rule
    public ActivityTestRule<MainDrawerActivity> activityRule
                                            = new ActivityTestRule<>(MainDrawerActivity.class, false ,false);

    private static final Intent MY_ACTIVITY_INTENT = new Intent(InstrumentationRegistry.getTargetContext(), MainDrawerActivity.class);

    @Before
    public void setUp() throws Exception {
        activityRule.launchActivity(MY_ACTIVITY_INTENT);
        CalendarFragment fragment = CalendarFragment.newInstance();
        activityRule.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_layout, fragment).commit();
    }

    @Test
    public void check_start_activity(){
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.main_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.button_floating_action)).check(matches(isDisplayed()));
        onView(withId(R.id.main_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_view)).check(matches(not(isDisplayed())));
    }
}
