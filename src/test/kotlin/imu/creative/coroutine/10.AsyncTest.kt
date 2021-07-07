package imu.creative.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class AsyncTest {
    private suspend fun getFoo(): Int{
        delay(1000)
        return 10
    }

    private suspend fun getBar(): Int{
        delay(1000)
        return(10)
    }

    @Test
    fun testAsync(){
        runBlocking {
            val time = measureTimeMillis {
                // membuat coroutine, namun ingin mereturn data
                // gunakan async
                val foo: Deferred<Int> = GlobalScope.async { getFoo() }
                val bar: Deferred<Int> = GlobalScope.async { getBar() }

                val result = foo.await() + bar.await()
                println("Result : $result")
            }

            println("Total Time : $time")
        }
    }

    @Test
    fun testAwaitAll(){
        runBlocking {
            val time = measureTimeMillis {
                // membuat coroutine, namun ingin mereturn data
                // gunakan async
                val foo1: Deferred<Int> = GlobalScope.async { getFoo() }
                val bar1: Deferred<Int> = GlobalScope.async { getBar() }
                val foo2: Deferred<Int> = GlobalScope.async { getFoo() }
                val bar2: Deferred<Int> = GlobalScope.async { getBar() }

                //val result = foo.await() + bar.await()
                // menunggu semuanya selesai dengan awaitAll, namun type data yg di return harus sama semua, karena
                // akan menyimpannya kedalam List<T> / listOf<T>
                val result = awaitAll(foo1, bar1, foo2, bar2).sum()
                println("Result : $result")
            }

            println("Total Time : $time")
        }
    }
}