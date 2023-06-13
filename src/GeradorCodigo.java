import java.util.Arrays;

public class GeradorCodigo {
    static long PC = 0x1000;

    static String generatedCode = "";
    
    static String generatedDeclarationCode = "";

    static String generatedCommandCode = "";

    static int labelCounter = 0;

    static int tempCounter = 0;


    public static void initialize() {
        generatedCode += """
                section .data
                M:
                    resb 0x1000
                """;

        generatedCommandCode += """
                section .text
                global _start
                _start:
                """;
    }

    public static String getNextRot()
    {
            return "ROTULO_" + ++labelCounter;
    }
    
    public static void movePC(int offset){
        PC += offset;
    }

    public static long allocateTemp(int offset){
        int preAdd = tempCounter;
        tempCounter += offset;

        return preAdd;
    }

    public static long getNextAvailablePosition(int offset){
        long currentAddress = PC;

        movePC(offset);

        return currentAddress;
    }


    public static long declareInteger() {
        generatedDeclarationCode += "resd 1\n";

        return getNextAvailablePosition(4);
    }

    public static long declareReal(){
        generatedDeclarationCode += "resd 1\n";

        return getNextAvailablePosition(4);
    }

    public static long declareBoolean(){
        generatedDeclarationCode += "resb 1\n";

        return getNextAvailablePosition(1);
    }

    public static long declareChar(){
        generatedDeclarationCode += "resb 1\n"; // resb msm?

        return getNextAvailablePosition(4);
    }

    public static long declareFinal(RegistroLexico cons){
        if(cons.tipoConst.equals("integer")){
            return declareInteger();
        }
        else if(cons.tipoConst.equals("real")){
            return declareReal();
        }
        else if(cons.tipoConst.equals("char")){
            return declareChar();
        }
        else if(cons.tipoConst.equals("boolean")){
            return declareBoolean();
        }
        return -1;
    }

    public static void writeInteger(String value, long varAddr){
        generatedDeclarationCode += "dd " + value + "\n";
        long constAddr = PC;

        writeVariable(constAddr, varAddr);
    }
    public static void writeReal(String value, long varAddr){

         if(value.length() > 1 &&(value.charAt(0) == '.' ||(value.contains("-") && value.charAt(1) == '.'))){
            String[] s = value.split("\\.");
            value = s[0]+"0."+s[1];
        }

        generatedDeclarationCode += "dd " + value + "\n";
        long constAddr = PC;

        writeVariable(constAddr, varAddr);
    }

    public static void writeBoolean(String value, long varAddr){
        generatedDeclarationCode += "dd" + (value.equals("true")? 1 : 0);
        long constAddr = PC;

        writeVariable(constAddr, varAddr);
    }

    public static void writeVariable(long addr1, long addr2){
        generatedCommandCode += "mov eax, [M +" + addr1 + "]\n";
        generatedCommandCode += "mov [M +" + addr2 + "], eax\n";
    }

    public static void printInteger(long addr){
        long actualMemoryPosition = allocateTemp(4);

        // Labels
        String label0 = getNextRot();
        String label1 = getNextRot();

        generatedCommandCode += "\tmov EAX, 0 \t\t\t ; \n";
        generatedCommandCode += "\tmov RDI, 0 \t\t\t ; \n";

        generatedCommandCode += "\tmov EAX, [ M + " + addr + " ] \t\t\t;\n";
        generatedCommandCode += "\tmov RDI, M + " + actualMemoryPosition + " \t\t\t ;\n";
        generatedCommandCode += "\tmov RCX, 0 \t\t\t ; \n";
        generatedCommandCode += "\tmov RSI, 0 \t\t\t ; \n";

        generatedCommandCode += "\tcmp EAX, 0 \t\t\t ; \n";
        generatedCommandCode += "\tjge " + label0 + " \t\t\t ; \n";

        generatedCommandCode += "\tmov BL, \'-\' \t\t\t ; \n";
        generatedCommandCode += "\tmov [RDI], BL \t\t\t ; \n";
        generatedCommandCode += "\tadd RDI, 1 \t\t\t ; \n";
        generatedCommandCode += "\tneg EAX \t\t\t ; \n";

        generatedCommandCode += label0 + ":\n";
        generatedCommandCode += "\tmov EBX, 10 \t\t\t ; \n";
        generatedCommandCode += "\tmov EDX, 0 \t\t\t ; \n";
        generatedCommandCode += "\tidiv EBX \t\t\t ;\n";
        generatedCommandCode += "\tpush DX \t\t\t ; \n";
        generatedCommandCode += "\tadd RCX, 1 \t\t\t ; \n";
        generatedCommandCode += "\tcmp EAX, 0 \t\t\t ; \n";
        generatedCommandCode += "\tjne " + label0 + " \t\t\t ; \n";

        generatedCommandCode += label1 + ":\n";
        generatedCommandCode += "\tpop AX \t\t\t ; \n";
        generatedCommandCode += "\tadd AX, \'0\' \t\t\t ; \n";
        generatedCommandCode += "\tmov [RDI], AL \t\t\t ; \n";

        generatedCommandCode += "\tadd RDI, 1 \t\t\t ; \n";
        generatedCommandCode += "\tsub RCX, 1 \t\t\t ; \n";
        generatedCommandCode += "\tcmp RCX, 0 \t\t\t ; \n";
        generatedCommandCode += "\tjg " + label1 + "\t\t\t ; \n";

        generatedCommandCode += "\tmov [RDI], byte 0 ; \n\n";
        generatedCommandCode += "\tsub RDI, M + " + actualMemoryPosition + " ; \n";

        // System call for write
        generatedCommandCode += "\tmov RSI, M + " + actualMemoryPosition + " ; \n";
        generatedCommandCode += "\tmov RDX, RDI ; \n";
        generatedCommandCode += "\tmov RAX, 1 ; \n";
        generatedCommandCode += "\tmov RDI, 1 ; \n";
        generatedCommandCode += "\tsyscall\n";
    }

