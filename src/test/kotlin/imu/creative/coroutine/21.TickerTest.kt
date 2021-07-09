package imu.creative.coroutine

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*

// ticker function
// ticker adalah function yg bisa kita gunakan untuk membuat channel mirip dengan timer
// dengan ticker, kita bisa menentukan sebuah pesan akan dikirim dalam waktu timer yg sudah kita tentukan
// cocok jika ingin membuat timer menggunakan coroutine dan channel
// return value ticker adalah ReceiverChannel<Unit>, dan setiap kita receive data. data hanya berupa null
class TickerTest {
    @ObsoleteCoroutinesApi
    @Test
    fun testTicker() {
        // ticker itu returnnya ReceiveChannel
        // tiap detik/delaynya yg di setup akan menerima data
        val receiveChannel = ticker(delayMillis = 1000)
        runBlocking {
            // jika menggunakan ticker dan di setup delayMillisnya berapa,
            // maka job ini akan di trigger setiap detiknya
            val job = launch {
                repeat(10) {
                    receiveChannel.receive()
                    println(Date())
                }
            }
            job.join()
        }
    }
}
