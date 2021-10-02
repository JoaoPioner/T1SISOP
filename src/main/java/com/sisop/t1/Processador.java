package com.sisop.t1;

import java.util.*;

public class Processador {

    private int acumulador;
    private int pc;
    private final Map<String, Integer> variables;
    private final Map<String, Integer> labels;
    private final Queue<String> ready;
    private final Queue<String> exit;

    public Processador() {
        this.acumulador = 0;
        this.pc = 0;
        this.variables = new HashMap<>();
        this.labels = new HashMap<>();
        this.ready = new PriorityQueue<>();// definir um comparator
        this.exit = new PriorityQueue<>();// definir um comparator
    }

    public void gerenciarMemoria() {
    }//memoria

    public void escalonador(List<String> jobs) {

    }

    public void executeLines(List<String> lines) {//running, tlvz 1 chamada por linha pelo escalonador?
        Scanner input = new Scanner(System.in);
        loadVariables(lines);
        loadLabels(lines);

        while (pc < lines.size()) {
            String line = lines.get(pc).trim();
            String replace = line.replace("  ", " ");
            String[] splittedLine = replace.split(" ");

            if (splittedLine[0].equalsIgnoreCase("BRZERO") && acumulador == 0) {
                pc = labels.get(splittedLine[1]);
            } else if (splittedLine[0].equalsIgnoreCase("BRANY")) {
                pc = labels.get(splittedLine[1]);
            } else if (splittedLine[0].equalsIgnoreCase("BRPOS") && acumulador > 0) {
                acumulador = variables.get(splittedLine[1]);
                pc++;
            } else if (splittedLine[0].equalsIgnoreCase("BRNEG") && acumulador < 0) {
                acumulador = variables.get(splittedLine[1]);
                pc++;
            } else if (splittedLine[0].equalsIgnoreCase("LOAD")) {
                acumulador = variables.get(splittedLine[1]);
                pc++;
            } else if (splittedLine[0].equalsIgnoreCase("STORE")) {
                variables.replace(splittedLine[1], variables.get(splittedLine[1]), acumulador);
                pc++;
            } else if (splittedLine[0].equalsIgnoreCase("ADD")) {
                String key = splittedLine[1];
                if (key.trim().startsWith("#")) {
                    acumulador += Integer.parseInt(key.substring(1));
                } else {
                    acumulador = variables.get(key);
                }
                pc++;
            } else if (splittedLine[0].equalsIgnoreCase("SUB")) {
                String key = splittedLine[1];
                if (key.trim().startsWith("#")) {
                    acumulador -= Integer.parseInt(key.substring(1));
                } else {
                    acumulador = variables.get(key);
                }
                pc++;
            } else if (splittedLine[0].equalsIgnoreCase("MULT")) {
                String key = splittedLine[1];
                if (key.trim().startsWith("#")) {
                    acumulador *= Integer.parseInt(key.substring(1));
                } else {
                    acumulador = variables.get(key);
                }
                pc++;
            } else if (splittedLine[0].equalsIgnoreCase("DIV")) {
                String key = splittedLine[1];
                if (key.trim().startsWith("#")) {
                    acumulador /= Integer.parseInt(key.substring(1));
                } else {
                    acumulador = variables.get(key);
                }
                pc++;
            } else if (splittedLine[0].equalsIgnoreCase("SYSCALL")) {
                String key = splittedLine[1];
                if (key.trim().equalsIgnoreCase("0")) {
                    System.exit(0);
                }
                if (key.trim().equalsIgnoreCase("1")) {
                    System.out.println("Acumulador: " + acumulador);
                }
                if (key.trim().equalsIgnoreCase("2")) {
                    System.out.print("Digite um valor: ");
                    acumulador = input.nextInt();
                }
                pc++;
            } else {
                pc++;
            }
        }
        //sinalizar exit
    }

    private void loadLabels(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.endsWith(":")) {
                labels.put(line.replace(":", ""), i);
            }
        }
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
