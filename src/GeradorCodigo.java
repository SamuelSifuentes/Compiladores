import java.util.Arrays;

public class GeradorCodigo {
    static long PC = 0x10000;

    static String generatedCode = "";
    
    static String generatedDeclarationCode = "";

    static String generatedCommandCode = "";

    static int labelCounter = 0;

    static int tempCounter = 0;


    public static void initialize() {
        generatedCode += """
                section .data
                M:
                    resb 0x10000
                """;

        generatedCommandCode += """
                section .text
                global _start
                _start:
                """;
    }

    public static String getNextRot(){
            return "ROTULO_" + ++labelCounter;
    }

    public static void movePC(int offset){
        PC += offset;
    }
    public static void moveTC(int offset){
        tempCounter += offset;
    }

    public static void resetTC(){
        tempCounter = 0;
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
        generatedDeclarationCode += "\tresd 1\n";

        return getNextAvailablePosition(4);
    }

    public static long declareReal(){
        generatedDeclarationCode += "\tresd 1\n";

        return getNextAvailablePosition(4);
    }

    public static long declareBoolean(){
        generatedDeclarationCode += "\tresb 1\n";

        return getNextAvailablePosition(4);
    }

    public static long declareChar(){
        generatedDeclarationCode += "resb 1\n"; // resb msm?

        return getNextAvailablePosition(1);
    }

    public static long declareFinal(RegistroLexico cons){
        return switch (cons.lexema.tipo) {
            case "integer" -> declareInteger();
            case "real" -> declareReal();
            case "char" -> declareChar();
            case "boolean" -> declareBoolean();
            default -> -1;
        };
    }

    public static void writeInteger(String value, long varAddr){
        generatedDeclarationCode += "\tdd " + value + "\n";
        long constAddr = PC;

        movePC(4);

        writeVariable(constAddr, varAddr);
    }
    public static void writeReal(String value, long varAddr){

         if(value.length() > 1 &&(value.charAt(0) == '.' ||(value.contains("-") && value.charAt(1) == '.'))){
            String[] s = value.split("\\.");
            value = s[0]+"0."+s[1];
         }else if(!value.contains(".")){
             value = value+".0";
         }

        generatedDeclarationCode += "dd " + value + "\n";
        long constAddr = PC;

        movePC(4);

        generatedCommandCode += "\tmovss XMM0, [M +" + constAddr + "]\n";
        generatedCommandCode += "\tmovss [M +" + varAddr + "], XMM0\n";
    }

    public static void writeBoolean(String value, long varAddr){
        generatedDeclarationCode += "dd " + (value.equals("true")? 1 : 0) + "\n";
        long constAddr = PC;

        movePC(1);

        writeVariable(constAddr, varAddr);
    }

    public static void writeChar(String value, long varAddr){
        generatedDeclarationCode += "db" + value + "\n";
        long constAddr = PC;

        movePC(1);

        generatedCommandCode += "mov AL, [M +" + constAddr + "]\n";
        generatedCommandCode += "mov [M +" + varAddr + "], AL\n";
    }

    public static void writeFinal(String value, long varAddr, String tipo){
        switch (tipo) {
            case "integer" -> writeInteger(value, varAddr);
            case "real" -> writeReal(value, varAddr);
            case "char" -> writeChar(value, varAddr);
            case "boolean" -> writeBoolean(value, varAddr);
        };
    }

    public static void writeVariable(long addr1, long addr2){
        generatedCommandCode += "mov EAX, [M +" + addr1 + "]\n";
        generatedCommandCode += "mov [M +" + addr2 + "], EAX\n";
    }

    public static void print(long addr, String tipo, boolean isEndLine){
        switch (tipo) {
            case "integer" -> printInteger(addr);
            case "real" -> printReal(addr);
            case "char" -> printChar(addr);
        }

        if(isEndLine){
            printEndLine();
        }
    }