    public static void printReal(long addr){
        long actualMemoryPosition = allocateTemp(4);

        String label0 = getNextRot();
        String label1 = getNextRot();
        String label2 = getNextRot();
        String label3 = getNextRot();
        String label4 = getNextRot();

        generatedCommandCode += "\tmovss XMM0, [ M + " + addr + " ] \t\t\t ; Real a ser convertido\n";
        generatedCommandCode += "\tmov RSI, M + " + actualMemoryPosition + " \t\t\t ; End. temporário\n";
        generatedCommandCode += "\tmov RCX, 0 \t\t\t ; Contador pilha\n";
        generatedCommandCode += "\tmov RDI, 6 \t\t\t ; Precisão 6 casas compart\n";
        generatedCommandCode += "\tmov RBX, 10 \t\t\t ; Divisor\n";
        generatedCommandCode += "\tcvtsi2ss XMM2, RBX \t\t\t ; Divisor real\n";
        generatedCommandCode += "\tsubss XMM1, XMM1 \t\t\t ; Zera registrador\n";
        generatedCommandCode += "\tcomiss XMM0, XMM1 \t\t\t ; Verifica sinal\n";
        generatedCommandCode += "\tjae " + label0 + " \t\t\t ; Salta se número positivo\n";
        generatedCommandCode += "\tmov DL, \'-\' \t\t\t; Senão, escreve sinal –\n";
        generatedCommandCode += "\tmov [RSI], DL\n";
        generatedCommandCode += "\tmov RDX, -1 \t\t\t ; Carrega -1 em RDX\n";
        generatedCommandCode += "\tcvtsi2ss XMM1, RDX \t\t\t ; Converte para real\n";
        generatedCommandCode += "\tmulss XMM0, XMM1 \t\t\t ; Toma módulo\n";
        generatedCommandCode += "\tadd RSI, 1 \t\t\t ; Incrementa índice\n";
        generatedCommandCode += label0 + ": \n";
        generatedCommandCode += "\troundss XMM1, XMM0, 0b0011 \t\t\t ; Parte inteira XMM1\n";
        generatedCommandCode += "\tsubss XMM0, XMM1 \t\t\t ; Parte frac XMM0\n";
        generatedCommandCode += "\tcvtss2si rax, XMM1 \t\t\t ; Convertido para int\n";
        generatedCommandCode += "\t; Converte parte inteira que está em rax\n";
        generatedCommandCode += label1 + ": \n";
        generatedCommandCode += "\tadd RCX, 1 \t\t\t ; Incrementa contador\n";
        generatedCommandCode += "\tcdq \t\t\t ; Estende EDX:EAX p/ div.\n";
        generatedCommandCode += "\tidiv EBX \t\t\t ; Divide EDX;EAX por EBX\n";
        generatedCommandCode += "\tpush RDX \t\t\t ; Empilha valor do resto\n";
        generatedCommandCode += "\tcmp EAX, 0 \t\t\t ; Verifica se quoc. é 0\n";
        generatedCommandCode += "\tjne " + label1 + " \t\t\t ; Se não é 0, continua\n";
        generatedCommandCode += "\tsub RDI, RCX \t\t\t;decrementa precisao\n";
        generatedCommandCode += "\t; Agora, desemp valores e escreve parte int\n";
        generatedCommandCode += label2 + ":\n";
        generatedCommandCode += "\tpop RDX \t\t\t ; Desempilha valor\n";
        generatedCommandCode += "\tadd DL, \'0\' \t\t\t ; Transforma em caractere\n";
        generatedCommandCode += "\tmov [RSI], DL \t\t\t ; Escreve caractere\n";
        generatedCommandCode += "\tadd RSI, 1 \t\t\t ; Incrementa base\n";
        generatedCommandCode += "\tsub RCX, 1 \t\t\t ; Decrementa contador\n";
        generatedCommandCode += "\tcmp RCX, 0 \t\t\t ; Verifica pilha vazia\n";
        generatedCommandCode += "\tjne " + label2 + " \t\t\t ; Se não pilha vazia, loop\n";
        generatedCommandCode += "\tmov DL, \'.\' \t\t\t ; Escreve ponto decimal\n";
        generatedCommandCode += "\tmov [RSI], DL\n";
        generatedCommandCode += "\tadd RSI, 1 \t\t\t ; Incrementa base\n";
        generatedCommandCode += "\t; Converte parte fracionaria que está em XMM0\n";
        generatedCommandCode += label3 + ":\n";
        generatedCommandCode += "\tcmp RDI, 0 \t\t\t ; Verifica precisao\n";
        generatedCommandCode += "\tjle " + label4 + " \t\t\t ; Terminou precisao ?\n";
        generatedCommandCode += "\tmulss XMM0,XMM2 \t\t\t ; Desloca para esquerda\n";
        generatedCommandCode += "\troundss XMM1,XMM0,0b0011 \t\t\t ; Parte inteira XMM1\n";
        generatedCommandCode += "\tsubss XMM0,XMM1 \t\t\t ; Atualiza XMM0\n";
        generatedCommandCode += "\tcvtss2si RDX, XMM1\t\t\t ; Convertido para int\n";
        generatedCommandCode += "\tadd DL, \'0\' \t\t\t ; Transforma em caractere\n";
        generatedCommandCode += "\tmov [RSI], DL \t\t\t ; Escreve caractere\n";
        generatedCommandCode += "\tadd RSI, 1 \t\t\t ; Incrementa base\n";
        generatedCommandCode += "\tsub RDI, 1 \t\t\t ; Decrementa precisão\n";
        generatedCommandCode += "\tjmp " + label3 + "\n";
        generatedCommandCode += "\t; Impressão\n";
        generatedCommandCode += label4 + ":\n";
        generatedCommandCode += "\tmov DL, 0 \t\t\t ; Fim string, opcional\n";
        generatedCommandCode += "\tmov [RSI], DL \t\t\t ; Escreve caractere\n";
        generatedCommandCode += "\tmov RDX, RSI ; Calc tam str convertido\n";
        generatedCommandCode += "\tmov RBX, M + " + actualMemoryPosition + " \n";
        generatedCommandCode += "\tsub RDX, RBX \t\t\t ; Tam=RSI-M-buffer.end\n";
        generatedCommandCode += "\tmov RSI, M + " + actualMemoryPosition + " \t\t\t; Endereço do buffer\n";

        generatedCommandCode += "\tmov RAX, 1 ; Chamada para saída\n";
        generatedCommandCode += "\tmov RDI, 1 ; Chamada para tela\n";
        generatedCommandCode += "\tsyscall\n";
    }

