import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class RegistroLexico {
    Token token;
    Lexema lexema;
    int pos;
    String valor;
    String tipo;

    @Override
    public String toString() {
        return "RegistroLexico{" +
                "token=" + token +
                ", lexema=" + lexema +
                ", pos=" + pos +
                ", valor='" + valor + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
