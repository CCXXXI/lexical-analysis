// Auto converted from java.
// Don't looks below! :)
#include <iostream>
#include <string>
#include <unordered_map>
#include <vector>

using namespace std;
struct TokenType {


    static int Comment;
    static int Constant;
    static int Identifier;
};

struct Token {
    // region punctuators
public:
    static Token MINUS;
    static Token MINUS_MINUS;
    static Token MINUS_EQ;
    static Token ARROW;
    static Token NOT;
    static Token NOT_EQ;
    static Token MOD;
    static Token MOD_EQ;
    static Token AND;
    static Token AND_AND;
    static Token AND_EQ;
    static Token L_PAREN;
    static Token R_PAREN;
    static Token MUL;
    static Token MUL_EQ;
    static Token COMMA;
    static Token DOT;
    static Token DIV;
    static Token DIV_EQ;
    static Token COLON;
    static Token SEMICOLON;
    static Token QUESTION;
    static Token L_BRACKET;
    static Token R_BRACKET;
    static Token XOR;
    static Token XOR_EQ;
    static Token L_BRACE;
    static Token OR;
    static Token OR_OR;
    static Token OR_EQ;
    static Token R_BRACE;
    static Token BIT_NOT;
    static Token PLUS;
    static Token PLUS_PLUS;
    static Token PLUS_EQ;
    static Token LT;
    static Token SHL;
    static Token SHL_EQ;
    static Token LE;
    static Token EQ;
    static Token EQ_EQ;
    static Token GT;
    static Token GE;
    static Token SHR;
    static Token SHR_EQ;
    static Token QUOTE;
    // endregion
    // region keywords
public:
    static Token AUTO;
    static Token BREAK;
    static Token CASE;
    static Token CHAR;
    static Token CONST;
    static Token CONTINUE;
    static Token DEFAULT;
    static Token DO;
    static Token DOUBLE;
    static Token ELSE;
    static Token ENUM;
    static Token EXTERN;
    static Token FLOAT;
    static Token FOR;
    static Token GOTO;
    static Token IF;
    static Token INT;
    static Token LONG;
    static Token REGISTER;
    static Token RETURN;
    static Token SHORT;
    static Token SIGNED;
    static Token SIZEOF;
    static Token STATIC;
    static Token STRUCT;
    static Token SWITCH;
    static Token TYPEDEF;
    static Token UNION;
    static Token UNSIGNED;
    static Token VOID;
    static Token VOLATILE;
    static Token WHILE;
    // endregion
    string text;
    int type;

    Token() {
        text = "";
        type = -1;
    }

    Token(string text, int type) {
        this->text = text;
        this->type = type;
    }

public:
    static unordered_map<string, Token> getKeywordsMap() {
        unordered_map<string, Token> map = unordered_map<string, Token>();
        map["auto"] = Token::AUTO;
        map["break"] = Token::BREAK;
        map["case"] = Token::CASE;
        map["char"] = Token::CHAR;
        map["const"] = Token::CONST;
        map["continue"] = Token::CONTINUE;
        map["default"] = Token::DEFAULT;
        map["do"] = Token::DO;
        map["double"] = Token::DOUBLE;
        map["else"] = Token::ELSE;
        map["enum"] = Token::ENUM;
        map["extern"] = Token::EXTERN;
        map["float"] = Token::FLOAT;
        map["for"] = Token::FOR;
        map["goto"] = Token::GOTO;
        map["if"] = Token::IF;
        map["int"] = Token::INT;
        map["long"] = Token::LONG;
        map["register"] = Token::REGISTER;
        map["return"] = Token::RETURN;
        map["short"] = Token::SHORT;
        map["signed"] = Token::SIGNED;
        map["sizeof"] = Token::SIZEOF;
        map["static"] = Token::STATIC;
        map["struct"] = Token::STRUCT;
        map["switch"] = Token::SWITCH;
        map["typedef"] = Token::TYPEDEF;
        map["union"] = Token::UNION;
        map["unsigned"] = Token::UNSIGNED;
        map["void"] = Token::VOID;
        map["volatile"] = Token::VOLATILE;
        map["while"] = Token::WHILE;
        return map;
    }

    static Token comment(string text) {
        return Token(text, TokenType::TokenType::Comment);
    }

    static Token constant(string text) {
        return Token(text, TokenType::TokenType::Constant);
    }

    static Token identifier(string text) {
        return Token(text, TokenType::TokenType::Identifier);
    }

    string toString() {
        return "<" + text + "," + to_string(type) + ">";
    }

};

int TokenType::Comment = 79;
int TokenType::Constant = 80;
int TokenType::Identifier = 81;

