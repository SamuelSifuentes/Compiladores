public class SyntaticException extends Throwable{
    public SyntaticException(int linha, SyntaticErrorEnum err){
        super(linha + ":" + err);
    }
}
