package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
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
}