struct Lexer {
public:
    vector<Token> tokens = vector<Token>();
    unordered_map<string, Token> words = Token::getKeywordsMap();
    char curChar = ' ';
public :
    vector<Token> getTokens() {
        while (true) {
            skipBlank();
            if (curChar == EOF) {
                return tokens;
            }
            if (isdigit(curChar)) {
                tokens.push_back(handleNumber());
            } else {
                if (isalpha(curChar)) {
                    tokens.push_back(handleWord());
                } else {
                    if (curChar == '\"') {
                        tokens.push_back(Token::QUOTE);
                        nextChar();
                        tokens.push_back(handleString());
                        tokens.push_back(Token::QUOTE);
                        nextChar();
                    } else {
                        tokens.push_back(handleOthers());
                    }
                }
            }
        }
    }

public:
    void nextChar() {
        curChar = getchar();
    }

    void skipBlank() {
        while (isspace(curChar)) {
            nextChar();
        }
    }

    Token handleNumber() {
        string builder;
        for (; isdigit(curChar); nextChar()) {
            builder += curChar;
        }
        return Token::constant(builder);
    }

    Token handleWord() {
        string builder;
        for (; isalpha(curChar); nextChar()) {
            builder += curChar;
        }
        if (words.find(builder) != words.end()) {
            return words[builder];
        }
        return Token::identifier(builder);
    }

    Token handleString() {
        string builder;
        for (; curChar != '\"'; nextChar()) {
            builder += curChar;
        }
        return Token::identifier(builder);
    }

    Token handleOthers() {
        char preChar = curChar;
        nextChar();
        switch (preChar) {
        case '-':
            switch (curChar) {
            case '-':
                nextChar();
                return Token::MINUS_MINUS;
            case '=':
                nextChar();
                return Token::MINUS_EQ;
            case '>':
                nextChar();
                return Token::ARROW;
            default:
                return Token::MINUS;
            }
        case '!':
            if (curChar == '=') {
                nextChar();
                return Token::NOT_EQ;
            }
            return Token::NOT;
        case '%':
            if (curChar == '=') {
                nextChar();
                return Token::MOD_EQ;
            }
            return Token::MOD;
        case '&':
            switch (curChar) {
            case '&':
                nextChar();
                return Token::AND_AND;
            case '=':
                nextChar();
                return Token::AND_EQ;
            default:
                return Token::AND;
            }
        case '(':
            return Token::L_PAREN;
        case ')':
            return Token::R_PAREN;
        case '*':
            if (curChar == '=') {
                nextChar();
                return Token::MUL_EQ;
            }
            return Token::MUL;
        case ',':
            return Token::COMMA;
        case '.':
            return Token::DOT;
        case '/':
            switch (curChar) {
            case '=':
                nextChar();
                return Token::DIV_EQ;
            case '/':
                nextChar();
                return handleSingleLineComment();
            case '*':
                nextChar();
                return handleMultiLineComment();
            default:
                return Token::DIV;
            }
        case ':':
            return Token::COLON;
        case ';':
            return Token::SEMICOLON;
        case '?':
            return Token::QUESTION;
        case '[':
            return Token::L_BRACKET;
        case ']':
            return Token::R_BRACKET;
        case '^':
            if (curChar == '=') {
                nextChar();
                return Token::XOR_EQ;
            }
            return Token::XOR;
        case '{':
            return Token::L_BRACE;
        case '|':
            switch (curChar) {
            case '|':
                nextChar();
                return Token::OR_OR;
            case '=':
                nextChar();
                return Token::OR_EQ;
            default:
                return Token::OR;
            }
        case '}':
            return Token::R_BRACE;
        case '~':
            return Token::BIT_NOT;
        case '+':
            switch (curChar) {
            case '+':
                nextChar();
                return Token::PLUS_PLUS;
            case '=':
                nextChar();
                return Token::PLUS_EQ;
            default:
                return Token::PLUS;
            }
        case '<':
            switch (curChar) {
            case '<':
                nextChar();
                if (curChar == '=') {
                    nextChar();
                    return Token::SHL_EQ;
                }
                return Token::SHL;
            case '=':
                nextChar();
                return Token::LE;
            default:
                return Token::LT;
            }
        case '=':
            if (curChar == '=') {
                nextChar();
                return Token::EQ_EQ;
            }
            return Token::EQ;
        case '>':
            switch (curChar) {
            case '=':
                nextChar();
                return Token::GE;
            case '>':
                nextChar();
                if (curChar == '=') {
                    nextChar();
                    return Token::SHR_EQ;
                }
                return Token::SHR;
            default:
                return Token::GT;
            }
        }
    }

    Token handleSingleLineComment() {
        string builder;
        for (; curChar != '\n'; nextChar()) {
            builder += curChar;
        }
        return Token::comment("//" + builder);
    }

