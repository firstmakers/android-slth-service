package cl.tide.hidusb.service;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import java.nio.ByteBuffer;


/**
 * Created by eDelgado on 13-08-14.
 */
public class SLTH implements Runnable{


    private UsbDevice mDevice;
    private UsbInterface usbInterface;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbEndpoint usbEndpointIN;
    private UsbEndpoint usbEndpointOUT;
    private Thread thread;

    private boolean monitoring = false;
    private int samples;
    private int interval;

    /** Commands */
    private final byte  CMD_TLH = (byte) 0x87 ;
    private final byte  CMD_TL = (byte) 0x81 ;
    private final byte  CMD_T = (byte) 0x82 ;
    private final byte  CMD_L = (byte) 0x83 ;
    private final byte  CMD_H = (byte) 0x86 ;

    /** Constructor **/
    public SLTH(UsbDevice device, UsbManager usbManager){
        this.mDevice = device;

        usbInterface = device.getInterface(0);
        usbDeviceConnection = usbManager.openDevice(device);

        if(usbDeviceConnection == null)
            return;
        if(usbDeviceConnection.claimInterface(usbInterface,true)){

            usbEndpointIN = usbInterface.getEndpoint(0);
            usbEndpointOUT = usbInterface.getEndpoint(1);

        }else{
            usbDeviceConnection.close();
        }
    }
    /** Begin monitor to slth **/
    public void startMonitor(int i, int s){
        if(!monitoring) {
            this.interval = i;
            this.samples = s;
            thread = new Thread(this);
            thread.start();
            monitoring = true;
        }
    }
    /** Stop monitoring **/
    public synchronized void stopMonitor(){
        if(monitoring) {
            monitoring = false;
            thread.interrupt();
        }
    }
    /** Return device object */
    public UsbDevice getDevice() {
        return mDevice;
    }

    /** closes the connection and releases objects of this class */
    public void close(){
        stopMonitor();
        usbDeviceConnection.releaseInterface(usbInterface);
        usbDeviceConnection.close();

        usbDeviceConnection = null;
        mDevice = null;
        usbInterface = null;
        usbEndpointOUT = null;
        usbEndpointIN = null;
    }

    /** Parse data from device */
    private void processData(byte [] data){
        switch(data[0]){
            /** process data for temperature, light and humidity command */
            case CMD_TLH:

                parseTemperature(data);
                parseLight(data);
                parseHumidity(data);

                break;
            default:
                System.out.println("unable to process this command " + data[0]);
        }
    }

    /** returns the temperature in celsius */
    private double parseTemperature(byte [] data){
        int celsius = ((data[2] & 0xFF) << 8) + (data[1] & 0xFF);
        // Temperature sensor is disconnected
        if(celsius == 32767){
            System. out.println("Temperature sensor is disconnected");
            return 0;
        }// Is a temperature below zero
        else if(celsius> 32767){
            String s = Integer.toBinaryString(~celsius ).substring(16);
            int d = - (Integer.parseInt(s,2) + 1);
            return Math.round((d/16) * 10.0)/10.0;
        }else{
            return Math.round((celsius * 0.0625)*10.0)/10.0;
        }
    }
    /** return humidity 1 - 10 scale **/
    private int parseHumidity(byte[] data){
        int humidity = ((data[6] & 0xFF) << 8) + (data[5] & 0xFF);
        return (int)(humidity/78.7);
    }
    /** return light in lux */
    private double parseLight(byte[] data ){
        double light = ((data[4] & 0xFF) << 8) + (data[3] & 0xFF);
        return Math.round((light / 1.2)*10.0)/10.0;
    }

    @Override
    public void run() {

        int bufferMaxLength = usbEndpointOUT.getMaxPacketSize();
        System.out.println("Max capacity of buffers : " + bufferMaxLength);
        ByteBuffer outBuffer = ByteBuffer.allocate(bufferMaxLength);
        UsbRequest request = new UsbRequest();
        request.initialize(usbDeviceConnection, usbEndpointOUT);
        ByteBuffer inBuffer = ByteBuffer.allocate(bufferMaxLength);
        //put slth command for temperature, light and humidity
        outBuffer.put(CMD_TLH);

        while (!thread.isInterrupted()) {

            request.queue(outBuffer, 1);

            if (usbDeviceConnection.requestWait() == request) {
                System.out.println("Queued out command " + CMD_TLH);
                // wait for confirmation (request was sent)
                UsbRequest inRequest = new UsbRequest();
                // URB for the incoming data
                inRequest.initialize(usbDeviceConnection, usbEndpointIN);
                // the direction is dictated by this initialisation to the incoming endpoint.
                if (inRequest.queue(inBuffer, bufferMaxLength)) {
                    usbDeviceConnection.requestWait();
                    byte[] data = new byte[bufferMaxLength];
                    for(int i = 0;i < 8; i++){
                        data [i] = inBuffer.get(i);
                    }
                    processData(data);
                    inBuffer.clear();

                }
            } else {
                System.out.println("unable to queue the out command ");
            }

            this.samples--;
            if(samples < 0)
                thread.interrupt();

            try {
                Thread.sleep(interval * 1000 - 960);
            } catch (InterruptedException e) {
                System.out.println("Thread monitor is interrupted ");
                break;
            }
        }

    }

    public boolean isCurentDevice(UsbDevice device) {
        return mDevice.getDeviceId() == device.getDeviceId();
    }
}
