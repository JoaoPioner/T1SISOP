package com.sisop.t1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCB {
    private final Map<String, Integer> variables;
    private final Map<String, Integer> labels;
    private final List<String> lines;
    private final List<String> code;
    private final Integer priority;
    private final Integer quantum;
    private final Integer arrivalTime;
    private Integer pid;
    private Integer pc;
    private ProcessState state;
    private Integer accumulator;
    private Integer blockTime;
    private Integer waitingTime;
    private Integer processingTime;
    private Integer turnaroundTime;
    private Integer admissionTime;

    public PCB(List<String> lines, Integer priority, Integer arrivalTime, Integer quantum, Integer pid) {
        this.variables = new HashMap<>();
        this.labels = new HashMap<>();
        this.code = new ArrayList<>();
        this.lines = lines;
        this.priority = priority;
        this.pc = 0;
        this.accumulator = 0;
        this.blockTime = 0;
        this.arrivalTime = arrivalTime;
        this.state = ProcessState.ADMISSION;
        this.quantum = quantum;
        this.waitingTime = 0;
        this.processingTime = 0;
        this.turnaroundTime = 0;
        this.admissionTime = 0;
        this.pid = pid;
        loadVariables(lines);
        loadLabels(lines);
        loadCode(lines);
    }

    private void loadCode(List<String> linhas) {
        int startCodeLines = 0;
        int endCodeLines = 0;
        for (int i = 0; i < linhas.size(); i++) {
            if (linhas.get(i).contains(".code")) {
                startCodeLines = i + 1;
            }
            if (linhas.get(i).contains(".endcode")) {
                endCodeLines = i;
            }
        }

        for (int i = startCodeLines; i < endCodeLines; i++) {
            code.add(linhas.get(i).trim());
        }
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

    public Map<String, Integer> getVariables() {
        return variables;
    }

    public Map<String, Integer> getLabels() {
        return labels;
    }

    public List<String> getLines() {
        return lines;
    }

    public List<String> getCode() {
        return code;
    }

    public Integer getPriority() {
        return priority;
    }

    public Integer getPc() {
        return pc;
    }

    public ProcessState getState() {
        return state;
    }

    public Integer getAccumulator() {
        return accumulator;
    }

    public void setPc(Integer pc) {
        this.pc = pc;
    }

    public void setAccumulator(Integer accumulator) {
        this.accumulator = accumulator;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public Integer getBlockTime() {
        return blockTime;
    }

    public Integer getArrivalTime() {
        return arrivalTime;
    }

    public Integer getQuantum() {
        return quantum;
    }

    public void setBlockTime(Integer blockTime) {
        this.blockTime = blockTime;
    }

    public Integer getWaitingTime() {
        return waitingTime;
    }

    public Integer getProcessingTime() {
        return processingTime;
    }

    public Integer getTurnaroundTime() {
        return turnaroundTime;
    }

    public Integer getAdmissionTime() {
        return admissionTime;
    }

    public void addWaitingTime() {
        this.waitingTime++;
    }

    public void addProcessingTime() {
        this.processingTime++;
    }

    public void addTurnaroundTime() {
        this.turnaroundTime++;
    }

    public void addAdmissionTime() {
        this.admissionTime++;
    }

    public Integer getPid() {
        return pid;
    }
}
