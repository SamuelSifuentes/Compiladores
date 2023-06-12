import java.io.IOException;

// TODO AVALIAR SE TEM QUE TIRAR VALOR

public class AnalisadorSintatico {
    AnalisadorLexico analisadorLexico;
    RegistroLexico reg;

    AnalisadorSintatico(String caminhoArquivo){
        analisadorLexico = new AnalisadorLexico(caminhoArquivo);
    }

    public void analisar() throws LexicalException, IOException, SyntaticException, SemanticalException {
        reg = analisadorLexico.analisar();
        S();

        System.out.println(analisadorLexico.linhaAtual + " linhas compiladas");
    }

    public void CasaToken(String tokenEsperado) throws IOException, LexicalException, SyntaticException {
        if(reg.token.nome.equals(tokenEsperado)){
            reg = analisadorLexico.analisar();
        }
        else{
            if(reg.token.nome.equals("FIM_DE_ARQUIVO")){
                throw new SyntaticException(analisadorLexico.linhaAtual, SyntaticErrorEnum.FIM_DE_ARQUIVO);
            }

            throw new SyntaticException(analisadorLexico.linhaAtual, SyntaticErrorEnum.TOKEN_NAO_ESPERADO, reg.lexema.nome);
        }
    }

    public void S() throws LexicalException, IOException, SyntaticException, SemanticalException {
        while(!reg.token.nome.equals("FIM_DE_ARQUIVO")){
            if(reg.token.nome.equals("integer") || reg.token.nome.equals("real") || reg.token.nome.equals("char") || reg.token.nome.equals("boolean") || reg.token.nome.equals("final")){
                D();
            }
            else{
                C();
            }
        }

        CasaToken("FIM_DE_ARQUIVO");
        System.out.println("Compilacão finalizada.");
    }

    // E -> E1 [( = | <> | < | <= | >= | > ) E1]
    public void E(AtributoHerdado atributoPai) throws LexicalException, IOException, SyntaticException, SemanticalException {
        
        AtributoHerdado atributoE1_1 = new AtributoHerdado();
        
        E1(atributoE1_1);
        s33(atributoPai, atributoE1_1);


        AtributoOperacao atributoOperacao = new AtributoOperacao();
        String tokenAtual = reg.token.nome;

        if(tokenAtual.equals("==") || tokenAtual.equals("<>") || tokenAtual.equals("<") ||
                tokenAtual.equals("<=") || tokenAtual.equals(">=") || tokenAtual.equals(">")) {
            switch (reg.token.nome) {
                case "==" -> CasaToken("==");
                case "<>" -> CasaToken("<>");
                case "<" -> CasaToken("<");
                case "<=" -> CasaToken("<=");
                case ">=" -> CasaToken(">=");
                case ">" -> CasaToken(">");
            }
            s34(atributoOperacao, tokenAtual);

            AtributoHerdado atributoE1_2 = new AtributoHerdado();
            E1(atributoE1_2);
            s35(atributoOperacao,atributoPai,atributoE1_2);
            s36(atributoPai);
        }

    }

    // E1 -> [-] E2 {(+ | - | or) E2}*
    public void E1(AtributoHerdado atributoPai) throws LexicalException, IOException, SyntaticException, SemanticalException {
        AtributoFlag atributoFlag = new AtributoFlag();

        if(reg.token.nome.equals("-")) {
            CasaToken("-");
            s27(atributoFlag);
        }

        AtributoHerdado atributoE2_1 = new AtributoHerdado();
        E2(atributoE2_1);
        s30(atributoE2_1, atributoFlag); // WARNING aqui é esse mesmo ou é atributopai?
        s23(atributoPai, atributoE2_1);

        while (reg.token.nome.equals("+") || reg.token.nome.equals("-") || reg.token.nome.equals("or")){

            AtributoOperacao atributoOperacao = new AtributoOperacao();
            String op = reg.token.nome;

            if(reg.token.nome.equals("+"))
            {
                CasaToken("+");
            }
            else if(reg.token.nome.equals("-"))
            {
                CasaToken("-");
            }
            else {
                CasaToken("or");
            }

            AtributoHerdado atributoE2_2 = new AtributoHerdado();
            s28(atributoOperacao, op);
            E2(atributoE2_2);
            s29(atributoOperacao,atributoPai,atributoE2_2);
            s32(atributoPai,atributoE2_2,atributoOperacao);
        }


    }

