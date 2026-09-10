@file:Suppress("ktlint:standard:max-line-length")

package aragones.sergio.readercollection.data.local

import aragones.sergio.readercollection.data.local.model.AuthData
import aragones.sergio.readercollection.data.local.model.UserData
import com.aragones.sergio.util.Preferences
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

class SharedPreferencesHandlerTest {

    private val appInfoProvider: AppInfoProvider = mockk()
    private val sharedPreferencesProvider: SharedPreferencesProvider = mockk()
    private val json = Json { ignoreUnknownKeys = true }
    private val preferences =
        SharedPreferencesHandler(appInfoProvider, sharedPreferencesProvider, json)

    @Test
    fun `GIVEN language in preferences WHEN get language THEN return value from preferences`() {
        val expectedLanguage = "en"
        every {
            sharedPreferencesProvider.readString(Preferences.LANGUAGE_PREFERENCE_NAME)
        } returns expectedLanguage

        val result = preferences.language

        assertEquals(expectedLanguage, result)
        verify(
            exactly = 1,
        ) { sharedPreferencesProvider.readString(Preferences.LANGUAGE_PREFERENCE_NAME) }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN no language in preferences WHEN get language THEN return default value and save it`() {
        val defaultLanguage = "en"
        every { appInfoProvider.getCurrentLanguage() } returns defaultLanguage
        every {
            sharedPreferencesProvider.readString(
                Preferences.LANGUAGE_PREFERENCE_NAME,
            )
        } returns null
        every {
            sharedPreferencesProvider.writeString(Preferences.LANGUAGE_PREFERENCE_NAME, any())
        } just Runs

        val result = preferences.language

        assertEquals(defaultLanguage, result)
        verify(exactly = 1) { appInfoProvider.getCurrentLanguage() }
        verify(
            exactly = 1,
        ) { sharedPreferencesProvider.readString(Preferences.LANGUAGE_PREFERENCE_NAME) }
        verify(exactly = 1) {
            sharedPreferencesProvider.writeString(
                Preferences.LANGUAGE_PREFERENCE_NAME,
                defaultLanguage,
            )
        }
        confirmVerified(appInfoProvider, sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN value WHEN set language THEN save value in preferences`() {
        val newLanguage = "en"
        every {
            sharedPreferencesProvider.writeString(Preferences.LANGUAGE_PREFERENCE_NAME, any())
        } just Runs

        preferences.language = newLanguage

        verify(exactly = 1) {
            sharedPreferencesProvider.writeString(Preferences.LANGUAGE_PREFERENCE_NAME, newLanguage)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN credentials in preferences WHEN get credentials THEN return value from preferences`() {
        val expectedCredentials = AuthData(uuid = "testUserId")
        val jsonCredentials =
            """
            {"token":"testUserId"}
            """.trimIndent()
        every {
            sharedPreferencesProvider.readString(Preferences.AUTH_DATA_PREFERENCES_NAME, true)
        } returns jsonCredentials

        val result = preferences.credentials

        assertEquals(expectedCredentials, result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readString(Preferences.AUTH_DATA_PREFERENCES_NAME, true)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN malformed credentials in preferences WHEN get credentials THEN return value from preferences with known params`() {
        val expectedCredentials = AuthData(uuid = "testUserId")
        val jsonCredentials =
            """
            {"token":"testUserId","otherField":true}
            """.trimIndent()
        every {
            sharedPreferencesProvider.readString(Preferences.AUTH_DATA_PREFERENCES_NAME, true)
        } returns jsonCredentials

        val result = preferences.credentials

        assertEquals(expectedCredentials, result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readString(Preferences.AUTH_DATA_PREFERENCES_NAME, true)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN no credentials in preferences WHEN get credentials THEN return empty value`() {
        every {
            sharedPreferencesProvider.readString(Preferences.AUTH_DATA_PREFERENCES_NAME, true)
        } returns null

        val result = preferences.credentials

        assertEquals(AuthData(""), result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readString(Preferences.AUTH_DATA_PREFERENCES_NAME, true)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN value WHEN set credentials THEN save value in preferences`() {
        val credentials = AuthData(uuid = "testUserId")
        val jsonCredentials =
            """
            {"token":"testUserId"}
            """.trimIndent()
        every {
            sharedPreferencesProvider.writeString(
                Preferences.AUTH_DATA_PREFERENCES_NAME,
                any(),
                true,
            )
        } just Runs

        preferences.credentials = credentials

        verify(exactly = 1) {
            sharedPreferencesProvider.writeString(
                Preferences.AUTH_DATA_PREFERENCES_NAME,
                jsonCredentials,
                true,
            )
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN userData in preferences WHEN get userData THEN return value from preferences`() {
        val expectedUserData = UserData("testUser", "testPassword", "")
        val jsonUserData =
            """
            {"username":"testUser","email":"testPassword","password":""}
            """.trimIndent()
        every {
            sharedPreferencesProvider.readString(Preferences.USER_DATA_PREFERENCES_NAME, true)
        } returns jsonUserData

        val result = preferences.userData

        assertEquals(expectedUserData, result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readString(Preferences.USER_DATA_PREFERENCES_NAME, true)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN malformed userData in preferences WHEN get userData THEN return value from preferences with known params`() {
        val expectedUserData = UserData("testUser", "testPassword", "")
        val jsonUserData =
            """
            {"username":"testUser","email":"testPassword","password":"","isLoggedIn":true}
            """.trimIndent()
        every {
            sharedPreferencesProvider.readString(Preferences.USER_DATA_PREFERENCES_NAME, true)
        } returns jsonUserData

        val result = preferences.userData

        assertEquals(expectedUserData, result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readString(Preferences.USER_DATA_PREFERENCES_NAME, true)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN no userData in preferences WHEN get userData THEN return empty value`() {
        every {
            sharedPreferencesProvider.readString(Preferences.USER_DATA_PREFERENCES_NAME, true)
        } returns null

        val result = preferences.userData

        assertEquals(UserData("", "", ""), result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readString(Preferences.USER_DATA_PREFERENCES_NAME, true)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN value WHEN set userData THEN save value in preferences`() {
        val userData = UserData("testUser", "testPassword", "")
        val jsonUserData =
            """
            {"username":"testUser","email":"testPassword","password":""}
            """.trimIndent()
        every {
            sharedPreferencesProvider.writeString(
                Preferences.USER_DATA_PREFERENCES_NAME,
                any(),
                true,
            )
        } just Runs

        preferences.userData = userData

        verify(exactly = 1) {
            sharedPreferencesProvider.writeString(
                Preferences.USER_DATA_PREFERENCES_NAME,
                jsonUserData,
                true,
            )
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN credentials with uuid WHEN check isLoggedIn THEN return true`() {
        val jsonCredentials =
            """
            {"token":"testUserId"}
            """.trimIndent()
        every {
            sharedPreferencesProvider.readString(Preferences.AUTH_DATA_PREFERENCES_NAME, true)
        } returns jsonCredentials

        val result = preferences.isLoggedIn

        assertEquals(true, result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readString(Preferences.AUTH_DATA_PREFERENCES_NAME, true)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN credentials without uuid WHEN check isLoggedIn THEN return false`() {
        val jsonCredentials =
            """
            {"token":""}
            """.trimIndent()
        every {
            sharedPreferencesProvider.readString(Preferences.AUTH_DATA_PREFERENCES_NAME, true)
        } returns jsonCredentials

        val result = preferences.isLoggedIn

        assertEquals(false, result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readString(Preferences.AUTH_DATA_PREFERENCES_NAME, true)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN isProfilePublic in preferences WHEN get isProfilePublic THEN return value from preferences`() {
        every {
            sharedPreferencesProvider.readBoolean(Preferences.PUBLIC_PROFILE_PREFERENCE_NAME, any())
        } returns true

        val result = preferences.isProfilePublic

        assertEquals(true, result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readBoolean(Preferences.PUBLIC_PROFILE_PREFERENCE_NAME, false)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN value WHEN set isProfilePublic THEN save value in preferences`() {
        val newValue = true
        every {
            sharedPreferencesProvider.writeBoolean(
                Preferences.PUBLIC_PROFILE_PREFERENCE_NAME,
                any(),
            )
        } just Runs

        preferences.isProfilePublic = newValue

        verify(exactly = 1) {
            sharedPreferencesProvider.writeBoolean(
                Preferences.PUBLIC_PROFILE_PREFERENCE_NAME,
                newValue,
            )
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN isAutomaticSyncEnabled in preferences WHEN get isAutomaticSyncEnabled THEN return value from preferences`() {
        every {
            sharedPreferencesProvider.readBoolean(Preferences.AUTOMATIC_SYNC_PREFERENCE_NAME, any())
        } returns true

        val result = preferences.isAutomaticSyncEnabled

        assertEquals(true, result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readBoolean(Preferences.AUTOMATIC_SYNC_PREFERENCE_NAME, true)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN value WHEN set isAutomaticSyncEnabled THEN save value in preferences`() {
        val newValue = true
        every {
            sharedPreferencesProvider.writeBoolean(
                Preferences.AUTOMATIC_SYNC_PREFERENCE_NAME,
                any(),
            )
        } just Runs

        preferences.isAutomaticSyncEnabled = newValue

        verify(exactly = 1) {
            sharedPreferencesProvider.writeBoolean(
                Preferences.AUTOMATIC_SYNC_PREFERENCE_NAME,
                newValue,
            )
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN sortParam in preferences WHEN get sortParam THEN return value from preferences`() {
        val expectedSortParam = "title"
        every {
            sharedPreferencesProvider.readString(Preferences.SORT_PARAM_PREFERENCE_NAME)
        } returns expectedSortParam

        val result = preferences.sortParam

        assertEquals(expectedSortParam, result)
        verify(
            exactly = 1,
        ) { sharedPreferencesProvider.readString(Preferences.SORT_PARAM_PREFERENCE_NAME) }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN value WHEN set sortParam THEN save value in preferences`() {
        val newValue = "author"
        every {
            sharedPreferencesProvider.writeString(Preferences.SORT_PARAM_PREFERENCE_NAME, any())
        } just Runs

        preferences.sortParam = newValue

        verify(exactly = 1) {
            sharedPreferencesProvider.writeString(Preferences.SORT_PARAM_PREFERENCE_NAME, newValue)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN themeMode in preferences WHEN get themeMode THEN return value from preferences`() {
        val expectedThemeMode = 1
        every {
            sharedPreferencesProvider.readInt(Preferences.THEME_MODE_PREFERENCE_NAME, any())
        } returns expectedThemeMode

        val result = preferences.themeMode

        assertEquals(expectedThemeMode, result)
        verify(
            exactly = 1,
        ) { sharedPreferencesProvider.readInt(Preferences.THEME_MODE_PREFERENCE_NAME, 0) }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN value WHEN set themeMode THEN save value in preferences`() {
        val newValue = 2
        every {
            sharedPreferencesProvider.writeInt(Preferences.THEME_MODE_PREFERENCE_NAME, any())
        } just Runs

        preferences.themeMode = newValue

        verify(exactly = 1) {
            sharedPreferencesProvider.writeInt(Preferences.THEME_MODE_PREFERENCE_NAME, newValue)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN isSortDescending in preferences WHEN get isSortDescending THEN return value from preferences`() {
        every {
            sharedPreferencesProvider.readBoolean(Preferences.SORT_ORDER_PREFERENCE_NAME, any())
        } returns true

        val result = preferences.isSortDescending

        assertEquals(true, result)
        verify(exactly = 1) {
            sharedPreferencesProvider.readBoolean(Preferences.SORT_ORDER_PREFERENCE_NAME, false)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `GIVEN value WHEN set isSortDescending THEN save value in preferences`() {
        val newValue = true
        every {
            sharedPreferencesProvider.writeBoolean(Preferences.SORT_ORDER_PREFERENCE_NAME, any())
        } just Runs

        preferences.isSortDescending = newValue

        verify(exactly = 1) {
            sharedPreferencesProvider.writeBoolean(Preferences.SORT_ORDER_PREFERENCE_NAME, newValue)
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `WHEN removeCredentials is called THEN preferences are invoked to remove data`() {
        val keys = listOf(Preferences.AUTH_DATA_PREFERENCES_NAME)
        every { sharedPreferencesProvider.removeValues(keys, true) } just Runs

        preferences.removeCredentials()

        verify(exactly = 1) { sharedPreferencesProvider.removeValues(keys, true) }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `WHEN storePassword is called THEN update userData with new password`() {
        val newPassword = "newPassword"
        val jsonOldUserData =
            """
            {"username":"testUser","email":"email","password":"oldPassword"}
            """.trimIndent()
        val jsonNewUserData =
            """
            {"username":"testUser","email":"email","password":"newPassword"}
            """.trimIndent()

        every {
            sharedPreferencesProvider.readString(Preferences.USER_DATA_PREFERENCES_NAME, true)
        } returns jsonOldUserData
        every {
            sharedPreferencesProvider.writeString(
                Preferences.USER_DATA_PREFERENCES_NAME,
                any(),
                true,
            )
        } just Runs

        preferences.storePassword(newPassword)

        verify(exactly = 1) {
            sharedPreferencesProvider.readString(Preferences.USER_DATA_PREFERENCES_NAME, true)
        }
        verify(exactly = 1) {
            sharedPreferencesProvider.writeString(
                Preferences.USER_DATA_PREFERENCES_NAME,
                jsonNewUserData,
                true,
            )
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `WHEN removePassword is called THEN update userData with empty password`() {
        val jsonOldUserData =
            """
            {"username":"testUser","email":"email","password":"oldPassword"}
            """.trimIndent()
        val jsonNewUserData =
            """
            {"username":"testUser","email":"email","password":""}
            """.trimIndent()

        every {
            sharedPreferencesProvider.readString(Preferences.USER_DATA_PREFERENCES_NAME, true)
        } returns jsonOldUserData
        every {
            sharedPreferencesProvider.writeString(
                Preferences.USER_DATA_PREFERENCES_NAME,
                any(),
                true,
            )
        } just Runs

        preferences.removePassword()

        verify(exactly = 1) {
            sharedPreferencesProvider.readString(Preferences.USER_DATA_PREFERENCES_NAME, true)
        }
        verify(exactly = 1) {
            sharedPreferencesProvider.writeString(
                Preferences.USER_DATA_PREFERENCES_NAME,
                jsonNewUserData,
                true,
            )
        }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `WHEN removeUserData is called THEN preferences are invoked to remove data`() {
        val keys = listOf(Preferences.USER_DATA_PREFERENCES_NAME)
        every { sharedPreferencesProvider.removeValues(keys, true) } just Runs

        preferences.removeUserData()

        verify(exactly = 1) { sharedPreferencesProvider.removeValues(keys, true) }
        confirmVerified(sharedPreferencesProvider)
    }

    @Test
    fun `WHEN removeUserPreferences is called THEN preferences are invoked to remove data`() {
        val keys = listOf(
            Preferences.LANGUAGE_PREFERENCE_NAME,
            Preferences.PUBLIC_PROFILE_PREFERENCE_NAME,
            Preferences.AUTOMATIC_SYNC_PREFERENCE_NAME,
            Preferences.SORT_PARAM_PREFERENCE_NAME,
            Preferences.THEME_MODE_PREFERENCE_NAME,
            Preferences.SORT_ORDER_PREFERENCE_NAME,
        )
        every { sharedPreferencesProvider.removeValues(keys, false) } just Runs

        preferences.removeUserPreferences()

        verify(exactly = 1) { sharedPreferencesProvider.removeValues(keys, false) }
        confirmVerified(sharedPreferencesProvider)
    }
}
