package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class CoroutineScopeTest {
    @Test
    fun testScope() {
        // membuat coroutine scope sendiri
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            delay(1000)
            println("Run 1 : ${Thread.currentThread().name}")
        }

        scope.launch {
            delay(1000)
            println("Run 2 : ${Thread.currentThread().name}")
        }

        runBlocking {
            delay(2000)
            println("Done")
        }
    }

    @Test
    fun testCancel() {
        // membuat coroutine scope sendiri
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            delay(2000)
            // delay 2 detik ini akan mengakibatkan coroutine tidak dijalankan
            // karena ketika dijalankan scopenya di cancel setelah delay 1 detik
            // dan semua scope, job, dispatcher akan dibatalkan
            // maka action setelahnya tidak akan dilakukan
            println("Run 1 : ${Thread.currentThread().name}")
        }

        scope.launch {
            delay(2000)
            println("Run 2 : ${Thread.currentThread().name}")
        }

        runBlocking {
            delay(1000)
            scope.cancel()
            delay(2000)
            println("Done")
        }
    }


    // ==============================

    private suspend fun getFoo(): Int {
        delay(1000)
        println("Foo ${Thread.currentThread().name}")
        return 10
    }

    private suspend fun getBar(): Int {
        delay(1000)
        println("Bar ${Thread.currentThread().name}")
        return 10
    }

    // saat panggil getSum, sebenarnya kita membuat corountineScope
    // dan memanggil kedua function foo & bar secara coroutine dan gabugkan keduanya
    private suspend fun getSum(): Int = coroutineScope {
        val foo = async { getFoo() }
        val bar = async { getBar() }
        foo.await() + bar.await()
    }

    @Test
    fun testCoroutineScopeFunction() {
        val scope = CoroutineScope(Dispatchers.IO)
        val job = scope.launch {
            val result = getSum()
            println("Result : $result")
        }

        runBlocking {
            job.join()
        }
    }

    @Test
    fun testParentChildDispatcher() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        // menjalankan coroutine scope dengan dispatcher yg dibuat dengan executors
        val job = scope.launch {
            println("Parent scope : ${Thread.currentThread().name}")

            // scope child
            // saat membuat child scope, secara otomatis akan menggunakan dispatcher milik parentnya
            coroutineScope { 
                launch { 
                    println("Child scope ${Thread.currentThread().name}")
                }
            }
        }

        runBlocking {
            job.join()
        }
    }

    @Test
    fun testParentChildCancel() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        // menjalankan coroutine scope dengan dispatcher yg dibuat dengan executors
        val job = scope.launch {
            println("Parent scope : ${Thread.currentThread().name}")

            // scope child
            // saat membuat child scope, secara otomatis akan menggunakan dispatcher milik parentnya
            coroutineScope {
                launch {
                    delay(2000) // ketika delay, akan cancel, dan throw cancellationException
                    // jadi proses setelahnya tidak dieksekusi
                    println("Child scope ${Thread.currentThread().name}")
                }
            }
        }

        runBlocking {
            job.cancelAndJoin()
        }
    }
}