public enum SemanticalErrorEnum {
    IDENTIFICADOR_NAO_DECLARADO("identificador não declarado"),
    IDENTIFICADOR_JA_DECLARADO("identificador já declarado"),
    CLASSE_IDENTIFICADOR_INCOMPATIVEL("classe de identificador incompatível"),

    TIPOS_INCOMPATIVEIS("tipos incompatíveis.");

    final String err;

    SemanticalErrorEnum(String err) {
        this.err = err;
    }
}
