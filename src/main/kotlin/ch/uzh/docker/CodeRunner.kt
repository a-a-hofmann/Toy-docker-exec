package ch.uzh.docker

import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.messages.ContainerConfig
import com.spotify.docker.client.messages.HostConfig
import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Service
import java.io.File
import kotlin.concurrent.thread

@Service
class CodeRunner {

    private val log = LoggerFactory.getLogger(CodeRunner::class.java)

    private final val docker = DefaultDockerClient.fromEnv().build()!!

    fun runCode(withAttachedVolume: Boolean, path: String): RunResult {
        val absolutePath = FileSystemResource(path).file.absolutePath
        val hostConfig = if (withAttachedVolume) hostConfigWithAttachedVolume(absolutePath) else hostConfig()

        if (withAttachedVolume) {
            log.info("Running with attached volume")
        } else {
            log.info("Running by copying files into container")
        }
        val containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image("python:3.8.0a4-alpine3.9")
                .networkDisabled(true)
                .cmd("python", "/usr/src/test.py")
                .build()

        val startExecutionTime = System.nanoTime()
        val creation = docker.createContainer(containerConfig)
        val id: String = creation.id()!!

        if (!withAttachedVolume) {
            docker.copyToContainer(File(absolutePath).toPath(), id, "/usr/src")
        }
        docker.startContainer(id)
        docker.waitContainer(id)
        val logs = docker.logs(id, DockerClient.LogsParam.stdout()).readFully()

        val endExecutionTime = System.nanoTime()

        thread {
            stopAndRemoveContainer(id)
        }
        return RunResult(logs, endExecutionTime - startExecutionTime)
    }

    private fun hostConfig(): HostConfig {
        return HostConfig.builder()
                .build()!!
    }

    private fun hostConfigWithAttachedVolume(path: String): HostConfig {
        return HostConfig.builder()
                .appendBinds("$path:/usr/src")
                .build()!!
    }

    private fun stopAndRemoveContainer(id: String) {
        log.info("Stopping container $id...")
        docker.stopContainer(id, 1)
        docker.removeContainer(id)
    }
}