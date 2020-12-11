package com.uxsino;

import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.List;

public class Oshi_core {
    public static void main(String[] args) {
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        HardwareAbstractionLayer hw = si.getHardware();
        FileSystem fs = os.getFileSystem();
//        System.out.println("system=\t"+hw.getComputerSystem());
//        System.out.println("diskStores=\t"+hw.getDiskStores());
//        System.out.println("Displays=\t"+hw.getDisplays());
//        System.out.println("GraphicsCards=\t"+hw.getGraphicsCards());
//        System.out.println("os=\t"+os);
        List<HWDiskStore> ds = hw.getDiskStores();
        for (HWDiskStore d: ds
             ) {
            System.out.println(d);
        }
        System.out.println("===============");
        List<OSFileStore> fst = fs.getFileStores();
        for (OSFileStore ofs : fst) {
            System.out.println(ofs);
        }


    }

}
