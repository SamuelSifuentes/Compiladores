import java.io.IOException;

public class LC {
    AnalisadorSintatico analisadorSintatico;

    LC(String caminhoArquivo){
        analisadorSintatico = new AnalisadorSintatico(caminhoArquivo);
    }

    public static void main(String[] args) {
        int len = args.length;
        if(args.length > 1) {
            String arqL = args[0];
            String extensao = arqL.substring(arqL.length()-2);

            if(extensao.equals(".l")){
                LC programa = new LC(arqL);

                try {
                    programa.analisadorSintatico.analisar();
                }
                catch(LexicalException | SyntaticException | IOException | SemanticalException e){
                    System.out.println(e.getMessage());
                }
            }
            else{
                System.out.println("Favor fornecer um arquivo de formato .L");
            }
        }
        else{
            if(len < 1)
                System.out.println("Nenhum dos arquivos encontrado, especifique o nome completo do programa fonte a ser compilado (extensão .L) e" +
                        " o nome completo do " +
                        "programa ASSEMBLY (extensão .ASM) a ser gerado");
            else
                System.out.println("Não foi especificado o nome completo do" +
                        " programa ASSEMBLY (extensão .ASM) a ser gerado");
        }

    }
}
