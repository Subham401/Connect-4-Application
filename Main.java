package com.internshala.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Main extends Application {

    private com.internshala.connect4.Controller controller;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(stage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);

        menuPane.getChildren().add(menuBar);

        controller = loader.getController();
        controller.createPlayground();

        Scene scene = new Scene(rootGridPane);

        stage.setTitle("Connect Four");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private MenuBar createMenu() {
        //File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.resetGame());

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(actionEvent -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");

        exitGame.setOnAction(actionEvent -> {
            Platform.exit();
            System.exit(0);
        });

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

        //Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutConnect4 = new MenuItem("About Connect4");
        aboutConnect4.setOnAction(actionEvent -> alertDialog());

        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(actionEvent -> aboutMe());

        helpMenu.getItems().addAll(aboutConnect4, separatorMenuItem, aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private void aboutMe() {
        Alert alertDialog = new Alert(Alert.AlertType.INFORMATION);
        alertDialog.setTitle("About The Developer");
        alertDialog.setHeaderText("Subham Bose");
        alertDialog.setContentText("I'm a beginner in making JAVA app. This is my first App.");

        alertDialog.show();
    }

    private void alertDialog() {
        Alert alertDialog = new Alert(Alert.AlertType.INFORMATION);
        alertDialog.setTitle("About The Game");
        alertDialog.setHeaderText("How To Play");
        TextArea textArea = new TextArea("Connect Four is a two-player connection game in which " +
                "the players first choose a color and then take turns dropping colored discs " +
                "from the top into a seven-column, six-row vertically suspended grid." +
                " The pieces fall straight down, occupying the next available space " +
                "within the column. The objective of the game is to be the first to form a horizontal, " +
                "vertical, or diagonal line of four of one's own discs. Connect Four is a solved game." +
                " The first player can always win by playing the right moves.");
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setFont(Font.font("Georgia", 15));
        textArea.setPrefWidth(500);

        alertDialog.getDialogPane().setContent(textArea);

        alertDialog.show();
    }

    public static void main(String[] args) {
        launch();
    }
}