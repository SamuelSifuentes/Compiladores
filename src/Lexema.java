public class Lexema {
    String nome;

    public Lexema(String tokenName) {
        this.nome = tokenName;
    }

    @Override
    public String toString() {
        return "Lexema{" +
                "nome='" + nome + '\'' +
                '}';
    }
}
