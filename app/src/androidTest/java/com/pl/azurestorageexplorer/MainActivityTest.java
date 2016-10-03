package com.pl.azurestorageexplorer;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private IdlingResource idlingResource;

    @Before
    public void Setup() {
        this.idlingResource = mActivityTestRule.getActivity().getAccessTokenIdlingResource();
        Espresso.registerIdlingResources(this.idlingResource);
    }

    @After
    public void Teardown() {
        if (this.idlingResource != null) {
            Espresso.unregisterIdlingResources(this.idlingResource);
        }
    }

    @Test
    public void mainActivityTest() {
        onView(allOf(withId(android.R.id.primary), isDisplayed()));

        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        imageButton.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Tables"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Add Table"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.table_name_edit_text), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Add"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton3.perform(click());
        appCompatEditText.check(matches(hasErrorText("Table name is required")));

        ViewInteraction imageButton2 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        imageButton2.perform(click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Blobs"), isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.title), withText("Add Container"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.table_name_edit_text), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction blobContainerAddButton = onView(
                allOf(withId(android.R.id.button1), withText("Add"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        blobContainerAddButton.perform(click());
        appCompatEditText2.check(matches(hasErrorText("Container name is required")));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Add"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.add_blob),
                        withParent(allOf(withId(R.id.blobListCoordinatorLayout),
                                withParent(withId(R.id.contentFrame)))),
                        isDisplayed()));
        floatingActionButton.perform(click());

    }

}
