package com.interra.denemesdfilecreate.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class CpuMaxFreq {
    private static final String BOGOMIPS_PATTERN = "BogoMIPS[\\s]*:[\\s]*(\\d+\\.\\d+)[\\s]*\n";
    private static final String MEMTOTAL_PATTERN = "MemTotal[\\s]*:[\\s]*(\\d+)[\\s]*kB\n";
    private static final String MEMFREE_PATTERN = "MemFree[\\s]*:[\\s]*(\\d+)[\\s]*kB\n";



    /**
     * @return in kiloHertz.
     * //@throws SystemUtilsException
     */
    public static int getCPUFrequencyMax() throws Exception {
        return CpuMaxFreq.readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
    }

    public static int readSystemFileAsInt(final String pSystemFile) throws Exception {
        InputStream in = null;
        try {
            final Process process = new ProcessBuilder(new String[] { "/system/bin/cat", pSystemFile }).start();

            in = process.getInputStream();
            final String content = readFully(in);
            return Integer.parseInt(content);
        } catch (final Exception e) {
            throw new Exception(e);
        }
    }
    public static final String readFully(final InputStream pInputStream) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final Scanner sc = new Scanner(pInputStream);
        while(sc.hasNextLine()) {
            sb.append(sc.nextLine());
        }
        return sb.toString();
    }
    private static MatchResult matchSystemFile(final String pSystemFile, final String pPattern, final int pHorizon) throws Exception {
        InputStream in = null;
        try {
            final Process process = new ProcessBuilder(new String[] { "/system/bin/cat", pSystemFile }).start();

            in = process.getInputStream();
            final Scanner scanner = new Scanner(in);

            final boolean matchFound = scanner.findWithinHorizon(pPattern, pHorizon) != null;
            if(matchFound) {
                return scanner.match();
            } else {
                throw new Exception();
            }
        } catch (final IOException e) {
            throw new Exception(e);
        }

    }
}
