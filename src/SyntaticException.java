public class SyntaticException extends Throwable{
    public SyntaticException(int linha, SyntaticErrorEnum syntaticErrorEnum, String lex){
        super(linha + ":" + syntaticErrorEnum.err + " [" + lex + "].");
    }

    public SyntaticException(int linha, SyntaticErrorEnum syntaticErrorEnum){
        super(linha + ":" + syntaticErrorEnum.err);
    }
}
