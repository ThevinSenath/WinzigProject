import java.util.ArrayList;
import java.util.Arrays;

public class WinzigScanner {
    private final String stringProgram;

    private int position;

    protected WinzigScanner(String stringProgram){
        this.stringProgram = stringProgram;
    }

    private char getCurrentChar(){
        if(position >= stringProgram.length())
            return '\0';
        return stringProgram.charAt(position);
    }

    private Token getPreDefinedTokensWithLetters(){
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
        String lexeme = null;
        int start = position;
        if(chars.indexOf(getCurrentChar()) >= 10){
            position++;
            while(chars.indexOf(getCurrentChar()) >= 0){
                position++;
            }
            lexeme = stringProgram.substring(start, position);
        }

        if(lexeme != null){
            return switch (lexeme) {
                case "program" -> new Token("Program Token", lexeme, "program");
                case "var" -> new Token("Variable Token", lexeme, "var");
                case "const" -> new Token("Constant Token", lexeme, "const");
                case "type" -> new Token("Type Token", lexeme, "type");
                case "function" -> new Token("Function Token", lexeme, "function");
                case "return" -> new Token("Return Token", lexeme, "return");
                case "begin" -> new Token("Begin Token", lexeme, "begin");
                case "end" -> new Token("End Token", lexeme, "end");
                case "output" -> new Token("Output Token", lexeme, "output");
                case "if" -> new Token("If Token", lexeme, "if");
                case "then" -> new Token("Then Token", lexeme, "then");
                case "else" -> new Token("Else Token", lexeme, "else");
                case "while" -> new Token("While Token", lexeme, "while");
                case "do" -> new Token("Do Token", lexeme, "do");
                case "case" -> new Token("Case Token", lexeme, "case");
                case "of" -> new Token("Of Token", lexeme, "of");
                case "otherwise" -> new Token("Otherwise Token", lexeme, "otherwise");
                case "repeat" -> new Token("Repeat Token", lexeme, "repeat");
                case "for" -> new Token("For Token", lexeme, "for");
                case "until" -> new Token("Until Token", lexeme, "until");
                case "loop" -> new Token("Loop Token", lexeme, "loop");
                case "pool" -> new Token("Pool Token", lexeme, "pool");
                case "exit" -> new Token("Exit Token", lexeme, "exit");
                case "mod" -> new Token("Mod Token", lexeme, "mod");
                case "or" -> new Token("Or Token", lexeme, "or");
                case "and" -> new Token("And Token", lexeme, "and");
                case "not" -> new Token("Not Token", lexeme, "not");
                case "read" -> new Token("Read Token", lexeme, "read");
                case "succ" -> new Token("Successor Token", lexeme, "succ");
                case "pred" -> new Token("Predecessor Token", lexeme, "pred");
                case "chr" -> new Token("Char Function Token", lexeme, "chr");
                case "ord" -> new Token("Ordinal Function Token", lexeme, "ord");
                case "eof" -> new Token("End of File Token", lexeme, "eof");
                default -> new Token("Identifier Token", lexeme, "<identifier>");
            };
        }
        return null;
    }

    private Token getCommentTypeOne(){
        int start = position;
        if(getCurrentChar() == '{'){
            position++;
            while(getCurrentChar() != '}'){
                position++;
            }
            position++;
            return new Token("Type 1 Comment Token" , stringProgram.substring(start, position), "#COMMENT");
        }
        return null;
    }

    private Token getCommentTypeTwo(){
        int start = position;
        if(getCurrentChar() == '#'){
            position++;
            while(getCurrentChar() != '\n'){
                position++;
            }
            return new Token("Type 2 Comment Token", stringProgram.substring(start, position), "#COMMENT");
        }
        return null;
    }

    private Token getNewLine(){
        if(getCurrentChar() == '\n'){
            position++;
            return new Token("New Line Token", "\n", "NEWLINE");
        }
        return null;
    }

    private Token getWhiteSpace(){
        ArrayList<Character> whiteSpaceCharacters =  new ArrayList<>(Arrays.asList(' ', '\t', '\r', '\f'));
        int start = position;
        if (whiteSpaceCharacters.contains(getCurrentChar())) {
            position++;
            while (whiteSpaceCharacters.contains(getCurrentChar())){
                position++;
            }
            return new Token("White Space Token", stringProgram.substring(start, position), "WHITESPACE");
        }
        return null;
    }

    private Token getInteger(){
        ArrayList<Character> integerCharacters = new ArrayList<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        int start  = position;
        if (integerCharacters.contains(getCurrentChar()))  {
            position++;
            while (integerCharacters.contains(getCurrentChar())) {
                position++;
            }
            return new Token("Integer Token", stringProgram.substring(start, position), "<integer>");
        }
        return null;
    }

    private Token getChars(){
        int start = position;
        if(getCurrentChar() == '\'' & stringProgram.charAt(position + 2 ) == '\'' & stringProgram.charAt(position +1 ) != '\''){
            position += 3;
            return new Token("Character Token", stringProgram.substring(start, position), "<char>");
        }
        return  null;
    }

