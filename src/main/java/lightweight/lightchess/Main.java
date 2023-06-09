package lightweight.lightchess;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lightweight.lightchess.client.net.ClientNet;
import lightweight.lightchess.client.ui.*;
import lightweight.lightchess.logic.Logic;

import java.io.IOException;
import java.util.List;

public class Main extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    public ClientNet clientNet;
    public String currentState = "";
    public ChessBoard chessBoard;
    Image icon = new Image(getClass().getResourceAsStream("/lightweight/lightchess/lightchess_logo_sttrooke.png"));


    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.getIcons().add(icon);

        this.primaryStage = primaryStage;
        this.primaryStage.setResizable(false);

        Board gameBoard = new Board();
        Logic logic = new Logic();

        //Debug stuff

        final Parameters params = getParameters();
        final List<String> args = params.getRaw();

        chessBoard = new ChessBoard(500, Color.web("#f0d9b5"), Color.web("#b58863"), gameBoard, logic);
        clientNet = new ClientNet(chessBoard);
        clientNet.main = this;
        chessBoard.clientnet = clientNet;

        new Thread(new Runnable() {
            @Override
            public void run() {
                clientNet.start(args);
            }
        }).start();

        this.primaryStage.setTitle("LightChess");

        primaryStage.centerOnScreen();
        showLogin();
        //showChessBoard();
        primaryStage.show();
    }

    public void showSignUp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        SignUp controller = loader.getController();
        controller.setMain(this);
        controller.setClientNet(clientNet);
        currentState = "signup";
    }

    public void showLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        Login controller = loader.getController();
        controller.setMain(this);
        controller.setClientNet(clientNet);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
