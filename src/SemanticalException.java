public class SemanticalException extends Throwable{
    public SemanticalException(int linha, SemanticalErrorEnum syntaticErrorEnum, String lex){
        super(linha + ":" + syntaticErrorEnum.err + " [" + lex + "].");
    }

    public SemanticalException(int linha, SemanticalErrorEnum syntaticErrorEnum){
        super(linha + ":" + syntaticErrorEnum.err);
    }
}
