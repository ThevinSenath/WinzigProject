import java.io.IOException;
import java.util.ArrayList;

public class WinzigComplier {

    public static void main(String[] args) throws IOException {
        String common_path = "D:\\University\\8th sem\\Compiler Design\\Project\\winzigc_new\\src\\winzig_test_programs\\";
        String program_file_name = "winzig_25";
        String program_path = common_path + program_file_name;

        ArrayList<Token> tokenStream = new ArrayList<>();
        WinzigScreener winzigScreener = new WinzigScreener(tokenStream);
        String programString = winzigScreener.readWinzigProgram(program_path);

        WinzigScanner winzigScanner = new WinzigScanner(programString);

        String tokenName;

        do{
            Token token = winzigScanner.findNextToken();
            tokenStream.add(token);
            tokenName = token.getTokenName();
        }
        while(!tokenName.equals("End of Program Token"));

        ArrayList<Token> screenedTokenStream = winzigScreener.screenTokenStream();

        WinzigParser parser = new WinzigParser(screenedTokenStream);
        parser.printAST();
    }
}
