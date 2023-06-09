/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lightweight.lightchess.client.net;

import lightweight.lightchess.net.Data;
import lightweight.lightchess.net.NetworkConnection;

import java.util.concurrent.LinkedBlockingQueue;

public class ProcessOutgoing implements Runnable {
    public NetworkConnection netConnection;
    public LinkedBlockingQueue<Data> Q;

    public ProcessOutgoing(NetworkConnection nc, LinkedBlockingQueue<Data> q) {
        netConnection = nc;
        this.Q = q;
    }

    @Override
    public void run() {
        Data data;

        while (true) {

            if (Q.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            try {
                data = Q.take();
                netConnection.write(data.clone());
            } catch (Exception ex) {
                System.out.println("sending failed");
            }
        }
    }

}
