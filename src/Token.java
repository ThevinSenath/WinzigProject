public class Token {
    private String tokenName;
    private String value;
    private String type;

    protected Token(String tokenName, String value, String type){
        this.tokenName = tokenName;
        this.value = value;
        this.type = type;
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
