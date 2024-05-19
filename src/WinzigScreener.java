import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class WinzigScreener {
    private final ArrayList<Token> tokenStream;

    protected WinzigScreener(ArrayList<Token> tokenStream) {
        this.tokenStream = tokenStream;
    }

    protected String readWinzigProgram(String path) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder + "   ";
        }
    }

    protected ArrayList<Token> screenTokenStream(){
        ArrayList<Token> screenedToken = new ArrayList<>();
        for(Token token : tokenStream){
            String tokenName = token.getTokenName();
            if(!tokenName.equals("Type 1 Comment Token") & !tokenName.equals("Type 2 Comment Token") & !tokenName.equals("White Space Token") & !tokenName.equals("New Line Token") & !tokenName.equals("End of Program Token")){
                screenedToken.add(token);
            }
        }
        return screenedToken;
    }
}
