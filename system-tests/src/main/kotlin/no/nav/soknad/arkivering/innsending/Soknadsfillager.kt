package no.nav.soknad.arkivering.innsending

import no.nav.soknad.arkivering.Configuration
import no.nav.soknad.arkivering.dto.FilElementDto
import no.nav.soknad.arkivering.utils.loopAndVerify
import java.time.LocalDateTime

class Soknadsfillager(private val restUtils: RestUtils) {

	fun sendFilesToFileStorage(uuid: String, appConfiguration: Configuration) {
		val message = "fileUuid is $uuid for test '${Thread.currentThread().stackTrace[2].methodName}'"
		sendFilesToFileStorage(uuid, "apabepa".toByteArray(), message, appConfiguration)
	}

	fun sendFilesToFileStorage(uuid: String, payload: ByteArray, message: String, appConfiguration: Configuration) {
		println(message)
		sendFilesToFileStorageAndVerify(uuid, payload, appConfiguration)
	}

	private fun sendFilesToFileStorageAndVerify(uuid: String, payload: ByteArray, appConfiguration: Configuration) {
		val files = listOf(FilElementDto(uuid, payload, LocalDateTime.now()))
		val url = appConfiguration.config.soknadsfillagerUrl + "/filer"

		val headers = createHeaders(appConfiguration)
		restUtils.performPostCall(files, url, headers, false)
		pollAndVerifyDataInFileStorage(uuid, 1, appConfiguration)
	}

	fun pollAndVerifyDataInFileStorage(uuid: String, expectedNumberOfHits: Int, appConfiguration: Configuration) {
		val url = appConfiguration.config.soknadsfillagerUrl + "/filer?ids=$uuid"
		loopAndVerify(expectedNumberOfHits, { getNumberOfFiles(url, appConfiguration) },
			{ assert(expectedNumberOfHits == getNumberOfFiles(url, appConfiguration)) { "Expected $expectedNumberOfHits files in File Storage" } })
	}


	private fun getNumberOfFiles(url: String, appConfiguration: Configuration): Int {
		val headers = createHeaders(appConfiguration)
		println("APABEPA Soknadsfillager: '$headers'")
		val listOfFiles = restUtils.performGetCall(url, headers)

		return listOfFiles?.filter { file -> file.fil != null }?.size ?: 0
	}

	private fun createHeaders(appConfiguration: Configuration) =
		restUtils.createHeaders(
			appConfiguration.config.soknadsfillagerUsername,
			appConfiguration.config.soknadsfillagerPassword
		)
}