/*        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
        primaryStage.show();*/
        currentState = "login";
    }

    public void showDashboard() throws IOException, InterruptedException {
        clientNet.fetchUpdatedUserInfo();
        clientNet.fetchUsersList();
        clientNet.fetchTournamentInfo();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        Dashboard controller = loader.getController();
        controller.setMain(this);
        controller.setClientNet(clientNet);
        //primaryStage.centerOnScreen();
/*        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);*/
        primaryStage.show();
        currentState = "dashboard";
    }

    public void changeTournamentGameStatus(String msg) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        clientNet.fetchTournamentInfo();

        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        Game controller = loader.getController();
        controller.setMain(this);
        controller.setClientNet(clientNet);
        controller.anchorPane.getChildren().add(clientNet.chessBoard);
        controller.anchorPane.getChildren().add(new AnchorPane());
        clientNet.chessBoard.setLayoutX((1000 - clientNet.chessBoard.length) / 2);
        clientNet.chessBoard.setLayoutY((800 - clientNet.chessBoard.length) / 2);


        controller.status.setText(msg);

        FXMLLoader clockLoader = new FXMLLoader(getClass().getResource("ChessClock.fxml"));

        AnchorPane clockAnchorPane = clockLoader.load();
        ChessClock clockController = clockLoader.getController();
        clientNet.chessBoard.setClocks(clientNet.match_time_format);

        controller.anchorPane.getChildren().add(clockAnchorPane);
        clockAnchorPane.setLayoutX(750);
        clockAnchorPane.setLayoutY(50 + clientNet.chessBoard.length / 2);
        clockController.init(clientNet.username, chessBoard.playerClock.getTimeString(), clientNet.opponentUsername, chessBoard.opponentClock.getTimeString());
    }

    public void showChessBoard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        clientNet.fetchTournamentInfo();

        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        Game controller = loader.getController();
        controller.setMain(this);
        controller.setClientNet(clientNet);
        controller.anchorPane.getChildren().add(clientNet.chessBoard);
        controller.anchorPane.getChildren().add(new AnchorPane());
        clientNet.chessBoard.setLayoutX((1000 - clientNet.chessBoard.length) / 2);
        clientNet.chessBoard.setLayoutY((800 - clientNet.chessBoard.length) / 2);

        if(chessBoard.inTournament == false) {
            System.out.println("Not in tournament match");
            controller.hideStatus();
        }

        else {
            clientNet.fetchScoreBoard();
            controller.status.setText("Not Ready");
        }

        FXMLLoader clockLoader = new FXMLLoader(getClass().getResource("ChessClock.fxml"));

        AnchorPane clockAnchorPane = clockLoader.load();
        ChessClock clockController = clockLoader.getController();
        clientNet.chessBoard.setClocks(clientNet.match_time_format);

        controller.anchorPane.getChildren().add(clockAnchorPane);
        clockAnchorPane.setLayoutX(750);
        clockAnchorPane.setLayoutY(50 + clientNet.chessBoard.length / 2);
        clockController.init(clientNet.username, chessBoard.playerClock.getTimeString(), clientNet.opponentUsername, chessBoard.opponentClock.getTimeString());
    }

    public void showCasualPlayerList() throws IOException {
        chessBoard.inTournament = false;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CasualPlayerList.fxml"));
        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        CasualPlayerList controller = loader.getController();

        chessBoard.inTournament = false;

        controller.setMain(this);
        controller.setClientNet(clientNet);
        controller.init();
/*        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);*/
        primaryStage.show();
        currentState = "casualplayers";
    }

    public void showTournamentsList() throws IOException {
        chessBoard.inTournament = true;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TournamentsList.fxml"));
        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        TournamentsList controller = loader.getController();
        controller.setMain(this);
        controller.setClientNet(clientNet);
        controller.init();
/*        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);*//*
        primaryStage.show();
        currentState = "tournaments";
    */
    }

    public void showLeaderboards() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("leaderboard.fxml"));
        try {
            rootLayout = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(rootLayout);
        Stage newStage = new Stage();
        newStage.getIcons().add(icon);
        newStage.setScene(scene);
        Leaderboard controller = loader.getController();
        controller.setMain(this);
        controller.setClientNet(clientNet);
        controller.init();
        newStage.show();
        newStage.setAlwaysOnTop(true);
    }

    public void gameRequest(String opponent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dialog_box.fxml"));
        rootLayout = loader.load();
        DialogBox controller = loader.getController();
        controller.setMain(this);
        controller.setClientNet(clientNet);
        controller.init(opponent, clientNet.userInfo.get("time_format"));
        Scene scene = new Scene(rootLayout);
        Stage newStage = new Stage();
        newStage.getIcons().add(icon);
        controller.setStage(newStage);
        newStage.setAlwaysOnTop(true);
        newStage.setScene(scene);
        newStage.show();
    }

    public void loginResponse(boolean ok) throws IOException, InterruptedException {
        if(ok) {
            showDashboard();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            Login controller = loader.getController();
            controller.setMain(this);
            controller.setClientNet(clientNet);
            controller.setMessage("Login Failed");
/*            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
            primaryStage.show();*/
        }
    }

    public void signUpResponse(boolean ok) throws IOException {
        if(ok)
            showLogin();
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            SignUp controller = loader.getController();
            controller.setMain(this);
            controller.setClientNet(clientNet);
            controller.setMessage("Signup Failed");
        }
    }

    public void showDialog(String msg) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dialog.fxml"));
        try {
            rootLayout = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(rootLayout);
        customDialog controller = loader.getController();
        Stage newStage = new Stage();
        newStage.getIcons().add(icon);
        controller.setPrompt(msg);
        newStage.setAlwaysOnTop(true);
        newStage.setScene(scene);
        newStage.show();
    }

    public void showUserStatistics() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("userStatistics.fxml"));
        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        userStatistics controller = loader.getController();
        controller.setMain(this);
        controller.setClientNet(clientNet);
        controller.init(clientNet.username, clientNet.userInfo.get("matchs_lost"), clientNet.userInfo.get("elo"), clientNet.userInfo.get("matchs_drawn"), clientNet.userInfo.get("matchs_won"), clientNet.userInfo.get("tournaments_won"));
    }

    public void startGame() throws IOException {
        showChessBoard();
    }
}
