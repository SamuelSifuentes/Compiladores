public class LexicalException extends Throwable {
    public LexicalException(int linhaAtual, LexicalErrorEnum lexicalErrorEnum) {
        super(linhaAtual + ":" + lexicalErrorEnum.err);
    }

    public LexicalException(int linhaAtual, LexicalErrorEnum lexicalErrorEnum, String lexema) {
        super(linhaAtual + ":" + lexicalErrorEnum.err + " [" + lexema + "].");
    }

    public LexicalException(int linhaAtual, LexicalErrorEnum lexicalErrorEnum, char c) {
        super(linhaAtual + ":" + lexicalErrorEnum.err + " ['" + c + "'].");
    }
}
