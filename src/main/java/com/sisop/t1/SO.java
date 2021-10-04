package com.sisop.t1;

import java.util.*;

public class SO {
    private final Queue<PCB> readyQueue;
    private final List<PCB> admissionQueue;
    private final List<PCB> blockList;
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

    public void start() {
        while (!readyQueue.isEmpty() || !blockList.isEmpty() || !admissionQueue.isEmpty() || runningPCB != null) {
            updateAdmissionToReadyQueues();
            updateBlockedProcess();
            setPriorityRunningProcess();
            processLine();
            time++;
            runningQuantum--;
        }
    }

    private void processLine() {
        if (runningPCB == null) {
            System.out.println("Nenhum programa pronto pra execução.");
            return;
        }
        String line = runningPCB.getCode().get(runningPCB.getPc()).trim();
        String clearLine = line.replace("  ", " ");
        executeInstruction(clearLine);
    }

    private void updateAdmissionToReadyQueues() {
        List<PCB> readyPCBs = new ArrayList<>();
        for (PCB pcb : admissionQueue) {
            if (pcb.getArrivalTime() == time) {
                readyQueue.add(pcb);
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
            if (runningPCB != null) {
                runningQuantum = runningPCB.getQuantum();
            }
        }
        if (runningQuantum == 0) {
            readyQueue.add(runningPCB);
            runningPCB = readyQueue.poll();
            if (runningPCB != null) {
                runningQuantum = runningPCB.getQuantum();
            }
        }
    }

    private void setPriorityPCB() {
        if (runningPCB == null) {
            runningPCB = readyQueue.poll();
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
                runningPCB = null;
            }
            if (key.trim().equalsIgnoreCase("1")) {
                int timeBlocked = new Random().nextInt(21) + 10;
                runningPCB.setBlockTime(timeBlocked);
                runningPCB.setState(ProcessState.BLOCKED);
                blockList.add(runningPCB);
                System.out.println("Acumulador: " + runningPCB.getAccumulator());
                runningPCB = null;
            }
            if (key.trim().equalsIgnoreCase("2")) {
                int timeBlocked = new Random().nextInt(21) + 10;
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
