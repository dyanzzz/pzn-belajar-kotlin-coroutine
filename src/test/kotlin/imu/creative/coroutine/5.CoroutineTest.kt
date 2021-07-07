package imu.creative.coroutine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.concurrent.thread

class CoroutineTest {

    private suspend fun hello(){
        delay(1_000)
        println("Hello coroutine")
    }

    @Test
    fun testCoroutine(){
        GlobalScope.launch {
            hello()
        }

        println("LOADING")
        runBlocking {
            delay(2_000)
        }
        println("FINISH")
    }

    // corouting sangat ringangt
    // mencoba membandingkan dengan thread banyak biasa secara parallel
    @Test
    fun testThread(){
        repeat(100_000){
            thread {
                Thread.sleep(1000)
                println("DONE $it : ${Date()}")
            }
        }

        println("WAITING")
        Thread.sleep(10_000)
        println("FINISH")
    }

    // lebih baik menggunakan coroutine
    @Test
    fun testCoroutineMany(){
        repeat(100_000){
            GlobalScope.launch {
                delay(1_000)
                println("DONE $it : ${Date()} : ${Thread.currentThread().name}")
            }
        }

        println("WAITING")
        runBlocking {
            delay(3_000)
        }
        println("FINISH")
    }
}