    public static void printInteger(long addr){
        String rot1 = getNextRot();
        String rot2 = getNextRot();

        long actualMemoryPosition = allocateTemp(4);

        generatedCommandCode += "\tmov EAX, 0 \n";
        generatedCommandCode += "\tmov RDI, 0 \n";
        generatedCommandCode += "\tmov EAX, [ M + " + addr + " ] \n";
        generatedCommandCode += "\tmov RDI, M + " + actualMemoryPosition + " \n";
        generatedCommandCode += "\tmov RCX, 0 \n";
        generatedCommandCode += "\tmov RSI, 0 \n";
        generatedCommandCode += "\tcmp EAX, 0 \n";
        generatedCommandCode += "\tjge " + rot1 + " \n";
        generatedCommandCode += "\tmov BL, \'-\' \n";
        generatedCommandCode += "\tmov [RDI], BL \n";
        generatedCommandCode += "\tadd RDI, 1 \n";
        generatedCommandCode += "\tneg EAX \n";
        generatedCommandCode += rot1 + ":\n";
        generatedCommandCode += "\tmov EBX, 10 \n";
        generatedCommandCode += "\tmov EDX, 0 \n";
        generatedCommandCode += "\tidiv EBX \n";

        generatedCommandCode += "\tpush DX \n";
        generatedCommandCode += "\tadd RCX, 1 \n";
        generatedCommandCode += "\tcmp EAX, 0 \n";
        generatedCommandCode += "\tjne " + rot1 + " \n";
        generatedCommandCode += rot2 + ":\n";

        generatedCommandCode += "\tpop AX \n";
        generatedCommandCode += "\tadd AX, \'0\' \n";
        generatedCommandCode += "\tmov [RDI], AL \n";
        generatedCommandCode += "\tadd RDI, 1 \n";
        generatedCommandCode += "\tsub RCX, 1 \n";
        generatedCommandCode += "\tcmp RCX, 0 \n";
        generatedCommandCode += "\tjg " + rot2 + "\n";
        generatedCommandCode += "\tmov [RDI], byte 0 \n";
        generatedCommandCode += "\tsub RDI, M + " + actualMemoryPosition + " ; \n";
        generatedCommandCode += "\tmov RSI, M + " + actualMemoryPosition + " ; \n";
        generatedCommandCode += "\tmov RDX, RDI\n";
        generatedCommandCode += "\tmov RAX, 1 ; \n";
        generatedCommandCode += "\tmov RDI, 1\n";
        generatedCommandCode += "\tsyscall\n";
    }

    public static void printReal(long addr){
        String rot0 = getNextRot();
        String rot1 = getNextRot();
        String rot2 = getNextRot();
        String rot3 = getNextRot();
        String rot4 = getNextRot();

        long memPos = allocateTemp(4);

        generatedCommandCode += "\tmovss XMM0, [ M + " + addr + " ]\n";
        generatedCommandCode += "\tmov RSI, M + " + memPos + "\n";
        generatedCommandCode += "\tmov RCX, 0\n";
        generatedCommandCode += "\tmov RDI, 6\n";
        generatedCommandCode += "\tmov RBX, 10\n";
        generatedCommandCode += "\tcvtsi2ss XMM2, RBX\n";
        generatedCommandCode += "\tsubss XMM1, XMM1\n";
        generatedCommandCode += "\tcomiss XMM0, XMM1\n";
        generatedCommandCode += "\tjae " + rot0 + "\n";
        generatedCommandCode += "\tmov DL, \'-\'\n";
        generatedCommandCode += "\tmov [RSI], DL\n";
        generatedCommandCode += "\tmov RDX, -1\n";
        generatedCommandCode += "\tcvtsi2ss XMM1, RDX\n";
        generatedCommandCode += "\tmulss XMM0, XMM1\n";
        generatedCommandCode += "\tadd RSI, 1\n";
        generatedCommandCode += rot0 + ": \n";
        generatedCommandCode += "\troundss XMM1, XMM0, 0b0011\n";
        generatedCommandCode += "\tsubss XMM0, XMM1\n";
        generatedCommandCode += "\tcvtss2si rax, XMM1\n";
        generatedCommandCode += "\t\n";
        generatedCommandCode += rot1 + ": \n";
        generatedCommandCode += "\tadd RCX, 1\n";
        generatedCommandCode += "\tcdq\n";
        generatedCommandCode += "\tidiv EBX\n";
        generatedCommandCode += "\tpush RDX\n";
        generatedCommandCode += "\tcmp EAX, 0\n";
        generatedCommandCode += "\tjne " + rot1 + "\n";
        generatedCommandCode += "\tsub RDI, RCX\n";
        generatedCommandCode += rot2 + ":\n";
        generatedCommandCode += "\tpop RDX\n";
        generatedCommandCode += "\tadd DL, \'0\'\n";
        generatedCommandCode += "\tmov [RSI], DL\n";
        generatedCommandCode += "\tadd RSI, 1\n";
        generatedCommandCode += "\tsub RCX, 1\n";
        generatedCommandCode += "\tcmp RCX, 0\n";
        generatedCommandCode += "\tjne " + rot2 + "\n";
        generatedCommandCode += "\tmov DL, \'.\'\n";
        generatedCommandCode += "\tmov [RSI], DL\n";
        generatedCommandCode += "\tadd RSI, 1\n";
        generatedCommandCode += rot3 + ":\n";
        generatedCommandCode += "\tcmp RDI, 0\n";
        generatedCommandCode += "\tjle " + rot4 + "\n";
        generatedCommandCode += "\tmulss XMM0,XMM2\n";
        generatedCommandCode += "\troundss XMM1,XMM0,0b0011\n";
        generatedCommandCode += "\tsubss XMM0,XMM1\n";
        generatedCommandCode += "\tcvtss2si RDX, XMM1\n";
        generatedCommandCode += "\tadd DL, \'0\'\n";
        generatedCommandCode += "\tmov [RSI], DL\n";

        generatedCommandCode += "\tadd RSI, 1\n";
        generatedCommandCode += "\tsub RDI, 1\n";
        generatedCommandCode += "\tjmp " + rot3 + "\n";
        generatedCommandCode += rot4 + ":\n";
        generatedCommandCode += "\tmov DL, 0\n";
        generatedCommandCode += "\tmov [RSI], DL\n";
        generatedCommandCode += "\tmov RDX, RSI \n";
        generatedCommandCode += "\tmov RBX, M + " + memPos + " \n";
        generatedCommandCode += "\tsub RDX, RBX\n";
        generatedCommandCode += "\tmov RSI, M + " + memPos + "\n";
        generatedCommandCode += "\tmov RAX, 1\n";
        generatedCommandCode += "\tmov RDI, 1\n";
        generatedCommandCode += "\tsyscall\n";
    }

