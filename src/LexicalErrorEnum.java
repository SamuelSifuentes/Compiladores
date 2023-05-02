public enum LexicalErrorEnum {
    CARACTER_INVALIDO("caractere invalido"),
    LEXEMA_NAO_IDENTIFICADO("lexema nao identificado"),
    FIM_DE_ARQUIVO_INESPERADO("fim de arquivo nao esperado.");

    final String err;

    LexicalErrorEnum(String err) {
        this.err = err;
    }
}
