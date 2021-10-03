public class PCB {
    private int prioridade;// 2 - baixa, 1 - media, 0 - alta
    private int localMemoria;// nsei se eh necessario
    private String estadoProcesso;// ready, blocked, running, exit
    private String idProcesso;// nome do arq(tlvz)
    private int pc;// pc do programa
    private int acumulador;// acumulador do programa/processo
    private int quantum;// nro de iteracoes definido

    public PCB(int prioridade, int localMemoria, String estadoProcesso, String idProcesso, int pc, int acumulador, int quantum) {
        this.prioridade = prioridade;
        this.localMemoria = localMemoria;
        this.estadoProcesso = estadoProcesso;
        this.idProcesso = idProcesso;
        this.pc = pc;
        this.acumulador = acumulador;
        this.quantum = quantum;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public int getLocalMemoria() {
        return localMemoria;
    }

    public void setLocalMemoria(int localMemoria) {
        this.localMemoria = localMemoria;
    }

    public String getEstadoProcesso() {
        return estadoProcesso;
    }

    public void setEstadoProcesso(String estadoProcesso) {
        this.estadoProcesso = estadoProcesso;
    }

    public String getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(String idProcesso) {
        this.idProcesso = idProcesso;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getAcumulador() {
        return acumulador;
    }

    public void setAcumulador(int acumulador) {
        this.acumulador = acumulador;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }
}
