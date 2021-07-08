package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

class ExceptionHandlingTest {
    @Test
    fun testExceptionLaunch() {
        runBlocking {
            val job = GlobalScope.launch {
                println("Start coroutine")
                throw IllegalArgumentException()
            }

            // akan error throw exception IllegalArgumentException
            // artinya, saat kita running join, dan didalam coroutinenya ada exception
            // maka joinnya tidak akan di throw juga (ga ngasih tau kalo ada throw)
            // ini membuat perintah berikutnya akan tetap dijalankan meskipun ada exception di coroutinenya
            // ini merupakan default behaviournya dari .launce
            // kalo ada exception, join, ga dikasih tau oleh coroutine yg mendapatkan exception
            job.join()
            println("Finish")
        }
    }

    // menggunakan async pada exception
    // ketika menggunakan async, async akan otomatis ngasih tau ketika kita manggil "await"
    // ketika coroutine mendapatkan exception
    @Test
    fun testExceptionAsync() {
        runBlocking {
            val deferred = GlobalScope.async {
                println("Start coroutine")
                throw IllegalArgumentException()
            }

            try {
                deferred.await()
            } catch (error: IllegalArgumentException) {
                println("Error")
            } finally {
                println("Finish")
            }
        }
    }

    @Test
    fun testExceptionHandler() {
        val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
            println("Ups, error \"${throwable.message}\"")
        }

        // memberikan exceptionHandler pada saat kita bikin scope
        val scope = CoroutineScope(Dispatchers.IO + exceptionHandler)

        runBlocking {
            // memberikan exception handler pada saat launce
            val job1 = GlobalScope.launch(exceptionHandler) {
                println("Start coroutine job 1")
                throw IllegalArgumentException("Error job 1")
            }

            val job2 = scope.launch {
                println("Start coroutine job 2")
                throw IllegalArgumentException("Error job 2")
            }

            joinAll(job1, job2)
            println("Finish")
        }
    }
}
