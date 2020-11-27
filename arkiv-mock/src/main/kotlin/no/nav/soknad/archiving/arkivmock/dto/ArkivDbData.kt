package no.nav.soknad.archiving.arkivmock.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "arkiv")
class ArkivDbData(
	@Id
	val id: String,
	val title: String,
	val tema: String,
	val timesaved: LocalDateTime
) {
	override fun toString(): String {
		val mapper = ObjectMapper()
		mapper.enable(SerializationFeature.INDENT_OUTPUT)
		return mapper.writeValueAsString(this)
	}
}