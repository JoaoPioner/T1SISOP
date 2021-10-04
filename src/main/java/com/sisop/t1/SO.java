package com.sisop.t1;

import java.util.*;

public class SO {
    private final Queue<PCB> readyQueue;
    private final List<PCB> admissionQueue;
    private final List<PCB> blockList;
    private final List<PCB> allPCBs;
    private final Scanner input;
    private final EscalationPolicies policy;
    private PCB runningPCB;
    private Integer runningQuantum;
    private Integer time;

    public SO(List<PCB> pcbs, EscalationPolicies policy) {
        this.readyQueue = setQueuePriority(policy);
        this.policy = policy;
        this.blockList = new ArrayList<>();
        this.admissionQueue = new ArrayList<>();
        this.allPCBs = pcbs;
        this.input = new Scanner(System.in);
        this.time = 0;
        this.runningQuantum = 0;
        this.admissionQueue.addAll(pcbs);
    }

    private Queue<PCB> setQueuePriority(EscalationPolicies policy) {
        if (policy == EscalationPolicies.PRIORITY) {
            return new PriorityQueue<>(Comparator.comparing(PCB::getPriority));
        } else {
            return new LinkedList<>();
        }
    }

    public void process() {
        while (!readyQueue.isEmpty() || !blockList.isEmpty() || !admissionQueue.isEmpty() || runningPCB != null) {
            updateAdmissionToReadyQueues();
            updateBlockedProcess();
            setPriorityRunningProcess();
            updateTimeInfos();
            processLine();
            time++;
            runningQuantum--;
            printAllProcess();
        }
    }

    private void printAllProcess() {
        List<Integer> readyProcess = new ArrayList<>();
        List<Integer> runningProcess = new ArrayList<>();
        List<Integer> blockedProcess = new ArrayList<>();
        List<Integer> finishedProcess = new ArrayList<>();
        List<Integer> admissionProcess = new ArrayList<>();

        for (int i = 0; i < allPCBs.size(); i++) {
            if (allPCBs.get(i).getState() == ProcessState.READY) {
                readyProcess.add(i);
            }
            if (allPCBs.get(i).getState() == ProcessState.RUNNING) {
                runningProcess.add(i);
            }
            if (allPCBs.get(i).getState() == ProcessState.BLOCKED) {
                blockedProcess.add(i);
            }
            if (allPCBs.get(i).getState() == ProcessState.FINISHED) {
                finishedProcess.add(i);
            }
            if (allPCBs.get(i).getState() == ProcessState.ADMISSION) {
                admissionProcess.add(i);
            }
        }
        System.out.println();
        System.out.println("Tempo de execução: " + time);
        System.out.println("Processos nas fila de admissão: " + admissionProcess);
        System.out.println("Processos prontos para execução: " + readyProcess);
        System.out.println("Processo em execução: " + runningProcess);
        System.out.println("Processos bloqueados: " + blockedProcess);
        System.out.println("Processos finalizados: " + finishedProcess);
    }

    private void updateTimeInfos() {
        readyQueue.forEach(PCB::addWaitingTime);
        readyQueue.forEach(PCB::addTurnaroundTime);

        if (runningPCB != null) {
            runningPCB.addProcessingTime();
            runningPCB.addTurnaroundTime();
        }
        for (PCB pcb : blockList) {
            pcb.addTurnaroundTime();
        }
    }

    private void processLine() {
        if (runningPCB == null) {
            return;
        }
        String line = runningPCB.getCode().get(runningPCB.getPc()).trim();
        String clearLine = line.replace("  ", " ");
        executeInstruction(clearLine);
    }

    private void updateAdmissionToReadyQueues() {
        List<PCB> readyPCBs = new ArrayList<>();
        for (PCB pcb : admissionQueue) {
            if (Objects.equals(pcb.getArrivalTime(), time)) {
                readyQueue.add(pcb);
                pcb.setState(ProcessState.READY);
                readyPCBs.add(pcb);
            }
        }
        admissionQueue.removeAll(readyPCBs);
    }

    private void updateBlockedProcess() {
        List<PCB> readyPCBs = new ArrayList<>();
        for (PCB blockedProcess : blockList) {
            if (blockedProcess.getBlockTime() == 0) {
                readyQueue.add(blockedProcess);
                blockedProcess.setState(ProcessState.READY);
                readyPCBs.add(blockedProcess);
            } else {
                blockedProcess.setBlockTime(blockedProcess.getBlockTime() - 1);
            }
        }
        blockList.removeAll(readyPCBs);
    }

    private void setPriorityRunningProcess() {
        if (policy == EscalationPolicies.PRIORITY) {
            setPriorityPCB();
        } else {
            setRoundRobinPCB();
        }
    }

    private void setRoundRobinPCB() {
        if (runningPCB == null) {
            runningPCB = readyQueue.poll();
        }
        if (runningQuantum == 0) {
            readyQueue.add(runningPCB);
            runningPCB = readyQueue.poll();
            if (runningPCB != null) {
                runningQuantum = runningPCB.getQuantum();
            }
        }
        if (runningPCB != null) {
            runningPCB.setState(ProcessState.RUNNING);
        }
    }