    // E2 -> E3 {(* | (div | /) | mod | and) E3}
    public void E2(AtributoHerdado atributoPai) throws LexicalException, IOException, SyntaticException, SemanticalException {
        AtributoHerdado atributoE3_1 = new AtributoHerdado();
        E3(atributoE3_1);
        s24(atributoPai, atributoE3_1);
        while(reg.token.nome.equals("*") || (reg.token.nome.equals("div") || reg.token.nome.equals("/")) || reg.token.nome.equals("mod") || reg.token.nome.equals("and")){
            AtributoOperacao op = new AtributoOperacao();
            String _op = reg.token.nome;
            switch (reg.token.nome) {
                case "*" ->    CasaToken("*");
                case "div" ->  CasaToken("div");
                case  "/" ->   CasaToken("/");
                case "mod" ->  CasaToken("mod");
                default ->     CasaToken("and");
            }
            AtributoHerdado atributoE3_2 = new AtributoHerdado();
            s25(op,_op);
            E3(atributoE3_2);
            s26(op,atributoPai,atributoE3_2);
            s31(atributoPai, atributoE3_1, op);
        }
    }

    // E3 -> not E4 | E4
    public void E3(AtributoHerdado atributoPai) throws LexicalException, IOException, SyntaticException, SemanticalException {
        AtributoHerdado atributoE4 = new AtributoHerdado();

        if(reg.token.nome.equals("not")  ) {
            CasaToken("not");
            E4(atributoE4);
            s22(atributoE4);
            s21(atributoPai, atributoE4);
        }else{
            E4(atributoE4);
            s21(atributoPai,atributoE4);
        }
    }

    // E4 -> integer '(' E5 ')' | real '(' E5 ')' | E5
    public void E4(AtributoHerdado atributoPai) throws LexicalException, IOException, SyntaticException, SemanticalException {
        AtributoHerdado atributoE5 = new AtributoHerdado();

        if(reg.token.nome.equals("integer")  ) {
            CasaToken("integer");
            CasaToken("(");
            E5(atributoE5);
            s17(atributoE5);
            CasaToken(")");
            s19(atributoPai,atributoE5);
        }else if(reg.token.nome.equals("real")){
            CasaToken("real");
            CasaToken("(");
            E5(atributoE5);
            s17(atributoE5);
            CasaToken(")");
            s20(atributoPai,atributoE5);
        }
        else {
            E5(atributoE5);
            s18(atributoPai,atributoE5);
        }
    }

    // E5 -> const | true | false | id [ '[' E ']' ] | '(' E ')'
    public void E5(AtributoHerdado atributoPai) throws LexicalException, IOException, SyntaticException, SemanticalException {
        RegistroLexico _reg = reg;

        AtributoHerdado atributoE = new AtributoHerdado();
        if(_reg.token.equals("CONST")) {
            CasaToken("CONST");
            s13(atributoPai, _reg);
        }
        else if(_reg.token.equals("true")) {
            CasaToken("true");
            s14(atributoPai, _reg);
        }
        else if(_reg.token.equals("false")){
            CasaToken("false");
            s14(atributoPai, _reg);
        }
        else if(_reg.token.equals("ID")) {
            CasaToken("ID");
            s3(_reg);
            s15(atributoPai,_reg);
            if(reg.token.nome.equals("[")){
                CasaToken("[");
                E(atributoE);
                CasaToken("]");
            }
        }
        else if(_reg.equals("(")) {
            CasaToken("(");
            E(atributoE);
            CasaToken(")");
            s16(atributoPai,atributoE);
        }
    }


