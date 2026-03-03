package cc.thonly.horainingyoubot.util;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BotStatus {
    private final SystemInfo si = new SystemInfo();
    private final OperatingSystem os = this.si.getOperatingSystem();
    private final CentralProcessor cpu = this.si.getHardware().getProcessor();
    private final GlobalMemory memory = this.si.getHardware().getMemory();
    private long[] prevTicks = this.cpu.getSystemCpuLoadTicks();

    public List<String> getStatusMarkdown() {
        List<String> mdList = new ArrayList<>();

        try {
            // 暂停 1 秒，测量 CPU 使用率
            Thread.sleep(1000);
            double cpuUsage = this.cpu.getSystemCpuLoadBetweenTicks(this.prevTicks) * 100;
            this.prevTicks = this.cpu.getSystemCpuLoadTicks();

            String osType = this.os.getFamily();
            String osRelease = this.os.getVersionInfo().getVersion();
            String osPlatform = System.getProperty("os.name");
            String osArch = System.getProperty("os.arch");

            double totalMem = this.memory.getTotal() / (1024.0 * 1024 * 1024);
            double freeMem = this.memory.getAvailable() / (1024.0 * 1024 * 1024);
            double usedMem = totalMem - freeMem;

            String cpuName = this.cpu.getProcessorIdentifier().getName();

            List<OSFileStore> fsList = this.os.getFileSystem().getFileStores();

            mdList.add("# 蓬莱人形Bot运行状况");
            mdList.add("## 系统信息");
            mdList.add("CPU 型号：" + cpuName + "\n");
            mdList.add(String.format("CPU 使用率：%.2f%%" + "\n", cpuUsage));
            mdList.add(String.format("操作系统：%s %s (%s) %s" + "\n", osType, osRelease, osPlatform, osArch));

            long bootSeconds = this.os.getSystemBootTime();
            long nowSeconds = Instant.now().getEpochSecond();
            long uptimeSeconds = nowSeconds - bootSeconds;

            long days = uptimeSeconds / 86400;
            long hours = (uptimeSeconds % 86400) / 3600;
            long minutes = (uptimeSeconds % 3600) / 60;
            long seconds = uptimeSeconds % 60;

            String uptime = String.format("%d天 %d小时 %d分钟 %d秒",
                    days, hours, minutes, seconds);
            mdList.add("开机时间：" + uptime + "\n");
            mdList.add(String.format("内存: 已用 %.1f/%.1f GB" + "\n", usedMem, totalMem));

            mdList.add("### 磁盘使用情况");
            for (OSFileStore disk : fsList) {
                double total = disk.getTotalSpace() / (1024.0 * 1024 * 1024);
                double used = (disk.getTotalSpace() - disk.getUsableSpace()) / (1024.0 * 1024 * 1024);
                mdList.add(String.format("* 磁盘 %s %.1f/%.1f GB", disk.getName(), used, total));
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }

        return mdList;
    }
}
