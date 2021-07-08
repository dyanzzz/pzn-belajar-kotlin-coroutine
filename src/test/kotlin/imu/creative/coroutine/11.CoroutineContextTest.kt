package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class CoroutineContextTest {
    @ExperimentalStdlibApi
    @Test
    fun testCoroutineContext() {
        runBlocking {
            val job = GlobalScope.launch {
                // jika didalam job ingin mendapatkan jobnya gunakan coroutineContext
                val context: CoroutineContext = this.coroutineContext
                println(context)    // mencetak listOf coroutineContext => [id, job, coroutineDispatcher]
                println(context[Job])
                // coroutine dispatcher => digunakan untuk menentukan thread mana yg bertanggungjawab untuk mengeksekusi coroutine
                println(context[CoroutineDispatcher])

                // bisa mencancel job dirinya sendiri, karena bisa memanggil coroutineContext job
                /*
                val job: Job? = context[Job]
                job?.cancel()
                 */
            }
            job.join()
        }
    }

    // jika ingin merubah nama coroutine contextnya
    // bisa dibedakan antara coroutine parent and childnya
    @Test
    fun testCoroutineName() {
        val scope = CoroutineScope(Dispatchers.IO)

        // bisa menambahkan coroutineName() disini
        val job = scope.launch(CoroutineName("Parent")) {
            println("Parent run in thread : ${Thread.currentThread().name}")
            withContext(CoroutineName("Child")){
                println("Child run in thread : ${Thread.currentThread().name}")
            }
        }

        runBlocking {
            job.join()
        }
    }

    // jika ingin membuat coroutineName parent and childnya sama, bisa di setup di atas ketika membuat coroutineScope, dengan dispatcher
    @Test
    fun testCoroutineNameContext() {
        // bisa menambahkan coroutineName() disini
        val scope = CoroutineScope(Dispatchers.IO + CoroutineName("TestCoroutineName"))

        val job = scope.launch {
            println("Parent run in thread : ${Thread.currentThread().name}")
            withContext(Dispatchers.IO){
                println("Child run in thread : ${Thread.currentThread().name}")
            }
        }

        runBlocking {
            job.join()
        }
    }

    // menggabungkan context element
    @Test
    fun testCoroutineElements() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        // menggabungkan context element
        val scope = CoroutineScope(Dispatchers.IO + CoroutineName("Test"))

        // bisa menambahkan coroutineName() disini
        val job = scope.launch(CoroutineName("Parent") + dispatcher) {
            println("Parent run in thread : ${Thread.currentThread().name}")
            withContext(CoroutineName("Child") + Dispatchers.IO){
                println("Child run in thread : ${Thread.currentThread().name}")
            }
        }

        runBlocking {
            job.join()
        }
    }
}