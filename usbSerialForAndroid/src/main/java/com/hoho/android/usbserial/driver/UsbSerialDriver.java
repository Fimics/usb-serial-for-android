/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

package com.hoho.android.usbserial.driver;

import android.hardware.usb.UsbDevice;

import java.util.List;

/**
 * 3. UsbSerialDriver
 * 功能： 定义了所有串口驱动必须实现的接口，是一个抽象层。
 *
 * 关键方法：
 * getDevice()：返回关联的 UsbDevice 对象。
 * getPorts()：返回与设备相关的所有串口端口（UsbSerialPort）列表。
 * 应用场景： 作为一个通用的接口，使不同厂商和型号的USB串口驱动实现一致的功能
 */

public interface UsbSerialDriver {

    /*
     * Additional interface properties. Invoked thru reflection.
     *
        UsbSerialDriver(UsbDevice device);                  // constructor with device
        static Map<Integer, int[]> getSupportedDevices();
        static boolean probe(UsbDevice device);             // optional
     */


    /**
     * Returns the raw {@link UsbDevice} backing this port.
     *
     * @return the device
     */
    UsbDevice getDevice();

    /**
     * Returns all available ports for this device. This list must have at least
     * one entry.
     *
     * @return the ports
     */
    List<UsbSerialPort> getPorts();
}
