package imu.creative.coroutine

import kotlinx.coroutines.*
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

    // membuat parent child coroutine
    // saat membuat coroutine child, otomatis kita mewarisi coroutine context yg ada di coroutine parent
    // dan coroutine parent akan menunggu sampai coroutine child selesai semua
    @Test
    fun testParentChild() {
        runBlocking {
            val job = GlobalScope.launch {
                launch {
                    delay(2000)
                    println("Child 1 : ${Thread.currentThread().name}")
                }
                launch {
                    delay(4000)
                    println("Child 2 : ${Thread.currentThread().name}")
                }
                delay(1000)
                println("Parent Done : ${Thread.currentThread().name}")
            }

            job.join()
        }
    }

    @Test
    fun testParentChildCancel() {
        runBlocking {
            val job = GlobalScope.launch {
                launch {
                    delay(2000)
                    println("Child 1 : ${Thread.currentThread().name}")
                }
                launch {
                    delay(4000)
                    println("Child 2 : ${Thread.currentThread().name}")
                }
                delay(1000)
                println("Parent Done : ${Thread.currentThread().name}")
            }

            // job.children    // untuk mendapatkan coroutine childnya
            job.cancelChildren()    // untuk membatalkan semua coroutine childrennya
            job.join()
        }
    }
}