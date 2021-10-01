import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class Main {

    private String fonte;
    private List<String> jobs;

    public static void main(String[] args) {
        Loader loader = new Loader("C:/Users/jjvpi/Desktop/Semestre8/SISOP/T1/prog11.txt");
        try {
            loader.load();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
