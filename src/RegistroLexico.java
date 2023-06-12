public class RegistroLexico {
    Token token;
    Lexema lexema;
    int pos;
    String valorConst;
    String tipoConst;

    @Override
    public String toString() {
        return "RegistroLexico{" +
                "token=" + token +
                ", lexema=" + lexema +
                ", pos=" + pos +
                ", valor='" + valorConst + '\'' +
                ", tipo='" + tipoConst + '\'' +
                '}';
    }
}
