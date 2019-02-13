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
import io.github.yunato.myscheduler.model.repository.EventItemRepository;
import io.github.yunato.myscheduler.ui.fragment.EditPlanInfoFragment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class EditEventItemActivityTest {
    @Rule
    public ActivityTestRule<EditEventItemActivity> activityRule
            = new ActivityTestRule<>(EditEventItemActivity.class, false ,false);

    private static final Intent MY_ACTIVITY_INTENT
            = new Intent(InstrumentationRegistry.getTargetContext(), EditEventItemActivity.class);

    @Before
    public void setUp() throws Exception {
        activityRule.launchActivity(MY_ACTIVITY_INTENT);
    }

    @Test
    public void checkViewItem() throws Exception {
        EditPlanInfoFragment fragment = EditPlanInfoFragment.newInstance(EventItemRepository.createEmpty());
        activityRule.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
        onView(withId(R.id.input_text_title)).check(matches(isDisplayed()));
        onView(withId(R.id.input_text_startTime)).check(matches(isDisplayed()));
        onView(withId(R.id.input_text_endTime)).check(matches(isDisplayed()));
        onView(withId(R.id.input_text_startDate)).check(matches(isDisplayed()));
        onView(withId(R.id.input_text_endDate)).check(matches(isDisplayed()));
        onView(withId(R.id.input_text_description)).check(matches(isDisplayed()));
    }
}
