import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.StringReader
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class LexerTest {
    @ParameterizedTest
    @MethodSource("provider")
    fun test(input: String, expected: String) {
        val lexer = Lexer(StringReader(input))
        val tokens = lexer.tokens
        val builder = StringBuilder()
        for (i in tokens.indices) {
            builder.append("${i + 1}: ${tokens[i]}\n")
        }
        assertEquals(expected, builder.toString())
    }

    private fun provider(): Stream<Arguments> = Stream.of(
        Arguments.of(
            """int main()
{
	printf("HelloWorld");
	return 0;
	}
""", """1: <int,17>
2: <main,81>
3: <(,44>
4: <),45>
5: <{,59>
6: <printf,81>
7: <(,44>
8: <",78>
9: <HelloWorld,81>
10: <",78>
11: <),45>
12: <;,53>
13: <return,20>
14: <0,80>
15: <;,53>
16: <},63>
"""
        ), Arguments.of(
            """int main()
{
	int i = 0;// 注释 test
	for (i = 0; i != 10; ++i)
	{
		printf("%d",i);
	}
	return 0;
}
""", """1: <int,17>
2: <main,81>
3: <(,44>
4: <),45>
5: <{,59>
6: <int,17>
7: <i,81>
8: <=,72>
9: <0,80>
10: <;,53>
11: <// 注释 test,79>
12: <for,14>
13: <(,44>
14: <i,81>
15: <=,72>
16: <0,80>
17: <;,53>
18: <i,81>
19: <!=,38>
20: <10,80>
21: <;,53>
22: <++,66>
23: <i,81>
24: <),45>
25: <{,59>
26: <printf,81>
27: <(,44>
28: <",78>
29: <%d,81>
30: <",78>
31: <,,48>
32: <i,81>
33: <),45>
34: <;,53>
35: <},63>
36: <return,20>
37: <0,80>
38: <;,53>
39: <},63>
"""
        ), Arguments.of(
            """int main(){char c = "h";/* 注释 12313test */if (c=="h")printf("%c",c);else print("k");return 0;}""",
            """1: <int,17>
2: <main,81>
3: <(,44>
4: <),45>
5: <{,59>
6: <char,4>
7: <c,81>
8: <=,72>
9: <",78>
10: <h,81>
11: <",78>
12: <;,53>
13: </* 注释 12313test */,79>
14: <if,16>
15: <(,44>
16: <c,81>
17: <==,73>
18: <",78>
19: <h,81>
20: <",78>
21: <),45>
22: <printf,81>
23: <(,44>
24: <",78>
25: <%c,81>
26: <",78>
27: <,,48>
28: <c,81>
29: <),45>
30: <;,53>
31: <else,10>
32: <print,81>
33: <(,44>
34: <",78>
35: <k,81>
36: <",78>
37: <),45>
38: <;,53>
39: <return,20>
40: <0,80>
41: <;,53>
42: <},63>
"""
        ), Arguments.of(
            """bool gofor(char& ch, string& pos, const string& prog)
{
	++pos;
	if (pos >= prog.size())
	{
		return false;
	}
	else
	{
		ch = prog[pos];
		return true;
	}
}
""", """1: <bool,81>
2: <gofor,81>
3: <(,44>
4: <char,4>
5: <&,41>
6: <ch,81>
7: <,,48>
8: <string,81>
9: <&,41>
10: <pos,81>
11: <,,48>
12: <const,5>
13: <string,81>
14: <&,41>
15: <prog,81>
16: <),45>
17: <{,59>
18: <++,66>
19: <pos,81>
20: <;,53>
21: <if,16>
22: <(,44>
23: <pos,81>
24: <>=,75>
25: <prog,81>
26: <.,49>
27: <size,81>
28: <(,44>
29: <),45>
30: <),45>
31: <{,59>
32: <return,20>
33: <false,81>
34: <;,53>
35: <},63>
36: <else,10>
37: <{,59>
38: <ch,81>
39: <=,72>
40: <prog,81>
41: <[,55>
42: <pos,81>
43: <],56>
44: <;,53>
45: <return,20>
46: <true,81>
47: <;,53>
48: <},63>
49: <},63>
"""
        )
    )
}
