package imu.creative.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class LockingTest {

    @Test
    fun testRestCondition() {
        // problem ketika kita menggunakan muttable data dan di sharing ke beberapa coroutine sekaligus, mengakibatkan restCondition
        var counter: Int = 0
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        repeat(100) {
            scope.launch {
                repeat(1000) {
                    counter++
                }
            }
        }

        runBlocking {
            delay(5000)
            println("Total Counter : $counter") // total tidak akan sampai 100_000
            // karena terjadi restCondition / ada beberapa data yg jalan secara paralell
            // dan melewatkan increment counter, yg membuat counter tidak sampai 100_000
        }
    }

    // test menggunakan mutex
    // jika kita ingin mengamankan data mutable(var) untuk di sharing ke beberapa coroutine sekaligus, bisa menggunakan mutex
    // mutex hanya 1 coroutine yg diperbolehkan mengakses coroutine secara bersamaan
    // proses disini yaitu => mengakses coroutine secara bersamaan
    @Test
    fun testMutex() {
        var counter: Int = 0
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        val mutex = Mutex()

        repeat(100) {
            scope.launch {
                repeat(1000) {
                    // ketika menggunakan .withMutex, coroutine yg digunakan untuk counter, hanya 1 coroutine yg dapat mengakses increment counter++,
                    // dan tidak akan berebut untuk melakukan increment
                    mutex.withLock {
                        counter++
                    }
                }
            }
        }

        runBlocking {
            delay(5000)
            println("Total Counter : $counter")
        }
    }

    // test semaphore
    // fungsinya sama seperti mutex sebagai object untuk locking
    // bedanya, mutex hanya bisa menggunakan 1 coroutine
    // dan semaphore, bisa menggunakan beberapa coroutine yg disetup dengan permits untuk melakukan proses
    // proses disini yaitu => mengakses coroutine secara bersamaan
    @Test
    fun testSemaphore() {
        var counter: Int = 0
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        val semaphore = Semaphore(permits = 2)  // semakin jauh di setup permitsnya

        repeat(100) {
            scope.launch {
                repeat(1000) {
                    // ketika menggunakan .withMutex, coroutine yg digunakan untuk counter, hanya 1 coroutine yg dapat mengakses increment counter++,
                    // dan tidak akan berebut untuk melakukan increment
                    semaphore.withPermit {
                        counter++
                    }
                }
            }
        }

        runBlocking {
            delay(5000)
            println("Total Counter : $counter")
        }
    }
}