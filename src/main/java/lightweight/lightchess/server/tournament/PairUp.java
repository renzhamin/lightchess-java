package lightweight.lightchess.server.tournament;

import lightweight.lightchess.net.CommandTypes;
import lightweight.lightchess.net.Data;
import lightweight.lightchess.server.net.ReaderWriterServer;

import java.util.HashMap;
import java.util.Random;

public class PairUp implements Runnable{
    Tournament tournament;
    Random rand = new Random();
    int sleep_time = 2000;
    ReaderWriterServer readerWriterServer;

    public PairUp(Tournament t){
        tournament = t;
        readerWriterServer = t.readerWriterServer;
    }

    public void startMatch(String player1, String player2){
        System.out.println("Match starting between "+player1 + " and "+ player2);

        Data d1 = new Data();
        Data d2 = new Data();
        d1.cmd = d2.cmd = CommandTypes.start_tournament_match;
        d1.sender = d2.sender = "Server";
        d1.receiver = player1; d1.content = player2;
        d2.receiver = player2; d2.content = player1;

        readerWriterServer.setColor(player1, player2);
        readerWriterServer.sendToClient(d1);
        readerWriterServer.sendToClient(d2);
    }

    public String extractBestPlayer(){
        String bestPlayer = tournament.readyList.get(0);
        int score = -1;

        for(String player: tournament.readyList){
            int s = tournament.registeredList.get(player).score;
            if(s > score){
                bestPlayer = player;
                score = s;
            }
        }

        tournament.readyList.remove(bestPlayer);
        return bestPlayer;
    }

    @Override
    public void run() {
        while (true){

            if(tournament.readyList.size()<2){
                try {
                    Thread.sleep(sleep_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }

            String player1 = extractBestPlayer();
            String player2 = extractBestPlayer();


            startMatch(player1,player2);



        }
    }
}
