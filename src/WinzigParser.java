import java.util.ArrayList;
import java.util.Stack;

public class WinzigParser {
    private final Stack<TreeNode> treeStack;

    private final ArrayList<Token> tokenStream;

    private int tokenIndex;

    private Token nextToken;

    protected WinzigParser(ArrayList<Token> tokenStream){
        this.tokenStream = tokenStream;
        treeStack = new Stack<>();
    }

    protected void printAST(){
        getNextToken();
        winZig();
    }

    private boolean hasNext(){
        return tokenIndex <= tokenStream.size() - 1;
    }

    private String peek(){
        if(tokenIndex <= tokenStream.size()-1){
            return tokenStream.get(tokenIndex).getType();
        }
        System.out.println("TOKEN ARE OVER");
        throw new Error();
    }

    // set the next token and increment the index
    private void getNextToken(){
        nextToken = tokenStream.get(tokenIndex);
        tokenIndex++;
    }

    private void read(String type) {
        if(!nextToken.getType().equals(type)){
            System.out.println("Error: expected |"+type+"| but found " + nextToken.getType() + ".");
            throw new Error();
        }

        if(hasNext()){
            getNextToken();
        }
    }

    private void readByTokenName(String tokenName){

        if(!nextToken.getTokenName().equals(tokenName)) {
            System.out.println("Error: expected " + tokenName + " but found " + nextToken.getTokenName() + ".");
            throw new Error();
        }

        TreeNode node_1 = new TreeNode(nextToken.getType());
        TreeNode node_2 = new TreeNode(nextToken.getValue());

        node_1.setLeftChild(node_2);

        node_1.setChildCount(1);
        treeStack.push(node_1);

        getNextToken();
    }

    private void buildTree(String node_label, int count){
        TreeNode node = new TreeNode(node_label);
        TreeNode p = null;

        for(int j = 0; j < count; j++){
            TreeNode c = treeStack.pop();
            if(p != null){
                c.setRightChild(p);
            }
            p = c;
        }
        node.setLeftChild(p);
        node.setChildCount(count);
        treeStack.push(node);
    }

    private void winZig(){
        read("program");
        Name();
        read(":");
        Consts();
        Types();
        Dclns();
        SubProgs();
        Body();
        Name();
        read(".");

        buildTree("program" ,7);

        for(TreeNode node : treeStack){
            node.traverseTree(0);
        }

    }

    private void Name(){
        readByTokenName("Identifier Token");
    }

    private void Consts(){
        if(nextToken.getType().equals("const")){
            read("const");
            int list_count = 1;
            Const();
            while(!nextToken.getType().equals(";")){
                read(",");
                Const();
                list_count += 1;
            }
            read(";");
            buildTree("consts" ,list_count);
        }else{
            buildTree("consts" ,0);
        }
    }

    private void Const(){
        Name();
        read("=");
        ConstValue();

        buildTree("const", 2);
    }

    private void ConstValue(){
        // skip <char> or <integer> but create a node for <identifier>
        if(nextToken.getType().equals("<char>") || nextToken.getType().equals("<integer>") ){
            readByTokenName(nextToken.getTokenName());
        }

        if(nextToken.getType().equals("<identifier>")){
            Name();
        }
    }

    private void Types(){
        if(nextToken.getType().equals("type")){
            read("type");
            int count = 0;
            while(nextToken.getType().equals("<identifier>")){
                Type();
                read(";");
                count++;
            }
            buildTree("types", count);

        }else{
            buildTree("types", 0);
        }
    }

    private void Type(){
        if(nextToken.getType().equals("<identifier>")){
            Name();
            read("=");
            LitList();

            buildTree("type", 2);
        }
    }

    private void LitList(){
        read("(");
        Name();
        int count = 1;
        while(!nextToken.getType().equals(")")){
            read(",");
            Name();
            count++;
        }
        read(")");
        buildTree("lit", count);
    }

    private void Dclns(){
        if(nextToken.getType().equals("var")){
            read("var");
            Dcln();
            read(";");
            int count = 1;
            while(nextToken.getType().equals("<identifier>")){
                Dcln();
                read(";");
                count++;
            }

            buildTree("dclns", count);
        }else{
            buildTree("dclns", 0);
        }
    }

    private void Dcln(){
        Name();
        int count = 1;
        while(!nextToken.getType().equals(":")){
            read(",");
            Name();
            count++;
        }
        read(":");
        Name();
        count++;

        buildTree("var", count);
    }

