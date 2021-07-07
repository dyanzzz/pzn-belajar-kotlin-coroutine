package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.*

class CancelableCoroutineTest {

    @Test
    fun tesCanNotCancel(){
        runBlocking {
            val job = GlobalScope.launch {
                println("Start coroutine : ${Date()}")
                Thread.sleep(2000)
                println("End coroutine : ${Date()}")
            }
            // meskipun job di cancel, job tidak dibatalkan karena di sleep selama 2 detik
            // kalo di delay, bisa di batalkan
            job.cancel()
            delay(3000)
        }
    }

    @Test
    fun tesCancelable(){
        runBlocking {
            val job = GlobalScope.launch {

                if (!isActive) throw CancellationException() // cek apakah coroutine aktif atau engga
                println("Start coroutine : ${Date()}")

                ensureActive()  // cek apakah coroutine aktif atau engga
                Thread.sleep(2000)

                // coroutine akan mengecek setelah thread.sleep apakah masih aktif atau tidak,
                // jika sudah tidak aktif, tidak akan melanjutkan ke proses berikutnya
                ensureActive() // cek apakah coroutine aktif atau engga
                println("End coroutine : ${Date()}")
            }
            // meskipun job di cancel, job tidak dibatalkan karena di sleep selama 2 detik
            // kalo di delay, bisa di batalkan
            job.cancel()
            delay(3000)
        }
    }

    @Test
    fun tesCancelFinally(){
        runBlocking {
            val job = GlobalScope.launch {
                try {
                    println("Start coroutine : ${Date()}")
                    delay(2000) // untuk mentriger proses cancel, gunakan delay, yg membuat coroutine bisa dibatalkan
                    println("End coroutine : ${Date()}")
                } finally {
                    // ketika dibatalkan, jika ingin melakukan sesuatu, cukup lakukan dengan finally
                        // gaperlu di catch, karena kotlin sudah melakukannya secara otomatis
                    println("Finish")
                }
            }
            job.cancelAndJoin()
        }
    }
}