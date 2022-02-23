package lightweight.lightchess.client.net;

import lightweight.lightchess.net.Data;

import java.util.concurrent.LinkedBlockingQueue;

public class ProcessIncoming implements Runnable{
    public LinkedBlockingQueue<Data> Q;
    ClientNet client;
    public ProcessIncoming(LinkedBlockingQueue QIn){
        Q = QIn;
    }

    public void handleMessage(Data din){
        System.out.println(din.sender + " : " + din.content);
    }

    public void handleOpponentsMove(Data din){

    }

    public void handlePlayRequest(Data din){
        client.startMatch(din.sender);
    }

    @Override
    public void run() {
        Data data;

        while(true){
            if(Q.isEmpty()){
                try {
                    Thread.sleep(100);
                    continue;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            try {
                data = Q.take();
                switch (data.cmd) {
                    case msg: {
                        handleMessage((Data) data.clone());
                        break;
                    }
                    case move:{
                        handleOpponentsMove((Data)data.clone());
                        break;
                    }
                    case requestToPlay:{
                        handlePlayRequest((Data)data.clone());
                        break;
                    }
                    default: {
                        System.out.println("Invalid incoming command : " + data.cmd);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

        }
    }
}