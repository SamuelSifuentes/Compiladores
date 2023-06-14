import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
                    String codigoGerado = programa.analisadorSintatico.analisar();

                    gerarArquivo(args[1], codigoGerado);

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

    private static void gerarArquivo(String fileName, String codigoGerado) throws IOException {
        File arquivoGerado = new File("src/" + fileName);
        arquivoGerado.createNewFile();
        FileOutputStream fos = new FileOutputStream(arquivoGerado, false);

        fos.write(codigoGerado.getBytes(StandardCharsets.UTF_8));
    }
}
