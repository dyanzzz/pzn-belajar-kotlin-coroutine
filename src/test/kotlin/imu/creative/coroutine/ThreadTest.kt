package imu.creative.coroutine

import org.junit.jupiter.api.Test

class ThreadTest {

    @Test
    fun testThreadName(){
        val threadName = Thread.currentThread().name
        println("Running in thread \"$threadName\"")
    }
}