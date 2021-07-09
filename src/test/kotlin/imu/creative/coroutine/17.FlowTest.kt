package imu.creative.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Test
import java.util.*

// menggunakan flow
// sifatnya lazy, belum dijalankan sebelum kita memanggilnya
// mulai dengan index
class FlowTest {

    // membuat flow
    @Test
    fun testFlow() {
        // emit() untuk mengirim data ke collect
        // collect() untuk mengakses/ menerima(receive) data

        // setelah di emit melakukan proses kirim, collect langsung proses terima
        val flow: Flow<Int> = flow {
            println("Start flow")
            repeat(100){
                println("Emit $it")
                emit(it)
            }
        }

        runBlocking {
            flow.collect {
                println("Receive $it")
            }
        }
    }

    // melakukan emit/ mengirim value dari 0-99 ke flow
    private suspend fun numberFlow(): Flow<Int> = flow {
        repeat(100){
            emit(it)
        }
    }

    // konversi dari integer ke string
    private suspend fun changeToString(number: Int): String {
        delay(100)
        return "Number $number"
    }

    // flow operator, untuk mencetak angka hasil bagi 0 yg dimodulus 2 == genap
    @Test
    fun testFlowOperator() {
        runBlocking {
            val flow = numberFlow()
            flow.filter { it % 2 == 0 }
                .map { changeToString(it) }
                .collect { println(it) }
        }
    }

    // flow exception
    @Test
    fun testFlowException() {
        // flow juga mempunyai function catch dan finnaly untuk exceptionHandler seperti try catch, jadi ga perlu bi
        // catch gunakan .catch()
        // finally gunakan .onCompletion()
        runBlocking {
            numberFlow()
                .map {
                    check(it < 10)  // valuenya harus selalu kurang dari 10
                    // check ini memeriksa, kalau true, tidak ada masalah, tapi jika false
                    // akan error IllegalStateException karena kondisi sudah tidak sesuai
                    it  // return it lagi
                }
                .onEach {
                    println(it) // print flownya
                }
                .catch {    // catch disini seperty try dan catch
                    println("Error : ${it.message}")
                }
                .onCompletion { // onCompletion disini seperti try dan finally
                    println("Done")
                }
                    // bisa collect menggunakan lambda atau tidak
                .collect()  // untuk menjalankan flownya, gunakan collect

        }
    }

    // cancellable flow
    @Test
    fun testCancelableFlow() {
        val scope = CoroutineScope(Dispatchers.IO)
        runBlocking {
            val job = scope.launch {
                numberFlow()
                    .onEach {
                        // membatalkan flow itu tinggal membatalkan coroutinenya aja
                        // maka otomatis coroutine yg menggunakan coroutinenya, tidak akan menggunakan coroutinenya lagi
                        // cancel() disini milik coroutinenya ya
                        // bisa juga ditulis dengan this.cancel()
                        if (it > 10) cancel()
                        else println(it)
                    }
                    .collect()
            }

            job.join()
        }
    }

    // shared flow
    // =====================================================
    // pada shared flow, kita bisa membuat lebih dari 1 receiver
    // sharedflow bersifat aktif atau hot. ketika kita mengirim data ke shared flow, data langsung dikirim ke receiver, tanpa perlu di collect dulu oleh receiver

    // shared flow vs broadcast channel
    // kotlin 1.4 shared flow release
    // dan menggantikan broadcast channel (tidak dianjurkan untuk digunakan di kotlin 1.4)
    // shared flow turunan dari flow, sehingga bisa menggunakan semua flow operator
    // shared flow bukanlah channel, sehingga ga ada operasi close()
    // untuk membuat receiver dari sharedFlow, kita bisa menggunakan function asSharedFlow()
    @Test
    fun testSharedFlow() {
        val scope = CoroutineScope(Dispatchers.IO)
        val sharedFlow = MutableSharedFlow<Int>()

        scope.launch {
            repeat(10) {
                println("   Send     1 : $it : ${Date()}")
                // ketika mengirim shared flow
                // dan jika data belum diterima oleh receivernya, akan di simpan di buffer
                // lebih fleksibel dibandingkan dengan broadcast channel
                sharedFlow.emit(it)
                delay(1000)
            }
        }

        // membuat receiver untuk shared flow
        scope.launch {
            sharedFlow.asSharedFlow()
                .buffer(10)
                .map { "Receive job 1 : $it : ${Date()}" }  // diberikan date, kapan dia menerima datanya
                .collect {
                    delay(1000) // diberikan delay, biar tau ada receiver yg lambat
                    println(it)
                }
        }

        scope.launch {
            sharedFlow.asSharedFlow()
                .buffer(10)
                .map { "Receive job 2 : $it : ${Date()}" }  // diberikan date, kapan dia menerima datanya
                .collect {
                    // delay 2 detik per 1 data
                    delay(2000) // diberikan delay, biar tau ada receiver yg lambat
                    println(it)
                }
        }

        runBlocking {
            delay(22_000)
            scope.cancel()
        }
    }

    // state flow
    // turunan dari shared flow
    // cocok untuk menggantikan conflated broadcast channel
    // pada state flow, receiver hanya akan menerima data paling baru
    // cocok untuk maintain state, dimana state itu biasanya hanya satu data, tidak peduli berapa kali jumlah perubahan datanya
    // yg paling penting pada state adalah data terakhirnya
    // untuk mendapatkan data statenya, kita bis gunakan field value di state flow
    // membuat receiver menggunakan asStateFlow()
    // state flow bisa dirancang sebagai pengganti Conflated Broadcast Channel
    @Test
    fun testStateFlow() {
        val scope = CoroutineScope(Dispatchers.IO)
        val stateFlow = MutableStateFlow(0)

        scope.launch {
            repeat(10) {
                println("   Send     1 : $it : ${Date()}")
                stateFlow.emit(it)
                delay(1000)
            }
        }

        // membuat receiver untuk state flow
        scope.launch {
            stateFlow.asStateFlow()
                .map { "Receive job 2 : $it : ${Date()}" }  // diberikan date, kapan dia menerima datanya
                .collect {
                    // delay 2 detik per 1 data
                    println(it)
                    delay(5000) // diberikan delay, biar tau ada receiver yg lambat
                }
        }

        runBlocking {
            delay(22_000)
            scope.cancel()
        }
    }
}