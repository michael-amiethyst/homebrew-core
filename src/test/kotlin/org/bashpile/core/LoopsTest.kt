package org.bashpile.core

import org.bashpile.core.antlr.AstConvertingVisitor.Companion.STRICT_HEADER
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class LoopsTest {

    val fixture = Main()

    @Test
    fun foreach_fileLine_works() {
        val script = """
            for(first: string, last: string, email: string, phone: string in "src/test/resources/data/example.csv"):
                print(first + " " + last + " " + email + " " + phone + "\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render()
        assertEquals(STRICT_HEADER + """
            cat "src/test/resources/data/example.csv" | sed '1d' | while IFS=',' read -r first last email phone; do
                printf "${'$'}{first} ${'$'}{last} ${'$'}{email} ${'$'}{phone}\n"
            done
            
            """.trimIndent(), renderedBash)

        val bashResult = renderedBash.runCommand()
        assertEquals(SCRIPT_SUCCESS, bashResult.second)
        assertEquals("""
            Alice Smith alice.smith@email.com 555-1234
            Bob Johnson bob.j@email.com 555-5678
            Charlie Williams c.williams@email.com 555-9012
            
        """.trimIndent(), bashResult.first)
    }

    @Test
    fun foreach_fileLine_multistatement_works() {
        val script = """
            // Real world example
            HOST: readonly exported string = "HOST_NAME"
            TOKEN: readonly exported string = "OAUTH_TOKEN"
            for(firstName: string, middleName: string, lastName: string, email: string, landline: string, cell: string in "src/test/resources/data/example_extended.csv"):
                // set progress status too
                landlineShort: exported string = #(printf "${'$'}landline" | cut -d " " -f 2)
                regionId: exported integer = 13
                print("Updating phone # " + landlineShort + " with values: lastName " + lastName + " cell " + cell + ".\n")
                curl -sL -H "..." -H "..." "https://${'$'}HOST/" -d "{ \"landlineShort\": ${'$'}landlineShort, \"lastName\": \"${'$'}lastName\" \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\"}"
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render()
        assertEquals(STRICT_HEADER + """
            declare -x HOST
            HOST="HOST_NAME"
            declare -x TOKEN
            TOKEN="OAUTH_TOKEN"
            pwd
            cat "src/test/resources/data/example_extended.csv" | sed '1d' | while IFS=',' read -r firstName middleName lastName email landline cell; do
                declare -x landlineShort
                landlineShort="$(printf "${'$'}landline" | cut -d " " -f 2)"
                declare -x regionId
                regionId="13"
                printf "Updating phone # ${'$'}{landlineShort} with values: lastName ${'$'}{lastName} cell ${'$'}{cell}.\n"
                curl -sL -H "..." -H "..." "https://${'$'}HOST/" -d "{ \"landlineShort\": ${'$'}landlineShort, \"lastName\": \"${'$'}lastName\" \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\"}"
            done
            
            """.trimIndent(), renderedBash)

        val bashResult = renderedBash.runCommand()
        assertEquals("""
            
        """.trimIndent(), bashResult.first)
        assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }

    @Test
    fun foreach_fileLine_multistatement_with_float_works() {
        val script = """
            // Real world example
            HOST: readonly exported string = "HOST_NAME"
            TOKEN: readonly exported string = "OAUTH_TOKEN"
            for(firstName: string, middleName: string, lastName: string, email: string, landline: float, cell: string in "src/test/resources/data/example_extended.csv"):
                // set progress status too
                cellShort: exported string = #(printf "${'$'}cell" | cut -d " " -f 2)
                regionId: exported integer = 13
                print("Updating phone # " + cellShort + " with values: lastName " + lastName + " cell " + cell + ".\n")
                print("{ \"cellShort\": ${'$'}cellShort, \"lastName\": \"${'$'}lastName\" \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\"}")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render()
        assertEquals(STRICT_HEADER + """
            declare -x HOST
            HOST="HOST_NAME"
            declare -x TOKEN
            TOKEN="OAUTH_TOKEN"
            cat "src/test/resources/data/example_extended.csv" | sed '1d' | while IFS=',' read -r firstName middleName lastName email landline cell; do
                declare -x cellShort
                cellShort="$(printf "${'$'}cell" | cut -d " " -f 2)"
                declare -x regionId
                regionId="13"
                printf "Updating phone # ${'$'}{cellShort} with values: lastName ${'$'}{lastName} cell ${'$'}{cell}.\n"
                printf "{ \"cellShort\": ${'$'}cellShort, \"lastName\": \"${'$'}lastName\" \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\"}"
            done
            
            """.trimIndent(), renderedBash)

        val bashResult = renderedBash.runCommand()
        assertEquals("", bashResult.first)
        assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }

    // TODO foreach -- test for windows line endings, "Mac" line endings

    // TODO foreach -- make test for non-csv
}
