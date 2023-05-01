import java.io.*;
import java.util.*;

public class AnalisadorLexico {
    TabelaSimbolos tabelaSimbolos;

    PushbackReader reader;

    AnalisadorLexico(){
        tabelaSimbolos = new TabelaSimbolos();

        try {
            reader = new PushbackReader(new FileReader("resources/codigo.txt"));

        } catch (FileNotFoundException e) {
            System.out.println("Erro ao ler arquivo");
            e.printStackTrace();
        }
    }

    public RegistroLexico analisar() throws IOException{
        int currentState = 1;
        String lexema = "";
        RegistroLexico registroLexico = new RegistroLexico();

        int i = reader.read();
        while (i != -1) {
            if(currentState == 3){
                return registroLexico;
            }

            char c = (char) i;
            lexema += c;

            if(currentState == 1){
                if(c == '\n' || c == ' '){
                    lexema = "";
                }

                if((c >= 71 && c <= 90) || c == '_' || (c >= 97 && c <= 122)){
                    currentState = 2;
                }
                else if(c == '='){
                    currentState = 4;
                }
                else if(List.of(new Character[]{'(', ')', ',', '+', '*', '/', ';', '[', ']'}).contains(c)){
                    currentState = 5;
                }
                else if(c == '{'){
                    currentState = 6;
                }
                else if(c == '<'){
                    currentState = 7;
                }
                else if(c == '>'){
                    currentState = 8;
                }
                else if(c >= 48 && c <= 57){
                    currentState = 9;
                }
                else if(c == '.'){
                    currentState = 10;
                }
                else if(c == '-'){
                    currentState = 11;
                }
                else if(c >= 65 && c <= 70){
                    currentState = 16;
                }
                else if(c == '\''){
                    currentState = 18;
                }
                else if(c == '"'){
                    currentState = 26;
                }
            }

            else if(currentState == 2){
                if(c != '_' && !(c >= 65 && c <= 90) && !(c >= 97 && c <= 122) && !(c >= 48 && c <= 57)) {
                    currentState = 3; // TODO PRA CASO SEJA DIFERENTE DISSO, DISPARAR ERRO

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    checarTabelaSimbolos(lexema, registroLexico);

                    continue;
                }
            }

            else if (currentState == 4){
                currentState = 3;
                if(c == '='){
                    checarTabelaSimbolos(lexema, registroLexico);

                    continue;
                }
                else {
                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    checarTabelaSimbolos(lexema, registroLexico);

                    continue;
                }
            }

            else if(currentState == 5){
                currentState = 3;

                reader.unread(c); // volta o char pro buffer
                lexema = lexema.substring(0, lexema.length() - 1);

                checarTabelaSimbolos(lexema, registroLexico);

                continue;
            }

            else if(currentState == 6){
                if(c == '}'){
                    currentState = 1;
                    lexema = "";
                }
            }

            else if(currentState == 7){
                if(c == '>'){
                    currentState = 3;

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                }
                else if(c == '='){
                    currentState = 3;

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                }
                else{
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                }
            }

            else if(currentState == 8){
                if(c == '='){
                    currentState = 3;

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                }
                else{
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                }
            }

            else if(currentState == 9){
                if(c >= 48 && c <= 57){
                    currentState = 13;
                }
                else if(c == '.'){
                    currentState = 10;
                }
                else if(c >= 65 && c <= 70){
                    currentState = 15;
                }
                else{
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipo = "INTEGER";
                    registroLexico.valor = lexema;
                    continue;
                }
            }

            else if(currentState == 10){
                if(c >= 48 && c <=57){
                    currentState = 12;
                }
            }

            else if(currentState == 11){
                if(c >= 48 && c <= 57){
                    currentState = 14;
                }
                else if(c == '.'){
                    currentState = 10;
                }
                else{
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    checarTabelaSimbolos(lexema, registroLexico);
                    continue;
                }
            }

            else if(currentState == 12){
                if(c == 'e') {
                    currentState = 20;
                }
                else if(!(c >= 48 && c <=57)){
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipo = "FLOAT";
                    registroLexico.valor = lexema;
                    continue;
                }
            }

            else if(currentState == 13){
                if(c >= 48 && c <= 57){
                    currentState = 14;
                }
                else if(c == 'h'){
                    currentState = 3;

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipo = "CHAR";
                    registroLexico.valor = lexema;
                    continue;
                }
                else if(c == '.'){
                    currentState = 10;
                }
                else{
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipo = "INTEGER";
                    registroLexico.valor = lexema;
                    continue;
                }
            }

            else if(currentState == 14){
                if(c == '.'){
                    currentState = 10;
                }
                else{
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipo = "FLOAT";
                    registroLexico.valor = lexema;
                    continue;
                }
            }

            else if(currentState == 15){
                if(c == 'h'){
                    currentState = 24;
                }
                if(c >= 71 && c <= 90){
                    currentState = 2;
                }
            }

            else if(currentState == 16){
                if(c >= 65 && c <= 70){
                    currentState = 15;
                }
                else if(c >= 48 && c <=57){
                    currentState = 17;
                }
                else if(c >= 71 && c <= 90){
                    currentState = 2;
                }
            }

            else if(currentState == 17){
                if(c == 'h'){
                    currentState = 24;
                }
                else if(c >= 71 && c <= 90){
                    currentState = 2;
                }
            }

            else if(currentState == 18){
                if(c >= 32 && c <= 126){
                    currentState = 19;
                }
            }

            else if(currentState == 19){
                if(c == '\''){
                    currentState = 3;

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipo = "CHAR";
                    registroLexico.valor = lexema;
                    continue;
                }
            }

            else if(currentState == 20){
                if(c == '-'){
                    currentState = 25;
                }
                else if(c >= 48 && c <= 57){
                    currentState = 21;
                }
            }

            else if(currentState == 21){
                if(!(c >= 48 && c <= 57)){
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipo = "FLOAT";
                    registroLexico.valor = lexema;
                    continue;
                }
            }

            else if(currentState == 22){ // TODO DELETAR?
                if(c == '\0'){
                    currentState = 23;
                }
            }

           else if(currentState == 23){
               if(c == '"'){
                   currentState = 3;

                   registroLexico.token = new Token("CONST");
                   registroLexico.tipo = "STRING";
                   registroLexico.valor = lexema;
                   continue;
               }
           }

           else if(currentState == 24){
                if((c >= 71 && c <= 90) || (c >= 48 && c <=57) || (c >= 97 && c <= 122) || (c == '_')){
                    currentState = 2;
                }
                else{
                    currentState = 3;

                    reader.unread(c); // volta o char pro buffer
                    lexema = lexema.substring(0, lexema.length() - 1);

                    registroLexico.token = new Token("CONST");
                    registroLexico.tipo = "CHAR";
                    registroLexico.valor = lexema;
                    continue;
                }
           }

           else if(currentState == 25){
               if(c >= 48 && c <= 57){
                   currentState = 21;
               }
           }

           else if(currentState == 26){
               if(c >= 32 && c <= 126){
                   currentState = 23;
               }
           }


           i = reader.read(); // le o prox valor em inteiro do char
        }

        return null;
    }

    private void checarTabelaSimbolos(String lexema, RegistroLexico registroLexico) {
        Integer pos = tabelaSimbolos.buscaToken(lexema);

        registroLexico.lexema = new Lexema(lexema);
        if(pos != null){
            LinhaTabela linhaTabela = tabelaSimbolos.tabela[pos];

            if(linhaTabela.entradas.size() > 1){
                Set<Map.Entry<String, List<Lexema>>> entradas = linhaTabela.entradas.entrySet();

                for(Map.Entry<String, List<Lexema>> entrada : entradas){
                    if(entrada.getKey().equals(lexema)){
                        registroLexico.token = new Token(entrada.getKey());
                        break;
                    }
                }
            }
            else{
                registroLexico.token = new Token((String) linhaTabela.entradas.keySet().toArray()[0]);
            }

            registroLexico.pos = pos;
        }
        else{
            registroLexico.pos = tabelaSimbolos.insereLexema("ID", new Lexema(lexema));
            registroLexico.token = new Token("ID");
        }
    }

    public static void main(String[] args) {
        AnalisadorLexico analisadorLexico = new AnalisadorLexico();

        try {
            RegistroLexico reg;
            do {
                reg = analisadorLexico.analisar();

                System.out.println(reg);
            } while(reg != null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
