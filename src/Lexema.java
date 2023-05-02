public class Lexema {
    public String nome;
    public String tipo;

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
