/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lightweight.lightchess.server.net;

import lightweight.lightchess.net.Information;
import lightweight.lightchess.net.NetworkConnection;
import lightweight.lightchess.server.tournament.Tournament;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ServerNet {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Server Started...");
        System.out.println(InetAddress.getLocalHost());
        HashMap<String, Information> clientList = new HashMap<>();
        HashMap<String, Information> loggedInClientList = new HashMap<>();

        Tournament tournament = new Tournament(loggedInClientList);
// Init a default tournament
        tournament.name = "NICE";
        tournament.startTime = LocalDateTime.now().plusMinutes(20);
        tournament.endTime = tournament.startTime.plusMinutes(30);

        System.out.println(tournament.get_tournament_details());

        new Thread(new ProcessCommands(tournament)).start();

        while (true) {
            Socket socket = serverSocket.accept();
            NetworkConnection nc = new NetworkConnection(socket);

            new Thread(new CreateConnection(clientList,loggedInClientList, nc, tournament)).start();

        }

    }
}
