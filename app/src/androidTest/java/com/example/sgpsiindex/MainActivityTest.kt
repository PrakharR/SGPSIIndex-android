package com.example.sgpsiindex

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.example.sgpsiindex.api.Api
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class MainActivityTest {

    @get:Rule
    var mActivityRule: ActivityTestRule<MainActivity> =
        ActivityTestRule(MainActivity::class.java, true, false)
    private var server: MockWebServer? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        server = MockWebServer()
        server!!.start()

        Api.BASE_URL = server!!.url("/").toString()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        server!!.shutdown()
    }

    @Test
    @Throws(Exception::class)
    fun testErrorToastShowsOnServerError() {
        val fileName = "sample_psi_404.json"

        server!!.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(TestUtility.getStringFromFile(getInstrumentation().context, fileName))
        )

        val intent = Intent()
        mActivityRule.launchActivity(intent)

        onView(withText("ERROR"))
            .inRoot(withDecorView(not(mActivityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun test() {
        val fileName = "sample_psi_200.json"
        server!!.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(TestUtility.getStringFromFile(getInstrumentation().context, fileName))
        )

        val intent = Intent()
        mActivityRule.launchActivity(intent)

        onView(withText(mActivityRule.activity.getString(R.string.moderate))).check(matches(isDisplayed()))
    }

}