    public static void printChar(long addr){
        generatedCommandCode += "\tmov RSI, M + " + addr + "\n";
        generatedCommandCode += "\tmov RDX, 1\n";

        generatedCommandCode += "\tmov RAX, 1\n";
        generatedCommandCode += "\tmov RDI, 1\n";
        generatedCommandCode += "\tsyscall\n";
    }

    public static void printEndLine()
    {
        long buffer = allocateTemp(1);

        generatedCommandCode += "\tmov RSI, M + " + buffer + "\n";
        generatedCommandCode += "\tmov [RSI], byte 10\n";
        generatedCommandCode += "\tmov RDX, 1\n";
        generatedCommandCode += "\tmov RAX, 1\n";
        generatedCommandCode += "\tmov RDI, 1\n";
        generatedCommandCode += "\tsyscall\n";
    }


    public static void read(long addr, String tipo){
        switch (tipo) {
            case "integer" -> readInteger(addr);
            case "real" -> readReal(addr);
            case "char" -> readChar(addr);
        }
    }

    public static void readInteger(long addr) {
        String rot0 = getNextRot();
        String rot1 = getNextRot();
        String rot2 = getNextRot();
        String rot4 = getNextRot();

        long buffer = allocateTemp(12);

        generatedCommandCode += "\tmov RSI, M + " + buffer + "\n";
        generatedCommandCode += "\tmov RDX, 100h\n";
        generatedCommandCode += "\tmov RAX, 0\n";
        generatedCommandCode += "\tmov RDI, 0\n";
        generatedCommandCode += "\tsyscall\n";
        generatedCommandCode += "\tmov EAX, 0\n";
        generatedCommandCode += "\tmov EBX, 0\n";
        generatedCommandCode += "\tmov ECX, 10\n";
        generatedCommandCode += "\tmov RDX, 1\n";
        generatedCommandCode += "\tmov RSI, M + " + buffer + "\n";
        generatedCommandCode += "\tmov BL, [RSI]\n";
        generatedCommandCode += "\tcmp BL, \'-\'\n";
        generatedCommandCode += "\tjne " + rot0 + " \n";
        generatedCommandCode += "\tmov RDX, -1 \n";
        generatedCommandCode += "\tadd RSI, 1 \n";
        generatedCommandCode += "\tmov BL, [RSI]\n";
        generatedCommandCode += rot0 + ":\n";
        generatedCommandCode += "\tpush RDX\n";
        generatedCommandCode += "\tmov EDX, 0\n";
        generatedCommandCode += rot1 + ":\n";
        generatedCommandCode += "\tcmp BL, 0Ah\n";
        generatedCommandCode += "\tje " + rot2 + "\n";
        generatedCommandCode += "\timul ECX\n";
        generatedCommandCode += "\tsub BL, \'0\'\n";
        generatedCommandCode += "\tadd EAX, EBX\n";
        generatedCommandCode += "\tadd RSI, 1\n";
        generatedCommandCode += "\tmov BL, [RSI]\n";
        generatedCommandCode += "\tjmp " + rot1 + "\n";
        generatedCommandCode += rot2 + ":\n";
        generatedCommandCode += "\tpop CX \n";
        generatedCommandCode += "\tcmp CX, 0\n";
        generatedCommandCode += "\tjg " + rot4 + "\n";
        generatedCommandCode += "\tneg EAX\n";
        generatedCommandCode += rot4 + ":\n";
        generatedCommandCode += "\tmov [ M + " + addr + " ], EAX\n";
    }