    private Token getStrings(){
        int start = position;
        if(getCurrentChar() == '"'){
            position++;
            while(getCurrentChar() != '"'){
                position++;
            }
            position++;
            return new Token("String Token", stringProgram.substring(start, position), "<string>");
        }
        return null;
    }

    // :=:
    private Token getTokensWithLengthThree() {
        String syntax_len_3 = stringProgram.substring(position, position + 3);
        if(syntax_len_3.equals(":=:")){
            position += 3;
            return new Token("Swap Token", syntax_len_3, ":=:");
        }
        return null;
    }

    // ":=", "..", "<=", "<>", ">="
    private Token getTokensWithLengthTwo() {
        String syntax_len_2 = stringProgram.substring(position, position + 2);
        switch (syntax_len_2) {
            case ":=" -> {
                position += 2;
                return new Token("Assign Token", syntax_len_2, ":=");
            }
            case ".." -> {
                position += 2;
                return new Token("Case Exp Token", syntax_len_2, "..");
            }
            case "<=" -> {
                position += 2;
                return new Token("Less or Equal Token", syntax_len_2, "<=");
            }
            case "<>" -> {
                position += 2;
                return new Token("Not Equal Token", syntax_len_2, "<>");
            }
            case ">=" -> {
                position += 2;
                return new Token("Greater or Equal Token", syntax_len_2, ">=");
            }
            default -> {
                return null;
            }
        }
    }

    //  ":", ".", "<", ">", "=", ";", ",", "(", ")", "+", "-", "*", "/"
    private Token getTokensWithLengthOne() {
        char syntax_len_1 =  getCurrentChar();
        switch (syntax_len_1) {
            case ':' -> {
                position++;
                return new Token("Colon Token", Character.toString(syntax_len_1), ":");
            }
            case '.' -> {
                position++;
                return new Token("Single Dot Token", Character.toString(syntax_len_1), ".");
            }
            case '<' -> {
                position++;
                return new Token("Less Than Token", Character.toString(syntax_len_1), "<");
            }
            case '>' -> {
                position++;
                return new Token("Greater Than Token", Character.toString(syntax_len_1), ">");
            }
            case '=' -> {
                position++;
                return new Token("Equal Token", Character.toString(syntax_len_1), "=");
            }
            case ';' -> {
                position++;
                return new Token("Semi Colon Token", Character.toString(syntax_len_1), ";");
            }
            case ',' -> {
                position++;
                return new Token("Comma Token", Character.toString(syntax_len_1), ",");
            }
            case '(' -> {
                position++;
                return new Token("Open Bracket Token", Character.toString(syntax_len_1), "(");
            }
            case ')' -> {
                position++;
                return new Token("Close Bracket Token", Character.toString(syntax_len_1), ")");
            }
            case '+' -> {
                position++;
                return new Token("Plus Token", Character.toString(syntax_len_1), "+");
            }
            case '-' -> {
                position++;
                return new Token("Minus Token", Character.toString(syntax_len_1), "-");
            }
            case '*' -> {
                position++;
                return new Token("Multiply Token", Character.toString(syntax_len_1), "*");
            }
            case '/' -> {
                position++;
                return new Token("Divide Token", Character.toString(syntax_len_1), "/");
            }
            default -> {
                return null;
            }
        }
    }

    protected Token findNextToken(){

        if(position >= stringProgram.length()){
            return new Token("End of Program Token", "\0", null);
        }

        Token preDefinedTokenWithLetters = getPreDefinedTokensWithLetters();
        if(preDefinedTokenWithLetters != null){ return preDefinedTokenWithLetters; }

        // comment type 1
        Token commentTypeOne = getCommentTypeOne();
        if(commentTypeOne != null){ return commentTypeOne; }

        // comment type 2
        Token commentTypeTwo = getCommentTypeTwo();
        if(commentTypeTwo != null){ return commentTypeTwo; }

        // new line
        Token newLine = getNewLine();
        if(newLine != null){ return newLine; }

        // white spaces
        Token whiteSpace = getWhiteSpace();
        if(whiteSpace != null){ return whiteSpace; }

        // integers
        Token integers = getInteger();
        if(integers != null){ return integers; }

        // chars
        Token chars = getChars();
        if(chars != null){ return chars; }

        // strings
        Token strings = getStrings();
        if(strings != null){ return strings; }

        // :=:
        Token tokensWithLengthThree = getTokensWithLengthThree();
        if(tokensWithLengthThree != null){ return tokensWithLengthThree; }

        // ":=", "..", "<=", "<>", ">="
        Token tokensWithLengthTwo = getTokensWithLengthTwo();
        if(tokensWithLengthTwo != null){ return tokensWithLengthTwo; }

        //  ":", ".", "<", ">", "=", ";", ",", "(", ")", "+", "-", "*", "/"
        Token tokensWithLengthOne = getTokensWithLengthOne();
        if(tokensWithLengthOne != null){ return tokensWithLengthOne; }

        return new Token("Unknown Token", null, "UNKNOWN_TOKEN");
    }
}
