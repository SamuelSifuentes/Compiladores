import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TabelaSimbolos {
    public LinhaTabela[] tabela;

    public int tamTabela;

    TabelaSimbolos() {
        tamTabela = 1000;

        tabela = new LinhaTabela[tamTabela];

        preenchePalavrasReservadas();
    }

    private void preenchePalavrasReservadas() {
        try {
            File myObj = new File("resources/alfabeto.txt");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                String[] extractedData = data.split("\\.");
                String tokenName = extractedData[1].trim();

                int pos = hash(tokenName);
                LinhaTabela linhaTabela = tabela[pos];

                if(linhaTabela == null){
                    linhaTabela = new LinhaTabela();
                }

                linhaTabela.preenche(tokenName);
                tabela[pos] = linhaTabela;
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao ler o alfabeto");
            e.printStackTrace();
        }
    }

    public Integer buscaToken(String lexema){
        int pos = hash(lexema);

        LinhaTabela linhaTabela = tabela[pos];

        // se for palavra reservada
        if(linhaTabela != null){
            if(linhaTabela.busca(lexema) != null){
                return pos;
            }
        }

        pos = hash("ID");
        linhaTabela = tabela[pos];

        if(linhaTabela != null){
            if(linhaTabela.buscaIdentificador(lexema) != null){
                return pos;
            }
        }

        return null;
    }

    public int insereLexema(String tokenName, Lexema lexema){
        int pos = hash(tokenName);

        LinhaTabela linhaTabela = tabela[pos];
        linhaTabela.insere(tokenName, lexema);

        return pos;
    }

    public int hash(String lexema){
        int soma = 0;

        for(char c : lexema.toCharArray()){
            soma += c;
        }

        return soma % tamTabela;
    }

    public static void main(String[] args) {
        TabelaSimbolos tabelaSimbolos = new TabelaSimbolos();

        System.out.println(tabelaSimbolos.insereLexema("ID", new Lexema("testeVar")));
        System.out.println(tabelaSimbolos.insereLexema("ID", new Lexema("variavel2")));
        System.out.println(tabelaSimbolos.insereLexema("ID", new Lexema("variavel3")));
        System.out.println(tabelaSimbolos.insereLexema("ID", new Lexema("variavel4")));

        int pos = tabelaSimbolos.buscaToken("testeVar");
        System.out.println(pos);

        System.out.println(tabelaSimbolos.tabela[pos]);

        LinhaTabela[] aNew = tabelaSimbolos.tabela;
        for (int i = 0; i < aNew.length; i++) {
            LinhaTabela linhaTabela = aNew[i];
            if (linhaTabela != null) {
                System.out.println(linhaTabela + " " + i);
            }
        }
    }
}