    public static void readReal(long addr){
        String rot0 = getNextRot();
        String rot1 = getNextRot();
        String rot2 = getNextRot();
        String rot3 = getNextRot();
        String rotEnd = getNextRot();

        long buffer = allocateTemp(12);

        generatedCommandCode += "\tmov RSI, M + " + buffer + "\n";
        generatedCommandCode += "\tmov RDX, 100h\n";
        generatedCommandCode += "\tmov RAX, 0\n";
        generatedCommandCode += "\tmov RDI, 0\n";
        generatedCommandCode += "\tsyscall\n";
        generatedCommandCode += "\tmov RAX, 0\n";
        generatedCommandCode += "\tsubss XMM0, XMM0\n";
        generatedCommandCode += "\tmov RBX, 0\n";
        
        generatedCommandCode += "\tmov RCX, 10\n";
        generatedCommandCode += "\tcvtsi2ss XMM3, RCX\n";
        generatedCommandCode += "\tmovss XMM2, XMM3\n";
        generatedCommandCode += "\tmov RDX, 1\n";
        generatedCommandCode += "\tmov RSI, M+" + buffer + "\n";
        generatedCommandCode += "\tmov BL, [RSI]\n";
        generatedCommandCode += "\tcmp BL, '-' \n";
        generatedCommandCode += "\tjne " + rot0 + "\n";
        generatedCommandCode += "\tmov RDX, -1\n";
        generatedCommandCode += "\tadd RSI, 1\n";
        generatedCommandCode += "\tmov BL, [RSI]\n";
        generatedCommandCode += rot0 + ":\n";
        generatedCommandCode += "\tpush RDX\n";
        generatedCommandCode += "\tmov RDX, 0\n";
        generatedCommandCode += rot1 + ":\n";
        generatedCommandCode += "\tcmp BL, 0Ah\n";
        generatedCommandCode += "\tje " + rot2 + "\n";
        generatedCommandCode += "\tcmp BL, \'.\'\n";
        generatedCommandCode += "\tje " + rot3 + "\n";
        generatedCommandCode += "\timul ECX\n";
        generatedCommandCode += "\tsub BL, \'0\'\n";
        generatedCommandCode += "\tadd EAX, EBX\n";
        generatedCommandCode += "\tadd RSI, 1\n";
        generatedCommandCode += "\tmov BL, [RSI]\n";
        generatedCommandCode += "\tjmp " + rot1 + "\n";

        generatedCommandCode += rot3 + ":\n";
        generatedCommandCode += "\tadd RSI, 1 \n";
        generatedCommandCode += "\tmov BL, [RSI]\n";
        generatedCommandCode += "\tcmp BL, 0Ah\n";
        generatedCommandCode += "\tje " + rot2 + "\n";
        generatedCommandCode += "\tsub BL, \'0\'\n";
        generatedCommandCode += "\tcvtsi2ss XMM1, RBX\n";
        generatedCommandCode += "\tdivss XMM1, XMM2\n";
        generatedCommandCode += "\taddss XMM0, XMM1\n";
        
        generatedCommandCode += "\tmulss XMM2, XMM3\n";
        generatedCommandCode += "\tjmp " + rot3 + "\n";
        generatedCommandCode += rot2 + ":\n";
        generatedCommandCode += "\tcvtsi2ss XMM1, RAX\n";
        generatedCommandCode += "\taddss XMM0, XMM1\n";
        generatedCommandCode += "\tpop RCX\n";
        generatedCommandCode += "\tcvtsi2ss XMM1, RCX\n";
        generatedCommandCode += "\tmulss XMM0, XMM1\n";
        
        generatedCommandCode += rotEnd + ":\n";
        generatedCommandCode += "\tmovss [ M + " + addr + " ], XMM0\n";
    }
    
    public static void readChar(long addr){
        String rot0 = getNextRot();
        long buffer = allocateTemp(1);

        generatedCommandCode += "\tmov RSI, M + " + addr + "\n";
        generatedCommandCode += "\tmov RDX, 1\n";
        generatedCommandCode += "\tmov RAX, 0\n";
        generatedCommandCode += "\tmov RDI, 0\n";
        generatedCommandCode += "\tsyscall\n\n";
        generatedCommandCode += rot0 + ":\n";
        generatedCommandCode += "\tmov RDX, 1\n";
        generatedCommandCode += "\tmov RSI, M + " + buffer + "\n";
        generatedCommandCode += "\tmov RAX, 0\n";
        generatedCommandCode += "\tmov RDI, 0\n";
        generatedCommandCode += "\tsyscall\n\n";
        generatedCommandCode += "\tmov AL,[ M + " + buffer + " ]\n";
        generatedCommandCode += "\tcmp AL, 0xA\n";
        generatedCommandCode += "\tjne " + rot0 + "\n";
    }


