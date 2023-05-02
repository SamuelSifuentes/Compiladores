import java.io.IOException;

public class AnalisadorSintatico {
    AnalisadorLexico analisadorLexico;
    RegistroLexico reg;

    AnalisadorSintatico(String caminhoArquivo){
        analisadorLexico = new AnalisadorLexico(caminhoArquivo);
    }

    public void analisar() throws LexicalException, IOException {
        reg = analisadorLexico.analisar();
        S();

        System.out.println(reg);
    }

    public void S(){
        if()
    }
}
