package no.nav.soknad.arkivering

fun main() {
	val loadTests = LoadTests(Configuration())
	println("Starting the Load Tests")

	loadTests.`10 simultaneous entities, 8 times 38 MB each`()
	loadTests.`100 simultaneous entities, 2 times 2 MB each`()
	loadTests.`100 simultaneous entities, 20 times 1 MB each`()
	loadTests.`10 000 simultaneous entities, 1 times 1 byte each`()

	println("Finished with the Load Tests")
}