    public static void fimTesteIf(String ROT1,AtributoHerdado E){

        generatedCommandCode += "\tmov AL,[ M+"+ E.endereco +"] \t\t\t ; \n";
        generatedCommandCode += "\tcmp AL, 1 \t\t\t ; \n";
        generatedCommandCode += "\tjne " + ROT1 + " \t\t\t ; \n";
    }
    public static void fimBlocoIf(String ROT2){
        generatedCommandCode += "\tjmp " + ROT2 + " \t\t\t ; \n";
    }
    public static void inicioElse(String ROT1){
        generatedCommandCode += ROT1+ ":\n";
    }
    public static void fimElse(String ROT2){
        generatedCommandCode += ROT2+ ":\n";
    }
    public static void inicioTesteFor(String ROT1){
        generatedCommandCode += ROT1+ ":\n";
    }
    public static void fimTesteFor(String ROT2,String ROT3,String ROT4,AtributoHerdado E){

        generatedCommandCode += "\tmov AL,[ M+"+ E.endereco +"] \t\t\t ; \n";
        generatedCommandCode += "\tcmp AL, 1 \t\t\t ; \n";
        generatedCommandCode += "\tje " + ROT2 + " \t\t\t ; \n";
        generatedCommandCode += "\tjmp " + ROT4 + " \t\t\t ; \n";
    }
    public static void inicioIncrementFor(String ROT3){
        generatedCommandCode += ROT3+ ":\n";
    }
    public static void fimIncrementFor(String ROT1){
        generatedCommandCode += "\tjmp " + ROT1 + " \t\t\t ; \n";
    }
    public static void inicioBlocoFor(String ROT2){
        generatedCommandCode += ROT2+ ":\n";
    }
    public static void fimBlocoFor(String ROT3,String ROT4){
        generatedCommandCode += "\tjmp " + ROT3 + " \t\t\t ; \n";
        generatedCommandCode += ROT4+ ":\n";
    }

    public static String finalizeCode(){
        generatedCommandCode += "\tmov rax, 60\n";
        generatedCommandCode += "\tmov rdi, 0\n";
        generatedCommandCode += "\tsyscall\n";


        return generatedCode + generatedDeclarationCode + generatedCommandCode;
    }

    public static void lerConst(RegistroLexico reg) {
        int tamanhoTipo=0;
        switch (reg.tipoConst){
            case "integer" ->{
                generatedCommandCode += "\tmov EAX," + reg.valorConst+"; \t\t\t move valor da constante para um registrador\n";
                generatedCommandCode += "\tmov [M + " + tempCounter +"], EAX; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                tamanhoTipo=4;
            }
            case "real" ->{
                if(reg.valorConst.length() > 1 &&(reg.valorConst.charAt(0) == '.' ||(reg.valorConst.contains("-") && reg.valorConst.charAt(1) == '.'))){
                    String[] s = reg.valorConst.split("\\.");
                    reg.valorConst = s[0]+"0."+s[1];
                }else if(!reg.valorConst.contains(".")){
                    reg.valorConst  =  reg.valorConst +".0";
                }
                generatedDeclarationCode += "\tdd "+ reg.valorConst+"\n";
                generatedCommandCode += "\tmovss XMM0,[M + " +PC +"]; \t\t\t move valor da constante para um registrador\n";
                movePC(4);
                generatedCommandCode += "\tmovss [M + " + tempCounter +"], XMM0; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                tamanhoTipo=4;
            }
            case "char" ->{
                generatedCommandCode += "\tmov AL," + reg.valorConst+"; \t\t\t move valor da constante para um registrador\n";
                generatedCommandCode += "\tmov [M + " + tempCounter +"], AL; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                tamanhoTipo=1;
            }
            case "boolean"->{
                generatedCommandCode += "\tmov AL," + (reg.valorConst.equals("true")?1:0) +"; \t\t\t move valor da constante para um registrador\n";
                generatedCommandCode += "\tmov [M + " + tempCounter +"], AL; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                tamanhoTipo=1;
            }
        }
        reg.endConst = tempCounter;
        moveTC(tamanhoTipo);
    }

