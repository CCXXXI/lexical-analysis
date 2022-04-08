import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Java_LexAnalysis {
  public static void main(String[] args) throws IOException {
    final Lexer lexer = new Lexer(new BufferedReader(new InputStreamReader(System.in)));
    final List<Token> tokens = lexer.getTokens();
    for (int i = 0; i < tokens.size(); i++) {
      System.out.println(i + 1 + ": " + tokens.get(i));
    }
  }
}

class Lexer {
  private final Reader reader;
  private final List<Token> tokens = new ArrayList<>();
  private final Map<String, Token> words = Token.getKeywordsMap();
  private char curChar = ' ';

  Lexer(Reader reader) {
    this.reader = reader;
  }

  public List<Token> getTokens() throws IOException {
    while (true) {
      skipBlank();
      if (curChar == (char) -1) {
        return tokens;
      }
      if (Character.isDigit(curChar)) {
        tokens.add(handleNumber());
      } else if (Character.isLetter(curChar)) {
        tokens.add(handleWord());
      } else if (curChar == '"') {
        tokens.add(Token.QUOTE);
        nextChar();
        tokens.add(handleString());
        tokens.add(Token.QUOTE);
        nextChar();
      } else {
        tokens.add(handleOthers());
      }
    }
  }

  private void nextChar() throws IOException {
    curChar = (char) reader.read();
  }

  private void skipBlank() throws IOException {
    while (Character.isWhitespace(curChar)) {
      nextChar();
    }
  }

  private Token handleNumber() throws IOException {
    final StringBuilder builder = new StringBuilder();
    for (; Character.isDigit(curChar); nextChar()) {
      builder.append(curChar);
    }
    return Token.constant(builder.toString());
  }

  private Token handleWord() throws IOException {
    final StringBuilder builder = new StringBuilder();
    for (; Character.isLetter(curChar); nextChar()) {
      builder.append(curChar);
    }
    return words.computeIfAbsent(builder.toString(), Token::identifier);
  }

  private Token handleString() throws IOException {
    final StringBuilder builder = new StringBuilder();
    for (; curChar != '"'; nextChar()) {
      if (curChar == '\n' || curChar == (char) -1) {
        throw new RuntimeException("Unclosed string: " + builder);
      }
      builder.append(curChar);
    }
    return Token.identifier(builder.toString());
  }

  private Token handleOthers() throws IOException {
    final char preChar = curChar;
    nextChar();
    switch (preChar) {
      case '-':
        switch (curChar) {
          case '-':
            nextChar();
            return Token.MINUS_MINUS;
          case '=':
            nextChar();
            return Token.MINUS_EQ;
          case '>':
            nextChar();
            return Token.ARROW;
          default:
            return Token.MINUS;
        }
      case '!':
        if (curChar == '=') {
          nextChar();
          return Token.NOT_EQ;
        }
        return Token.NOT;
      case '%':
        if (curChar == '=') {
          nextChar();
          return Token.MOD_EQ;
        }
        return Token.MOD;
      case '&':
        switch (curChar) {
          case '&':
            nextChar();
            return Token.AND_AND;
          case '=':
            nextChar();
            return Token.AND_EQ;
          default:
            return Token.AND;
        }
      case '(':
        return Token.L_PAREN;
      case ')':
        return Token.R_PAREN;
      case '*':
        if (curChar == '=') {
          nextChar();
          return Token.MUL_EQ;
        }
        return Token.MUL;
      case ',':
        return Token.COMMA;
      case '.':
        return Token.DOT;
      case '/':
        switch (curChar) {
          case '=':
            nextChar();
            return Token.DIV_EQ;
          case '/':
            nextChar();
            return handleSingleLineComment();
          case '*':
            nextChar();
            return handleMultiLineComment();
          default:
            return Token.DIV;
        }
      case ':':
        return Token.COLON;
      case ';':
        return Token.SEMICOLON;
      case '?':
        return Token.QUESTION;
      case '[':
        return Token.L_BRACKET;
      case ']':
        return Token.R_BRACKET;
      case '^':
        if (curChar == '=') {
          nextChar();
          return Token.XOR_EQ;
        }
        return Token.XOR;
      case '{':
        return Token.L_BRACE;
      case '|':
        switch (curChar) {
          case '|':
            nextChar();
            return Token.OR_OR;
          case '=':
            nextChar();
            return Token.OR_EQ;
          default:
            return Token.OR;
        }
      case '}':
        return Token.R_BRACE;
      case '~':
        return Token.BIT_NOT;
      case '+':
        switch (curChar) {
          case '+':
            nextChar();
            return Token.PLUS_PLUS;
          case '=':
            nextChar();
            return Token.PLUS_EQ;
          default:
            return Token.PLUS;
        }
      case '<':
        switch (curChar) {
          case '<':
            nextChar();
            if (curChar == '=') {
              nextChar();
              return Token.SHL_EQ;
            }
            return Token.SHL;
          case '=':
            nextChar();
            return Token.LE;
          default:
            return Token.LT;
        }
      case '=':
        if (curChar == '=') {
          nextChar();
          return Token.EQ_EQ;
        }
        return Token.EQ;
      case '>':
        switch (curChar) {
          case '=':
            nextChar();
            return Token.GE;
          case '>':
            nextChar();
            if (curChar == '=') {
              nextChar();
              return Token.SHR_EQ;
            }
            return Token.SHR;
          default:
            return Token.GT;
        }
      default:
        throw new RuntimeException("Unhandled: " + preChar);
    }
  }

