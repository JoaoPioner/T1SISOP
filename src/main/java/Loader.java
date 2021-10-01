import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Loader {
    private String fonte;
    private List<String> jobs;
    public Loader(String fonte) {
        this.fonte = fonte;
        this.jobs = new LinkedList<>();
    }
    public void load()throws FileNotFoundException, InterruptedException  {
        Executor executor = new Executor();
        BufferedReader br = new BufferedReader(new FileReader(fonte));
        String linha = "";

        try {
            br.readLine();
            while ((linha = br.readLine()) != null) {
                if(linha.equals(".code")) {
                    do {
                        jobs.add(linha);
                    } while (linha.equals(".endcode"));
                } else {
                    jobs.add(linha);
                }
            }
            br.close();
            executor.executeLines(jobs);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
