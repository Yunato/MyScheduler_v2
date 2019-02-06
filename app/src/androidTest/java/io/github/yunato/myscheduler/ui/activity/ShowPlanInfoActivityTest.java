package io.github.yunato.myscheduler.ui.activity;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.EventInfo;
import io.github.yunato.myscheduler.ui.fragment.ShowPlanFragment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ShowPlanInfoActivityTest {
    @Rule
    public ActivityTestRule<ShowPlanInfoActivity> activityRule
            = new ActivityTestRule<>(ShowPlanInfoActivity.class, false ,false);

    private static final Intent MY_ACTIVITY_INTENT
            = new Intent(InstrumentationRegistry.getTargetContext(), ShowPlanInfoActivity.class);

    @Before
    public void setUp() throws Exception {
        activityRule.launchActivity(MY_ACTIVITY_INTENT);
    }

    @Test
    public void checkViewItem() throws Exception {
        ShowPlanFragment fragment = ShowPlanFragment.newInstance(EventInfo.createEmptyEventItem());
        activityRule.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
        onView(withId(R.id.text_title)).check(matches(isDisplayed()));
        onView(withId(R.id.text_startMillis)).check(matches(isDisplayed()));
        onView(withId(R.id.text_endMillis)).check(matches(isDisplayed()));
        onView(withId(R.id.text_description)).check(matches(isDisplayed()));
    }
}
