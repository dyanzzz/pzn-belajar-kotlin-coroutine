package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

// suspend function merupakan sequential, dan bukan async
// karena tidak ada hubungannya menggunakan suspend function dan diakses secara async
// function suspend itu bisa ditangguhkan/disuspend, beda dengan function biasa, meskipun berjalan sama" secara sequential
class SequentialSuspendFunctionTest{
    private suspend fun getFoo(): Int{
        delay(1000)
        return 10
    }

    private suspend fun getBar(): Int{
        delay(1000)
        return(10)
    }

    @Test
    fun testSequential(){
        // kenapa menggunakan runBlocking, karena hanya untuk testing di unit test, dan menjalankan suspend function di unit test tidak bisa, maka harus menggunakan runBlocking
        // pada kenyataannya di class aplikasi kita, jangan menggunakan runBlocking, karena akan memblock thread
        runBlocking {
            val time = measureTimeMillis {
                getFoo()
                getBar()
            }
            println("Total time : $time")
        }
    }

    @Test
    fun testSequentialCoroutine(){
        runBlocking {
            // jadikan job
            // walaupun menggunakan coroutine, job akan berjalan secara sequential
            val job = GlobalScope.launch {
                val time = measureTimeMillis {
                    getFoo()
                    getBar()
                }
                println("Total time : $time")
            }
            // jobnya di join
            job.join()  // return 2 detik, karena dipanggil 1 demi 1
        }
    }

    @Test
    fun testConcurrent(){
        runBlocking {
            val time = measureTimeMillis {
                // launch tidak bisa mengembalikan value / return Unit / void
                val job1 = GlobalScope.launch { getFoo() }
                val job2 = GlobalScope.launch { getBar() }

                joinAll(job1, job2) // return 1 detik, karena dipanggil secara bersamaan dengan 2 thread dijalankan bersamaan
            }
            println("Total time : $time")
        }
    }

    // membuat yieldFunction
    @Test
    fun testYieldFunction() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        runBlocking {
            scope.launch { runJob(1) }
            scope.launch { runJob(2) }

            delay(2000)
        }
    }

    private suspend fun runJob(number: Int){
        println("Start job \"$number\" in thread \"${Thread.currentThread().name}\"")
        // memberikan kesempatan kepada coroutine lain untuk dieksekusi oleh dispatcher yg sedang kita gunakan
        // jadi bisa eksekusi coroutine secara random => bisa menggunakan yield
        yield()
        println("End job \"$number\" in thread \"${Thread.currentThread().name}\"")
    }
}