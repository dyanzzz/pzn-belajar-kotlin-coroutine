package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SupervisorJobTest {
    @Test
    fun testJob() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        // secara default, coroutineScope, sudah menambahkan otomatis Job dari coroutineScope
        // seperti ini
        val scope = CoroutineScope(dispatcher + Job())

        // job 1 delay 2 detik, dan menunggu job 2 selesai terlebih dulu, baru job 1 selesai
        // tapi, karena job 2 throw exception, maka job 1 tidak akan dieksekusi
        // coroutine job 1 ikut dibatalkan karena job 2 error throw exception
        val job1 = scope.launch {
            delay(2000)
            println("Job 1 done")
        }

        // coroutine dibatalkan karena throw exception
        val job2 = scope.launch {
            delay(1000)
            throw IllegalArgumentException("Job 2 Failed")
        }

        runBlocking {
            joinAll(job1, job2)
        }
    }

    @Test
    fun testSupervisorJob() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        // menggunakan supervisorJob
        // agar ketika salah 1 dari coroutine childnya ada yg throw exception
        // suvervisorJob tsb membuat coroutine tsb secara independent/mandiri/bebas, tanpa mempengaruhi coroutine lain
        // jika ada error pada salah satu coroutine, maka coroutine yg sukses, akan jalan terus
        val scope = CoroutineScope(dispatcher + SupervisorJob())

        // job 1 tidak dibatalkan, karena menggunakan supervisorJob()
        val job1 = scope.launch {
            delay(2000)
            println("Job 1 done")
        }

        // coroutine dibatalkan karena throw exception
        val job2 = scope.launch {
            delay(1000)
            throw IllegalArgumentException("Job 2 Failed")
        }

        runBlocking {
            joinAll(job1, job2)
        }
    }

    // supervisorScope
    @Test
    fun testSupervisorScopeFunction() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()

        // scopenya bukan bertipe superviserJob
        val scope = CoroutineScope(dispatcher + Job())

        runBlocking {
            scope.launch {
                // menggunakan supervisorScope
                // jika ada 1 coroutine yg error, maka coroutine lain tidak akan ikut error
                // masing" coroutine akan mandiri/ independent/ bebas, tidak terpengaruh oleh exception pada coroutine lain
                supervisorScope {
                    launch {
                        delay(2000)
                        println("Child 1 Done")
                    }
                    launch {
                        delay(1000)
                        throw IllegalArgumentException("Child 2 Error")
                    }
                }
            }

            delay(3000)
        }
    }

    // testJobExceptionHandle yg salah
    @Test
    fun testJobExceptionHandle() {
        // exceptionHandler hanya bisa dilakukan di parent, tidak bisa di child coroutine
        val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
            println("Error : ${throwable.message}")
        }

        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        runBlocking {
            val job = scope.launch {
                // jika menggunakan exception handler di childnya,
                // exceptionHandler tidak akan digunakan
                // karena child coroutine akan selalu mengeskalasi / melempar ke parentnya ketika terjadi error throw exception
                // maka exception handler tidak bisa digunakan child si job coroutine
                launch(exceptionHandler) {
                    println("Job child")
                    throw IllegalArgumentException("Child Error")
                }
            }

            job.join()
        }
    }

    // testJobExceptionHandler yg benar menggunakan supervisorScope
    @Test
    fun testSupervisorJobExceptionHandle() {
        // exceptionHandler hanya bisa dilakukan di parent, tidak bisa di child coroutine
        val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
            println("Error : ${throwable.message}")
        }

        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        runBlocking {
            val job = scope.launch {
                supervisorScope {
                    // menggunakan supervisorScope akan membuat child coroutine bisa melakukan exception
                    // tanpa harus melempar throw exception ke parentnya, karena sudah menggunakan supervisorScope
                    // tapi, jika coroutine child ini mempunyai child coroutine lagi
                    // exceptionHandler tetap harus di parentnya, tidak bisa menggunakan exception handler ini di childnya coroutine child dibawahnya
                    launch(exceptionHandler) {
                        println("Job child")
                        // throw exception akan dilempar ke exceptionHandler yg sudah di custom
                        throw IllegalArgumentException("Child Error")
                    }

                    // contoh membuat exceptionHandler yg tidak jalan
                    /*
                    launch {
                        launch(exceptionHandler) {
                            println("Job child")
                            // throw exception akan dilempar ke exceptionHandler yg sudah di custom
                            throw IllegalArgumentException("Child Error")
                        }
                    }
                    */
                }
            }

            job.join()
        }
    }
}