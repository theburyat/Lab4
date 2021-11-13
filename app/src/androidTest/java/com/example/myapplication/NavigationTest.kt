package com.example.myapplication

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception
import java.lang.Thread.sleep

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    private fun checkAbout() {
        openAbout()
        onView(withId(R.id.activity_about)).check(matches(isDisplayed()))
    }

    private fun exitAboutWithBack(fragment: Int) {
        pressBack()
        onView(withId(fragment)).check(matches(isDisplayed()))
    }

    private fun exitAboutWithUp(fragment: Int) {
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(fragment)).check(matches(isDisplayed()))
    }

    private fun jump(fragmentFrom: Int, fragmentTo: Int) {
        when (fragmentFrom to fragmentTo) {
            1 to 2 -> {
                onView(withId(R.id.bnToSecond)).perform(click())
            }
            1 to 3 -> {
                onView(withId(R.id.bnToSecond)).perform(click())
                onView(withId(R.id.bnToThird)).perform(click())
            }
            2 to 3 -> {
                onView(withId(R.id.bnToThird)).perform(click())
            }
            2 to 1 -> {
                onView(withId(R.id.bnToFirst)).perform(click())
            }
            3 to 2 -> {
                onView(withId(R.id.bnToSecond)).perform(click())
            }
            3 to 1 -> {
                onView(withId(R.id.bnToFirst)).perform(click())
            }
        }
    }

    private fun testJump(fragmentFrom: Int, fragmentTo: Int) {
        val x = when (fragmentTo) {
            1 -> R.id.fragment1
            2 -> R.id.fragment2
            3 -> R.id.fragment3
            else -> throw Exception("Bad fragment to jump")
        }
        jump(fragmentFrom, fragmentTo)
        onView(withId(x)).check(matches(isDisplayed()))
        //check about with back
        checkAbout()
        exitAboutWithBack(x)
        //check about with up
        checkAbout()
        exitAboutWithUp(x)
    }

    @Test
    fun testCorrectJumps() {
        launchActivity<MainActivity>()
        //check that 1 fragment is view when open app
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))
        // jump from 1 to 2 fragment
        testJump(1, 2)
        //jump from 2 to 3
        testJump(2, 3)
        //from 3 to 1
        testJump(3, 1)
        //from 1 to 3
        testJump(1, 3)
        //from 3 to 2
        testJump(3, 2)
        //from 2 to 1
        testJump(2, 1)
    }

    @Test
    fun checkBackStackWithBack() {
        var scenario = launchActivity<MainActivity>()
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        repeat(2) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        repeat(3) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        jump(2, 3)
        repeat(3) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        jump(2, 1)
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        jump(3, 2)
        repeat(2) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        jump(3, 1)
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)
    }

    @Test
    fun checkBackStackWithAboutBack() {
        var scenario = launchActivity<MainActivity>()
        openAbout()
        repeat(2) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        openAbout()
        repeat(3) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        openAbout()
        repeat(4) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        jump(2, 3)
        openAbout()
        repeat(4) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        jump(2, 1)
        openAbout()
        repeat(2) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        jump(3, 2)
        openAbout()
        repeat(3) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        jump(3, 1)
        openAbout()
        repeat(2) { pressBackUnconditionally() }
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)
    }

    @Test
    fun checkBackStackWithUp() {
        var scenario = launchActivity<MainActivity>()
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        repeat(2) {
            onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
                .perform(click())
        }
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        jump(2, 3)
        repeat(2) {
            onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
                .perform(click())
        }
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        jump(3, 2)
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)
    }

    @Test
    fun checkBackStackWithUpAbout() {
        var scenario = launchActivity<MainActivity>()
        openAbout()
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        openAbout()
        repeat(2) {
            onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
                .perform(click())
        }
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        openAbout()
        repeat(3) {
            onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
                .perform(click())
        }
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        jump(2, 3)
        openAbout()
        repeat(3) {
            onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
                .perform(click())
        }
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        jump(3, 2)
        openAbout()
        repeat(2) {
            onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
                .perform(click())
        }
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 2)
        jump(2, 1)
        openAbout()
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)

        scenario = launchActivity()
        jump(1, 3)
        jump(3, 1)
        openAbout()
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        pressBackUnconditionally()
        assertEquals(scenario.state, Lifecycle.State.DESTROYED)
    }

    @Test
    fun checkBack() {
        launchActivity<MainActivity>()
        jump(1,2)
        openAbout()
        pressBack()
        onView(withId(R.id.fragment2)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))

        launchActivity<MainActivity>()
        jump(1,3)
        openAbout()
        pressBack()
        onView(withId(R.id.fragment3)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.fragment2)).check(matches(isDisplayed()))

        launchActivity<MainActivity>()
        jump(1,2)
        jump(2,3)
        openAbout()
        pressBack()
        onView(withId(R.id.fragment3)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.fragment2)).check(matches(isDisplayed()))

        launchActivity<MainActivity>()
        jump(1,2)
        jump(2,1)
        openAbout()
        pressBack()
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))

        launchActivity<MainActivity>()
        jump(1,3)
        jump(3,2)
        openAbout()
        pressBack()
        onView(withId(R.id.fragment2)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))

        launchActivity<MainActivity>()
        jump(1,3)
        jump(3,1)
        openAbout()
        pressBack()
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))
    }

    @Test
    fun checkUp() {
        launchActivity<MainActivity>()
        jump(1,2)
        openAbout()
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(R.id.fragment2)).check(matches(isDisplayed()))
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))

        launchActivity<MainActivity>()
        jump(1,3)
        openAbout()
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(R.id.fragment3)).check(matches(isDisplayed()))
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(R.id.fragment2)).check(matches(isDisplayed()))

        launchActivity<MainActivity>()
        jump(1,2)
        jump(2,3)
        openAbout()
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(R.id.fragment3)).check(matches(isDisplayed()))
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(R.id.fragment2)).check(matches(isDisplayed()))

        launchActivity<MainActivity>()
        jump(1,2)
        jump(2,1)
        openAbout()
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))

        launchActivity<MainActivity>()
        jump(1,3)
        jump(3,2)
        openAbout()
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(R.id.fragment2)).check(matches(isDisplayed()))
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))

        launchActivity<MainActivity>()
        jump(1,3)
        jump(3,1)
        openAbout()
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))
    }

    @Test
    fun checkRecreate() {
        var scenario = launchActivity<MainActivity>()

        // check 1 fragment when open about and change screen orientation
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToSecond)).check(matches(isDisplayed()))
        openAbout()
        onView(withId(R.id.activity_about)).check(matches(isDisplayed()))
        onView(withId(R.id.tvAbout)).check(matches(isDisplayed()))
        scenario.onActivity { activity ->
            activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
        }
        sleep(1000)
        onView(withId(R.id.activity_about)).check(matches(isDisplayed()))
        onView(withId(R.id.tvAbout)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToSecond)).check(matches(isDisplayed()))

        // check 2 fragment when open about and change screen orientation
        scenario = launchActivity()
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToSecond)).check(matches(isDisplayed()))
        jump(1,2)
        onView(withId(R.id.fragment2)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToFirst)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToThird)).check(matches(isDisplayed()))
        openAbout()
        onView(withId(R.id.activity_about)).check(matches(isDisplayed()))
        onView(withId(R.id.tvAbout)).check(matches(isDisplayed()))
        scenario.onActivity { activity ->
            activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
        }
        sleep(1000)
        onView(withId(R.id.activity_about)).check(matches(isDisplayed()))
        onView(withId(R.id.tvAbout)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment2)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToFirst)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToThird)).check(matches(isDisplayed()))

        // check 3 fragment when open about and change screen orientation
        scenario = launchActivity()
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment1)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToSecond)).check(matches(isDisplayed()))
        jump(1,3)
        onView(withId(R.id.fragment3)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToFirst)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToSecond)).check(matches(isDisplayed()))
        openAbout()
        onView(withId(R.id.activity_about)).check(matches(isDisplayed()))
        onView(withId(R.id.tvAbout)).check(matches(isDisplayed()))
        scenario.onActivity { activity ->
            activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
        }
        sleep(1000)
        onView(withId(R.id.activity_about)).check(matches(isDisplayed()))
        onView(withId(R.id.tvAbout)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment3)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToFirst)).check(matches(isDisplayed()))
        onView(withId(R.id.bnToSecond)).check(matches(isDisplayed()))
    }
}