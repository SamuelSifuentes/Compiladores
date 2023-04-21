import java.io.*;
import java.util.*;

public class app {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        try {
            File myObj = new File("codigo.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
              String data = myReader.nextLine();
              System.out.println(data);
            }
            myReader.close();
          } catch (FileNotFoundException e) {
            System.out.println("Erro ao ler arquivo");
            e.printStackTrace();
        }
    }    
}
