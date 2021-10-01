import java.util.*;

public class Executor {

    private int acumulador;
    private int pc;
    private Map<String, Integer> variables;
    private Queue <String> ready;
    private Queue <String> exit;

    public Executor() {
        this.acumulador = 0;
        this.pc = 0;
        this.variables = new HashMap<>();
        this.ready = new PriorityQueue<>();// definir um comparator
    }

    public void gerenciarMemoria(){}//memoria

    public void escalonador(List<String> jobs) {

    }

    public void executeLines(List<String> lines) {//running, tlvz 1 chamada por linha pelo escalonador?
        loadVariables(lines);
        System.out.println(variables);

        for (int i = 0; i < lines.size(); i++) {
            String[] splittedLine = lines.get(i).trim().split(" ");
            System.out.println(splittedLine[0]);
            if (splittedLine[0].endsWith(":")) {
                acumulador = variables.get(splittedLine[splittedLine.length-1]);
            }
            if (splittedLine[0].equalsIgnoreCase("LOAD")) {
                acumulador = variables.get(splittedLine[1]);
            }
            if (splittedLine[0].equalsIgnoreCase("STORE")) {
                variables.replace(splittedLine[1],variables.get(splittedLine[1]),acumulador);
            }
            if (splittedLine[0].equalsIgnoreCase("ADD")) {
                acumulador += Integer.parseInt(splittedLine[1]);
            }
        }
        //sinalizar exit
    }

    private void loadVariables(List<String> lines) {
        int startVariableLines = 0;
        int endVariableLines = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(".data")) {
                startVariableLines = i;
            }
            if (lines.get(i).contains(".enddata")) {
                endVariableLines = i;
            }
        }

        for (int i = startVariableLines + 1; i < endVariableLines; i++) {
            String[] variableLine = lines.get(i).trim().split(" ");
            variables.put(variableLine[0], Integer.valueOf(variableLine[1]));
        }
    }
}
