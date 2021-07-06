package imu.creative.coroutine

class App {
    val greeting: String
        get() {
            return "Hello world."
        }
}

fun main(args: Array<String>) {
    println(App().greeting)
    val threadName = Thread.currentThread().name
    println("Running in thread \"$threadName\"")
}
