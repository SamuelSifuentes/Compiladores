import com.sun.source.tree.CaseTree;

import java.io.IOException;

public class AnalisadorSintatico {
    AnalisadorLexico analisadorLexico;
    RegistroLexico reg;

    AnalisadorSintatico(String caminhoArquivo){
        analisadorLexico = new AnalisadorLexico(caminhoArquivo);
    }

    public void analisar() throws LexicalException, IOException, SyntaticException {
        reg = analisadorLexico.analisar();
        S();

        System.out.println(reg);
    }

    public void CasaToken(String tokenEsperado) throws IOException, LexicalException, SyntaticException {
        if(reg.token.nome.equals(tokenEsperado)){
            reg = analisadorLexico.analisar();
        }
        else{
            throw new SyntaticException(-1, SyntaticErrorEnum.TOKEN_NAO_ESPERADO);
        }
    }

    public void S() throws LexicalException, IOException, SyntaticException {
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
    public void E() throws LexicalException, IOException, SyntaticException {
        E1();
        String tokenAtual = reg.token.nome;
        if(
                tokenAtual.equals("==") || tokenAtual.equals("<>") ||
                tokenAtual.equals("<") || tokenAtual.equals("<=") ||
                tokenAtual.equals(">=") || tokenAtual.equals(">")) {
            switch (reg.token.nome){
                case "==":
                    CasaToken("==");
                    break;
                case "<>":
                    CasaToken("<>");
                    break;
                case "<":
                    CasaToken("<");
                    break;
                case "<=":
                    CasaToken("<=");
                    break;
                case ">=":
                    CasaToken(">=");
                    break;
                case ">":
                    CasaToken(">");
                    break;
            }
            E1();
        }

    }

    // E1 -> [-] E2 {(+ | - | or) E2}*
    public void E1() throws LexicalException, IOException, SyntaticException {

        if(reg.token.nome.equals("-")  ) {
            CasaToken("-");
        }
        E2();
        while (reg.token.nome.equals("+") || reg.token.nome.equals("-") || reg.token.nome.equals("or")){
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
            E2();
        }
    }

    // E2 -> E3 {(* | (div | /) | mod | and) E3}
    public void E2() throws LexicalException, IOException, SyntaticException {
        E3();

        while(reg.token.nome.equals("*") || (reg.token.nome.equals("div") || reg.token.nome.equals("/")) || reg.token.nome.equals("mod") || reg.token.nome.equals("and")){
            switch (reg.token.nome) {
                case "*" -> CasaToken("*");
                case "div", "/" -> {
                    if (reg.token.nome.equals("div")) {
                        CasaToken("div");
                    } else {
                        CasaToken("/");
                    }
                }
                case "mod" -> CasaToken("mod");
                default -> CasaToken("and");
            }

            E3();
        }
    }

    // E3 -> not E4 | E4
    public void E3() throws LexicalException, IOException, SyntaticException {
        if(reg.token.nome.equals("not")  ) {
            CasaToken("not");
            E4();
        }else{
            E4();
        }
    }

    // E4 -> integer '(' E5 ')' | real '(' E5 ')' | E5
    public void E4() throws LexicalException, IOException, SyntaticException {
        if(reg.token.nome.equals("integer")  ) {
            CasaToken("integer");
            CasaToken("(");
            E5();
            CasaToken(")");
        }else if(reg.token.nome.equals("real")){
            CasaToken("real");
            CasaToken("(");
            E5();
            CasaToken(")");
        }
        else {
            E5();
        }
    }

    // E5 -> const | true | false | id [ '[' E ']' ] | '(' E ')'
    public void E5() throws LexicalException, IOException, SyntaticException {
        String token = reg.token.nome;

        if(token.equals("CONST") || token.equals("true") || token.equals("false") || token.equals("ID") || token.equals("(")){
            if(token.equals("CONST")) {
                CasaToken("CONST");
            }
            else if(token.equals("true")) {
                CasaToken("true");
            }
            else if(token.equals("false")){
                CasaToken("false");
            }
            else if(token.equals("ID")) {
                CasaToken("ID");
                if(reg.token.nome.equals("[")){
                    CasaToken("[");
                    E();
                    CasaToken("]");
                }
            }
            else if(token.equals("(")) {
                CasaToken("(");
                E();
                CasaToken(")");
            }
        }
    }

    public void D() throws LexicalException, IOException, SyntaticException {
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
    public void D1() throws LexicalException, IOException, SyntaticException {
        CasaToken("integer");
        CasaToken("ID");
        if(reg.token.nome.equals("[")){
            CasaToken("[");
            E();
            CasaToken("]");

        }else if(reg.token.nome.equals("=")) {
            CasaToken("=");
            E();
        }
        if(reg.token.nome.equals(",")){
            H();
        }
        CasaToken(";");
    }

    //D2 -> 'real' (id [ '[' E ']' ] [H] ';' | id = E [H] ';')
    public void D2() throws LexicalException, IOException, SyntaticException {
        CasaToken("real");
        CasaToken("ID");

        if(reg.token.nome.equals("[")){
            CasaToken("[");
            E();
            CasaToken("]");

        }
        else if(reg.token.nome.equals("=")) {
            CasaToken("=");
            E();

        }
        if(reg.token.nome.equals(",")){
            H();
        }

        CasaToken(";");
    }

    // D3 -> 'char' (id [ '[' E ']' ] [H1] ';' | id = E [H1] ';')
    public void D3() throws LexicalException, IOException, SyntaticException {
        CasaToken("char");
        CasaToken("ID");
        if(reg.token.nome.equals("[")){
            CasaToken("[");
            E();
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
    public void D4() throws LexicalException, IOException, SyntaticException {
        CasaToken("boolean");
        CasaToken("ID");
        if(reg.token.nome.equals("[")){
            CasaToken("[");
            E();
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
    public void D5() throws LexicalException, IOException, SyntaticException {
        CasaToken("final");
        CasaToken("ID"); if(reg.token.nome.equals("=")) {
            CasaToken("=");
            E();
        }
        if(reg.token.nome.equals(",")){
            H3();
        }
        CasaToken(";");
    }

    // I -> true | false
    public void I() throws LexicalException, IOException, SyntaticException {
        if(reg.token.nome.equals("true")){
            CasaToken("true");
        }
        else if(reg.token.nome.equals("false")){
            CasaToken(("false"));
        }
    }

    // H -> {',' id [ '[' const | id ']' ] | [ = E]}+
    public void H() throws LexicalException, IOException, SyntaticException{
        do {
            CasaToken(",");
            CasaToken("ID");

            if (reg.token.nome.equals("[")) {
                CasaToken("[");

                if (reg.token.nome.equals("CONST")) {
                    CasaToken("CONST");
                } else if (reg.token.nome.equals("ID")) {
                    CasaToken("ID");
                }
                else {
                    throw new SyntaticException(-1, SyntaticErrorEnum.TOKEN_NAO_ESPERADO);
                }

                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");

                E();
            }
        } while (reg.token.nome.equals(","));
    }

    // H1 -> {',' id ([ '[' const | id ']' ] | [ = const])}+
    public void H1() throws LexicalException, IOException, SyntaticException {
        do {
            CasaToken(",");
            CasaToken("ID");

            if (reg.token.nome.equals("[")) {
                CasaToken("[");

                if (reg.token.nome.equals("CONST")) {
                    CasaToken("CONST");
                } else if (reg.token.nome.equals("ID")) {
                    CasaToken("ID");
                }
                else {
                    throw new SyntaticException(-1, SyntaticErrorEnum.TOKEN_NAO_ESPERADO);
                }

                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");

                CasaToken("CONST");
            }
        } while (reg.token.nome.equals(","));
    }

    // H2 -> {',' id ([ '[' const | id ']' ] [ = I])}+
    public void H2() throws LexicalException, IOException, SyntaticException {
        do {
            CasaToken(",");
            CasaToken("ID");

            if (reg.token.nome.equals("[")) {
                CasaToken("[");

                if (reg.token.nome.equals("CONST")) {
                    CasaToken("CONST");
                } else if (reg.token.nome.equals("ID")) {
                    CasaToken("ID");
                }
                else {
                    throw new SyntaticException(-1, SyntaticErrorEnum.TOKEN_NAO_ESPERADO);
                }

                CasaToken("]");
            } else if (reg.token.nome.equals("=")) {
                CasaToken("=");

                I();
            }
        } while (reg.token.nome.equals(","));
    }

    // H3 -> {',' id [ = E5]}+
    public void H3() throws LexicalException, IOException, SyntaticException {
        do {
            CasaToken(",");
            CasaToken("ID");

            if (reg.token.nome.equals("=")) {
                CasaToken("=");

                E5();
            }
        } while (reg.token.nome.equals(","));
    }

    // C -> C1 | C2 ';'
    public void C() throws LexicalException, IOException, SyntaticException {
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
    public void C0() throws LexicalException, IOException, SyntaticException {
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
    public void C1() throws LexicalException, IOException, SyntaticException {
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
    public void C2() throws LexicalException, IOException, SyntaticException {
        if(reg.token.nome.equals("readln")){
            CasaToken("readln");
            CasaToken("(");

            if(reg.token.nome.equals("-")){
                CasaToken("-");
            }

            CasaToken("ID");
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
    public void C3() throws LexicalException, IOException, SyntaticException {
        CasaToken("ID");

        if (reg.token.nome.equals("[")) {
            CasaToken("[");
            E();
            CasaToken("]");
        }

        CasaToken("=");

        E();
    }

    //A -> C | begin C* end
    public void A() throws LexicalException, IOException, SyntaticException {
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
    public void G() throws LexicalException, IOException, SyntaticException {
        do{
            CasaToken(",");

            E();
        } while (reg.token.nome.equals(","));
    }


}
