import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinhaTabela {

    Map<String, List<Lexema>> entradas;

    public LinhaTabela(){
        entradas = new HashMap<>();
    }

    public void preenche(String token){
        entradas.put(token, new ArrayList<>());
    }

    public void insere(String token, Lexema lexema){
        List<Lexema> lexemas = entradas.get(token);

        lexemas.add(lexema);
    }

    public List<Lexema> busca(String token){
        return entradas.get(token);
    }

    public Lexema buscaIdentificador(String lexema){
        return entradas.get("ID").stream().filter(lex -> lex.nome.equals(lexema)).findFirst().orElse(null);
    }

//    public Token buscaToken(String lexema){
//
//    }

    @Override
    public String toString() {
        return "LinhaTabela{" +
                "entradas=" + entradas +
                '}';
    }
}