    Token handleMultiLineComment() {
        string builder;
        while (true) {
            for (; curChar != '*'; nextChar()) {
                builder += curChar;
            }
            nextChar();
            if (curChar == '/') {
                nextChar();
                return Token::comment("/*" + builder + "*/");
            } else {
                builder += '*';
            }
        }
    }
};

struct Java_LexAnalysis {
public:
    static void main() {
        Lexer lexer = Lexer();
        auto tokens = lexer.getTokens();
        for (int i = 0; i < tokens.size(); i++) {
            cout << (i + 1) << ": " << tokens[i].toString() << endl;
        }
    }
};

Token Token::MINUS = Token("-", 33);
Token Token::MINUS_MINUS = Token("--", 34);
Token Token::MINUS_EQ = Token("-=", 35);
Token Token::ARROW = Token("->", 36);
Token Token::NOT = Token("!", 37);
Token Token::NOT_EQ = Token("!=", 38);
Token Token::MOD = Token("%", 39);
Token Token::MOD_EQ = Token("%=", 40);
Token Token::AND = Token("&", 41);
Token Token::AND_AND = Token("&&", 42);
Token Token::AND_EQ = Token("&=", 43);
Token Token::L_PAREN = Token("(", 44);
Token Token::R_PAREN = Token(")", 45);
Token Token::MUL = Token("*", 46);
Token Token::MUL_EQ = Token("*=", 47);
Token Token::COMMA = Token(",", 48);
Token Token::DOT = Token(".", 49);
Token Token::DIV = Token("/", 50);
Token Token::DIV_EQ = Token("/=", 51);
Token Token::COLON = Token(":", 52);
Token Token::SEMICOLON = Token(";", 53);
Token Token::QUESTION = Token("?", 54);
Token Token::L_BRACKET = Token("[", 55);
Token Token::R_BRACKET = Token("]", 56);
Token Token::XOR = Token("^", 57);
Token Token::XOR_EQ = Token("^=", 58);
Token Token::L_BRACE = Token("{", 59);
Token Token::OR = Token("|", 60);
Token Token::OR_OR = Token("||", 61);
Token Token::OR_EQ = Token("|=", 62);
Token Token::R_BRACE = Token("}", 63);
Token Token::BIT_NOT = Token("~", 64);
Token Token::PLUS = Token("+", 65);
Token Token::PLUS_PLUS = Token("++", 66);
Token Token::PLUS_EQ = Token("+=", 67);
Token Token::LT = Token("<", 68);
Token Token::SHL = Token("<<", 69);
Token Token::SHL_EQ = Token("<<=", 70);
Token Token::LE = Token("<=", 71);
Token Token::EQ = Token("=", 72);
Token Token::EQ_EQ = Token("==", 73);
Token Token::GT = Token(">", 74);
Token Token::GE = Token(">=", 75);
Token Token::SHR = Token(">>", 76);
Token Token::SHR_EQ = Token(">>=", 77);
Token Token::QUOTE = Token("\"", 78);
Token Token::AUTO = Token("auto", 1);
Token Token::BREAK = Token("break", 2);
Token Token::CASE = Token("case", 3);
Token Token::CHAR = Token("char", 4);
Token Token::CONST = Token("const", 5);
Token Token::CONTINUE = Token("continue", 6);
Token Token::DEFAULT = Token("default", 7);
Token Token::DO = Token("do", 8);
Token Token::DOUBLE = Token("double", 9);
Token Token::ELSE = Token("else", 10);
Token Token::ENUM = Token("enum", 11);
Token Token::EXTERN = Token("extern", 12);
Token Token::FLOAT = Token("float", 13);
Token Token::FOR = Token("for", 14);
Token Token::GOTO = Token("goto", 15);
Token Token::IF = Token("if", 16);
Token Token::INT = Token("int", 17);
Token Token::LONG = Token("long", 18);
Token Token::REGISTER = Token("register", 19);
Token Token::RETURN = Token("return", 20);
Token Token::SHORT = Token("short", 21);
Token Token::SIGNED = Token("signed", 22);
Token Token::SIZEOF = Token("sizeof", 23);
Token Token::STATIC = Token("static", 24);
Token Token::STRUCT = Token("struct", 25);
Token Token::SWITCH = Token("switch", 26);
Token Token::TYPEDEF = Token("typedef", 27);
Token Token::UNION = Token("union", 28);
Token Token::UNSIGNED = Token("unsigned", 29);
Token Token::VOID = Token("void", 30);
Token Token::VOLATILE = Token("volatile", 31);
Token Token::WHILE = Token("while", 32);

void Analysis() {
    Java_LexAnalysis::main();
};

