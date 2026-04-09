package com.heallog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end navigation flow test: Home → BodyMap → Record → Home
 *
 * Requires a connected device or emulator with an empty database.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun inject() {
        hiltRule.inject()
    }

    @Test
    fun homeScreen_displaysEmptyState_onFirstLaunch() {
        // Empty DB → home shows empty state message
        composeRule.onNodeWithText("부상 기록이 없어요").assertIsDisplayed()
    }

    @Test
    fun fabClick_navigatesToBodyMapScreen() {
        // Tap the FAB on the home screen
        composeRule.onNodeWithContentDescription("부상 기록").performClick()

        // BodyMap screen should be visible
        composeRule.onNodeWithText("부위 선택").assertIsDisplayed()
    }

    @Test
    fun backFromBodyMap_returnsToHome() {
        composeRule.onNodeWithContentDescription("부상 기록").performClick()
        composeRule.onNodeWithText("부위 선택").assertIsDisplayed()

        composeRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        composeRule.onNodeWithText("부상 기록이 없어요").assertIsDisplayed()
    }
}
