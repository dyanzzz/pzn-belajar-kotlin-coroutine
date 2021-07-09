package imu.creative.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select
import org.junit.jupiter.api.Test

// select function
// mencari yg paling cepat dari async & channel menggunakan select function ini
// select function memungkinkan kita untuk menunggu beberapa suspending function dan memilih yg pertama datanya tersedia
// bisa digunakan di Defered dan juga Channel
// untuk Deffered, bisa menggunakan onAwait()
// untuk ReceiveChannel, kita bisa menggunakan onReceive()
class SelectFunctionTest {

    // menggunakan deferred
    @Test
    fun testSelectDeferred() {
        val scope = CoroutineScope(Dispatchers.IO)

        val deferred1 = scope.async {
            delay(1000)
            1000
        }

        val deferred2 = scope.async {
            delay(2000)
            2000
        }

        val deferred3 = scope.async {
            delay(500)
            500
        }

        val job = scope.launch {
            val win = select<Int> {
                deferred1.onAwait{ it }
                deferred2.onAwait{ it }
                deferred3.onAwait{ it }
            }

            println("Win : $win")
        }

        runBlocking {
            job.join()
        }
    }

    // menggunakan channel
    @ExperimentalCoroutinesApi
    @Test
    fun testSelectChannel() {
        val scope = CoroutineScope(Dispatchers.IO)

        val receiveChannel1 = scope.produce {
            delay(1000)
            send(1000)
        }

        val receiveChannel2 = scope.produce {
            delay(2000)
            send(2000)
        }

        val receiveChannel3 = scope.produce {
            delay(500)
            send(500)
        }

        val job = scope.launch {
            val win = select<Int> {
                receiveChannel1.onReceive{ it }
                receiveChannel2.onReceive{ it }
                receiveChannel3.onReceive{ it }
            }

            println("Win : $win")
        }

        runBlocking {
            job.join()
        }
    }

    // gabungan antara menggunakan deferred dan channel
    @ExperimentalCoroutinesApi
    @Test
    fun testSelectChannelAndDeferred() {
        val scope = CoroutineScope(Dispatchers.IO)

        val receiveChannel1 = scope.produce {
            delay(1000)
            send(1000)
        }

        val deferred2 = scope.async {
            delay(2000)
            2000
        }

        val deferred3 = scope.async {
            delay(500)
            500
        }

        val job = scope.launch {
            val win = select<Int> {
                receiveChannel1.onReceive{ it }
                deferred2.onAwait{ it }
                deferred3.onAwait{ it }
            }

            println("Win : $win")
        }

        runBlocking {
            job.join()
        }
    }
}