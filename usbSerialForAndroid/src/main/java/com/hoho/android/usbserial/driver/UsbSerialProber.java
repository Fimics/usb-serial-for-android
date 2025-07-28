/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

package com.hoho.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 用于检测和创建与USB设备相关的串口驱动的工具类，用于从USB设备中动态探测并初始化适合的串口驱动。
 */
public class UsbSerialProber {

    private final ProbeTable mProbeTable;

    public UsbSerialProber(ProbeTable probeTable) {
        mProbeTable = probeTable;
    }

    /**
     * 返回一个包含默认驱动程序的 UsbSerialProber 实例
     * @return
     */
    public static UsbSerialProber getDefaultProber() {
        return new UsbSerialProber(getDefaultProbeTable());
    }

    /**
     * 返回一个包含默认驱动程序的 UsbSerialProber 实例
     * @return
     */
    public static ProbeTable getDefaultProbeTable() {
        final ProbeTable probeTable = new ProbeTable();
        probeTable.addDriver(CdcAcmSerialDriver.class);
        probeTable.addDriver(Cp21xxSerialDriver.class);
        probeTable.addDriver(FtdiSerialDriver.class);
        probeTable.addDriver(ProlificSerialDriver.class);
        probeTable.addDriver(Ch34xSerialDriver.class);
        probeTable.addDriver(GsmModemSerialDriver.class);
        probeTable.addDriver(ChromeCcdSerialDriver.class);
        return probeTable;
    }

    /**
     * Finds and builds all possible {@link UsbSerialDriver UsbSerialDrivers}
     * from the currently-attached {@link UsbDevice} hierarchy. This method does
     * not require permission from the Android USB system, since it does not
     * open any of the devices.
     *
     * @param usbManager usb manager
     * @return a list, possibly empty, of all compatible drivers
     * 扫描当前连接的USB设备，寻找兼容的串口驱动。
     */
    public List<UsbSerialDriver> findAllDrivers(final UsbManager usbManager) {
        final List<UsbSerialDriver> result = new ArrayList<>();

        for (final UsbDevice usbDevice : usbManager.getDeviceList().values()) {
            final UsbSerialDriver driver = probeDevice(usbDevice);
            if (driver != null) {
                result.add(driver);
            }
        }
        return result;
    }
    
    /**
     * 针对单个USB设备，尝试查找一个兼容的串口驱动。如果找到，会实例化一个对应的 UsbSerialDriver 对象。
     * Probes a single device for a compatible driver.
     * 
     * @param usbDevice the usb device to probe
     * @return a new {@link UsbSerialDriver} compatible with this device, or
     *         {@code null} if none available.
     */
    public UsbSerialDriver probeDevice(final UsbDevice usbDevice) {
        final Class<? extends UsbSerialDriver> driverClass = mProbeTable.findDriver(usbDevice);
        if (driverClass != null) {
            final UsbSerialDriver driver;
            try {
                final Constructor<? extends UsbSerialDriver> ctor =
                        driverClass.getConstructor(UsbDevice.class);
                driver = ctor.newInstance(usbDevice);
            } catch (NoSuchMethodException | IllegalArgumentException | InstantiationException |
                     IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return driver;
        }
        return null;
    }

}
