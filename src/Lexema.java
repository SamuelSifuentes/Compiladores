public class Lexema {
    String nome;
    String tipo;

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
