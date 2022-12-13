package devilSpiderX.server.webServer.module.serverInfo.statistics;

public class CurrentOS {
    /**
     * 操作系统名
     */
    private String name;
    /**
     * 操作系统位数
     */
    private int bitness;

    /**
     * 进程数量
     */
    private int processCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBitness() {
        return bitness;
    }

    public void setBitness(int bitness) {
        this.bitness = bitness;
    }

    public int getProcessCount() {
        return processCount;
    }

    public void setProcessCount(int processCount) {
        this.processCount = processCount;
    }

    @Override
    public String toString() {
        return "CurrentOS{" +
                "bitness=" + bitness +
                ", processCount=" + processCount +
                '}';
    }
}
