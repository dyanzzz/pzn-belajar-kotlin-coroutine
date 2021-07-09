package imu.creative.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.junit.jupiter.api.Test

// channel => untuk mentransfer aliran data dari satu tempat ke tempat lain
// channel == antrian
// send() => untuk mengirim data ke channel
// receive() => mengambil data dari channel
// channel ini sifatnya blocking ==> ketika channel kosong saat ada yg mengambil data menggunakan receive(), maka dia akan menunggu sampai datanya ada
// begitu juga sebaliknya, saat akan mengirim data ke channel, tapi masih ada datanya. dia akan nunggu sampai channelnya kosong
// close() => untuk menutup channel
class ChannelTest {

    // membuat channel
    @Test
    fun testChannel() {
        runBlocking {
            // channel typenya Int
            val channel = Channel<Int>()

            val job1 = launch {
                println("Send data 1 to channel")
                channel.send(1)
                println("Send data 2 to channel")
                channel.send(2)
            }

            val job2 = launch {
                println("Receive data 1 : ${channel.receive()}")
                println("Receive data 2 : ${channel.receive()}")
            }

            joinAll(job1, job2)
            channel.close()
        }
    }

    // channel backpreasure
    // ketika channel mengirim data, namun tidak ada yg mengambil, penumpukan di channel bisa ditangani dengan buffer
    // buffer channel ini merupakan capacity dari channel sebanyak apa bisa menyimpan data di channel, sebelum data pada channel diambil/dipanggil
    // cara menggunakannya => val channel = Channel<Int>(capacity = Channel.UNLIMITED)
    // Channel.UNLIMITED => menampung data didalam channel sebanyak max Int
    // Channel.CONFLATED => -1, ketika mengirim beberapa data ke channel, artinya mereplace data sebelumnya dengan data yg dikirim setelahnya
    // Channel.RENDEZVOUS => 0, defaultnya channel, artinya tidak memiliki buffer, tidak bisa mengirim data ke channel jika masih ada data sebelumnya didalam channel
    // Channel.BUFFER => defaultnya 64 atau bisa di setup via properties
    // capacity pada channel tsb adalah angka yg bertype Int, jadi bisa memasukan angka berapapun yg diinginkan untuk menyimpan data sementara didalam channel
    // =================
    // Channel.UNLIMITED
    @Test
    fun testChannelUnlimited() {
        runBlocking {
            // channel typenya Int
            // UNLIMITED => data akan tetap masuk antrian channel sampai batas MAX INT, tanpa menunggu data pertama dipanggil
            val channel = Channel<Int>(capacity = Channel.UNLIMITED)

            val job1 = launch {
                // semua data yg dikirim akan tetap masuk ke channel, meskipun tidak pernah dipanggil
                println("Send data 1 to channel")
                channel.send(1)
                println("Send data 2 to channel")
                channel.send(2)
            }

            val job2 = launch {
                //println("Receive data 1 : ${channel.receive()}")
                //println("Receive data 2 : ${channel.receive()}")
            }

            joinAll(job1, job2)
            channel.close()
        }
    }

    // Channel.CONFLATED
    @Test
    fun testChannelConflated() {
        runBlocking {
            // channel type Int
            // data yg dikirim ke channel, hanya data yg terakhir aja
            val channel = Channel<Int>(capacity = Channel.CONFLATED)

            val job1 = launch {
                println("Send data 1 to channel")
                channel.send(1)
                println("Send data 2 to channel")
                channel.send(2)
            }

            val job2 = launch {
                println("Receive data 1 : ${channel.receive()}")    // resultnya data 2 yg diterima
                //println("Receive data 2 : ${channel.receive()}")
            }

            joinAll(job1, job2)
            channel.close()
        }
    }

    // menggunakan buffer overflow
    // meskipun kita sudah menggunakan buffer, adakalanya buffer sudah penuh dan sender tetap mengirim data
    // kita bisa menggunakan enum BufferOverflow untuk menangani kasus ini
    // SUSPEND => block sender (default), artinya menunggu pengiriman data jika data yg diterima oleh channel sudah mencapai batas, dan menunggu data awal pada channel dipanggil terebih dulu
    // DROP_OLDEST => menghapus data yg sudah masuk lebih dulu dan di replace oleh data berikutnya, jika kapasitas channel sudah penuh
    // DROP_LATEST => mengignore data yg masuk ke channel, ketika channel sudah dipenuhi lebih dulu oleh data sebelumnya
    // ===================
    // menggunakan SUSPEND, DROP_OLDEST, DROP_LATEST
    @Test
    fun testChannelBufferOverflow() {
        runBlocking {
            // channel type Int
            // val channel = Channel<Int>(capacity = 10, onBufferOverflow = BufferOverflow.SUSPEND)
            // val channel = Channel<Int>(capacity = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)
            val channel = Channel<Int>(capacity = 10, onBufferOverflow = BufferOverflow.DROP_LATEST)

            val job1 = launch {
                repeat(100) {
                    println("Send data $it to channel")
                    channel.send(it)
                }
            }

            val job2 = launch {
                repeat(10) {
                    println("Receive data ${channel.receive()}")
                }
            }

            joinAll(job1, job2)
            channel.close()
        }
    }

    // channel undelivered element
    // kadang ada channel yg udah di close, tapi ada coroutine yg masih ngirim data ke channel tsb
    // dan otomatis channel mereturn ClosedSendChannelException
    // kita bisa menambahkan lambda function ketika membuat channel, sebagai fallback ketika data dikirim dan channel sudah di close
    // fallback tsb yg akan dieksekusi, mau diapakan data tsb
    // onUndeliveredElement adalah keywordnya
    // cocok ketika data yg dikirim berupa resource, jangan sampai ada memory leak di aplikasi kita
    @Test
    fun testChannelUndeliveredElement() {
        runBlocking {
            // channel type Int
            val channel = Channel<Int>(capacity = 10) {
                // lambda ini bisa diisi oleh undelivered element
                // atau element" yg tidak dikirim ke receivernya, karena channel sudah di close
                println("Undelivered value : $it")
            }

            channel.close()

            val job1 = launch {
                channel.send(10)
                channel.send(100)
            }

            job1.join()
        }
    }

    // produce function
    // dengan produce, ketika membuat coroutine dan membuat scope, bisa langsung membuat channel didalamnya
    // dengan cara membuat lambda di coroutine, bisa langsung mengirim channel data didalam lambda tsb
    // return value dari produce, adalah receive channel. bukan job/defered
    // bisa digunakan untuk menerima datanya
    @ExperimentalCoroutinesApi
    @Test
    fun testProduce() {
        val scope = CoroutineScope(Dispatchers.IO)
        val channel: ReceiveChannel<Int> = scope.produce {
            repeat(100){
                // println("Send data $it to channel")
                send(it)
            }
        }

        val job = scope.launch {
            repeat(100){
                println("Receive data ${channel.receive()}")
            }
        }

        runBlocking {
            joinAll(job)
        }
    }
}