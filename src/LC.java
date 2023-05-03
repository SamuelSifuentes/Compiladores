import java.io.IOException;

public class LC {
    AnalisadorSintatico analisadorSintatico;

    LC(String caminhoArquivo){
        analisadorSintatico = new AnalisadorSintatico(caminhoArquivo);
    }

    public static void main(String[] args) throws LexicalException, IOException, SyntaticException {
        int len = args.length;
        if(args.length > 1) {
            String arqL = args[0];
            String extensao = arqL.substring(arqL.length()-2);

            if(extensao.equals(".L")){
                LC programa = new LC(arqL);

            programa.analisadorSintatico.analisar();
            }
            else{
                throw new RuntimeException("Favor fornecer um arquivo de formato .L");
            }
        }
        else{
            if(len < 1)
                throw new RuntimeException("Nenhum dos arquivos encontrado, especifique o nome completo do programa fonte a ser compilado (extensão .L) e" +
                        " o nome completo do " +
                        "programa ASSEMBLY (extensão .ASM) a ser gerado");
            else
                throw new RuntimeException("Não foi especificado o nome completo do" +
                        " programa ASSEMBLY (extensão .ASM) a ser gerado");
        }

    }
}
