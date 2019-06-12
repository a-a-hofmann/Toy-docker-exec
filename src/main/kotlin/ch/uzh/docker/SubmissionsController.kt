package ch.uzh.docker

import org.apache.commons.compress.utils.IOUtils
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.io.FileCleaningTracker
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileOutputStream
import javax.servlet.http.HttpServletRequest


@RestController
class SubmissionsController constructor(private val runner: CodeRunner) {

    private val log = LoggerFactory.getLogger(SubmissionsController::class.java)

    @PostMapping("/upload")
    @Throws(Exception::class)
    fun handleUpload(request: HttpServletRequest): RunResult {
        val isMultipart = ServletFileUpload.isMultipartContent(request)

        val tmpdir = System.getProperty("java.io.tmpdir")
        val factory = DiskFileItemFactory()
        factory.repository = File(tmpdir)
        factory.sizeThreshold = -1
        factory.fileCleaningTracker = FileCleaningTracker()

        val upload = ServletFileUpload(factory)

        val items = upload.parseRequest(request)
        val numberOfItems = items.size

        log.info("Uploaded $numberOfItems files")
        items.forEach { item -> log.info("File: " + item.name) }

        var withVolumes = false
        val iter = items.iterator()
        while (iter.hasNext()) {
            val item = iter.next()
            val filename = "tmp/" + item.name

            if (!item.isFormField) {
                item.inputStream.use { uploadedStream ->
                    FileOutputStream(filename).use { out ->
                        IOUtils.copy(uploadedStream, out)
                    }
                }
            } else if (item.fieldName == "volumes") {
                withVolumes = item.string!!.toBoolean()
            } else {
                println(item.toString())
            }
        }

        val output = runner.runCode(withVolumes, "tmp")

        log.info("Code executed: $output")
        return output
    }
}