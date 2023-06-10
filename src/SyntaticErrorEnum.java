public enum SyntaticErrorEnum {
    TOKEN_NAO_ESPERADO("token não esperado"),
    FIM_DE_ARQUIVO("fim de arquivo não esperado.");

    final String err;

    SyntaticErrorEnum(String err){
        this.err = err;
    }
}
