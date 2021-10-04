package com.sisop.t1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        List<PCB> pcbs = new ArrayList<>();
        EscalationPolicies escalationPolicies = defineEscalationPolicy(scanner);
        System.out.println("Deseja adicionar um programa? S - Sim / N - Não");
        System.out.print("Opção: ");
        String opcao = scanner.next();

        while (opcao.equalsIgnoreCase("S")) {
            System.out.println("Digite o caminho do arquivo: ");
            scanner = new Scanner(System.in);
            String path = scanner.nextLine();
            List<String> lines = Files.readAllLines(Path.of(path));
            System.out.println("Digite o tempo de inicio do programa: ");
            Integer arrivalTime = scanner.nextInt();
            int prioridade = 0;
            int quantum = 0;
            if (escalationPolicies == EscalationPolicies.PRIORITY) {
                System.out.println("Deseja definir a prioridade do programa? S - Sim / N - Não");
                System.out.print("Opção: ");
                String opcaoPrioridade = scanner.next();
                prioridade = getPriority(scanner, opcaoPrioridade);
            } else {
                System.out.println("Digite o quantum do programa: ");
                System.out.print("Opção: ");
                quantum = scanner.nextInt();
            }
            pcbs.add(new PCB(lines, prioridade, arrivalTime, quantum));
            System.out.println("Deseja adicionar um novo programa? S - Sim / N - Não");
            System.out.print("Opção: ");
            opcao = scanner.next();
        }
        System.out.println("\n");
        System.out.println("INICIO DA EXECUÇÃO\n");
        SO so = new SO(pcbs, escalationPolicies);
        so.process();

        System.out.println("\n\nResultado da execução:");
        for (int i = 0; i < pcbs.size(); i++) {
            System.out.println("\nExecução do processo " + (i + 1) + ": ");
            System.out.println("Tempo de espera: " + pcbs.get(i).getWaitingTime());
            System.out.println("Tempo em processo: " + pcbs.get(i).getProcessingTime());
            System.out.println("Tempo total: " + pcbs.get(i).getTurnaroundTime());
        }
    }

    private static Integer getPriority(Scanner scanner, String opcaoPrioridade) {
        if (opcaoPrioridade.equalsIgnoreCase("S")) {
            System.out.print("Digite a prioridade: ");
            return scanner.nextInt();
        } else {
            return 2;
        }
    }

    private static EscalationPolicies defineEscalationPolicy(Scanner scanner) {
        System.out.println("Digite qual politica de escalonamento desejada: ");
        System.out.println("1 - Prioridade");
        System.out.println("2 - Round robin");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        if (opcao == 1) {
            return EscalationPolicies.PRIORITY;
        } else {
            return EscalationPolicies.ROUND_ROBIN;
        }
    }
}
