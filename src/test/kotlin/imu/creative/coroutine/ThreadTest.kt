package imu.creative.coroutine

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.concurrent.thread

class ThreadTest {

    @Test
    fun testThreadName(){
        val threadName = Thread.currentThread().name
        println("Running in thread \"$threadName\"")
    }

    // fun test ini adalah main thread
    // running thread dijalankan secara pararel
    @Test
    fun testNewThread(){
        // thread bawaan java
        /*
        // membuat runnable yg akan dijalankan oleh thread
        val runnable = Runnable{
            println("Start : ${Date()}")
            // proses selama 2 detik
            Thread.sleep(2_000)

            println("Finish : ${Date()}")
        }

        // membuat thread
        val thread = Thread(runnable)
        // menjalankan thread
        thread.start()
         */

        // thread bawaan kotlin
        thread(start = true) {
            println("Start : ${Date()}")
            // proses selama 2 detik
            Thread.sleep(2_000)

            println("Finish : ${Date()}")
        }

        println("MENUNGGU SELESAI")
        // tunggu dulu selama 3 detik
        Thread.sleep(3_000)
        println("SELESAI")
    }

    // running thread dijalankan secara pararel, berjalan masing-masing
    // bebas membuat thread sebanyak-banyaknya
    // membuat multiple thread
    @Test
    fun testMultipleThread(){
        // thread bawaan java

        // membuat runnable yg akan dijalankan oleh thread
        /*
        val runnable1 = Runnable{
            println("Start ${Thread.currentThread().name} : ${Date()}")
            // proses selama 2 detik
            Thread.sleep(2_000)

            println("Finish ${Thread.currentThread().name} : ${Date()}")
        }
        val runnable2 = Runnable{
            println("Start ${Thread.currentThread().name} : ${Date()}")
            // proses selama 2 detik
            Thread.sleep(2_000)

            println("Finish ${Thread.currentThread().name} : ${Date()}")
        }

        // membuat thread
        val thread1 = Thread(runnable1)
        val thread2 = Thread(runnable2)
        // menjalankan thread
        thread1.start()
        thread2.start()
         */

        // thread bawaan kotlin
        thread(start = true) {
            println("Start ${Thread.currentThread().name} : ${Date()}")
            // proses selama 2 detik
            Thread.sleep(2_000)

            println("Finish ${Thread.currentThread().name} : ${Date()}")
        }

        thread(start = true) {
            println("Start ${Thread.currentThread().name} : ${Date()}")
            // proses selama 2 detik
            Thread.sleep(2_000)

            println("Finish ${Thread.currentThread().name} : ${Date()}")
        }

        println("MENUNGGU SELESAI")
        // tunggu dulu selama 3 detik
        Thread.sleep(3_000)
        println("SELESAI")
    }
}