    public static void atribuicaoEx(RegistroLexico id, AtributoHerdado e) {
        switch (id.lexema.tipo){
            case "integer" ->{
                generatedCommandCode += "\tmov EAX,[M +" + e.endereco+"];\t\t\t  move endereco da expressao para registrador\n";
                generatedCommandCode += "\tmov [M +" + id.lexema.endereco+"], EAX; \t\t\t  move valor do registrador para o endereco do identificador\n";
            }
            case "real" ->{
                generatedCommandCode += "\tmovss XMM0,[M +" + e.endereco+"];\t\t\t  Le endereco da expressao e coloca em registrador\n";
                generatedCommandCode += "\tmovss [M +" + id.lexema.endereco+"], XMM0; \t\t\t  move valor do registrador para o endereco do identificador\n";
            }
            case "char" ->{
                generatedCommandCode += "\tmov AL,[M +" + e.endereco+"];\t\t\t Le endereco da expressao e coloca em registrador\n";
                generatedCommandCode += "\tmov [M +" + id.lexema.endereco+"], AL; \t\t\t  move valor do registrador para o endereco do identificador\n";
            }
            //case "boolean"
        }
    }
    public static void lerId(RegistroLexico reg) {
        int tamanhoTipo=0;
        switch (reg.lexema.tipo){
            case "integer" ->{
                generatedCommandCode += "\tmov EAX,[M + " + reg.lexema.endereco+"]; \t\t\t move valor da constante para um registrador\n";
                generatedCommandCode += "\tmov [M + " + tempCounter +"], EAX; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                tamanhoTipo=4;
            }
            case "real" ->{
                generatedCommandCode += "\tmovss XMM0,[M + " +reg.lexema.endereco +"]; \t\t\t move valor da constante para um registrador\n";
                generatedCommandCode += "\tmovss [M + " + tempCounter +"], XMM0; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                tamanhoTipo=4;
            }
            case "char" ->{
                generatedCommandCode += "\tmov AL,[M +" + reg.lexema.endereco+"]; \t\t\t move valor da constante para um registrador\n";
                generatedCommandCode += "\tmov [M + " + tempCounter +"], AL; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                tamanhoTipo=1;
            }
            case "boolean"->{
                generatedCommandCode += "\tmov AL,[M +" + reg.lexema.endereco +"]; \t\t\t move valor da constante para um registrador\n";
                generatedCommandCode += "\tmov [M + " + tempCounter +"], AL; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                tamanhoTipo=1;
            }
        }
        reg.endConst = tempCounter;
        moveTC(tamanhoTipo);
    }

    public static void integer2real(AtributoHerdado e) {
        generatedCommandCode += "\tmov EAX,[M + " + e.endereco+"]; \t\t\t move valor da constante para um registrador\n";
        generatedCommandCode += "\tcdqe \t\t\t; Expandindo o valor do integer\n";
        generatedCommandCode += "\tcvtsi2ss XMM0, RAX; \t\t    \n";
        generatedCommandCode += "\tmovss [M + " + e.endereco +"], XMM0; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
    }

    public static void real2integer(AtributoHerdado e) {


       generatedCommandCode += "\tmovss XMM0,[M + " + e.endereco+"]; \t\t\t move valor da constante para um registrador\n";
       generatedCommandCode += "\tcvtss2si RAX, XMM0; \t\t\t   \n";
       generatedCommandCode += "\tmov [M + " + e.endereco +"], RAX; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
    }

    public static void not(AtributoHerdado atributoE4) {
        generatedCommandCode += "\tmov AL, [M + " + atributoE4.endereco +"]; \t\t\t move valor da constante para um registrador\n";
        generatedCommandCode += "\tneg AL; \t\t\t move valor da constante para um registrador\n";
        generatedCommandCode += "\tmov [M + " + atributoE4.endereco +"], AL; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
    }

