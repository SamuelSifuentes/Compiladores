import java.io.*;
import java.util.*;

public class AnalisadorLexico {
    TabelaSimbolos tabelaSimbolos;

    // Reader que permite a devolucão de carateres ao buffer
    PushbackReader reader;

    int linhaAtual = 1;

    AnalisadorLexico(String caminhoArquivo){
        tabelaSimbolos = new TabelaSimbolos();

        try {
            reader = new PushbackReader(new FileReader(caminhoArquivo));

        } catch (FileNotFoundException e) {
            System.out.println("Erro ao ler arquivo");
            e.printStackTrace();
        }

    }

    public RegistroLexico analisar() throws IOException, LexicalException {
        String lexema = "";
        int currentState = 1;
        boolean fimArquivo = false;
        char c;
        RegistroLexico registroLexico = new RegistroLexico();

        int i = reader.read(); // retornará -1 caso não haja um caracter a ser lido. Caso haja, salva o caracter como um inteiro na variavel i
        while (!fimArquivo) {
            // Estado final. Uma vez que o compilador léxico chegue nesse estado, resta somente retornar a analise feita para o próx token
            if (currentState == 3) {
                return registroLexico;
            }

            if (i == -1) {
                c = '\r';
            } else {
                c = (char) i;
            }

            lexema += c;

            // Estado inicial
            if (currentState == 1) {
                if (c == '\n') {
                    linhaAtual++;
                    lexema = "";
                } else if (c == '\r') {
                    lexema = "";
                } else if (c == ' ') {
                    lexema = "";
                } else if ((c >= 71 && c <= 90) || c == '_' || (c >= 97 && c <= 122)) {
                    currentState = 2;
                } else if (c == '=') {
                    currentState = 4;
                } else if (List.of(new Character[]{'(', ')', ',', '+', '*', '/', ';', '[', ']'}).contains(c)) {
                    currentState = 5;
                } else if (c == '{') {
                    currentState = 6;
                } else if (c == '<') {
                    currentState = 7;
                } else if (c == '>') {
                    currentState = 8;
                } else if (c >= 48 && c <= 57) {
                    currentState = 9;
                } else if (c == '.') {
                    currentState = 10;
                } else if (c == '-') {
                    currentState = 11;
                } else if (c >= 65 && c <= 70) {
                    currentState = 16;
                } else if (c == '\'') {
                    currentState = 18;
                } else if (c == '"') {
                    currentState = 26;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }

            // Estado inicial da identificacão de IDs e palavras reservadas
            else if (currentState == 2) {
                if (c != '_' && !(c >= 65 && c <= 90) && !(c >= 97 && c <= 122) && !(c >= 48 && c <= 57)) {
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    checarTabelaSimbolos(lexema, registroLexico);

                    continue;
                }
            }

            // Estado para a identificacão do token = e ==
            else if (currentState == 4) {
                currentState = 3;
                if (c == '=') {
                    checarTabelaSimbolos(lexema, registroLexico);

                    continue;
                } else {
                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    checarTabelaSimbolos(lexema, registroLexico);

                    continue;
                }
            }

            // Estado para a identificacão dos tokens '(', ')', ',', '+', '*', '/', ';', '[', ']'
            else if (currentState == 5) {
                currentState = 3;

                reader.unread(c); // volta o char pro buffer
                lexema = lexema.substring(0, lexema.length() - 1);

                checarTabelaSimbolos(lexema, registroLexico);

                continue;
            }

            // Estado para a identificacão de comentários
            else if (currentState == 6) {
                if (c == '}') {
                    currentState = 1;
                    lexema = "";
                }
            }

            // Estado para a identificacão dos tokens de <, <= e <>
            else if (currentState == 7) {
                if (c == '>') {
                    currentState = 3;

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                } else if (c == '=') {
                    currentState = 3;

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                } else {
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                }
            }

            // Estado para a identificacão dos tokens de > e >=
            else if (currentState == 8) {
                if (c == '=') {
                    currentState = 3;

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                } else {
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                }
            }

            // Estado de inicio de identificacão de constantes inteiras, reais ou hexadecimais
            else if (currentState == 9) {
                if (c >= 48 && c <= 57) {
                    currentState = 13;
                } else if (c == '.') {
                    currentState = 10;
                } else if (c >= 65 && c <= 70) {
                    currentState = 15;
                } else {
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipoConst = "integer";
                    registroLexico.valorConst = lexema;
                    continue;
                }
            }

            // Estado de identificacão de constantes reais
            else if (currentState == 10) {
                if (c >= 48 && c <= 57) {
                    currentState = 12;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }


            // Estado de identificacão de constantes inteiras ou reais negativas, ou do token -
            else if (currentState == 11) {
                if (c >= 48 && c <= 57) {
                    currentState = 14;
                } else if (c == '.') {
                    currentState = 10;
                } else {
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                }
            }

            // Estado de identificacão de constantes inteiras ou reais
            else if (currentState == 12) {
                if (c == 'e') {
                    currentState = 20;
                } else if (!(c >= 48 && c <= 57)) {
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipoConst = "real";
                    registroLexico.valorConst = lexema;
                    continue;
                }
            }

            // Estado de identificacão de constantes hexadecimais, inteiras ou reais
            else if (currentState == 13) {
                if (c >= 48 && c <= 57) {
                    currentState = 14;
                } else if (c == 'h') {
                    currentState = 3;

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipoConst = "char";
                    registroLexico.valorConst = lexema;
                    continue;
                } else if (c == '.') {
                    currentState = 10;
                } else {
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipoConst = "integer";
                    registroLexico.valorConst = lexema;
                    continue;
                }
            }

            // Estado de identificacão de constantes inteiras
            else if (currentState == 14) {
                if (c == '.') {
                    currentState = 10;
                } else if(!(c >= 48 && c <= 57)) {
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipoConst = "integer";
                    registroLexico.valorConst = lexema;
                    continue;
                }
            }

            // Estado de identificacão de constantes hexadecimais, IDs ou palavras reservadas
            else if (currentState == 15) {
                if (c == 'h') {
                    currentState = 24;
                } else if ((c >= 71 && c <= 90) || (c >= 97 && c <= 122) || c == '_') {
                    currentState = 2;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }

            // Estado de identificacão de constantes hexadecimais, IDs ou palavras reservadas
            else if (currentState == 16) {
                if (c >= 65 && c <= 70) {
                    currentState = 15;
                } else if (c >= 48 && c <= 57) {
                    currentState = 17;
                } else if (c == '_' || (c >= 71 && c <= 90) || (c >= 97 && c <= 122)) {
                    currentState = 2;
                } else if (pertenceAoAlfabeto(c)) {
                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    currentState = 2;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }

            // Estado de identificacão de constantes hexadecimais, IDs ou palavras reservadas
            else if (currentState == 17) {
                if (c == 'h') {
                    currentState = 24;
                } else if ((c >= 71 && c <= 90) || c == '_' || (c >= 97 && c <= 122)) {
                    currentState = 2;
                } else if (pertenceAoAlfabeto(c)) {
                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    currentState = 2;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }

            // Estado de identificacão de constantes alfanuméricas para caracteres
            else if (currentState == 18) {
                if (c >= 32 && c <= 126) {
                    currentState = 19;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }

            // Estado de identificacão de constantes alfanuméricas para caracteres
            else if (currentState == 19) {
                if (c == '\'') {
                    currentState = 3;

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipoConst = "char";
                    registroLexico.valorConst = lexema;
                    continue;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }

            // Estado de identificacão de notacão cientifica em constantes reais
            else if (currentState == 20) {
                if (c == '-') {
                    currentState = 25;
                } else if (c >= 48 && c <= 57) {
                    currentState = 21;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }

            // Estado de identificacão de notacão cientifica em constantes reais
            else if (currentState == 21) {
                if (!(c >= 48 && c <= 57)) {
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipoConst = "real";
                    registroLexico.valorConst = lexema;
                    continue;
                }
            } else if (currentState == 22) { // TODO DELETAR?
                if (c == '\0') {
                    currentState = 23;
                }
            }

            // Estado de identificacão de constantes de strings
            else if (currentState == 23) {
                if (c == '"') {
                    currentState = 3;

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipoConst = "string";
                    registroLexico.valorConst = lexema;
                    continue;
                } else if (c >= 32 && c <= 126) {
                    i = reader.read(); // le o prox valor em inteiro do char
                    continue;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }

            // Estado de identificacão de constantes hexadecimais, IDs ou palavras reservadas
            else if (currentState == 24) {
                if ((c >= 71 && c <= 90) || (c >= 48 && c <= 57) || (c >= 97 && c <= 122) || (c == '_')) {
                    currentState = 2;
                } else {
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipoConst = "char";
                    registroLexico.valorConst = lexema;
                    continue;
                }
            }

            // Estado de identificacão de notacão cientifica negativa em constantes reais
            else if (currentState == 25) {
                if (c >= 48 && c <= 57) {
                    currentState = 21;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }

            // Estado de identificacão de constantes de strings
            else if (currentState == 26) {
                if (c >= 32 && c <= 126) {
                    currentState = 23;
                } else {
                    throw new LexicalException(linhaAtual, LexicalErrorEnum.CARACTER_INVALIDO, c);
                }
            }

            if (i == -1)
                fimArquivo = true;
            else
                i = reader.read(); // le o prox valor em inteiro do char
        }

        if(currentState != 1){
           throw new LexicalException(linhaAtual, LexicalErrorEnum.FIM_DE_ARQUIVO_INESPERADO);
        }

        registroLexico.token = new Token("FIM_DE_ARQUIVO");
        registroLexico.lexema = new Lexema("FIM_DE_ARQUIVO");
        registroLexico.pos = tabelaSimbolos.buscaToken("FIM_DE_ARQUIVO");

        return registroLexico;
    }

    private boolean pertenceAoAlfabeto(char c) {
        return List.of(new Character[]{'(', ')', ',', '+', '*', '/', ';', '[', ']', '>', '<', '='}).contains(c);
    }

    private void checarTabelaSimbolos(String lexema, RegistroLexico registroLexico) throws LexicalException {
        Integer pos = tabelaSimbolos.buscaToken(lexema);

        if(pos != null){
            LinhaTabela linhaTabela = tabelaSimbolos.tabela[pos];

            if(linhaTabela.entradas.size() > 1){ // Caso tenha acontecido um conflito, é necessário verificar qual das entradas salvas é referente ao token sendo buscado
                Set<Map.Entry<String, List<Lexema>>> entradas = linhaTabela.entradas.entrySet();

                for(Map.Entry<String, List<Lexema>> entrada : entradas){
                    if(entrada.getKey().equals(lexema)){
                        registroLexico.token = new Token(entrada.getKey());

                        if(registroLexico.token.nome.equals("ID")){
                            Lexema aux = entrada.getValue().stream().filter(lex -> lex.nome.equals(lexema)).findFirst().orElse(null);

                            if(aux == null){
                                throw new LexicalException(linhaAtual, LexicalErrorEnum.LEXEMA_NAO_IDENTIFICADO, lexema);
                            }

                            registroLexico.lexema = aux;
                        }
                        else{
                            registroLexico.lexema = entrada.getValue().get(0);

                            if(registroLexico.lexema.nome.equals("true") || registroLexico.lexema.nome.equals("false")){
                                registroLexico.tipoConst = "boolean";
                                registroLexico.valorConst = registroLexico.lexema.nome;
                            }
                        }

                        break;
                    }
                }
            }
            else{ // Caso não tenha conflito, basta salvar o token salvo na posicão encontrada
                registroLexico.token = new Token((String) linhaTabela.entradas.keySet().toArray()[0]);

                if(registroLexico.token.nome.equals("ID")){
                    Lexema aux = linhaTabela.entradas.values().stream().toList().get(0).stream().filter(lex -> lex.nome.equals(lexema)).findFirst().orElse(null);

                    if(aux == null){
                        throw new LexicalException(linhaAtual, LexicalErrorEnum.LEXEMA_NAO_IDENTIFICADO, lexema);
                    }

                    registroLexico.lexema = aux;
                }
                else{
                    registroLexico.lexema = linhaTabela.entradas.values().stream().toList().get(0).get(0);

                    if(registroLexico.lexema.nome.equals("true") || registroLexico.lexema.nome.equals("false")){
                        registroLexico.tipoConst = "boolean";
                        registroLexico.valorConst = registroLexico.lexema.nome;
                    }
                }
            }

            registroLexico.pos = pos;
        }
        else{
            Lexema lex =  new Lexema(lexema);

            registroLexico.pos = tabelaSimbolos.insereLexema("ID", lex);
            registroLexico.token = new Token("ID");
            registroLexico.lexema = lex;
        }
    }

    public static void main(String[] args) {
        AnalisadorLexico analisadorLexico = new AnalisadorLexico("resources/codigo.txt");

        try {
            RegistroLexico reg;
            do {
                reg = analisadorLexico.analisar();

                System.out.println(reg);
            } while(!reg.token.nome.equals("FIM_DE_ARQUIVO"));
        } catch (IOException | LexicalException e) {
            throw new RuntimeException(e);
        }
    }
}
