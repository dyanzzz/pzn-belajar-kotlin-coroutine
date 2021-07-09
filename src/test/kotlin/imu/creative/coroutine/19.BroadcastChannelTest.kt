package imu.creative.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.broadcast
import org.junit.jupiter.api.Test

// broadcast channel
// secara default channel hanya boleh memiliki 1 receiver
// Broadcast Channel => channel khusus yg mendukung receivernya lebih dari 1
// menggunakan keyword .openSubscription()  => untuk membuat chanel dapat di broadcast
// bradcast channel mempunyai 2 cara, bisa bikin pake function BroadcastChannel
    // val broadcastChannel = BroadcastChannel<Int>(capacity = 10)
    // atau bisa juga menggunakan broadcast function di coroutine scope
    // val broadcastChannel = scope.broadcast<Int>(capacity = 10) {
    //            repeat(10){
    //                send(it)
    //            }
    //        }
class BroadcastChannelTest {

    // mengirim dari broadcast channel
    // terimanya dari receiveChannel1 dan 2
    @ExperimentalCoroutinesApi
    @Test
    fun testBroadcastChannel() {
        val broadcastChannel = BroadcastChannel<Int>(capacity = 10)

        val receiveChannel1 = broadcastChannel.openSubscription()
        val receiveChannel2 = broadcastChannel.openSubscription()

        val scope = CoroutineScope(Dispatchers.IO)

        val jobSend = scope.launch {
            repeat(10) {
                broadcastChannel.send(it)
            }
        }

        val job1 = scope.launch {
            repeat(10) {
                println("Job 1 ${receiveChannel1.receive()}")
            }
        }

        val job2 = scope.launch {
            repeat(10) {
                println("Job 2 ${receiveChannel2.receive()}")
            }
        }

        runBlocking {
            joinAll(job1, job2, jobSend)
        }
    }

    // test broadcast function
    @ExperimentalCoroutinesApi
    @Test
    fun testBroadcastFunction() {
        val scope = CoroutineScope(Dispatchers.IO)

        val broadcastChannel = scope.broadcast<Int>(capacity = 10) {
            repeat(10){
                send(it)
            }
        }

        val receiveChannel1 = broadcastChannel.openSubscription()
        val receiveChannel2 = broadcastChannel.openSubscription()

        val job1 = scope.launch {
            repeat(10) {
                println("Job 1 ${receiveChannel1.receive()}")
            }
        }

        val job2 = scope.launch {
            repeat(10) {
                println("Job 2 ${receiveChannel2.receive()}")
            }
        }

        runBlocking {
            joinAll(job1, job2)
        }
    }

    // conflated broadcast channel
    // turunan dari broadcast channel, sehingga cara kerjanya sama
    // perbedaannya ada pada penerima channel receivernya
    // jika broadcast channel, walaupun receivernya lambat menerima data, maka receiver tetap akan mendapatkan seluruh data dari sender
    // berbeda dengan conflated broadcast channel
    // receiver hanya akan mendapatkan data paling baru dari sender, jika receivernya lambat
    @ExperimentalCoroutinesApi
    @Test
    fun testConflatedBroadcastChannel() {
        val conflatedBroadcastChannel = ConflatedBroadcastChannel<Int>()

        val receiveChannel = conflatedBroadcastChannel.openSubscription()

        val scope = CoroutineScope(Dispatchers.IO)

        val jobSender = scope.launch {
            repeat(11) {
                // mengirim per 1 detik 1 data
                delay(1000)
                println("send $it")
                conflatedBroadcastChannel.send(it)
            }
        }

        val jobReceiver = scope.launch {
            repeat(10) {
                // tapi hanya diterima per 2 detik 1 data
                delay(2000)
                println("Job Receiver ${receiveChannel.receive()}")
            }
        }

        runBlocking {
            delay(11_000)
            // joinAll(jobSender, jobReceiver)
            scope.cancel()
        }
    }
}