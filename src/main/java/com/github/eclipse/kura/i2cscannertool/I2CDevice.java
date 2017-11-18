package com.github.eclipse.kura.i2cscannertool;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class I2CDevice {

    @XmlAttribute
    private byte address;
    @XmlAttribute
    private byte chipId;

    public I2CDevice(byte address, byte chipId) {
        this.address = address;
        this.chipId = chipId;
    }

    public byte getAddress() {
        return address;
    }

    public void setAddress(byte address) {
        this.address = address;
    }

    public byte getChipId() {
        return chipId;
    }

    public void setChipId(byte chipId) {
        this.chipId = chipId;
    }

    @Override
    public String toString() {
        return "0x" + Integer.toHexString(address);
    }

}