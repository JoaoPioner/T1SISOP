package com.sisop.t1;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    private String fonte;
    private List<String> jobs;

    public static void main(String[] args) throws IOException {
//        List<String> lines = Files.readAllLines(Path.of("C:/Users/jjvpi/Desktop/Semestre8/SISOP/T1/prog11.txt"));
        List<String> prog1 = Arrays.asList((".code\n" +
                "  load controle\n" +
                "  syscall 2\n" +
                "  store controle\n" +
                "loop:\n" +
                "  BRZERO fim\n" +
                "  load a\n" +
                "  add b\n" +
                "  store aux\n" +
                "  load b\n" +
                "  store a\n" +
                "  load aux\n" +
                "  store b\n" +
                "  load controle\n" +
                "  sub #1\n" +
                "  store controle\n" +
                "  BRANY loop\n" +
                "fim:\n" +
                "  load b\n" +
                "  syscall 1\n" +
                "  syscall 0\n" +
                ".endcode\n" +
                "\n" +
                ".data\n" +
                "  a 0\n" +
                "  b 1\n" +
                "  controle 0\n" +
                "  aux 0\n" +
                ".enddata").split("\n"));
        Integer prioridade1 = 0;
        List<String> prog2 = Arrays.asList((".code\n" +
                "  load controle\n" +
                "  syscall 2\n" +
                "  store controle\n" +
                "loop:\n" +
                "  load a\n" +
                "  store controle\n" +
                "  syscall 0\n" +
                ".endcode\n" +
                "\n" +
                ".data\n" +
                "  a 0\n" +
                "  b 1\n" +
                "  controle 0\n" +
                "  aux 0\n" +
                ".enddata").split("\n"));
        Integer prioridade2 = 2;
        PCB pcb1 = new PCB(prog1, prioridade1);
        PCB pcb2 = new PCB(prog2, prioridade2);
        SO so = new SO(pcb1, pcb2);
        so.start();
    }
}
