import java.util.List;

public class LinhaTabela {
    Token token;
    List<Lexema> lexemas;

    public LinhaTabela(Token newToken, List<Lexema> lexemas) {
        this.token = newToken;
        this.lexemas = lexemas;
    }

    @Override
    public String toString() {
        return "LinhaTabela{" +
                "token=" + token +
                ", lexemas=" + lexemas +
                '}';
    }
}
