public class Lexema {
    public String nome;

    public String classe;

    public String tipo;

    public int tamanho;

    public long endereco;

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
