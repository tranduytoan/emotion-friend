package com.emotionfriend.feature.forms

import com.emotionfriend.domain.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FormValidationTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `email validation accepts standard email format`() {
        assertTrue("Should accept valid email", isValidEmail("user@example.com"))
        assertTrue("Should accept subdomain email", isValidEmail("user@mail.example.com"))
        assertTrue("Should accept plus addressing", isValidEmail("user+tag@example.com"))
    }

    @Test
    fun `email validation rejects invalid formats`() {
        assertFalse("Should reject no @", isValidEmail("userexample.com"))
        assertFalse("Should reject no domain", isValidEmail("user@"))
        assertFalse("Should reject no local part", isValidEmail("@example.com"))
        assertFalse("Should reject spaces", isValidEmail("user @example.com"))
        assertFalse("Should reject empty", isValidEmail(""))
    }

    @Test
    fun `password validation enforces minimum length`() {
        assertFalse("Too short", isValidPassword("12345"))
        assertTrue("Minimum 6", isValidPassword("123456"))
        assertTrue("Longer", isValidPassword("password123"))
    }

    @Test
    fun `password validation with special characters`() {
        assertTrue("With special chars", isValidPassword("pass@123"))
        assertTrue("With numbers", isValidPassword("password123"))
        assertTrue("With uppercase", isValidPassword("Password123"))
    }

    @Test
    fun `display name validation allows reasonable names`() {
        assertTrue("Simple name", isValidDisplayName("John"))
        assertTrue("With space", isValidDisplayName("John Doe"))
        assertTrue("With apostrophe", isValidDisplayName("O'Brien"))
        assertTrue("With hyphen", isValidDisplayName("Mary-Jane"))
    }

    @Test
    fun `display name validation rejects invalid names`() {
        assertFalse("Empty", isValidDisplayName(""))
        assertFalse("Too long", isValidDisplayName("A".repeat(100)))
        assertFalse("Only spaces", isValidDisplayName("   "))
    }

    @Test
    fun `confirm password matches original`() {
        val password = "securepass123"
        assertTrue("Should match", passwordsMatch(password, password))
        assertFalse("Should not match", passwordsMatch(password, "differentpass"))
    }

    @Test
    fun `password strength indicator`() {
        val weak = getPasswordStrength("123")
        val medium = getPasswordStrength("password123")
        val strong = getPasswordStrength("P@ssw0rd!Secure")

        assertTrue("Weak strength", weak < medium)
        assertTrue("Medium < strong", medium < strong)
    }

    @Test
    fun `form state transitions for registration`() = runTest {
        val validator = FormValidator()

        // Initially empty
        assertFalse("Not valid initially", validator.isFormValid())

        // Add email
        validator.setEmail("test@example.com")
        assertFalse("Only email not sufficient", validator.isFormValid())

        // Add password
        validator.setPassword("password123")
        assertFalse("Email + password not sufficient", validator.isFormValid())

        // Add display name
        validator.setDisplayName("Test User")
        assertFalse("Missing confirm password", validator.isFormValid())

        // Confirm password
        validator.setConfirmPassword("password123")
        assertTrue("All fields filled", validator.isFormValid())

        // Wrong confirm password
        validator.setConfirmPassword("wrongpassword")
        assertFalse("Passwords don't match", validator.isFormValid())
    }

    @Test
    fun `form error messages are descriptive`() = runTest {
        val validator = FormValidator()

        validator.setEmail("invalidemail")
        val emailError = validator.getEmailError()
        assertNotNull("Should have email error", emailError)

        validator.setPassword("123")
        val passwordError = validator.getPasswordError()
        assertNotNull("Should have password error", passwordError)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LoginFormTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var form: LoginForm

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        form = LoginForm()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty form is not valid`() {
        assertFalse("Empty form invalid", form.isValid())
    }

    @Test
    fun `form requires both email and password`() {
        form.email = "test@example.com"
        assertFalse("Email only insufficient", form.isValid())

        form.password = "password"
        assertTrue("Email + password valid", form.isValid())
    }

    @Test
    fun `email field can be cleared`() {
        form.email = "test@example.com"
        assertTrue("With email", form.isValid())

        form.email = ""
        assertFalse("Email cleared", form.isValid())
    }

    @Test
    fun `remember me preference is tracked`() {
        assertFalse("Initially false", form.rememberMe)

        form.rememberMe = true
        assertTrue("Can be toggled true", form.rememberMe)

        form.rememberMe = false
        assertFalse("Can be toggled false", form.rememberMe)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterFormTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var form: RegisterForm

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        form = RegisterForm()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `role selection defaults to child`() {
        assertEquals(UserRole.CHILD, form.selectedRole)
    }

    @Test
    fun `role can be changed to parent`() {
        form.selectedRole = UserRole.PARENT
        assertEquals(UserRole.PARENT, form.selectedRole)
    }

    @Test
    fun `all fields required for registration`() = runTest {
        form.email = "test@example.com"
        form.password = "password123"
        form.confirmPassword = "password123"
        form.displayName = "Test User"
        form.selectedRole = UserRole.CHILD

        assertTrue("All fields valid", form.isValid())
    }

    @Test
    fun `password confirmation must match`() = runTest {
        form.email = "test@example.com"
        form.password = "password123"
        form.confirmPassword = "different"
        form.displayName = "Test User"

        assertFalse("Passwords don't match", form.isValid())

        form.confirmPassword = "password123"
        assertTrue("Passwords match", form.isValid())
    }

    @Test
    fun `validation feedback for each field`() {
        form.email = "invalid"
        assertNotNull("Email error", form.emailError)

        form.password = "123"
        assertNotNull("Password error", form.passwordError)

        form.displayName = ""
        assertNotNull("Name error", form.displayNameError)
    }
}

// Validation functions

private fun isValidEmail(email: String): Boolean =
    email.isNotEmpty() && 
    email.contains("@") && 
    email.contains(".") && 
    email.indexOf("@") < email.lastIndexOf(".")

private fun isValidPassword(password: String): Boolean =
    password.length >= 6

private fun isValidDisplayName(name: String): Boolean =
    name.isNotEmpty() && 
    name.trim().isNotEmpty() && 
    name.length in 1..50

private fun passwordsMatch(password: String, confirm: String): Boolean =
    password == confirm

private fun getPasswordStrength(password: String): Int {
    var strength = 0
    if (password.length >= 6) strength++
    if (password.length >= 10) strength++
    if (password.any { it.isUpperCase() }) strength++
    if (password.any { it.isDigit() }) strength++
    if (password.any { !it.isLetterOrDigit() }) strength++
    return strength
}

// Form models

private class FormValidator {
    var email = ""
    var password = ""
    var confirmPassword = ""
    var displayName = ""

    fun setEmail(value: String) { email = value }
    fun setPassword(value: String) { password = value }
    fun setConfirmPassword(value: String) { confirmPassword = value }
    fun setDisplayName(value: String) { displayName = value }

    fun isFormValid(): Boolean =
        email.isNotEmpty() &&
        password.isNotEmpty() &&
        confirmPassword.isNotEmpty() &&
        displayName.isNotEmpty() &&
        password == confirmPassword &&
        isValidEmail(email) &&
        isValidPassword(password) &&
        isValidDisplayName(displayName)

    fun getEmailError(): String? =
        if (email.isNotEmpty() && !isValidEmail(email)) "Invalid email" else null

    fun getPasswordError(): String? =
        if (password.isNotEmpty() && !isValidPassword(password)) "Password too short" else null
}

private class LoginForm {
    var email = ""
    var password = ""
    var rememberMe = false

    fun isValid(): Boolean = email.isNotEmpty() && password.isNotEmpty()
}

private class RegisterForm {
    var email = ""
    var password = ""
    var confirmPassword = ""
    var displayName = ""
    var selectedRole = UserRole.CHILD

    val emailError: String?
        get() = if (email.isNotEmpty() && !isValidEmail(email)) "Invalid email" else null

    val passwordError: String?
        get() = if (password.isNotEmpty() && !isValidPassword(password)) "Too short" else null

    val displayNameError: String?
        get() = if (displayName.isEmpty()) "Name required" else null

    fun isValid(): Boolean =
        email.isNotEmpty() &&
        password.isNotEmpty() &&
        confirmPassword.isNotEmpty() &&
        displayName.isNotEmpty() &&
        password == confirmPassword &&
        isValidEmail(email) &&
        isValidPassword(password) &&
        isValidDisplayName(displayName)
}
