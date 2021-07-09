package imu.creative.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import org.junit.jupiter.api.Test


// actor() function
// kebalikan dari produce() function
// saat kita menggunakan produce() function, kita membuat coroutine sekaligus sebagai channel sendernya
// untuk membuat coroutine sekaligus receivernya, kita bisa menggunakan actor() function
// konsep seperti ini dikenal dengan konsep Actor Model

// actor itu balikannya adalah send channel, digunakan untuk mengirim data
// produce itu balikannya receive channel, digunakan untuk menerima data
class ActorTest {
    @ObsoleteCoroutinesApi
    @Test
    fun testActor() {
        val scope = CoroutineScope(Dispatchers.IO)

        val sendChannel = scope.actor<Int>(capacity = 10) {
            // ini lambda dari receive channel
            // jadi bisa langsung receive()
            repeat(10) {
                println("Actor Receive data ${receive()}")
            }
        }

        val job = scope.launch {
            repeat(10) {
                sendChannel.send(it)
            }
        }

        runBlocking {
            job.join()
        }
    }
}