/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package imu.creative.coroutine

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class AppTest {
    @Test
    fun testAppHasAGreeting() {
        val classUnderTest = App()
        assertNotNull(classUnderTest.greeting, "app should have a greeting")
    }
}