    public static void printString(long addr)
    {
        // Labels
        String labelStartLoop = getNextRot();
        String labelEndLoop = getNextRot();

        generatedCode += "\tmov RSI, M + " + addr + " \t\t\t ; Copiando endereço da string para um registrador de índice\n";
        generatedCode += "\tmov RDX, 0 \t\t\t ; contador de caracteres = 0\n";

        // Begin of loop to calculate the length of the string
        generatedCode += "\t; Loop para calcular tamanho da string\n";
        generatedCode += labelStartLoop + ": \t\t\t ; Inicio do loop\n";
        generatedCode += "\tmov AL, [RSI] \t\t\t ; Leitura de caractere na posicao rax da memória\n";
        generatedCode += "\tcmp AL, 0 \t\t\t ; Verificação de flag de fim de string\n";
        generatedCode += "\tje " + labelEndLoop + " \t\t\t ; Se caractere lido = flag de fim de string finalizar loop\n";

        generatedCode += "\tadd RDX, 1 \t\t\t ; Incrementando numero de caracteres\n";
        generatedCode += "\tadd RSI, 1 \t\t\t ; Incrementando indice da string\n";
        generatedCode += "\tjmp " + labelStartLoop + "  ; Se caractere lido != flag de fim de string continuar loop\n";

        // End of loop
        generatedCode += labelEndLoop + ": ; Fim do loop\n";

        // System call for write
        generatedCode += "\tmov RSI, M + " + addr + " \t\t\t ; Copiando endereço inicial da string\n";
        generatedCode += "\tmov RAX, 1 \t\t\t ; Chamada para saída\n";
        generatedCode += "\tmov RDI, 1 \t\t\t ; Chamada para tela\n";
        generatedCode += "\tsyscall\n";
    }

    public static String finalizeCode(){
        return generatedCode + generatedDeclarationCode + generatedCommandCode;
    }
}

