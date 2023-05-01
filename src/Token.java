
public class Token {
    String nome;

    public Token(String tokenName) {
        this.nome = tokenName;
    }

    @Override
    public String toString() {
        return "Token{" +
                "nome='" + nome + '\'' +
                '}';
    }
}
