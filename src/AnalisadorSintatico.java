import java.io.IOException;

// TODO AVALIAR SE TEM QUE TIRAR VALOR

public class AnalisadorSintatico {
    AnalisadorLexico analisadorLexico;
    RegistroLexico reg;

    AnalisadorSintatico(String caminhoArquivo){
        analisadorLexico = new AnalisadorLexico(caminhoArquivo);
    }

    public String analisar() throws LexicalException, IOException, SyntaticException, SemanticalException {
        GeradorCodigo.initialize();

        reg = analisadorLexico.analisar();
        S();

        System.out.println(analisadorLexico.linhaAtual + " linhas compiladas");

        return GeradorCodigo.finalizeCode();
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
                break;
            }
        }
        while(!reg.token.nome.equals("FIM_DE_ARQUIVO")){
            C();
        }

        CasaToken("FIM_DE_ARQUIVO");
    }

    // E -> E1 [( = | <> | < | <= | >= | > ) E1]
    public void E(AtributoHerdado atributoPai) throws LexicalException, IOException, SyntaticException, SemanticalException {
        GeradorCodigo.resetTC();
        
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
            s35(atributoOperacao, atributoPai, atributoE1_2);
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
            GeradorCodigo.operacoesSimples(atributoPai, atributoE2_2, atributoOperacao);
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
            GeradorCodigo.operacoesComplexas(atributoPai, atributoE3_2, op);
            s31(atributoPai, atributoE3_1, op);

        }
    }

    // E3 -> not E4 | E4
    public void E3  (AtributoHerdado atributoPai) throws LexicalException, IOException, SyntaticException, SemanticalException {
        AtributoHerdado atributoE4 = new AtributoHerdado();

        if(reg.token.nome.equals("not")  ) {
            CasaToken("not");
            E4(atributoE4);
            s22(atributoE4);
            GeradorCodigo.not(atributoE4);
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
            GeradorCodigo.real2integer(atributoE5);
            CasaToken(")");
            s19(atributoPai,atributoE5);
        }else if(reg.token.nome.equals("real")){
            CasaToken("real");
            CasaToken("(");
            E5(atributoE5);
            s17(atributoE5);
            GeradorCodigo.integer2real(atributoE5);
            CasaToken(")");
            s20(atributoPai,atributoE5);
        }
        else {
            E5(atributoE5);
            s18(atributoPai,atributoE5);
        }
    }

    // E5 -> CONST | true | false | id [ '[' E ']' ] | '(' E ')'
    public void E5(AtributoHerdado atributoPai) throws LexicalException, IOException, SyntaticException, SemanticalException {
        RegistroLexico _reg = reg;

        AtributoHerdado atributoE = new AtributoHerdado();
        switch (_reg.token.nome) {
            case "CONST" -> {
                CasaToken("CONST");
                GeradorCodigo.lerConst(_reg);
                s13(atributoPai, _reg);
            }
            case "true" -> {
                CasaToken("true");
                GeradorCodigo.lerConst(_reg);
                s14(atributoPai, _reg);
            }
            case "false" -> {
                CasaToken("false");
                GeradorCodigo.lerConst(_reg);
                s14(atributoPai, _reg);
            }
            case "ID" -> {
                CasaToken("ID");
                s3(_reg);
                s15(atributoPai, _reg);
                if (reg.token.nome.equals("[")) {
                    CasaToken("[");
                    E(atributoE);
                    CasaToken("]");
                }else{
                    GeradorCodigo.lerId(_reg);
                }
            }
            case "(" -> {
                CasaToken("(");
                E(atributoE);
                CasaToken(")");
                s16(atributoPai, atributoE);
            }
            default -> {
                throw new SyntaticException(analisadorLexico.linhaAtual, SyntaticErrorEnum.TOKEN_NAO_ESPERADO, reg.lexema.nome);
            }
        }
    }


    public void D() throws LexicalException, IOException, SyntaticException, SemanticalException {
        switch (reg.token.nome) {
            case "integer" -> D1();
            case "real" -> D2();
            case "char" -> D3();
            case "boolean" -> D4();
            case "final" -> D5();
            case "string" -> D6();
        }
    }

    //D1 -> 'integer' (id [ '[' E ']' ] [H] ';' | id = E [H] ';')
    public void D1() throws LexicalException, IOException, SyntaticException, SemanticalException {
        CasaToken("integer");
        
        RegistroLexico id = reg;
        
        CasaToken("ID");
        s1(id);
        s5(id);

        id.lexema.endereco = GeradorCodigo.declareInteger();
        
        if(reg.token.nome.equals("[")){
            CasaToken("[");
            s42(id);

            RegistroLexico constReg = reg;
            CasaToken("CONST");
            s38(constReg);
            s43(id,constReg);

            CasaToken("]");

        }else if(reg.token.nome.equals("=")) {
            CasaToken("=");

            RegistroLexico constReg = reg;
            CasaToken("CONST");

            s10(id, constReg);
            GeradorCodigo.writeReal(constReg.valorConst, id.lexema.endereco);
        }

        if(reg.token.nome.equals(",")){
            H();
        }
        CasaToken(";");
    }

    //D2 -> 'real' (id [ '[' E ']' ] [H] ';' | id = E [H] ';')
    public void D2() throws LexicalException, IOException, SyntaticException, SemanticalException {
        CasaToken("real");

        RegistroLexico id = reg;
        AtributoHerdado E = new AtributoHerdado();

        CasaToken("ID");
        s1(id);
        s6(id);

        id.lexema.endereco = GeradorCodigo.declareReal();

        if(reg.token.nome.equals("[")){
            CasaToken("[");
            s42(id);

            RegistroLexico constReg = reg;
            CasaToken("CONST");

            s38(constReg);
            s43(id,constReg);
            CasaToken("]");

        }
        else if(reg.token.nome.equals("=")) {
            CasaToken("=");
            RegistroLexico constReg = reg;
            CasaToken("CONST");

            s12(id,constReg);
            GeradorCodigo.writeReal(constReg.valorConst, id.lexema.endereco);
        }
        if(reg.token.nome.equals(",")){
            H4();
        }

        CasaToken(";");
    }

    // D3 -> 'char' (id [ '[' E ']' ] [H1] ';' | id = E [H1] ';')
    public void D3() throws LexicalException, IOException, SyntaticException, SemanticalException {
        CasaToken("char");
        RegistroLexico id = reg;
        AtributoHerdado E = new AtributoHerdado();

        CasaToken("ID");
        s1(id);
        s7(id);

        id.lexema.endereco = GeradorCodigo.declareChar();

        if(reg.token.nome.equals("[")){
            CasaToken("[");
            s42(id);

            RegistroLexico constReg = reg;
            CasaToken("CONST");

            s38(constReg);
            s43(id,constReg);
            CasaToken("]");

        }else if(reg.token.nome.equals("=")) {
            CasaToken("=");

            RegistroLexico constReg = reg;
            CasaToken("CONST");

            s10(id, constReg);
            GeradorCodigo.writeChar(constReg.valorConst, id.lexema.endereco);
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

        id.lexema.endereco = GeradorCodigo.declareBoolean();

        if(reg.token.nome.equals("[")){
            CasaToken("[");
            s42(id);

            RegistroLexico constReg = reg;
            CasaToken("CONST");

            s38(constReg);
            s43(id,constReg);
            CasaToken("]");

        }else if(reg.token.nome.equals("=")) {
            CasaToken("=");

            AtributoHerdado I = new AtributoHerdado();
            I(I);

            s37(id, I);
            GeradorCodigo.writeBoolean(I.valor, id.lexema.endereco);
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

        RegistroLexico constReg = reg;
        CasaToken("CONST");
        s41(id, constReg);

        id.lexema.endereco = GeradorCodigo.declareFinal(id);
        GeradorCodigo.writeFinal(constReg.valorConst, id.lexema.endereco, id.lexema.tipo);

        if(reg.token.nome.equals(",")){
            H3();
        }
        CasaToken(";");
    }

    public void D6() throws LexicalException, IOException, SyntaticException, SemanticalException {
        CasaToken("string");
        RegistroLexico id = reg;

        CasaToken("ID");
        s1(id);
        s9(id);
        if(reg.token.nome.equals("[")){
            CasaToken("[");
            s42(id);

            RegistroLexico constReg = reg;
            CasaToken("CONST");

            s38(constReg);
            s43(id,constReg);
            CasaToken("]");

        }else if(reg.token.nome.equals("=")) {
            CasaToken("=");

            RegistroLexico constReg = reg;
            CasaToken("CONST");

            s10(id, constReg);
        }
        if(reg.token.nome.equals(",")){
            H5();
        }
        CasaToken(";");
    }

    // I -> true | false
    public void I(AtributoHerdado I) throws LexicalException, IOException, SyntaticException, SemanticalException {

        if(reg.token.nome.equals("true")){
            RegistroLexico constReg = reg;

            CasaToken("true");
            s11(I, constReg);

        }
        else if(reg.token.nome.equals("false")){
            RegistroLexico constReg = reg;

            CasaToken(("false"));
            s11(I, constReg);
        }

    }


    // H -> {',' id [ '[' CONST | id ']' ] | [ = E]}+
    public void H() throws LexicalException, IOException, SyntaticException, SemanticalException{
        do {
            CasaToken(",");
            RegistroLexico id = reg;

            CasaToken("ID");
            s1(id);
            s5(id);
            id.lexema.endereco = GeradorCodigo.declareInteger();

            if (reg.token.nome.equals("[")) {
                CasaToken("[");
                s42(id);

                RegistroLexico constReg = reg;
                CasaToken("CONST");

                s38(constReg);
                s43(id,constReg);
                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");

                RegistroLexico constReg = reg;
                CasaToken("CONST");

                s10(id, constReg);
                GeradorCodigo.writeInteger(constReg.valorConst, id.lexema.endereco);
            }
        } while (reg.token.nome.equals(","));
    }

    // H1 -> {',' id ([ '[' CONST | id ']' ] | [ = CONST])}+
    public void H1() throws LexicalException, IOException, SyntaticException, SemanticalException {
        do {
            CasaToken(",");
            RegistroLexico id = reg;

            CasaToken("ID");
            s1(id);
            s7(id);
            id.lexema.endereco = GeradorCodigo.declareChar();

            if (reg.token.nome.equals("[")) {
                CasaToken("[");
                s42(id);

                RegistroLexico constReg = reg;
                CasaToken("CONST");

                s38(constReg);
                s43(id,constReg);
                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");
                RegistroLexico constReg = reg;
                CasaToken("CONST");
                s10(id,constReg);
                GeradorCodigo.writeChar(constReg.valorConst, id.lexema.endereco);
            }
        } while (reg.token.nome.equals(","));
    }

    // H2 -> {',' id ([ '[' CONST | id ']' ] [ = I])}+
    public void H2() throws LexicalException, IOException, SyntaticException, SemanticalException {
        do {
            CasaToken(",");
            RegistroLexico id = reg;

            CasaToken("ID");
            s1(id);
            s8(id);
            id.lexema.endereco = GeradorCodigo.declareBoolean();
            if (reg.token.nome.equals("[")) {
                CasaToken("[");
                s42(id);

                RegistroLexico constReg = reg;
                CasaToken("CONST");

                s38(constReg);
                s43(id,constReg);
                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");

                AtributoHerdado I = new AtributoHerdado();
                I(I);

                s37(id,I);
                GeradorCodigo.writeBoolean(I.valor, id.lexema.endereco);
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

            RegistroLexico constReg = reg;
            CasaToken("CONST");
            s41(id, constReg);

            id.lexema.endereco = GeradorCodigo.declareFinal(id);
            GeradorCodigo.writeFinal(constReg.valorConst, id.lexema.endereco, id.lexema.tipo);
        } while (reg.token.nome.equals(","));
    }

    public void H4() throws LexicalException, IOException, SyntaticException, SemanticalException {
        do {
            CasaToken(",");
            RegistroLexico id = reg;

            CasaToken("ID");
            s1(id);
            s6(id);
            id.lexema.endereco = GeradorCodigo.declareReal();

            if (reg.token.nome.equals("[")) {
                CasaToken("[");
                s42(id);

                RegistroLexico constReg = reg;
                CasaToken("CONST");

                s38(constReg);
                s43(id,constReg);
                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");

                RegistroLexico constReg = reg;
                CasaToken("CONST");

                s12(id,constReg);
                GeradorCodigo.writeReal(constReg.valorConst, id.lexema.endereco);
            }
        } while (reg.token.nome.equals(","));
    }
    public void H5() throws LexicalException, IOException, SyntaticException, SemanticalException {
        do {
            CasaToken(",");
            RegistroLexico id = reg;

            CasaToken("ID");
            s1(id);
            s9(id);
            if (reg.token.nome.equals("[")) {
                CasaToken("[");
                s42(id);

                RegistroLexico constReg = reg;
                CasaToken("CONST");

                s38(constReg);
                s43(id,constReg);
                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");
                RegistroLexico constReg = reg;
                CasaToken("CONST");
                s10(id,constReg);
                GeradorCodigo.writeBoolean(constReg.valorConst, id.lexema.endereco);
            }
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
        switch (reg.token.nome) {
            case "for", "if" -> C1();
            case "readln", "write", "writeln" -> C2();
            case "ID" -> C3();
        }
    }

    //C1 -> for '(' C ';' E ';' C ')' A | if '(' E ')' B
    public void C1() throws LexicalException, IOException, SyntaticException, SemanticalException {
        AtributoHerdado E = new AtributoHerdado();

        if(reg.token.nome.equals("for")){
            String ROT1 =   GeradorCodigo.getNextRot(),
                    ROT2 =   GeradorCodigo.getNextRot(),
                    ROT3 =   GeradorCodigo.getNextRot(),
                    ROT4 =   GeradorCodigo.getNextRot();

            CasaToken("for");
            CasaToken("(");
            C0();
            CasaToken(";");

            GeradorCodigo.inicioTesteFor(ROT1);

            E(E);
            s39(E);

            GeradorCodigo.fimTesteFor(ROT2,ROT3,ROT4);

            CasaToken(";");

            GeradorCodigo.inicioIncrementFor(ROT3);

            C0();

            GeradorCodigo.fimIncrementFor(ROT1);

            CasaToken(")");

            GeradorCodigo.inicioBlocoFor(ROT2);
            A();

            GeradorCodigo.fimBlocoFor(ROT3,ROT4);
        }else{
            CasaToken("if");
            CasaToken("(");
            E(E);
            s39(E);
            CasaToken(")");
            A();

            if(reg.token.nome.equals("else")){
                CasaToken("else");
                A();
            }
        }

    }

    int end = 0x10000;

    // C2 -> readln '(' [-]id ')' | write '(' E [G] ')' | writeln '(' E [G] ')'
    public void C2() throws LexicalException, IOException, SyntaticException, SemanticalException {
        switch (reg.token.nome) {
            case "readln" -> {
                CasaToken("readln");
                CasaToken("(");
                RegistroLexico id = reg;
                CasaToken("ID");
                s3(id);
                s4(id);

                GeradorCodigo.read(end, id.lexema.tipo);

                CasaToken(")");
            }
            case "write" -> {
                CasaToken("write");
                CasaToken("(");
                AtributoHerdado E = new AtributoHerdado();

                E(E);
                s45(E);
                if (reg.token.nome.equals(",")) {
                    G();
                }

                GeradorCodigo.print(end, E.tipo, false);
                end += 8;

                CasaToken(")");
            }
            case "writeln" -> {
                CasaToken("writeln");
                CasaToken("(");
                AtributoHerdado E = new AtributoHerdado();

                E(E);
                s45(E);
                if (reg.token.nome.equals(",")) {
                    G();
                }

                GeradorCodigo.print(end, E.tipo, true);
                end += 8;

                CasaToken(")");
            }
        }
    }

    // C3 -> id[ '[' E ']' ] = E
    public void C3() throws LexicalException, IOException, SyntaticException, SemanticalException {
        RegistroLexico id = reg;
        CasaToken("ID");
        s3(id);
        s4(id);
        AtributoHerdado E = new AtributoHerdado();

        RegistroLexico constReg = new RegistroLexico();

        if (reg.token.nome.equals("[")) {
            CasaToken("[");
            constReg = reg;
            CasaToken("CONST");

            s38(constReg);
            s44(id,constReg);
            CasaToken("]");
        }

        CasaToken("=");

        E(E);
        GeradorCodigo.atribuicaoEx(id,E);
        s40(id, E, constReg);
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
            AtributoHerdado E = new AtributoHerdado();

            E(E);
            s45(E);
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
        else id.lexema.classe = "CONST";
    }

    public void s3(RegistroLexico id) throws SemanticalException {
        if(id.lexema.classe == null){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.IDENTIFICADOR_NAO_DECLARADO , id.lexema.nome);
        }
    }

    public void s4(RegistroLexico id) throws SemanticalException {
        if(id.lexema.classe.equals("const")){
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

    public void s9(RegistroLexico id) {
        id.lexema.tipo = "string";
    }

    public void s10(RegistroLexico id, RegistroLexico con) throws SemanticalException {
        if(!id.lexema.tipo.equals(con.tipoConst)){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, id.lexema.nome);
        }
    }

    public void s11(AtributoHerdado I, RegistroLexico con) {
        I.tipo = con.tipoConst;
        I.nome = con.valorConst;
        I.valor = con.valorConst;
    }

    public void s12(RegistroLexico id, RegistroLexico con) throws SemanticalException {
        if(!(con.tipoConst.equals("integer") || con.tipoConst.equals("real"))){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, id.lexema.nome);
        }
    }

    public void s13(AtributoHerdado atributoPai, RegistroLexico _reg) {
        atributoPai.tipo = _reg.tipoConst;
        atributoPai.nome = _reg.token.nome.equals("CONST")? _reg.valorConst : _reg.lexema.nome;
        atributoPai.endereco = _reg.endConst;
    }

    public void s14(AtributoHerdado atributoPai, RegistroLexico _reg) {
        atributoPai.tipo = _reg.tipoConst;
        atributoPai.nome = _reg.token.nome.equals("CONST")? _reg.valorConst : _reg.lexema.nome;
    }

    public void s15(AtributoHerdado E5, RegistroLexico id) throws SemanticalException {
        E5.tipo = id.lexema.tipo;
        E5.nome = id.lexema.nome;
        E5.endereco = id.lexema.endereco;
    }

    public void s16(AtributoHerdado E5, AtributoHerdado E) throws SemanticalException {
        E5.tipo = E.tipo;
        E5.nome = E.nome;
        E5.endereco = E.endereco;
    }

    public void s17(AtributoHerdado E4) throws SemanticalException {
        if(!(E4.tipo.equals("real")) && !(E4.tipo.equals("integer"))){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E4.nome);
        }
    }

    public void s18(AtributoHerdado E4, AtributoHerdado E5) throws SemanticalException {
        E4.tipo = E5.tipo;
        E4.nome = E5.nome;
        E4.endereco = E5.endereco;

    }

    public void s19(AtributoHerdado E4, AtributoHerdado E5) throws SemanticalException {
        E4.tipo = "integer";
        E4.nome = E5.nome;
//        float valor =  Float.parseFloat(E5.valor);
//        int vFinal = (int)valor;
//        E4.valor = Integer.toString(vFinal);
    }

    public void s20(AtributoHerdado E4, AtributoHerdado E5) throws SemanticalException {
        E4.tipo = "real";
        E4.nome = E5.nome;
//        float vFinal =  Float.parseFloat(E5.valor);
//        E4.valor = Float.toString(vFinal);
    }

    public void s21(AtributoHerdado E3, AtributoHerdado E4) throws SemanticalException {
        E3.tipo = E4.tipo;
        E3.nome = E4.nome;
        E3.endereco = E4.endereco;
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
        E1.endereco = E2.endereco;
    }

    public void s24(AtributoHerdado E2, AtributoHerdado E3) throws SemanticalException {
        E2.tipo = E3.tipo;
        E2.nome = E3.nome;
        E2.endereco = E3.endereco;
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
            case "/" -> E2.tipo = "real";
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
        E.nome = E1.nome;
        E.endereco = E1.endereco;
    }

    public void s34(AtributoOperacao E, String op){
        E.op = op;
    }
    public void s35(AtributoOperacao op, AtributoHerdado E_1, AtributoHerdado E1_2 ) throws SemanticalException {
        switch (op.op){
            case "==" ->{
                switch (E_1.tipo) {
                    case "real", "integer" -> {
                        if (!(E1_2.tipo.equals("real") || E1_2.tipo.equals("integer"))) {
                            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                        }
                    }
                    case "char" -> {
                        if (!E1_2.tipo.equals("char")) {
                            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                        }
                    }
                    case "string" -> {
                        if (!E1_2.tipo.equals("string")) {
                            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E1_2.nome);
                        }
                    }
                    default ->
                            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E_1.nome);
                }
            }
            case "<>" ->{
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

    public void s37(RegistroLexico id, AtributoHerdado I) throws SemanticalException{
        if(!id.lexema.tipo.equals(I.tipo)){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, I.nome);
        }
    }

    public void s38(RegistroLexico con) throws SemanticalException {
        if(!con.tipoConst.equals("integer")){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, con.valorConst);
        }
    }

    public void s39(AtributoHerdado E) throws SemanticalException {
        if(!E.tipo.equals("boolean")){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E.nome);
        }
    }
    
    public void s40(RegistroLexico id,AtributoHerdado E, RegistroLexico con)throws SemanticalException {
        if(id.lexema.classe.equals("vetor") ){
            if(con.valorConst == null && !E.tipo.equals("string")){
                throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E.nome);
            }
            else if(!id.lexema.tipo.equals("char") && !E.tipo.equals("string") && !id.lexema.tipo.equals(E.tipo) ){
                throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E.nome);
            }
        }
        else if(id.lexema.tipo.equals("real")){
            if (!(E.tipo.equals("real") || E.tipo.equals("integer"))){
                throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E.nome);
            }
        }else if(!id.lexema.tipo.equals(E.tipo)){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E.nome);
        }
    }

    public void s41(RegistroLexico id, RegistroLexico con){
        id.lexema.tipo = con.tipoConst;
    }

    public void s42(RegistroLexico id){
        id.lexema.classe = "vetor";
    }

    public void s43(RegistroLexico id  , RegistroLexico con){
        id.lexema.tamanho = Integer.parseInt(con.valorConst);
    }
    public void s44(RegistroLexico id, RegistroLexico con)  throws SemanticalException{
        if(id.lexema.tamanho  <= Integer.parseInt(con.valorConst) || !id.lexema.classe.equals("vetor")){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.IDENTIFICADOR_NAO_DECLARADO, id.lexema.nome);
        }

    }

    public void s45(AtributoHerdado E)  throws SemanticalException{
        if(E.tipo.equals("boolean")){
            throw new SemanticalException(analisadorLexico.linhaAtual, SemanticalErrorEnum.TIPOS_INCOMPATIVEIS, E.nome);
        }
    }
    
    
}