    public void D() throws LexicalException, IOException, SyntaticException, SemanticalException {
        switch (reg.token.nome){
            case "integer":
                D1();
                break;
            case "real":
                D2();
                break;
            case "char":
                D3();
                break;
            case "boolean":
                D4();
                break;
            case "final":
                D5();
                break;
        }
    }

    //D1 -> 'integer' (id [ '[' E ']' ] [H] ';' | id = E [H] ';')
    public void D1() throws LexicalException, IOException, SyntaticException, SemanticalException {
        CasaToken("integer");
        
        RegistroLexico id = reg;
        
        CasaToken("ID");
        s1(id);
        s5(id);
        
        if(reg.token.nome.equals("[")){
            CasaToken("[");
            CasaToken("const"); // TODO POR VERIFICACAO DE TIPOS!!!
            CasaToken("]");

        }else if(reg.token.nome.equals("=")) {
            CasaToken("=");
            E();
        }
        if(reg.token.nome.equals(",")){
            H(id.tipo);
        }
        CasaToken(";");
    }

    //D2 -> 'real' (id [ '[' E ']' ] [H] ';' | id = E [H] ';')
    public void D2() throws LexicalException, IOException, SyntaticException, SemanticalException {
        CasaToken("real");

        RegistroLexico id = reg;

        CasaToken("ID");
        s1(id);
        s6(id);
        if(reg.token.nome.equals("[")){
            CasaToken("[");
            CasaToken("const");
            CasaToken("]");

        }
        else if(reg.token.nome.equals("=")) {
            CasaToken("=");
            E();

        }
        if(reg.token.nome.equals(",")){
            H(id.tipo);
        }

        CasaToken(";");
    }

    // D3 -> 'char' (id [ '[' E ']' ] [H1] ';' | id = E [H1] ';')
    public void D3() throws LexicalException, IOException, SyntaticException, SemanticalException {
        CasaToken("char");
        RegistroLexico id = reg;

        CasaToken("ID");
        s1(id);
        s7(id);
        if(reg.token.nome.equals("[")){
            CasaToken("[");
            CasaToken("const");
            CasaToken("]");

        }else if(reg.token.nome.equals("=")) {
            CasaToken("=");
            E();
        }
        if(reg.token.nome.equals(",")){
            H1();
        }
        CasaToken(";");
    }

    // D4 -> 'boolean' (id [ '[' E ']' ] [H2] ';' | id = E [H2] ';')
    public void D4() throws LexicalException, IOException, SyntaticException, SemanticalException {
        CasaToken("boolean");
        RegistroLexico id = reg;

        CasaToken("ID");
        s1(id);
        s8(id);
        if(reg.token.nome.equals("[")){
            CasaToken("[");
            CasaToken("const");
            CasaToken("]");

        }else if(reg.token.nome.equals("=")) {
            CasaToken("=");
            E();
        }
        if(reg.token.nome.equals(",")){
            H2();
        }
        CasaToken(";");
    }

    // D5 -> 'final' (id [H3] ';' | id = E [H3] ';')
    public void D5() throws LexicalException, IOException, SyntaticException, SemanticalException {
        CasaToken("final");

        RegistroLexico id = reg;

        CasaToken("ID");
        s2(id);
        CasaToken("=");
        E();
        if(reg.token.nome.equals(",")){
            H3();
        }
        CasaToken(";");
    }

    // I -> true | false
    public void I() throws LexicalException, IOException, SyntaticException, SemanticalException {
        if(reg.token.nome.equals("true")){
            CasaToken("true");
        }
        else if(reg.token.nome.equals("false")){
            CasaToken(("false"));
        }
    }


