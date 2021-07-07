package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CoroutineDispatchersObjectTest {
    @Test
    fun testDispatcher() {
        runBlocking {

            // mencetak thread pada test worker => running di thread bawaan jUnit
            println("runBlocking : ${Thread.currentThread().name}")

            // dispatchers.default => minimal ada 2 thread, atau sebanyak jumlah CPU(mana yg lebih banyak)
            // dispatcher ini cocok untuk yg CPU-bound
            val job1 = GlobalScope.launch(Dispatchers.Default) {
                println("Job 1 : ${Thread.currentThread().name}")
            }

            // dispatcher.io => berisikan thread sesuai kebutuhan. ketika butuh, akan dibuat, ketika tidak butuh akan di hapus threadnya
            // mirip cache thread pool di executorService
            // dispatcher ini akan sharing thread dengan default dispatcher
            val job2 = GlobalScope.launch(Dispatchers.IO) {
                println("Job 2 : ${Thread.currentThread().name}")
            }

            joinAll(job1, job2)
        }
    }

    @Test
    fun testUnconfined() {
        // Dispatcher.unconfined => dispatcher yg tidak menunjuk thread manapun, biasanya akan melanjutkan thread di coroutine sebelumnya
        // thread itu bisa berubah ditengah jalan jika memang terdapat code yg melakukan perubahan thread
        // unconfined == threadnya fleksibel karena bisa berubah
        runBlocking {

            println("runBlocking : ${Thread.currentThread().name}")

            GlobalScope.launch(Dispatchers.Unconfined) {
                println("Unconfined : ${Thread.currentThread().name}")
                delay(1000)
                println("Unconfined : ${Thread.currentThread().name}")
                delay(1000)
                println("Unconfined : ${Thread.currentThread().name}")
            }

            // kalo ga pake dispatche adalah confined
            // Dispatcher.confined => dispatcher yg akan melanjutkan thread dari coroutine sebelumnya
            // confined == threadnya fixed
            GlobalScope.launch {
                println("Confined : ${Thread.currentThread().name}")
                delay(1000)
                println("Confined : ${Thread.currentThread().name}")
                delay(1000)
                println("Confined : ${Thread.currentThread().name}")
            }

            delay(3000)
        }
    }

    @Test
    fun testExecutorService() {
        // dispatcher yg dibuat custom dengan executors service
        val dispatcherService = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val dispatcherWeb = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

        runBlocking {
            val job1 = GlobalScope.launch(dispatcherService) {
                println("Job 1 : ${Thread.currentThread().name}")
            }
            val job2 = GlobalScope.launch(dispatcherWeb) {
                println("Job 2 : ${Thread.currentThread().name}")
            }

            joinAll(job1, job2)
        }
    }

    @Test
    fun testWithContext() {
        // dispatcher yg menggunakan withContext untuk mengganti-ganti thread
        val dispatcherClient = Executors.newFixedThreadPool(10).asCoroutineDispatcher()

        runBlocking {
            val job = GlobalScope.launch(Dispatchers.IO) {
                println("Job 1 : ${Thread.currentThread().name}")

                // kalo mau ganti threadnya, gunakan withContext(dispatcherClient/coroutine contextnya)
                withContext(dispatcherClient){
                    println("Job 2 : ${Thread.currentThread().name}")
                }

                // thread akan kembali ke thread awal/ job 1 pada thread yg sebelumnya
                println("Job 3 : ${Thread.currentThread().name}")

                // kalo mau ganti threadnya, gunakan withContext(dispatcherClient/coroutine contextnya)
                withContext(dispatcherClient){
                    println("Job 4 : ${Thread.currentThread().name}")
                }
            }
            job.join()
        }
    }

    @Test
    fun testCancelFinally() {
        runBlocking {
            val job = GlobalScope.launch {
                try {
                    println("Start Job")
                    delay(1000) // disini, job akan di cancel ketika di delay
                    println("End job")
                } finally {
                    // isActive => mengecek job aktif atau engga
                    println(isActive)   // false karena jobnya di cancel sebelumnya
                    // delay itu juga melakukan pengecekan isActive, kalo false, proses dibawahnya tidak aakn dieksekusi
                    delay(1000) // false, karena sebelum masuk finally, jobnya sudah di cancel
                    println("Finally")
                }
            }
            job.cancelAndJoin()
        }
    }

    @Test
    fun testNonCancelable() {
        runBlocking {
            val job = GlobalScope.launch {
                try {
                    println("Start Job")
                    delay(1000) // disini, job akan di cancel ketika di delay => threadnya akan bernilai false karena di cancel
                    println("End job")
                } finally {
                    // contextnya diganti menjadi nonCancelable, dan threadnya menjadi aktif
                        // threadnya gaboleh di cancel => maka threadnya selalu true
                        // code program menjadi tidak bisa dibatalkan
                    withContext(NonCancellable){
                        // isActive => mengecek job aktif atau engga
                        println(isActive)   // true karena jobnya di cancel, kemudian diubah oleh withContext(NonCancelable) menjadi true lagi threadnya
                        // delay itu juga melakukan pengecekan isActive, kalo false, proses dibawahnya tidak aakn dieksekusi
                        delay(1000) // true, karena ketika masuk finally, threadnya diubah oleh withContext(NonCancelable) menjadi true lagi, karena gaboleh di cancel
                        println("Finally")
                    }
                }
            }
            job.cancelAndJoin()
        }
    }
}