    private void SubProgs(){

        Fcn();
        int count = 1;
        while(nextToken.getTokenName().equals("Function Token")){
            Fcn();
            count++;
        }

        buildTree("subprogs", count);
    }

    private void Fcn(){
        read("function");
        Name();
        read("(");
        Params();
        read(")");
        read(":");
        Name();
        read(";");
        Consts();
        Types();
        Dclns();
        Body();
        Name();
        read(";");

        buildTree("fcn",8);
    }

    private void Params(){
        Dcln();
        int count = 1;
        while(nextToken.getType().equals(";")){
            read(";");
            Dcln();
            count++;
        }

        buildTree("params", count);
    }

    private void Body(){
        read("begin");
        Statement();
        int count = 1;
        while(nextToken.getType().equals(";")){
            read(";");
            Statement();
            count++;
        }
        read("end");

        buildTree("block", count);
    }

    private void Statement(){
        int count;
        switch (nextToken.getType()) {
            case "if" -> {
                read("if");
                Expression();
                read("then");
                Statement();
                count = 2;
                if (nextToken.getType().equals("else")) {
                    read("else");
                    Statement();
                    count++;
                }
                buildTree("if", count);
            }
            case "for" -> {
                read("for");
                read("(");
                ForStat();
                read(";");
                ForExp();
                read(";");
                ForStat();
                read(")");
                Statement();
                buildTree("for", 4);
            }
            case "while" -> {
                read("while");
                Expression();
                read("do");
                Statement();
                buildTree("while", 2);
            }
            case "repeat" -> {
                read("repeat");
                Statement();
                count = 1;
                while (nextToken.getType().equals(";")) {
                    read(";");
                    Statement();
                    count++;
                }
                read("until");
                Expression();
                count++;
                buildTree("repeat", count);
            }
            case "loop" -> {
                read("loop");
                Statement();
                count = 1;
                while (nextToken.getType().equals(";")) {
                    read(";");
                    Statement();
                    count++;
                }
                read("pool");
                buildTree("loop", count);
            }
            case "output" -> {
                read("output");
                read("(");
                OutEXp();
                count = 1;
                // out exp list
                while (nextToken.getType().equals(",")) {
                    read(",");
                    OutEXp();
                    count++;
                }
                read(")");
                buildTree("output", count);
            }
            case "exit" -> {
                read("exit");
                buildTree("exit", 0);
            }
            case "return" -> {
                read("return");
                Expression();
                buildTree("return", 1);
            }
            case "read" -> {
                read("read");
                read("(");
                Name();
                count = 1;
                while (nextToken.getType().equals(",")) {
                    read(",");
                    Name();
                    count++;
                }
                read(")");
                buildTree("read", count);
            }
            case "case" -> {
                read("case");
                Expression();
                read("of");
                count = 1;
                count += Caseclauses();
                count += OtherwiseClause();
                read("end");
                buildTree("case", count);
            }
            case "<identifier>" -> Assignment();
            case "begin" -> Body();
            default -> buildTree("<null>", 0);
        }
    }

    private int Caseclauses(){
        Caseclause();
        read(";");
        int count = 1;
        while(nextToken.getType().equals("<integer>") || nextToken.getType().equals("<char>") || nextToken.getType().equals("<identifier>")){
            Caseclause();
            read(";");
            count++;
        }

        return count;
    }

    private void Caseclause(){
        CaseExpression();
        int count = 1;
        while(nextToken.getType().equals(",")){
            read(",");
            CaseExpression();
            count++;
        }
        read(":");
        Statement();
        count++;
        buildTree("case_clause", count);
    }

    private void CaseExpression(){
        ConstValue();
        if(nextToken.getType().equals("..")){
            read("..");
            ConstValue();
            buildTree("..",2);
        }
    }

    private int  OtherwiseClause(){
        if(nextToken.getType().equals("otherwise")){
            read("otherwise");
            Statement();
            buildTree("otherwise",1);
            return 1;
        }else{
            return 0;
        }
    }

    private void OutEXp(){
        if(nextToken.getType().equals("<string>")){
            StringNode();
        }else{
            Expression();
            buildTree("integer", 1);
        }
    }

    private void StringNode(){
        readByTokenName("String Token");
    }

