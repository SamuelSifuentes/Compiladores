public class RegistroLexico {
    Token token;
    Lexema lexema;
    int pos;
    String valorConst;
    String tipoConst;
    int endConst;

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
