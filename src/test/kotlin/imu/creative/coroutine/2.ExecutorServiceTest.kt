package imu.creative.coroutine

import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.Executors

// 2. membuat ExecutorService
// thread object yg lumayah berat (mahal)
// ukutannya 512kb-1mb
// jika terlalu banyak menggunakan thread, akan memakan banyak memori
// thread bisa digunakan ulang
// menggunakan ExecutorService => fitur JVM untuk manajemen thread
// bikin beberapa thread dimasukan ke dalam executorService -> kode program dimasukan ke executorServicenya
// maka executorService yg akan memanage threadnya
// menggunakan class Executors untuk membuat executorService
    // newSingleThreadExecutor  => membuat executorService yg didalamnya hanya ada 1 thread
    // newFixedThreadPool(int) => membuat n thread secara fixed ditentukan diawal
    // newCachedThreadPool() => membuat thread yg akan meningkat sesuai kebutuhan secara otomatis
class ExecutorServiceTest {

    @Test
    fun testSingleThreadPool(){
        // membuat thread hanya 1
        val executorService = Executors.newSingleThreadExecutor()
        repeat(10){
            // runnable tidak mengembalikan nilai / void / unit
            val runnable = Runnable {
                Thread.sleep(1_000)
                println("Done $it ${Thread.currentThread().name} ${Date()}")
            }

            // submit runnable ke executorService
            // dan akan mengeksekusi thread 1 1 bergantian untuk seluruh runnable
            // .execute untuk memasukan antrian runable kedalam executorService
            executorService.execute(runnable)
            println("Selesai memasukan runnable $it")
        }

        println("LOADING")
        Thread.sleep(11_000)
        println("FINISH")
    }

    @Test
    fun testFixThreadPool(){
        // membuat thread sebanyak n
        val executorService = Executors.newFixedThreadPool(3)
        repeat(10){
            // semua runnable akan dimasukan ke dalam antrian executorService, dan biarkan executorService yg mengatur untuk menjalankan threadnya
            val runnable = Runnable {
                Thread.sleep(1_000)
                println("Done $it ${Thread.currentThread().name} ${Date()}")
            }

            // submit runnable ke executorService
            // dan akan mengeksekusi thread 1 1 bergantian untuk seluruh runnable
            executorService.execute(runnable)
            println("Selesai memasukan runnable $it")
        }

        println("LOADING")
        Thread.sleep(11_000)
        println("FINISH")
    }

    @Test
    fun testCacheThreadPool(){
        // tidak disarankan untuk menggunakan .newCachedThreadPool() karena akan memakan banyak memori
        // manfaatnya, bisa membuat thread secara fleksibel
        // penggunakan .newCachedThreadPool() => akan membuat thread sebanyak"nya untuk membuat semua thread sekaligus, sampai memori kita habis
        val executorService = Executors.newCachedThreadPool()
        repeat(10){
            // semua runnable akan dimasukan ke dalam antrian executorService, dan biarkan executorService yg mengatur untuk menjalankan threadnya
            val runnable = Runnable {
                Thread.sleep(1_000)
                println("Done $it ${Thread.currentThread().name} ${Date()}")
            }

            // submit runnable ke executorService
            // dan akan mengeksekusi thread 1 1 bergantian untuk seluruh runnable
            executorService.execute(runnable)
            println("Selesai memasukan runnable $it")
        }

        println("LOADING")
        Thread.sleep(11_000)
        println("FINISH")
    }

}