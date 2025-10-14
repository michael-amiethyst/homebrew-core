package org.bashpile.core.maintests

import org.bashpile.core.SCRIPT_SUCCESS
import org.bashpile.core.antlr.AstConvertingVisitor
import org.bashpile.core.bast.statements.ForeachFileLineLoopBashNode
import org.bashpile.core.engine.RenderOptions
import org.bashpile.core.runCommand
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LoopsMainTest : MainTest() {

    @Test
    fun foreach_fileLine_works() {
        val script = """
            for(first: string, last: string, email: string, phone: string in "src/test/resources/data/example.csv"):
                print(first + " " + last + " " + email + " " + phone + "\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render(RenderOptions.UNQUOTED)
        Assertions.assertEquals(
            AstConvertingVisitor.Companion.STRICT_HEADER + """
            cat "src/test/resources/data/example.csv" | ${ForeachFileLineLoopBashNode.Companion.sed} -e '1d' -e 's/\r//g' | ${ForeachFileLineLoopBashNode.Companion.sed} -ze '/\n$/!s/$/\n$/g' | while IFS=',' read -r first last email phone; do
                printf "${'$'}{first} ${'$'}{last} ${'$'}{email} ${'$'}{phone}\n"
            done
            
            """.trimIndent(), renderedBash
        )

        val bashResult = renderedBash.runCommand()
        Assertions.assertEquals(SCRIPT_SUCCESS, bashResult.second)
        Assertions.assertEquals(
            """
            Alice Smith alice.smith@email.com 555-1234
            Bob Johnson bob.j@email.com 555-5678
            Charlie Williams c.williams@email.com 555-9012
            
        """.trimIndent(), bashResult.first
        )
    }

    @Test
    fun foreach_fileLine_multistatement_works() {
        val script = """
            // Real world example
            HOST: readonly exported string = "HOST_NAME"
            TOKEN: readonly exported string = "OAUTH_TOKEN"
            for(firstName: string, middleName: string, lastName: string, email: string, landline: string, cell: string \
                    in "src/test/resources/data/example_extended.csv"):
                // set progress status too
                cellShort: exported string = #(printf "${'$'}cell" | cut -d " " -f 2)
                regionId: exported integer = 13
                print("Updating phone # " + cellShort + " with values: lastName " + lastName + " cell " + cell + ".\n")
                print("{ \"cellShort\": ${'$'}cellShort, \"lastName\": \"${'$'}lastName\" \"cell\": \"${'$'}cell\", " + \
                    "\"regionId\": \"${'$'}regionId\" }\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render(RenderOptions.UNQUOTED)
        Assertions.assertEquals(
            AstConvertingVisitor.Companion.STRICT_HEADER + """
            declare -x HOST
            HOST="HOST_NAME"
            declare -x TOKEN
            TOKEN="OAUTH_TOKEN"
            cat "src/test/resources/data/example_extended.csv" | ${ForeachFileLineLoopBashNode.Companion.sed} -e '1d' -e 's/\r//g' | ${ForeachFileLineLoopBashNode.Companion.sed} -ze '/\n$/!s/$/\n$/g' | while IFS=',' read -r firstName middleName lastName email landline cell; do
                declare -x cellShort
                cellShort="$(printf "${'$'}cell" | cut -d " " -f 2)"
                declare -x regionId
                regionId="13"
                printf "Updating phone # ${'$'}{cellShort} with values: lastName ${'$'}{lastName} cell ${'$'}{cell}.\n"
                printf "{ \"cellShort\": ${'$'}cellShort, \"lastName\": \"${'$'}lastName\" \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\" }\n"
            done
            
            """.trimIndent(), renderedBash
        )

        val bashResult = renderedBash.runCommand()
        Assertions.assertEquals(
            """
            Updating phone # 555-1235 with values: lastName Smith cell (555) 555-1235.
            { "cellShort": 555-1235, "lastName": "Smith" "cell": "(555) 555-1235", "regionId": "13" }
            Updating phone # 555-5679 with values: lastName Johnson cell (555) 555-5679.
            { "cellShort": 555-5679, "lastName": "Johnson" "cell": "(555) 555-5679", "regionId": "13" }
            Updating phone # 555-1701 with values: lastName Williams cell (555) 555-1701.
            { "cellShort": 555-1701, "lastName": "Williams" "cell": "(555) 555-1701", "regionId": "13" }

        """.trimIndent(), bashResult.first
        )
        Assertions.assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }

    @Test
    fun foreach_fileLine_multistatement_with_float_works() {
        val script = """
            // Real world example
            HOST: readonly exported string = "HOST_NAME"
            TOKEN: readonly exported string = "OAUTH_TOKEN"
            for(firstName: string, middleName: string, lastName: string, email: string, landline: float, \
                    cell: string in "src/test/resources/data/example_extended.csv"):
                // set progress status too
                cellShort: exported string = #(printf "${'$'}cell" | cut -d " " -f 2)
                regionId: exported integer = 13
                print("Updating phone # " + cellShort + " with values: lastName " + lastName + " cell " + cell + ".\n")
                print("{ \"cellShort\": ${'$'}cellShort, \"lastName\": \"${'$'}lastName\" \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\" }\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render(RenderOptions.UNQUOTED)
        Assertions.assertEquals(
            AstConvertingVisitor.Companion.STRICT_HEADER + """
            declare -x HOST
            HOST="HOST_NAME"
            declare -x TOKEN
            TOKEN="OAUTH_TOKEN"
            cat "src/test/resources/data/example_extended.csv" | ${ForeachFileLineLoopBashNode.Companion.sed} -e '1d' -e 's/\r//g' | ${ForeachFileLineLoopBashNode.Companion.sed} -ze '/\n$/!s/$/\n$/g' | while IFS=',' read -r firstName middleName lastName email landline cell; do
                declare -x cellShort
                cellShort="$(printf "${'$'}cell" | cut -d " " -f 2)"
                declare -x regionId
                regionId="13"
                printf "Updating phone # ${'$'}{cellShort} with values: lastName ${'$'}{lastName} cell ${'$'}{cell}.\n"
                printf "{ \"cellShort\": ${'$'}cellShort, \"lastName\": \"${'$'}lastName\" \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\" }\n"
            done
            
            """.trimIndent(), renderedBash
        )

        val bashResult = renderedBash.runCommand()
        Assertions.assertEquals(
            """
            Updating phone # 555-1235 with values: lastName Smith cell (555) 555-1235.
            { "cellShort": 555-1235, "lastName": "Smith" "cell": "(555) 555-1235", "regionId": "13" }
            Updating phone # 555-5679 with values: lastName Johnson cell (555) 555-5679.
            { "cellShort": 555-5679, "lastName": "Johnson" "cell": "(555) 555-5679", "regionId": "13" }
            Updating phone # 555-1701 with values: lastName Williams cell (555) 555-1701.
            { "cellShort": 555-1701, "lastName": "Williams" "cell": "(555) 555-1701", "regionId": "13" }

        """.trimIndent(), bashResult.first
        )
        Assertions.assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }

    @Test
    fun foreach_fileLine_multistatement_with_windows_line_endings_float_works() {
        val script = """
            // Real world example
            HOST: readonly exported string = "HOST_NAME"
            TOKEN: readonly exported string = "OAUTH_TOKEN"
            for(firstName: string, middleName: string, lastName: string, email: string, landline: float, cell: string\
                    in "src/test/resources/data/example_extended_windows_line_endings.csv"):
                // set progress status too
                cellShort: exported string = #(printf "${'$'}cell" | cut -d " " -f 2)
                regionId: exported integer = 13
                print("Updating phone # " + cellShort + " with values: lastName " + lastName + " cell " + cell + ".\n")
                print("{ \"cellShort\": ${'$'}cellShort, \"lastName\": \"${'$'}lastName\" \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\" }\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render(RenderOptions.UNQUOTED)
        Assertions.assertEquals(
            AstConvertingVisitor.Companion.STRICT_HEADER + """
            declare -x HOST
            HOST="HOST_NAME"
            declare -x TOKEN
            TOKEN="OAUTH_TOKEN"
            cat "src/test/resources/data/example_extended_windows_line_endings.csv" | ${ForeachFileLineLoopBashNode.Companion.sed} -e '1d' -e 's/\r//g' | ${ForeachFileLineLoopBashNode.Companion.sed} -ze '/\n$/!s/$/\n$/g' | while IFS=',' read -r firstName middleName lastName email landline cell; do
                declare -x cellShort
                cellShort="$(printf "${'$'}cell" | cut -d " " -f 2)"
                declare -x regionId
                regionId="13"
                printf "Updating phone # ${'$'}{cellShort} with values: lastName ${'$'}{lastName} cell ${'$'}{cell}.\n"
                printf "{ \"cellShort\": ${'$'}cellShort, \"lastName\": \"${'$'}lastName\" \"cell\": \"${'$'}cell\", \"regionId\": \"${'$'}regionId\" }\n"
            done
            
            """.trimIndent(), renderedBash
        )

        val bashResult = renderedBash.runCommand()
        Assertions.assertEquals(
            """
            Updating phone # 555-1235 with values: lastName Smith cell (555) 555-1235.
            { "cellShort": 555-1235, "lastName": "Smith" "cell": "(555) 555-1235", "regionId": "13" }
            Updating phone # 555-5679 with values: lastName Johnson cell (555) 555-5679.
            { "cellShort": 555-5679, "lastName": "Johnson" "cell": "(555) 555-5679", "regionId": "13" }
            Updating phone # 555-1701 with values: lastName Williams cell (555) 555-1701.
            { "cellShort": 555-1701, "lastName": "Williams" "cell": "(555) 555-1701", "regionId": "13" }

        """.trimIndent(), bashResult.first
        )
        Assertions.assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }

    @Test
    fun foreach_fileLine_non_csv_works() {
        val filename = "src/test/resources/data/plain.txt"
        val script = """
            for(line: string in "$filename"):
                print(line + "\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render(RenderOptions.UNQUOTED)
        Assertions.assertEquals(
            AstConvertingVisitor.Companion.STRICT_HEADER + """
            cat "$filename" | ${ForeachFileLineLoopBashNode.Companion.sed} -e 's/\r//g' | ${ForeachFileLineLoopBashNode.Companion.sed} -ze '/\n$/!s/$/\n$/g' | while IFS='' read -r line; do
                printf "${'$'}{line}\n"
            done
            
            """.trimIndent(), renderedBash
        )

        val bashResult = renderedBash.runCommand()
        Assertions.assertEquals(
            """
            lorum
            ipsum

        """.trimIndent(), bashResult.first
        )
        Assertions.assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }

    @Test
    fun foreach_fileLine_non_csv_no_trailing_newline_works() {
        val filename = "src/test/resources/data/plain_no_trailing_newline.txt"
        val script = """
            for(line: string in "$filename"):
                print(line + "\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render(RenderOptions.UNQUOTED)
        Assertions.assertEquals(
            AstConvertingVisitor.Companion.STRICT_HEADER + """
            cat "$filename" | ${ForeachFileLineLoopBashNode.Companion.sed} -e 's/\r//g' | ${ForeachFileLineLoopBashNode.Companion.sed} -ze '/\n$/!s/$/\n$/g' | while IFS='' read -r line; do
                printf "${'$'}{line}\n"
            done
            
            """.trimIndent(), renderedBash
        )

        val bashResult = renderedBash.runCommand()
        Assertions.assertEquals(
            """
            lorum
            ipsum

        """.trimIndent(), bashResult.first
        )
        Assertions.assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }

    @Test
    fun foreach_fileLine_scoping_works() {
        val filename = "src/test/resources/data/plain.txt"
        val script = """
            for(line: string in "$filename"):
                scoped: string = "Hello World"
                print(line + "\n")
            print(scoped + "\n")
        """.trimIndent().byteInputStream()
        val thrown: IllegalStateException? = assertThrows { fixture._getBast(script).render(RenderOptions.UNQUOTED) }
        Assertions.assertNotNull(thrown)
    }

    @Test
    fun foreach_fileLine_scoping_referenceOuterScope_works() {
        val filename = "src/test/resources/data/plain.txt"
        val script = """
            outerScope: string = "Hello Mars"
            for(line: string in "$filename"):
                print(outerScope + "\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render(RenderOptions.UNQUOTED)

        val bashResult = renderedBash.runCommand()
        Assertions.assertEquals(
            """
            Hello Mars
            Hello Mars

        """.trimIndent(), bashResult.first
        )
        Assertions.assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }

    @Test
    fun foreach_fileLine_scoping_variableShadowing_works() {
        val filename = "src/test/resources/data/plain.txt"
        val script = """
            line: string = "Who's line is it Anyway?"
            for(line: string in "$filename"):
                print(line + "\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render(RenderOptions.UNQUOTED)

        val bashResult = renderedBash.runCommand()
        Assertions.assertEquals(
            """
            lorum
            ipsum

        """.trimIndent(), bashResult.first
        )
        Assertions.assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }

    @Test
    fun foreach_fileLine_nested_works() {
        val outerFilename = "src/test/resources/data/labeled_lines.txt"
        val innerFilename = "src/test/resources/data/plain.txt"
        val script = """
            line: string = "Who's line is it Anyway?"
            for(line: string in "$outerFilename"):
                print(line + "\n")
                for(line2: string in "$innerFilename"):
                    print(line2 + "\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render(RenderOptions.UNQUOTED)

        val bashResult = renderedBash.runCommand()
        Assertions.assertEquals(
            """
            row1
            lorum
            ipsum
            row2
            lorum
            ipsum

        """.trimIndent(), bashResult.first
        )
        Assertions.assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }

    @Test
    fun foreach_fileLine_nested_withShadowing_works() {
        val outerFilename = "src/test/resources/data/labeled_lines.txt"
        val innerFilename = "src/test/resources/data/plain.txt"
        val script = """
            line: string = "Who's line is it Anyway?"
            for(line: string in "$outerFilename"):
                print(line + "\n")
                for(line: string in "$innerFilename"):
                    print(line + "\n")
        """.trimIndent().byteInputStream()
        val renderedBash = fixture._getBast(script).render(RenderOptions.UNQUOTED)

        val bashResult = renderedBash.runCommand()
        Assertions.assertEquals(
            """
            row1
            lorum
            ipsum
            row2
            lorum
            ipsum

        """.trimIndent(), bashResult.first
        )
        Assertions.assertEquals(SCRIPT_SUCCESS, bashResult.second)
    }
}