    // H -> {',' id [ '[' const | id ']' ] | [ = E]}+
    public void H(String tipo) throws LexicalException, IOException, SyntaticException, SemanticalException{
        do {
            CasaToken(",");
            RegistroLexico id = reg;

            CasaToken("ID");
            s1(id);
            s10(id, tipo);

            if (reg.token.nome.equals("[")) {
                CasaToken("[");
                CasaToken("const");

                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");

                E();
            }
        } while (reg.token.nome.equals(","));
    }

    // H1 -> {',' id ([ '[' const | id ']' ] | [ = const])}+
    public void H1() throws LexicalException, IOException, SyntaticException, SemanticalException {
        do {
            CasaToken(",");
            RegistroLexico id = reg;

            CasaToken("ID");
            s1(id);
            s7(id);
            if (reg.token.nome.equals("[")) {
                CasaToken("[");
                CasaToken("const");

                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");

                CasaToken("CONST");
            }
        } while (reg.token.nome.equals(","));
    }

    // H2 -> {',' id ([ '[' const | id ']' ] [ = I])}+
    public void H2() throws LexicalException, IOException, SyntaticException, SemanticalException {
        do {
            CasaToken(",");
            RegistroLexico id = reg;

            CasaToken("ID");
            s1(id);
            s8(id);
            if (reg.token.nome.equals("[")) {
                CasaToken("[");

                CasaToken("const");

                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");

                I();
            }
        } while (reg.token.nome.equals(","));
    }

    // H3 -> {',' id [ = E5]}+
    public void H3() throws LexicalException, IOException, SyntaticException, SemanticalException {
        do {
            CasaToken(",");
            RegistroLexico id = reg;

            CasaToken("ID");
            s2(id);
            CasaToken("=");
            E5();
        } while (reg.token.nome.equals(","));
    }

    // C -> C1 | C2 ';'
    public void C() throws LexicalException, IOException, SyntaticException, SemanticalException {
        if(reg.token.nome.equals("for") || reg.token.nome.equals("if")){
            C1();
        }
        else if(reg.token.nome.equals("readln") || reg.token.nome.equals("write") || reg.token.nome.equals("writeln")){
            C2();
            CasaToken(";");
        }
        else{
            C3();
            CasaToken(";");
        }
    }

    // C -> C1 | C2 ';'
    public void C0() throws LexicalException, IOException, SyntaticException, SemanticalException {
        if(reg.token.nome.equals("for") || reg.token.nome.equals("if")){
            C1();
        }
        else if(reg.token.nome.equals("readln") || reg.token.nome.equals("write") || reg.token.nome.equals("writeln")){
            C2();
        }
        else if(reg.token.nome.equals("ID")){
            C3();
        }
    }

    //C1 -> for '(' C ';' E ';' C ')' A | if '(' E ')' B
    public void C1() throws LexicalException, IOException, SyntaticException, SemanticalException {
        if(reg.token.nome.equals("for")){
            CasaToken("for");
            CasaToken("(");
            C0();
            CasaToken(";");
            E();
            CasaToken(";");
            C0();
            CasaToken(")");
            A();
        }else{
            CasaToken("if");
            CasaToken("(");
            E();
            CasaToken(")");
            A();

            if(reg.token.nome.equals("else")){
                CasaToken("else");
                A();
            }
        }

    }

    // C2 -> readln '(' [-]id ')' | write '(' E [G] ')' | writeln '(' E [G] ')'
    public void C2() throws LexicalException, IOException, SyntaticException, SemanticalException {
        if(reg.token.nome.equals("readln")){
            CasaToken("readln");
            CasaToken("(");

            if(reg.token.nome.equals("-")){
                CasaToken("-");
            }

            RegistroLexico id = reg;
            CasaToken("ID");
            s3(id);
            s4(id);
            CasaToken(")");
        }
        else if(reg.token.nome.equals("write")){
            CasaToken("write");
            CasaToken("(");

            E();
            if(reg.token.nome.equals(",")){
                G();
            }

            CasaToken(")");
        }
        else if(reg.token.nome.equals("writeln")){
            CasaToken("writeln");
            CasaToken("(");

            E();
            if(reg.token.nome.equals(",")){
                G();
            }

            CasaToken(")");
        }
    }

