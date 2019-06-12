package ch.uzh.docker

import java.time.Instant

data class RunResult(val output: String = "", val timeInNanoseconds: Long) {
    var timeInSeconds: Double = this.timeInNanoseconds / 1e9
    var timeInMilliseconds: Double = this.timeInNanoseconds / 1e6
    val timestamp: Instant = Instant.now()
}