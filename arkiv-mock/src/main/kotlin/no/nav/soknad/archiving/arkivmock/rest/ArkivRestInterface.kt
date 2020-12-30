package no.nav.soknad.archiving.arkivmock.rest

import no.nav.soknad.archiving.arkivmock.dto.ArkivData
import no.nav.soknad.archiving.arkivmock.service.ArkivMockService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/journalpostapi/v1")
class ArkivRestInterface(private val arkivMockService: ArkivMockService) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@PostMapping(value = ["/journalpost"])
	fun receiveMessage(@RequestBody arkivData: ArkivData): ResponseEntity<String> {
		logger.info("Received message: '$arkivData'")

		val responseBody = arkivMockService.archive(arkivData)
		return ResponseEntity(responseBody, HttpStatus.OK)
	}
}
