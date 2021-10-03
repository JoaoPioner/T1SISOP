package com.sisop.t1;

import java.util.*;

public class SO {
    private final Queue<PCB> readyQueue;
    private final List<PCB> blockList;
    private final List<PCB> pcbs;
    private final Scanner input;
    private Integer time;

    public SO(PCB... pcbs) {
        this.readyQueue = new PriorityQueue<>(Comparator.comparing(PCB::getPriority));
        this.blockList = new ArrayList<>();
        this.pcbs = Arrays.asList(pcbs);
        this.input = new Scanner(System.in);
        this.time = 0;
        fillReadyQueue();
    }

    public void start() {
        while (!readyQueue.isEmpty() || !blockList.isEmpty()) {
            process();
        }
    }

    private void fillReadyQueue() {
        for (PCB pcb : pcbs) {
            if (pcb.getState() == ProcessState.READY) {
                readyQueue.add(pcb);
            }
        }
    }

    private void process() {
        PCB pcb = readyQueue.poll();
        if (pcb == null) {
            System.out.println("Nenhum programa pronto pra execução.");
            updateBlockedProcess();
            return;
        }
        pcb.setState(ProcessState.RUNNING);
        while (pcb.getState() == ProcessState.RUNNING && !pcb.getFinished()) {
            time++;
            updateBlockedProcess();
            pcb = getPriorityProcess(pcb);
            if (pcb == null) continue;
            String line = pcb.getCode().get(pcb.getPc()).trim();
            String clearLine = line.replace("  ", " ");
            executeInstruction(pcb, clearLine);
        }
    }

    private void updateBlockedProcess() {
        List<PCB> newBlockList = new ArrayList<>();
        for (PCB blockedProcess : blockList) {
            if (blockedProcess.getBlockTime() == 0) {
                readyQueue.add(blockedProcess);
                newBlockList.add(blockedProcess);
            } else {
                blockedProcess.setBlockTime(blockedProcess.getBlockTime() - 1);
            }
        }
        blockList.removeAll(newBlockList);
    }

    private PCB getPriorityProcess(PCB actualPcb) {
        PCB newPcb = readyQueue.peek();
        if (newPcb != null && newPcb.getPriority() < actualPcb.getPriority()) {
            actualPcb.setState(ProcessState.READY);
            readyQueue.add(actualPcb);
            actualPcb = readyQueue.poll();
            actualPcb.setState(ProcessState.RUNNING);
            return actualPcb;
        }
        return actualPcb;
    }

    private void executeInstruction(PCB pcb, String clearLine) {
        String[] instruction = clearLine.split(" ");
        pcb.setPc(pcb.getPc() + 1);
        if (instruction[0].equalsIgnoreCase("BRZERO") && pcb.getAccumulator() == 0) {
            pcb.setPc(pcb.getLabels().get(instruction[1]));
        } else if (instruction[0].equalsIgnoreCase("BRANY")) {
            pcb.setPc(pcb.getLabels().get(instruction[1]));
        } else if (instruction[0].equalsIgnoreCase("BRPOS") && pcb.getAccumulator() > 0) {
            pcb.setAccumulator(pcb.getVariables().get(instruction[1]));
        } else if (instruction[0].equalsIgnoreCase("BRNEG") && pcb.getAccumulator() < 0) {
            pcb.setAccumulator(pcb.getVariables().get(instruction[1]));
        } else if (instruction[0].equalsIgnoreCase("LOAD")) {
            pcb.setAccumulator(pcb.getVariables().get(instruction[1]));
        } else if (instruction[0].equalsIgnoreCase("STORE")) {
            pcb.getVariables().replace(instruction[1], pcb.getVariables().get(instruction[1]), pcb.getAccumulator());
        } else if (instruction[0].equalsIgnoreCase("ADD")) {
            String key = instruction[1];
            if (key.trim().startsWith("#")) {
                pcb.setAccumulator(pcb.getAccumulator() + Integer.parseInt(key.substring(1)));
            } else {
                pcb.setAccumulator(pcb.getVariables().get(key));
            }
        } else if (instruction[0].equalsIgnoreCase("SUB")) {
            String key = instruction[1];
            if (key.trim().startsWith("#")) {
                pcb.setAccumulator(pcb.getAccumulator() - Integer.parseInt(key.substring(1)));
            } else {
                pcb.setAccumulator(pcb.getVariables().get(key));
            }
        } else if (instruction[0].equalsIgnoreCase("MULT")) {
            String key = instruction[1];
            if (key.trim().startsWith("#")) {
                pcb.setAccumulator(pcb.getAccumulator() * Integer.parseInt(key.substring(1)));
            } else {
                pcb.setAccumulator(pcb.getVariables().get(key));
            }
        } else if (instruction[0].equalsIgnoreCase("DIV")) {
            String key = instruction[1];
            if (key.trim().startsWith("#")) {
                pcb.setAccumulator(pcb.getAccumulator() / Integer.parseInt(key.substring(1)));
            } else {
                pcb.setAccumulator(pcb.getVariables().get(key));
            }
        } else if (instruction[0].equalsIgnoreCase("SYSCALL")) {
            String key = instruction[1];
            if (key.trim().equalsIgnoreCase("0")) {
                pcb.setFinished(true);
            }
            if (key.trim().equalsIgnoreCase("1")) {
                int timeBlocked = new Random().nextInt(21) + 10;
                pcb.setBlockTime(timeBlocked);
                pcb.setState(ProcessState.BLOCKED);
                blockList.add(pcb);
                System.out.println("Acumulador: " + pcb.getAccumulator());
            }
            if (key.trim().equalsIgnoreCase("2")) {
                int timeBlocked = new Random().nextInt(21) + 10;
                pcb.setBlockTime(timeBlocked);
                pcb.setState(ProcessState.BLOCKED);
                blockList.add(pcb);
                System.out.print("Digite um valor: ");
                pcb.setAccumulator(input.nextInt());
            }
        }
    }
}
