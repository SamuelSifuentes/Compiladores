
public class Token {
    int pos;
    String nome;

    public Token(int tokenPos, String tokenName) {
        this.pos = tokenPos;
        this.nome = tokenName;
    }

    public Token(String tokenName) {
        this.nome = tokenName;
    }

    @Override
    public String toString() {
        return "Token{" +
                "pos=" + pos +
                ", nome='" + nome + '\'' +
                '}';
    }
}
