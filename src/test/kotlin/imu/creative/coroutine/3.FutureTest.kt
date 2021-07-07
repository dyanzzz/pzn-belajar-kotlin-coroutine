package imu.creative.coroutine

import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.measureTimeMillis

// 3. membuat Future
class FutureTest {

    private fun getFoo(): Int {
        Thread.sleep(1_000)
        return 10
    }

    private fun getBar(): Int {
        Thread.sleep(1_000)
        return 10
    }

    // kode program berjalan sequencial, tidak parallel
    @Test
    fun testNonParallel(){
        // measureTimeMillis => untuk mengambil time milisecond
        val time = measureTimeMillis {
            val foo = getFoo()
            val bar = getBar()
            val result = foo + bar
            // result nilai 20 karena foo=10 + bar=10
            println("Total : $result")
        }
        // total time akan menghasilkan 2detik, karena pada masing-masing foo & bar melakukan sleep selama 1 detik
        println("Total time : $time")
    }

    private val executorService: ExecutorService = Executors.newFixedThreadPool(10)
    // berjalan paralel dan ingin mengembalikan data
    // gunakan future dan callable untuk mengembalikan data, bukan runnable
    // callable == promise == janji => yg akan mengembalikan nilai
    @Test
    fun testFuture(){
        val time = measureTimeMillis {
            val foo: Future<Int> = executorService.submit(Callable { getFoo() })
            val bar: Future<Int> = executorService.submit(Callable { getBar() })

            val result = foo.get() + bar.get()
            println("Result : $result")
        }

        println("Total time : $time")
    }
}