  private Token handleSingleLineComment() throws IOException {
    final StringBuilder builder = new StringBuilder();
    for (; curChar != '\n'; nextChar()) {
      builder.append(curChar);
    }
    return Token.comment("//" + builder);
  }

  private Token handleMultiLineComment() throws IOException {
    final StringBuilder builder = new StringBuilder();
    while (true) {
      for (; curChar != '*'; nextChar()) {
        builder.append(curChar);
      }
      nextChar();
      if (curChar == '/') {
        nextChar();
        return Token.comment("/*" + builder + "*/");
      } else {
        builder.append('*');
      }
    }
  }
}

class Token {
  // region punctuators
  public static final Token MINUS = new Token("-", 33);
  public static final Token MINUS_MINUS = new Token("--", 34);
  public static final Token MINUS_EQ = new Token("-=", 35);
  public static final Token ARROW = new Token("->", 36);
  public static final Token NOT = new Token("!", 37);
  public static final Token NOT_EQ = new Token("!=", 38);
  public static final Token MOD = new Token("%", 39);
  public static final Token MOD_EQ = new Token("%=", 40);
  public static final Token AND = new Token("&", 41);
  public static final Token AND_AND = new Token("&&", 42);
  public static final Token AND_EQ = new Token("&=", 43);
  public static final Token L_PAREN = new Token("(", 44);
  public static final Token R_PAREN = new Token(")", 45);
  public static final Token MUL = new Token("*", 46);
  public static final Token MUL_EQ = new Token("*=", 47);
  public static final Token COMMA = new Token(",", 48);
  public static final Token DOT = new Token(".", 49);
  public static final Token DIV = new Token("/", 50);
  public static final Token DIV_EQ = new Token("/=", 51);
  public static final Token COLON = new Token(":", 52);
  public static final Token SEMICOLON = new Token(";", 53);
  public static final Token QUESTION = new Token("?", 54);
  public static final Token L_BRACKET = new Token("[", 55);
  public static final Token R_BRACKET = new Token("]", 56);
  public static final Token XOR = new Token("^", 57);
  public static final Token XOR_EQ = new Token("^=", 58);
  public static final Token L_BRACE = new Token("{", 59);
  public static final Token OR = new Token("|", 60);
  public static final Token OR_OR = new Token("||", 61);
  public static final Token OR_EQ = new Token("|=", 62);
  public static final Token R_BRACE = new Token("}", 63);
  public static final Token BIT_NOT = new Token("~", 64);
  public static final Token PLUS = new Token("+", 65);
  public static final Token PLUS_PLUS = new Token("++", 66);
  public static final Token PLUS_EQ = new Token("+=", 67);
  public static final Token LT = new Token("<", 68);
  public static final Token SHL = new Token("<<", 69);
  public static final Token SHL_EQ = new Token("<<=", 70);
  public static final Token LE = new Token("<=", 71);
  public static final Token EQ = new Token("=", 72);
  public static final Token EQ_EQ = new Token("==", 73);
  public static final Token GT = new Token(">", 74);
  public static final Token GE = new Token(">=", 75);
  public static final Token SHR = new Token(">>", 76);
  public static final Token SHR_EQ = new Token(">>=", 77);
  public static final Token QUOTE = new Token("\"", 78);
  // endregion