    public static void operacoesComplexas(AtributoHerdado atributoPai, AtributoHerdado atributoE, AtributoOperacao op) {
        switch (op.op) {
            case "*" -> {
                boolean flagReal = false;
                if(atributoPai.tipo.equals("real")||atributoE.tipo.equals("real")){
                    if( atributoE.tipo.equals("integer") )
                        integer2real(atributoE);
                    if( atributoPai.tipo.equals("integer") )
                        integer2real(atributoPai);
                    flagReal = true;
                }
                if(flagReal) {
                    generatedCommandCode += "\tmovss XMM0,[M + " + atributoPai.endereco + "]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\tmovss XMM1,[M + " + atributoE.endereco + "]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\tmulss XMM0, XMM1; \t\t\t multiplica os valores\n";
                    generatedCommandCode += "\tmovss [M + " + tempCounter + "], XMM0; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                    atributoPai.endereco = tempCounter;
                    moveTC(4);
                }else{
                    generatedCommandCode += "\tmov EAX,[M + " +atributoPai.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\tmov EBX,[M + " +atributoE.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\timul EAX, EBX; \t\t\t multiplica os valores\n";
                    generatedCommandCode += "\tmov [M + " + tempCounter +"], EAX; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                    atributoPai.endereco = tempCounter;
                    moveTC(4);
                }

            }
            case "/" -> {
                boolean flagReal = false;

                if( atributoE.tipo.equals("integer") )
                    integer2real(atributoE);
                if( atributoPai.tipo.equals("integer") )
                    integer2real(atributoPai);


                generatedCommandCode += "\tmovss XMM0,[M + " +atributoPai.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                generatedCommandCode += "\tmovss XMM1,[M + " +atributoE.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                generatedCommandCode += "\tdivss XMM0, XMM1; \t\t\t divide os valores\n";
                generatedCommandCode += "\tmovss [M + " + tempCounter +"], XMM0; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                moveTC(4);
            }
            case "and" -> {

                generatedCommandCode += "\tmov AL,[M +" + atributoPai.endereco +"]; \t\t\t move valor da constante para um registrador\n";
                generatedCommandCode += "\tmov BL,[M +" + atributoE.endereco +"]; \t\t\t move valor da constante para um registrador\n";
                generatedCommandCode += "\timul AL, BL; \t\t\t AND é o mesmo que multiplicacao 1*0 = 0 1*1 = 1\n";
                generatedCommandCode += "\tmov [M + " + tempCounter +"], AL; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                atributoPai.endereco = tempCounter;
                moveTC(1);
            }
            case "mod" -> {
                generatedCommandCode += "\tmov EAX,[M + " +atributoPai.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                generatedCommandCode += "\tmov EBX,[M + " +atributoE.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                generatedCommandCode += "\tcdq; \t\t\t move valor da memoria para um registrador\n";
                generatedCommandCode += "\tidiv EBX; \t\t\t multiplica os valores\n";
                generatedCommandCode += "\tmov [M + " + tempCounter +"], EDX; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                atributoPai.endereco = tempCounter;
                moveTC(4);
            }
            case "div" -> {
                generatedCommandCode += "\tmov EAX,[M + " +atributoPai.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                generatedCommandCode += "\tmov EBX,[M + " +atributoE.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                generatedCommandCode += "\tcdq; \t\t\t move valor da memoria para um registrador\n";
                generatedCommandCode += "\tidiv EBX; \t\t\t multiplica os valores\n";
                generatedCommandCode += "\tmov [M + " + tempCounter +"], EAX; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                atributoPai.endereco = tempCounter;
                moveTC(4);
            }
        }
    }

    public static void operacoesSimples(AtributoHerdado atributoPai, AtributoHerdado atributoE, AtributoOperacao op) {
        switch (op.op) {
            case "+" -> {
                boolean flagReal = false;
                if(atributoPai.tipo.equals("real")||atributoE.tipo.equals("real")){
                    if( atributoE.tipo.equals("integer") )
                        integer2real(atributoE);
                    if( atributoPai.tipo.equals("integer") )
                        integer2real(atributoPai);
                    flagReal = true;
                }
                if(flagReal) {
                    generatedCommandCode += "\tmovss XMM0,[M + " + atributoPai.endereco + "]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\tmovss XMM1,[M + " + atributoE.endereco + "]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\taddss XMM0, XMM1; \t\t\t soma os valores\n";
                    generatedCommandCode += "\tmovss [M + " + tempCounter + "], XMM0; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                    atributoPai.endereco = tempCounter;
                    moveTC(4);
                }else{
                    generatedCommandCode += "\tmov EAX,[M + " +atributoPai.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\tmov EBX,[M + " +atributoE.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\tadd EAX, EBX; \t\t\t soma os valores\n";
                    generatedCommandCode += "\tmov [M + " + tempCounter +"], EAX; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                    atributoPai.endereco = tempCounter;
                    moveTC(4);
                }

            }
            case "-" -> {
                boolean flagReal = false;
                if(atributoPai.tipo.equals("real")||atributoE.tipo.equals("real")){
                    if( atributoE.tipo.equals("integer") )
                        integer2real(atributoE);
                    if( atributoPai.tipo.equals("integer") )
                        integer2real(atributoPai);
                    flagReal = true;
                }
                if(flagReal) {
                    generatedCommandCode += "\tmovss XMM0,[M + " + atributoPai.endereco + "]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\tmovss XMM1,[M + " + atributoE.endereco + "]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\tsubss XMM0, XMM1; \t\t\t soma os valores\n";
                    generatedCommandCode += "\tmovss [M + " + tempCounter + "], XMM0; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                    atributoPai.endereco = tempCounter;
                    moveTC(4);
                }else{
                    generatedCommandCode += "\tmov EAX,[M + " +atributoPai.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\tmov EBX,[M + " +atributoE.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                    generatedCommandCode += "\tsub EAX, EBX; \t\t\t soma os valores\n";
                    generatedCommandCode += "\tmov [M + " + tempCounter +"], EAX; \t\t\t move valor do registrador para endereco de memoria temporaria\n";
                    atributoPai.endereco = tempCounter;
                    moveTC(4);
                }
            }
            case "or" -> {
                //a+b - a*b
                generatedCommandCode += "\tmov AL,[M +" + atributoPai.endereco +"]; \t\t\t move valor da memoria para um registrador\n";
                generatedCommandCode += "\tmov BL,[M +" + atributoE.endereco +"]; \t\t\t \n";
                generatedCommandCode += "\tmov CL,[M +" + atributoPai.endereco +"]; \t\t\t \n";
                generatedCommandCode += "\tmov DL,[M +" + atributoE.endereco +"]; \t\t\t \n";
                generatedCommandCode += "\timul BL; \t\t\t\n";
                generatedCommandCode += "\tadd CL, DL; \t\t\t \n";
                generatedCommandCode += "\tsub CL, AL; \t\t\t \n";
                generatedCommandCode += "\tmov [M + " + tempCounter +"], CL; \t\t\t\n";
                atributoPai.endereco = tempCounter;
                moveTC(1);
            }
        }
    }

//   public static void comparacoes(AtributoHerdado atributoPai, AtributoHerdado atributoE, AtributoOperacao op) {
//        if(atributoPai.tipo.equals("char") && atributoE.tipo.equals("char")){
//            compChars(atributoPai, atributoE, op);
//        } else if(atributoPai.tipo.equals("integer") && atributoE.tipo.equals("integer")){
//            compIntegers(atributoPai, atributoE, op);
//        } else if(atributoPai.tipo.equals("real") && atributoE.tipo.equals("real")){
//            compReal(atributoPai, atributoE, op);
//        } else if(atributoPai.tipo.equals("integer") && atributoE.tipo.equals("real")){
//            compIntReal(atributoPai, atributoE, op);
//            if(atributoPai.tipo.equals("real") && atributoE.tipo.equals("integer")){
//            compRealInt(atributoPai, atributoE, op);
//            }
//        }
//
//}


//    private static long compChars(AtributoHerdado atributoPai, AtributoHerdado atributoE, AtributoOperacao op) {
//        long memPos = tempCounter;
//
//        String labelTrue = getNextRot();
//        String labelEnd = getNextRot();
//
//        generatedCommandCode += "\tmov AL, [ M + " + atributoPai.endereco + " ]\n";
//        generatedCommandCode += "\tmov BL, [ M + " + atributoE.endereco + " ]\n";
//        generatedCommandCode += "\tcmp AL, BL\n";
//
//        switch (op.op) {
//            case "==" -> generatedCommandCode += "\tje " + labelTrue + "\n";
//            case ">" -> generatedCommandCode += "\tjg " + labelTrue + "\n";
//            case ">=" -> generatedCommandCode += "\tjge " + labelTrue + "\n";
//            case "<" -> generatedCommandCode += "\tjl " + labelTrue + "\n";
//            case "<=" -> generatedCommandCode += "\tjle " + labelTrue + "\n";
//            case "<>" -> generatedCommandCode += "\tjne " + labelTrue + "2\n";
//        }
//
//        generatedCommandCode += "\tmov EAX, 0\n";
//        generatedCommandCode += "\tjmp " + labelEnd;
//        generatedCommandCode += "\n" + labelTrue + ":\n";
//        generatedCommandCode += "\tmov EAX, 1\n";
//        generatedCommandCode += labelEnd + ":\n";
//        generatedCommandCode += "\tmov [ M + " + tempCounter + " ], EAX\n";
//
//        return memPos;
//    }


//    private static long compChars(AtributoHerdado atributoPai, AtributoHerdado atributoE, AtributoOperacao op) {
//        long memPos = tempCounter;
//
//        String labelTrue = getNextRot();
//        String labelEnd = getNextRot();
//
//        generatedCommandCode += "\tmov AL, [ M + " + atributoPai.endereco + " ]\n";
//        generatedCommandCode += "\tmov BL, [ M + " + atributoE.endereco + " ]\n";
//        generatedCommandCode += "\tcmp AL, BL\n";
//
//        switch (op.op) {
//            case "==" -> generatedCommandCode += "\tje " + labelTrue + "\n";
//            case ">" -> generatedCommandCode += "\tjg " + labelTrue + "\n";
//            case ">=" -> generatedCommandCode += "\tjge " + labelTrue + "\n";
//            case "<" -> generatedCommandCode += "\tjl " + labelTrue + "\n";
//            case "<=" -> generatedCommandCode += "\tjle " + labelTrue + "\n";
//            case "<>" -> generatedCommandCode += "\tjne " + labelTrue + "2\n";
//        }
//
//        generatedCommandCode += "\tmov EAX, 0\n";
//        generatedCommandCode += "\tjmp " + labelEnd;
//        generatedCommandCode += "\n" + labelTrue + ":\n";
//        generatedCommandCode += "\tmov EAX, 1\n";
//        generatedCommandCode += labelEnd + ":\n";
//        generatedCommandCode += "\tmov [ M + " + tempCounter + " ], EAX\n";
//
//        return memPos;
//    }
}

