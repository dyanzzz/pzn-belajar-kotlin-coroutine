package imu.creative.coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*

// kontiln coroutine
// 4. suspend function
class SuspendFunctionTest {
    private suspend fun helloWorld(){
        println("Hello : ${Date()} : ${Thread.currentThread().name}")
        // delay => menunggu selama 2 detik tanpa menghentikan thread, jadi thread bisa digunakan untuk menjalankan proses yg lainnya
        // delay hanya digunakan pada suspend function / coroutine
        delay(2_000)
        println("World : ${Date()} : : ${Thread.currentThread().name}")
    }

    @Test
    fun testSuspendFunction(){
        // runBlocking sebenarnya meruning coroutine, tapi memblock thread
        // pada kenyataannya, tidak boleh digunakan karena akan menghentikan thread
        runBlocking {
            helloWorld()
        }
    }
}