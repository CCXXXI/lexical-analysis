import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.io.StringReader

class LexerTest {
    @ParameterizedTest
    @CsvFileSource(resources = ["data.csv"])
    fun test(input: String, expected: String) {
        val lexer = Lexer(StringReader(input))
        val tokens = lexer.tokens
        val builder = StringBuilder()
        for (i in tokens.indices) {
            builder.append("${i + 1}: ${tokens[i]}\n")
        }
        assertEquals(expected.trim(), builder.toString().trim())
    }
}