    // C3 -> id[ '[' E ']' ] = E
    public void C3() throws LexicalException, IOException, SyntaticException, SemanticalException {
        RegistroLexico id = reg;
        CasaToken("ID");
        s3(id);
        s4(id);

        if (reg.token.nome.equals("[")) {
            CasaToken("[");
            E();
            CasaToken("]");
        }

        CasaToken("=");

        E();
    }

    //A -> C | begin C* end
    public void A() throws LexicalException, IOException, SyntaticException, SemanticalException {
        if(reg.token.nome.equals("begin")){
            CasaToken("begin");
            while(!reg.token.nome.equals("end")){
                C();
            }
            CasaToken("end");
        }else{
            C();
        }

    }

    // G -> {',' E}+
    public void G() throws LexicalException, IOException, SyntaticException, SemanticalException {
        do{
            CasaToken(",");

            E();
        } while (reg.token.nome.equals(","));
    }

    // Regras semânticas

    public void s1(RegistroLexico id) throws SemanticalException {
        if(id.lexema.classe != null){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.IDENTIFICADOR_JA_DECLARADO , id.lexema.nome);
        }
        else{
            id.lexema.classe = "var";
        }
    }

    public void s2(RegistroLexico id) throws SemanticalException {
        if(id.lexema.classe != null){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.IDENTIFICADOR_JA_DECLARADO , id.lexema.nome);
        }
        else id.lexema.classe = "const";
    }

    public void s3(RegistroLexico id) throws SemanticalException {
        if(id.lexema.classe == null){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.IDENTIFICADOR_NAO_DECLARADO , id.lexema.nome);
        }
    }

    public void s4(RegistroLexico id) throws SemanticalException {
        if(!id.lexema.classe.equals("var")){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.CLASSE_IDENTIFICADOR_INCOMPATIVEL, id.lexema.nome);
        }
    }

    public void s5(RegistroLexico id){
        id.lexema.tipo = "integer";
    }

    public void s6(RegistroLexico id) {
        id.lexema.tipo = "real";
    }

    public void s7(RegistroLexico id) {
        id.lexema.tipo = "char";
    }

    public void s8(RegistroLexico id) {
        id.lexema.tipo = "boolean";
    }

    public void s10(RegistroLexico id, String tipo) {
        id.lexema.tipo = tipo;
    }

    public void s11(RegistroLexico id, String expressao) throws SemanticalException {
        if(!id.lexema.tipo.equals(expressao)){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, id.lexema.nome);
        }
    }

    public void s12(RegistroLexico id, String expressao) throws SemanticalException {
        if(!(expressao.equals("integer") || expressao.equals("real"))){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, id.lexema.nome);
        }
    }

    public void s13(AtributoHerdado atributoPai, RegistroLexico _reg) {
        atributoPai.tipo = _reg.tipo;
        atributoPai.nome = reg.lexema.nome;
    }

    public void s14(AtributoHerdado atributoPai, RegistroLexico _reg) throws SemanticalException {
        atributoPai.tipo = _reg.tipo;
        atributoPai.nome = reg.lexema.nome;
    }

    public void s15(AtributoHerdado E5, RegistroLexico id) throws SemanticalException {
        E5.tipo = id.tipo;
        E5.nome = id.lexema.nome;
    }
    public void s16(AtributoHerdado E5, AtributoHerdado E) throws SemanticalException {
        E5.tipo = E.tipo;
        E5.nome = E.nome;
    }
    public void s17(AtributoHerdado E4) throws SemanticalException {
        if(!(E4.tipo.equals("real") || E4.tipo.equals("inteiro"))){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E4.nome);
        }
    }
    public void s18(AtributoHerdado E4, AtributoHerdado E5) throws SemanticalException {
        E4.tipo = E5.tipo;
        E4.nome = E5.nome;
    }

    public void s19(AtributoHerdado E4, AtributoHerdado E5) throws SemanticalException {
        E4.tipo = "integer";
//        float valor =  Float.parseFloat(E5.valor);
//        int vFinal = (int)valor;
//        E4.valor = Integer.toString(vFinal);
    }

    public void s20(AtributoHerdado E4, AtributoHerdado E5) throws SemanticalException {
        E4.tipo = "real";
//        float vFinal =  Float.parseFloat(E5.valor);
//        E4.valor = Float.toString(vFinal);
    }

    public void s21(AtributoHerdado E3, AtributoHerdado E4) throws SemanticalException {
        E3.tipo = E4.tipo;
        E3.nome = E4.nome;
    }

    public void s22(AtributoHerdado E4) throws SemanticalException {
        if(!E4.tipo.equals("boolean")){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E4.nome );
        }
    }

    public void s23(AtributoHerdado E1, AtributoHerdado E2) throws SemanticalException {
//        if(E4.valor.equals("true")){
//            E4.valor="false";
//        }else{
//            E4.valor="true";
//        }

        E1.tipo = E2.tipo;
        E1.nome = E2.nome;
    }

    public void s24(AtributoHerdado E2, AtributoHerdado E3) throws SemanticalException {
        E2.tipo = E3.tipo;
        E2.nome = E3.nome;
    }

    public void s25(AtributoOperacao E2, String op) throws SemanticalException {
        E2.op = op;
    }

    public void s26(AtributoOperacao E3_op, AtributoHerdado E2, AtributoHerdado E3_2) throws SemanticalException {
        switch (E3_op.op){
            case "and" -> {
                if(!E2.tipo.equals("boolean") ){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E2.nome);
                } else if(!E3_2.tipo.equals("boolean")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E3_2.nome);
                }
            }
            case "*" -> {
                if(!E2.tipo.equals("real") && !E2.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E2.nome);
                }
                else if(!E3_2.tipo.equals("real") && !E3_2.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E3_2.nome);
                }
            }
            case "/" -> {
                System.out.println("POR COISA DE VALOR AQUI DPS");

                if(!E2.tipo.equals("real") && !E2.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E2.nome);
                }
                else if(!E3_2.tipo.equals("real") && !E3_2.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E3_2.nome);
                }
            }
            case "div" -> {
                if(!E2.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E2.nome);
                }
                if(!E3_2.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E3_2.nome);
                }
            }
            case "mod" -> {
                System.out.println("POR COISA DE VALOR AQUI DPS");

                if(!E2.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E2.nome);
                }
                if(!E3_2.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E3_2.nome);
                }
            }
        }
    }

    public void s27(AtributoFlag flag){
        flag.flag = true;
    }

    public void s28(AtributoOperacao E1, String op){
        E1.op = op;
    }
    public void s29(AtributoOperacao op, AtributoHerdado E1 ,AtributoHerdado E2_2) throws SemanticalException {
        switch (op.op) {
            case "-" ->{
                if(!E1.tipo.equals("real") && !E1.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1.nome);
                }
                else if(!E2_2.tipo.equals("real") && !E2_2.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E2_2.nome);
                }
            }
            case "+" ->{
                System.out.println("POR COISA DE VALOR AQUI DPS");

                if(!E1.tipo.equals("real") && !E1.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1.nome);
                }
                else if(!E2_2.tipo.equals("real") && !E2_2.tipo.equals("integer")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E2_2.nome);
                }
            }
            case "or" ->{
                if(!E1.tipo.equals("boolean") ){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1.nome);
                } else if(!E2_2.tipo.equals("boolean")){
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E2_2.nome);
                }
            }

        }
    }

    public void s30(AtributoHerdado E2, AtributoFlag flag) throws SemanticalException {
        if(flag.flag && !E2.tipo.equals("real") && !E2.tipo.equals("integer")){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E2.nome);
        }
    }

    public void s31(AtributoHerdado E2, AtributoHerdado E3_1, AtributoOperacao op){
        switch (op.op) {
            case "*" -> {
                if (E2.tipo.equals("real") || E3_1.tipo.equals("real")) {
                    E2.tipo = "real";
                }
            }
            case "/" -> {
                if (E2.tipo.equals("real") || E3_1.tipo.equals("real")) {
                    E2.tipo = "real";
                }
            }
            case "and" -> E2.tipo = "boolean";
            case "mod" -> E2.tipo = "integer";
            case "div" -> E2.tipo = "integer";
        }
    }

    public void s32(AtributoHerdado E1, AtributoHerdado E2_1, AtributoOperacao op){
        switch (op.op) {
            case "+" -> {
                if (E1.tipo.equals("real") || E2_1.tipo.equals("real")) {
                    E1.tipo = "real";
                }
            }
            case "-" -> {
                if (E1.tipo.equals("real") || E2_1.tipo.equals("real")) {
                    E1.tipo = "real";
                }
            }
            case "or" -> E1.tipo = "boolean";
        }
    }

    public void s33(AtributoHerdado E, AtributoHerdado E1) {
        E.tipo = E1.tipo;
    }

    public void s34(AtributoOperacao E, String op){
        E.op = op;
    }
    public void s35(AtributoOperacao op,AtributoHerdado E_1, AtributoHerdado E1_2 ) throws SemanticalException {
        switch (op.op){
            case "==" ->{
                if(E_1.tipo.equals("real") || E_1.tipo.equals("integer")){
                    if(!(E1_2.tipo.equals("real") || E1_2.tipo.equals("integer"))){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else if(E_1.tipo.equals("char")){
                    if(!E1_2.tipo.equals("char")){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else if(E_1.tipo.equals("string")){
                    if(!E1_2.tipo.equals("string")){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else{
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E_1.nome);
                }
            }
            case "<>" ->{
                System.out.println(1);
                if(E_1.tipo.equals("real") || E_1.tipo.equals("integer")){
                    if(!(E1_2.tipo.equals("real") || E1_2.tipo.equals("integer"))){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else if(E_1.tipo.equals("char")){
                    if(!E1_2.tipo.equals("char")){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else{
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E_1.nome);
                }
            }
            case "<" ->{
                System.out.println(2);
                if(E_1.tipo.equals("real") || E_1.tipo.equals("integer")){
                    if(!(E1_2.tipo.equals("real") || E1_2.tipo.equals("integer"))){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else if(E_1.tipo.equals("char")){
                    if(!E1_2.tipo.equals("char")){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else{
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E_1.nome);
                }
            }
            case ">" ->{
                System.out.println(3);
                if(E_1.tipo.equals("real") || E_1.tipo.equals("integer")){
                    if(!(E1_2.tipo.equals("real") || E1_2.tipo.equals("integer"))){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else if(E_1.tipo.equals("char")){
                    if(!E1_2.tipo.equals("char")){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else{
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E_1.nome);
                }
            }
            case "<=" ->{
                System.out.println(4);
                if(E_1.tipo.equals("real") || E_1.tipo.equals("integer")){
                    if(!(E1_2.tipo.equals("real") || E1_2.tipo.equals("integer"))){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else if(E_1.tipo.equals("char")){
                    if(!E1_2.tipo.equals("char")){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else{
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E_1.nome);
                }
            }
            case ">=" ->{
                System.out.println(5);
                if(E_1.tipo.equals("real") || E_1.tipo.equals("integer")){
                    if(!(E1_2.tipo.equals("real") || E1_2.tipo.equals("integer"))){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else if(E_1.tipo.equals("char")){
                    if(!E1_2.tipo.equals("char")){
                        throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                    }
                }else{
                    throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E_1.nome);
                }
            }

        }

    }

    public void s36(AtributoHerdado E){
        E.tipo = "boolean";
    }

}
