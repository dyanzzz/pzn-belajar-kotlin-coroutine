package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class JobTest{

    @Test
    fun testJob(){
        // runBlocking berguna untuk melakukan blcoking code" yg bukan / diluar coroutine
        runBlocking {
            // jika kita membuat coroutinenya di dalam runBlocking, maka GlobalScope.launce coroutine
            // akan berjalan sendiri, diluar dari runBlocking secara asyncronous
            // terus gimana caranya menjalankan coroutine di dalam runBlocking ini?
                // gunakan job, untuk memulai menjalankan coroutinenya
            GlobalScope.launch {
                delay(2000)
                println("Coroutine Done ${Thread.currentThread().name}")
            }
        }
    }

    @Test
    fun testJobLazy(){
        runBlocking {
            // bikin coroutine, returnnya adalah Job
            // dan berjalan secara LAZY/ jalan jika dipanggil aja
            // jika tidak menggunakan start = maka akan berjalan otomatis
            val job: Job = GlobalScope.launch(start = CoroutineStart.LAZY) {
                delay(2000)
                println("Coroutine Done ${Thread.currentThread().name}")
            }
            job.start()

            delay(3000)
        }
    }

    @Test
    fun testJobJoin(){
        runBlocking {
            val job: Job = GlobalScope.launch {
                delay(2000)
                println("Coroutine Done ${Thread.currentThread().name}")
            }
            // join => menunggu coroutine globalscope sampai dia selesai proses didalam runBlocking
            // sebgaai pengganti delay
            job.join()
        }
    }

    @Test
    fun testJobCancel(){
        runBlocking {
            val job: Job = GlobalScope.launch {
                delay(2000)
                println("Coroutine Done ${Thread.currentThread().name}")
            }
            // coroutine dibatalkan
            job.cancel()

            delay(3000)
        }
    }
}