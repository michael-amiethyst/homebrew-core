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
        val result = fixture._getBast(script).render()
        assertEquals(STRICT_HEADER + """
            cat "src/test/resources/data/example.csv" | while IFS=',' read -r first last email phone; do
                printf "${'$'}{first} ${'$'}{last} ${'$'}{email} ${'$'}{phone}\n"
            done
            
            """.trimIndent(), result)
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
                curl -sL -H “...” -H “..." \
                  “https://${'$'}HOST/” \
                  -d “{ \“landlineShort\“: ${'$'}landlineShort, \“lastName\“: \“${'$'}lastName\” \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\"}“
        """.trimIndent().byteInputStream()
        val result = fixture._getBast(script).render()
        assertEquals(STRICT_HEADER + """
            declare -x HOST
            HOST="HOST_NAME"
            declare -x TOKEN
            TOKEN="OAUTH_TOKEN"
            cat "src/test/resources/data/example_extended.csv" | while IFS=',' read -r firstName middleName lastName email landline cell; do
                declare -x landlineShort
                landlineShort="$(printf "${'$'}landline" | cut -d " " -f 2)"
                declare -x regionId
                regionId="13"
                printf "Updating phone # ${'$'}{landlineShort} with values: lastName ${'$'}{lastName} cell ${'$'}{cell}.\n"
                curl -sL -H “...” -H “..." \
                      “https://${'$'}HOST/” \
                      -d “{ \“landlineShort\“: ${'$'}landlineShort, \“lastName\“: \“${'$'}lastName\” \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\"}“
            done
            
            """.trimIndent(), result)
    }

    // TODO foreach -- make test with non-string column variable
}