  // region keywords
  private static final Token AUTO = new Token("auto", 1);
  private static final Token BREAK = new Token("break", 2);
  private static final Token CASE = new Token("case", 3);
  private static final Token CHAR = new Token("char", 4);
  private static final Token CONST = new Token("const", 5);
  private static final Token CONTINUE = new Token("continue", 6);
  private static final Token DEFAULT = new Token("default", 7);
  private static final Token DO = new Token("do", 8);
  private static final Token DOUBLE = new Token("double", 9);
  private static final Token ELSE = new Token("else", 10);
  private static final Token ENUM = new Token("enum", 11);
  private static final Token EXTERN = new Token("extern", 12);
  private static final Token FLOAT = new Token("float", 13);
  private static final Token FOR = new Token("for", 14);
  private static final Token GOTO = new Token("goto", 15);
  private static final Token IF = new Token("if", 16);
  private static final Token INT = new Token("int", 17);
  private static final Token LONG = new Token("long", 18);
  private static final Token REGISTER = new Token("register", 19);
  private static final Token RETURN = new Token("return", 20);
  private static final Token SHORT = new Token("short", 21);
  private static final Token SIGNED = new Token("signed", 22);
  private static final Token SIZEOF = new Token("sizeof", 23);
  private static final Token STATIC = new Token("static", 24);
  private static final Token STRUCT = new Token("struct", 25);
  private static final Token SWITCH = new Token("switch", 26);
  private static final Token TYPEDEF = new Token("typedef", 27);
  private static final Token UNION = new Token("union", 28);
  private static final Token UNSIGNED = new Token("unsigned", 29);
  private static final Token VOID = new Token("void", 30);
  private static final Token VOLATILE = new Token("volatile", 31);
  private static final Token WHILE = new Token("while", 32);
  // endregion
  private final String text;
  private final int type;

  private Token(String text, int type) {
    this.text = text;
    this.type = type;
  }

  public static Map<String, Token> getKeywordsMap() {
    Map<String, Token> map = new HashMap<>();

    map.put("auto", AUTO);
    map.put("break", BREAK);
    map.put("case", CASE);
    map.put("char", CHAR);
    map.put("const", CONST);
    map.put("continue", CONTINUE);
    map.put("default", DEFAULT);
    map.put("do", DO);
    map.put("double", DOUBLE);
    map.put("else", ELSE);
    map.put("enum", ENUM);
    map.put("extern", EXTERN);
    map.put("float", FLOAT);
    map.put("for", FOR);
    map.put("goto", GOTO);
    map.put("if", IF);
    map.put("int", INT);
    map.put("long", LONG);
    map.put("register", REGISTER);
    map.put("return", RETURN);
    map.put("short", SHORT);
    map.put("signed", SIGNED);
    map.put("sizeof", SIZEOF);
    map.put("static", STATIC);
    map.put("struct", STRUCT);
    map.put("switch", SWITCH);
    map.put("typedef", TYPEDEF);
    map.put("union", UNION);
    map.put("unsigned", UNSIGNED);
    map.put("void", VOID);
    map.put("volatile", VOLATILE);
    map.put("while", WHILE);

    return map;
  }

  public static Token comment(String text) {
    return new Token(text, TokenType.Comment);
  }

  public static Token constant(String text) {
    return new Token(text, TokenType.Constant);
  }

  public static Token identifier(String text) {
    return new Token(text, TokenType.Identifier);
  }

  @Override
  public String toString() {
    return "<" + text + "," + type + ">";
  }

  private static class TokenType {
    public static final int Comment = 79;
    public static final int Constant = 80;
    public static final int Identifier = 81;
  }
}