    private void setPriorityPCB() {
        if (runningPCB == null) {
            runningPCB = readyQueue.poll();
            if (runningPCB != null) {
                runningPCB.setState(ProcessState.RUNNING);
            }
        }
        PCB newPcb = readyQueue.peek();
        if (newPcb != null && newPcb.getPriority() < this.runningPCB.getPriority()) {
            runningPCB.setState(ProcessState.READY);
            readyQueue.add(runningPCB);
            runningPCB = readyQueue.poll();
            runningPCB.setState(ProcessState.RUNNING);
        }
    }

    private void executeInstruction(String clearLine) {
        String[] instruction = clearLine.split(" ");
        runningPCB.setPc(runningPCB.getPc() + 1);
        verifyAndExecuteBranchCommands(instruction);
        verifyAndExecuteVariableChangeCommands(instruction);
        verifyAndExecuteMathCommands(instruction);
        verifyAndExecuteSyscallCommands(instruction);
    }

    private void verifyAndExecuteSyscallCommands(String[] instruction) {
        if (instruction[0].equalsIgnoreCase("SYSCALL")) {
            String key = instruction[1];
            if (key.trim().equalsIgnoreCase("0")) {
                runningPCB.setState(ProcessState.FINISHED);
                runningPCB = null;
            }
            if (key.trim().equalsIgnoreCase("1")) {
                int timeBlocked = 15;
                runningPCB.setBlockTime(timeBlocked);
                runningPCB.setState(ProcessState.BLOCKED);
                blockList.add(runningPCB);
                System.out.println("Acumulador: " + runningPCB.getAccumulator());
                runningPCB = null;
            }
            if (key.trim().equalsIgnoreCase("2")) {
                int timeBlocked = 15;
                runningPCB.setBlockTime(timeBlocked);
                runningPCB.setState(ProcessState.BLOCKED);
                blockList.add(runningPCB);
                System.out.print("Digite um valor: ");
                runningPCB.setAccumulator(input.nextInt());
                runningPCB = null;
            }
        }
    }

    private void verifyAndExecuteMathCommands(String[] instruction) {
        if (instruction[0].equalsIgnoreCase("ADD")) {
            String key = instruction[1];
            if (key.trim().startsWith("#")) {
                runningPCB.setAccumulator(runningPCB.getAccumulator() + Integer.parseInt(key.substring(1)));
            } else {
                runningPCB.setAccumulator(runningPCB.getVariables().get(key));
            }
        } else if (instruction[0].equalsIgnoreCase("SUB")) {
            String key = instruction[1];
            if (key.trim().startsWith("#")) {
                runningPCB.setAccumulator(runningPCB.getAccumulator() - Integer.parseInt(key.substring(1)));
            } else {
                runningPCB.setAccumulator(runningPCB.getVariables().get(key));
            }
        } else if (instruction[0].equalsIgnoreCase("MULT")) {
            String key = instruction[1];
            if (key.trim().startsWith("#")) {
                runningPCB.setAccumulator(runningPCB.getAccumulator() * Integer.parseInt(key.substring(1)));
            } else {
                runningPCB.setAccumulator(runningPCB.getVariables().get(key));
            }
        } else if (instruction[0].equalsIgnoreCase("DIV")) {
            String key = instruction[1];
            if (key.trim().startsWith("#")) {
                runningPCB.setAccumulator(runningPCB.getAccumulator() / Integer.parseInt(key.substring(1)));
            } else {
                runningPCB.setAccumulator(runningPCB.getVariables().get(key));
            }
        }
    }

    private void verifyAndExecuteVariableChangeCommands(String[] instruction) {
        if (instruction[0].equalsIgnoreCase("LOAD")) {
            runningPCB.setAccumulator(runningPCB.getVariables().get(instruction[1]));
        } else if (instruction[0].equalsIgnoreCase("STORE")) {
            runningPCB.getVariables().replace(instruction[1], runningPCB.getVariables().get(instruction[1]), runningPCB.getAccumulator());
        }
    }

    private void verifyAndExecuteBranchCommands(String[] instruction) {
        if (instruction[0].equalsIgnoreCase("BRZERO") && runningPCB.getAccumulator() == 0) {
            runningPCB.setPc(runningPCB.getLabels().get(instruction[1]));
        } else if (instruction[0].equalsIgnoreCase("BRANY")) {
            runningPCB.setPc(runningPCB.getLabels().get(instruction[1]));
        } else if (instruction[0].equalsIgnoreCase("BRPOS") && runningPCB.getAccumulator() > 0) {
            runningPCB.setAccumulator(runningPCB.getVariables().get(instruction[1]));
        } else if (instruction[0].equalsIgnoreCase("BRNEG") && runningPCB.getAccumulator() < 0) {
            runningPCB.setAccumulator(runningPCB.getVariables().get(instruction[1]));
        }
    }
}
