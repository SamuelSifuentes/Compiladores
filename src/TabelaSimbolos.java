import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TabelaSimbolos {
    List<LinhaTabela> tabela; // TODO TRANSFORMAR EM TABELA HASH

    TabelaSimbolos() {
        tabela = new ArrayList<>();

        preenchePalavrasReservadas();
    }

    private void preenchePalavrasReservadas() {
        try {
            File myObj = new File("resources/alfabeto.txt");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                String[] extractedData = data.split("\\.");
                int tokenPos = Integer.parseInt(extractedData[0]);
                String tokenName = extractedData[1].trim();

                Token newToken = new Token(tokenPos, tokenName);
                List<Lexema> lexemas = new ArrayList<>();

                if(!(tokenName.equals("CONST") || tokenName.equals("ID"))){
                    lexemas.add(new Lexema(tokenName));
                }

                tabela.add(new LinhaTabela(newToken, lexemas));

//                System.out.println(tokenPos + ". " + tokenName);
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao ler o alfabeto");
            e.printStackTrace();
        }
    }

    public Integer buscaToken(String lexema){
        for(LinhaTabela linha : tabela){
            Optional<Lexema> lexemaEncontrado = linha.lexemas.stream().filter(lexemaAtual -> lexemaAtual.nome.equals(lexema)).findFirst();

            if(lexemaEncontrado.orElse(null) != null){
                return tabela.indexOf(linha);
            }
        }

        return null;
    }

    public Integer insereLexema(String tokenName, Lexema lexema){
        LinhaTabela linhaDesejada = tabela.stream().filter(lt -> lt.token.nome.equals(tokenName)).findFirst().orElse(null);

        if(linhaDesejada == null){
            return null;
        }

        linhaDesejada.lexemas.add(lexema);

        return tabela.indexOf(linhaDesejada);
    }

    public static void main(String[] args) {
        TabelaSimbolos tabelaSimbolos = new TabelaSimbolos();

        System.out.println(tabelaSimbolos.insereLexema("ID", new Lexema("testeVar")));

        int pos = tabelaSimbolos.buscaToken("testeVar");
        System.out.println(pos);

        System.out.println(tabelaSimbolos.tabela.get(pos));

        System.out.println("test".hashCode());
    }
}
