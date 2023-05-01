import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
                return null; // TODO FINALIZAR
            }

            char c = (char) i;
            lexema += c;

            if(currentState == 1){
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
            }

            else if(currentState == 2){
                if(!(c >= 65 && c <= 90) && c != '_' && !(c >= 97 && c <= 122)) {
                    currentState = 3;

                    Integer pos = tabelaSimbolos.buscaToken(lexema);

                    if(pos != null){
                        registroLexico.token = tabelaSimbolos.tabela.get(pos).token;
                        reader.unread(i); // volta o char pro buffer
                    }

                    registroLexico.token = new Token("ID");
                }
            }

            else if (currentState == 4){
                currentState = 3;
                if(c == '='){
                    registroLexico.token = new Token("==");
                }
                else {
                    registroLexico.token = new Token("=");
                    reader.unread(i); // volta o char pro buffer
                }
            }

            else if(currentState == 5){
                if(!List.of(new Character[]{'(', ')', ',', '+', '*', '/', ';', '[', ']'}).contains(c)){
                    currentState = 3;
                }
            }

            else if(currentState == 6){
                if(!List.of(new Character[]{'(', ')', ',', '+', '*', '/', ';', '[', ']'}).contains(c)){
                    currentState = 3;
                }
            }

            else if(currentState == 7){
                if(c == '>'){
                    currentState = 3;
                    registroLexico.token = new Token("<>");
                }
                else if(c == '='){
                    currentState = 3;
                    registroLexico.token = new Token("<=");
                }
                else{
                    currentState = 3;
                    registroLexico.token = new Token("<");
                }
            }

            else if(currentState == 8){
                if(c == '='){
                    currentState = 3;
                    registroLexico.token = new Token(">=");
                }
                else{
                    currentState = 3;
                    registroLexico.token = new Token(">");
                }
            }

            else if(currentState == 9){
                if(c >= 48 && c <=57){
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
                    registroLexico.token = new Token("CONST");
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
                    registroLexico.token = new Token("_");
                }
            }

            else if(currentState == 12){
                if(c == 'e') {
                    currentState = 20;
                }
                else{
                    currentState = 3;
                    registroLexico.token = new Token("CONST");
                }
            }

            else if(currentState == 13){
                if(c >= 48 && c <=57){
                    currentState = 14;
                }
                else if(c == 'h'){
                    currentState = 3;
                    registroLexico.token = new Token("CONST");
                }
                else if(c == '.'){
                    currentState = 10;
                }
                else{
                    currentState = 3;
                    registroLexico.token = new Token("CONST");
                }
            }

            else if(currentState == 14){
                if(c == '.'){
                    currentState = 10;
                }
                else{
                    currentState = 3;
                    registroLexico.token = new Token("CONST");
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
                if((c >= 71 && c <= 90) || (c >= 48 && c <=57) || (c >= 97 && c <= 122)){
                    currentState = 19;
                }
            }

            else if(currentState == 19){
                if(c == '\''){
                    currentState = 3;
                    registroLexico.token = new Token("char");
                }
                else if((c >= 71 && c <= 90) || (c >= 48 && c <=57) || (c >= 97 && c <= 122)) {
                    currentState = 22;
                }
            }

            else if(currentState == 20){
                if(c == '-'){
                    currentState = 25;
                }
                else if(c >= 48 && c <=57){
                    currentState = 21;
                }
            }

            else if(currentState == 21){
                if()
            }


            i = reader.read(); // le o prox valor em inteiro do char
        }

        return null;
    }

    public static void main(String[] args) {
        AnalisadorLexico analisadorLexico = new AnalisadorLexico();

        try {
            analisadorLexico.analisar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
