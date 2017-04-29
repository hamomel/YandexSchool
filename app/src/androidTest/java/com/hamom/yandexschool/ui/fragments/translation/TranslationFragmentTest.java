package com.hamom.yandexschool.ui.fragments.translation;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import com.hamom.yandexschool.R;
import com.hamom.yandexschool.ui.activities.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasHost;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;

/**
 * Created by hamom on 25.04.17.
 */
public class TranslationFragmentTest {

  @Rule
  public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

  private ViewInteraction mMainInputText;
  private ViewInteraction mClearBtn;
  private ViewInteraction mSpinnerTo;
  private ViewInteraction mSpinnerFrom;
  private ViewInteraction mSwapDirectionBtn;
  private ViewInteraction mMainInputFrame;

  private String TEST_SPINNER_TEXT_1 = "English";
  private String TEST_SPINNER_TEXT_2 = "Russian";

  @Before
  public void setUp() throws Exception {
    // set translation fragment
    onView(withId(R.id.navigation_translate)).perform(click());
    mMainInputText = onView(withId(R.id.user_input_et));
    mClearBtn = onView(withId(R.id.clear_button_ib));
    mSpinnerFrom = onView(withId(R.id.spinner_from));
    mSpinnerTo = onView(withId(R.id.spinner_to));
    mSwapDirectionBtn = onView(withId(R.id.swap_language_iv));
    mMainInputFrame = onView(withId(R.id.user_input_layout));
  }

  @Test
  public void clearBtnTest() throws Exception {
    mMainInputFrame.perform(click());
    mMainInputText.check(matches(hasFocus()));
    String TEST_STRING = "anyString";
    mMainInputText.perform(typeTextIntoFocusedView(TEST_STRING));
    mClearBtn.perform(click());
    mMainInputText.check(matches(withText("")));
  }

  @Test
  public void spinnersChooseTest() throws Exception {
    mSpinnerFrom.perform(click());
    onData(allOf(isA(String.class), is(TEST_SPINNER_TEXT_1))).perform(click());
    mSpinnerFrom.check(matches(withSpinnerText(TEST_SPINNER_TEXT_1)));

    mSpinnerTo.perform(click());
    onData(allOf(isA(String.class), is(TEST_SPINNER_TEXT_2))).perform(click());
    mSpinnerTo.check(matches(withSpinnerText(TEST_SPINNER_TEXT_2)));
  }

  @Test
  public void spinnersSwapTest() throws Exception {
    mSpinnerFrom.perform(click());
    onData(allOf(isA(String.class), is(TEST_SPINNER_TEXT_1))).perform(click());
    mSpinnerFrom.check(matches(withSpinnerText(TEST_SPINNER_TEXT_1)));

    mSpinnerTo.perform(click());
    onData(allOf(isA(String.class), is(TEST_SPINNER_TEXT_2))).perform(click());
    mSpinnerTo.check(matches(withSpinnerText(TEST_SPINNER_TEXT_2)));

    mSwapDirectionBtn.perform(click());
    mSpinnerFrom.check(matches(withSpinnerText(TEST_SPINNER_TEXT_2)));
    mSpinnerTo.check(matches(withSpinnerText(TEST_SPINNER_TEXT_1)));
  }

  @Test
  public void yandexLinkIntentTest() throws Exception {
    Intents.init();
    onView(withId(R.id.yandex_tv)).perform(click());
    intended(allOf(hasAction(equalTo(Intent.ACTION_VIEW)), hasData(hasHost("translate.yandex.ru"))));
    Intents.release();
  }
}