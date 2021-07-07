package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.*

class TimeoutTest {
    // coroutine tidak lebih dari waktu yg telah ditentukan
    // menggunakan function withTimeOut untuk memvalidasi jika coroutine melebihi waktu yg sudah ditentukan
    // jika melebihi waktu yg sudah ditentukan maka akan error TimeoutCancelableException -> dan akan membatalkan coroutinenya

    @Test
    fun testTimeout() {
        runBlocking {
            val job = GlobalScope.launch {
                println("Start coroutine")
                // jika ingin kode programmnya berjalan tidak lebih dari beberapa waktu, bisa gunakan withTimeout
                withTimeout(5000){
                    repeat(100){
                        // dimana tiap iterasinya akan delay 1detik
                        delay(1000)
                        println("$it ${Date()}")
                    }
                }
                // jika melewati batas withTimeout yg sudah di setup, maka code program berikutnya tidak akan di eksekusi
                // dan akan menjalankan throw CancellationException
                // jika tetap ingin melakukan eksekusi pada code berikutnya, harus menggunakan try finally, tidak perlu catch, karena catch sudah dilakukan oleh kotlin secara otomatis
                println("Finish coroutine")
            }
            job.join()
        }
    }

    @Test
    fun testTimeoutOrNull() {
        runBlocking {
            val job = GlobalScope.launch {
                println("Start coroutine")
                // jika ingin kode programmnya berjalan tidak lebih dari beberapa waktu, bisa gunakan withTimeout
                // jika ingin kode program dibawa withTimeout tetap berjalan meskipun sudah melewati batas waktu yg sudah ditentukan, gunakan function
                // withTimeoutOrNull()
                withTimeoutOrNull(5000){
                    repeat(100){
                        // dimana tiap iterasinya akan delay 1detik
                        delay(1000)
                        println("$it ${Date()}")
                    }
                }
                // jika melewati batas withTimeout yg sudah di setup, maka code program berikutnya tidak akan di eksekusi
                // dan akan menjalankan throw CancellationException
                // jika tetap ingin melakukan eksekusi pada code berikutnya, harus menggunakan try finally, tidak perlu catch, karena catch sudah dilakukan oleh kotlin secara otomatis
                // bisa juga menggunakan withTimeoutOrNull(), jika kode program berikutnya ingin dijalankan
                println("Finish coroutine")
            }
            job.join()
        }
    }
}