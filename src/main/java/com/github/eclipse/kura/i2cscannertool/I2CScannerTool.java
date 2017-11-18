package com.github.eclipse.kura.i2cscannertool;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import jdk.dio.DeviceManager;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

public class I2CScannerTool {

    public static final int CHIPID = 0xD0 & 0xFFFF;

    public List<com.github.eclipse.kura.i2cscannertool.I2CDevice> scan() {
        return scan(1);
    }

    public List<com.github.eclipse.kura.i2cscannertool.I2CDevice> scan(int i2cBusIndex) {

        List<com.github.eclipse.kura.i2cscannertool.I2CDevice> list = new LinkedList<>();
        for (byte address = 0; address < 127; address++) {
            I2CDeviceConfig config = new I2CDeviceConfig(i2cBusIndex, address, 7, 400000);
            ByteBuffer chipIdBuffer = ByteBuffer.allocate(7);
            try (I2CDevice device = DeviceManager.open(I2CDevice.class, config)) {
                if (device != null) {
                    device.read(CHIPID, 1, chipIdBuffer);
                    chipIdBuffer.flip();
                    list.add(new com.github.eclipse.kura.i2cscannertool.I2CDevice(address, chipIdBuffer.get()));
                    device.close();
                }
            } catch (Exception e) {
            }
        }
        return list;
    }

    public String readAsHex(int i2cBus, int id, int address) {
        return Integer.toHexString(read(i2cBus, id, address));
    }

    public byte read(int i2cBus, int id, int address) {
        I2CDeviceConfig config = new I2CDeviceConfig(i2cBus, id, 7, 400000);
        ByteBuffer chipIdBuffer = ByteBuffer.allocate(7);
        try (I2CDevice device = DeviceManager.open(I2CDevice.class, config)) {
            if (device != null) {
                device.read(address, 1, chipIdBuffer);
                chipIdBuffer.flip();
                device.close();
            }
        } catch (Exception e) {
        }
        return chipIdBuffer.get();
    }
}