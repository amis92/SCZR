package view;

import event.guievents.ContinuousSimulationEvent;
import event.guievents.NewPassengerEvent;
import event.guievents.PassengerGenerationInterval;
import mockup.Mockup;
import network.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class ClientWrapper extends Thread {
    private Client<Mockup> client = null;
    private LinkedBlockingQueue<Mockup> mockups = new LinkedBlockingQueue<Mockup>();
    private GuiFunctionality gun=null;
    private boolean runny = true;
    private Properties config;
    private File configFile = new File("server.cfg");

    public ClientWrapper(GuiFunctionality gui){
        addImplementor(gui);
        config = new Properties();
        try {
            config.load(new FileInputStream(configFile));
        } catch (IOException e) {}
    }

    public ClientWrapper(){
        this(null);
    }

    public void addImplementor(GuiFunctionality gui){
        gun=gui;
    }

    @Override
    public void run(){
        connect();
        try {
            while(runny) {
                Mockup mockup = null;
                do{//przewijanie do ostatniej otrzymanej
                    mockup = mockups.take();
                }while (!mockups.isEmpty());
                gun.newMockup(mockup);
            }
        }catch(InterruptedException e){
            if(runny) gun.connectionLost();
        }
    }

    public void setContinuous(boolean set){
        client.send(new ContinuousSimulationEvent(set));
    }

    public void nextStep(){client.send(new ContinuousSimulationEvent(false)); }

    public void createPassenger(String from, String to){
        client.send(new NewPassengerEvent(from, to));
    }

    public void setPassengerGenerationIntervals(int min, int max){
        client.send(new PassengerGenerationInterval(min, max));
    }

    public void kill(){
        client.closeConnection();
        client = null;
        runny = false;
        this.interrupt();
    }

    private void connect(){
        String ip = config.getProperty("ip","127.0.0.1");
        int port = Integer.parseInt(config.getProperty("port", "8123"));
        client = new Client<Mockup>(ip,port);
        mockups.clear();
        client.setEventsBlockingQueue(mockups);
        if(client.connect()){
            gun.connectionEstablished();
        }else{
            gun.connectionLost();
        }
    }

    public String getPrettyAddress(){
        return config.getProperty("ip","127.0.0.1")+":"+config.getProperty("port", "8123");
    }
}