    private void ForStat(){
        if(nextToken.getType().equals(";")){
            buildTree("<null>",0);
        }else{
            Assignment();
        }
    }

    private void ForExp(){
        if(nextToken.getType().equals(";")){
            buildTree("true",0);
        }else{
            Expression();
        }
    }

    private void Assignment(){
        switch (peek()) {
            case ":=" -> {
                Name();
                read(":=");
                Expression();
                buildTree("assign", 2);
            }
            case ":=:" -> {
                Name();
                read(":=:");
                Name();
                buildTree("swap", 2);
            }
            default -> throw new Error();
        }
    }

    private void Expression(){
        Term();
        if(nextToken.getType().equals("<=") || nextToken.getType().equals("<") || nextToken.getType().equals(">=") || nextToken.getType().equals(">") || nextToken.getType().equals("=") || nextToken.getType().equals("<>")){
            switch (nextToken.getType()) {
                case "<=" -> {
                    read("<=");
                    Term();
                    buildTree("<=", 2);
                }
                case "<" -> {
                    read("<");
                    Term();
                    buildTree("<", 2);
                }
                case ">=" -> {
                    read(">=");
                    Term();
                    buildTree(">=", 2);
                }
                case ">" -> {
                    read(">");
                    Term();
                    buildTree(">", 2);
                }
                case "=" -> {
                    read("=");
                    Term();
                    buildTree("=", 2);
                }
                case "<>" -> {
                    read("<>");
                    Term();
                    buildTree("<>", 2);
                }
                default -> throw new Error();
            }
        }
    }

    private void Term() {
        Factor();
        while (nextToken.getType().equals("+") || nextToken.getType().equals("-") || nextToken.getType().equals("or")) {
            switch (nextToken.getType()) {
                case "+" -> {
                    read("+");
                    Factor();
                    buildTree("+", 2);
                }
                case "-" -> {
                    read("-");
                    Factor();
                    buildTree("-", 2);
                }
                case "or" -> {
                    read("or");
                    Factor();
                    buildTree("or", 2);
                }
                default -> throw new Error();
            }
        }
    }

    private void Factor(){
        Primary();
        while(nextToken.getType().equals("*") || nextToken.getType().equals("/") || nextToken.getType().equals("and") || nextToken.getType().equals("mod")){
            switch (nextToken.getType()) {
                case "*" -> {
                    read("*");
                    Factor();
                    buildTree("*", 2);
                }
                case "/" -> {
                    read("/");
                    Factor();
                    buildTree("/", 2);
                }
                case "and" -> {
                    read("and");
                    Factor();
                    buildTree("and", 2);
                }
                case "mod" -> {
                    read("mod");
                    Factor();
                    buildTree("mod", 2);
                }
            }
        }
    }

    private void Primary(){

        switch (nextToken.getType()) {
            case "<char>" -> readByTokenName("Character Token");
            case "<integer>" -> readByTokenName("Integer Token");
            case "eof" -> {
                read("eof");
                buildTree("eof", 0);
            }
            case "-" -> {
                read("-");
                Primary();
                buildTree("-", 1);
            }
            case "+" -> {
                read("+");
                Primary();
                buildTree("+", 1);
            }
            case "not" -> {
                read("not");
                Primary();
                buildTree("not", 1);
            }
            case "(" -> {
                read("(");
                Expression();
                read(")");
            }
            case "succ" -> {
                read("succ");
                read("(");
                Expression();
                read(")");
                buildTree("succ", 1);
            }
            case "pred" -> {
                read("pred");
                read("(");
                Expression();
                read(")");
                buildTree("pred", 1);
            }
            case "chr" -> {
                read("chr");
                read("(");
                Expression();
                read(")");
                buildTree("chr", 1);
            }
            case "ord" -> {
                read("ord");
                read("(");
                Expression();
                read(")");
                buildTree("ord", 1);
            }
            case "<identifier>" -> {
                if (peek().equals("(")) {
                    Name();
                    read("(");
                    Expression();
                    int count = 2;
                    while (nextToken.getType().equals(",")) {
                        read(",");
                        Expression();
                        count++;
                    }
                    read(")");

                    buildTree("call", count);
                } else {
                    Name();
                }
            }
            default -> {
                System.out.println("ERROR OCCURRED WHILE PARSING: " + nextToken.getType());
                throw new Error();
            }
        }
    }
}
