package no.nav.soknad.arkivering.arkiveringendtoendtests

import no.nav.soknad.arkivering.arkiveringendtoendtests.dto.SoknadInnsendtDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ArkiveringEndToEndTestsApplicationTests {

	private val dependencies = HashMap<String, Int>().also {
		it["soknadsmottaker"] = 8090
		it["soknadsarkiverer"] = 8091
		it["joark-mock"] = 8092
	}

	private val restTemplate = RestTemplate()

	@BeforeEach
	fun setup() {
		checkThatDependenciesAreUp()
	}

	private fun checkThatDependenciesAreUp() {
		for (dep in dependencies) {
			try {
				val healthStatusResponse = restTemplate.getForEntity("http://localhost:${dep.value}/actuator/health", Health::class.java)
				assertEquals("UP", healthStatusResponse.body?.status, "Dependency '${dep.key}' seems to be down")
			} catch (e: Exception) {
				fail("Dependency '${dep.key}' seems to be down")
			}
		}
	}

	@Test
	fun `Send data to Mottaker -- can find data in JoarkMock`() {
		val dto = createDto()

		sendDataToMottaker(dto)

		verifyDataInJoark(dto)
	}


	private fun verifyDataInJoark(dto: SoknadInnsendtDto) {
		val key = dto.personId
		val url = "http://localhost:${dependencies["joark-mock"]}/joark/lookup/$key"

		val responseEntity : ResponseEntity<List<LinkedHashMap<String, String>>> = pollJoarkUntilResponse(url)

		assertEquals(dto.tema, responseEntity.body?.get(0)!!["message"])
		assertEquals(dto.personId, responseEntity.body?.get(0)!!["name"])
	}

	private fun <T> pollJoarkUntilResponse(url: String): ResponseEntity<List<T>> {

		val respType = object: ParameterizedTypeReference<List<T>>(){}

		val startTime = System.currentTimeMillis()
		val timeout = 10 * 1000

		while (System.currentTimeMillis() < startTime + timeout) {
			val responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, respType)

			if (responseEntity.body != null && responseEntity.body!!.isNotEmpty()) {
				return responseEntity
			}
			TimeUnit.MILLISECONDS.sleep(50)
		}
		fail("Failed to get response from Joark")
	}

	private fun sendDataToMottaker(dto: SoknadInnsendtDto) {
		val url = "http://localhost:${dependencies["soknadsmottaker"]}/save"

		val headers = HttpHeaders()
		headers.contentType = MediaType.APPLICATION_JSON
		val request = HttpEntity(dto, headers)
		restTemplate.postForObject(url, request, String::class.java)
	}

	private fun createDto() = SoknadInnsendtDto("innsendingId", false, "personId", "tema", LocalDateTime.now(), emptyList())
}

class Health {
	lateinit var status: String
}