package com.fauzimaulana.warungku.login

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.fauzimaulana.warungku.R
import org.junit.Before
import org.junit.Test

class LoginActivityTest {
    @Before
    fun setUp() {
        ActivityScenario.launch(LoginActivity::class.java)
    }

    @Test
    fun loginUser() {
        onView(withId(R.id.shopAnimation)).check(matches(isDisplayed()))
        onView(withId(R.id.emailEditText)).perform(typeText("fauzimaulana.id@gmail.com"))
        onView(withId(R.id.emailEditText)).check(matches(withText("fauzimaulana.id@gmail.com")))
        onView(withId(R.id.passwordEditText)).perform(typeText("@123321"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())
    }
}