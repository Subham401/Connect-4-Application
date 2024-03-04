package com.internshala.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 80;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;
    private final Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];

    @FXML
    public GridPane rootGridPane;
    @FXML
    public Pane insertedDiscPane;
    @FXML
    public Label playerNameLabel;
    @FXML
    public Button setNamesButton;
    @FXML
    public TextField playerOneTextField , playerTwoTextField;

    private boolean isAllowedToInsert = true;

    public void createPlayground(){
        setNamesButton.setOnAction(event -> {
            PLAYER_ONE = playerOneTextField.getText();
            PLAYER_TWO = playerTwoTextField.getText();
        });

        Shape rectangleWithHoles = createGameStructuralGrid();
        rootGridPane.add(rectangleWithHoles,0,1);

        List<Rectangle> rectangleList = createClickableColumn();

        for(Rectangle rectangle : rectangleList) {
            rootGridPane.add(rectangle, 0, 1);
        }

    }

    private Shape createGameStructuralGrid(){

        Shape rectangleWithHoles = new Rectangle((COLUMNS + 0.85) * CIRCLE_DIAMETER,(ROWS + 0.8) * CIRCLE_DIAMETER );

        for(int row = 0; row < ROWS ; row++){
            for(int col = 0 ; col < COLUMNS ; col++){

                Circle circle = new Circle((double) CIRCLE_DIAMETER / 2);
                circle.setCenterX((double) CIRCLE_DIAMETER / 2);
                circle.setCenterY((double) CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + (double) CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + (double) CIRCLE_DIAMETER / 4);

                rectangleWithHoles = Shape.subtract(rectangleWithHoles , circle);

            }
        }

        rectangleWithHoles.setFill(Color.WHITE);
        return  rectangleWithHoles;

    }

    private List<Rectangle> createClickableColumn(){

        List<Rectangle> rectangleList = new ArrayList<>();

        for(int col = 0 ; col<COLUMNS ; col++){
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER , (ROWS + 0.8) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + (double) CIRCLE_DIAMETER / 4);

            rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee45")) );
            rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;
            rectangle.setOnMouseClicked(mouseEvent -> {
                if(isAllowedToInsert){
                    isAllowedToInsert = false;
                    insertDisc(new Disc(isPlayerOneTurn) , column);
                }
            });
            rectangleList.add(rectangle);
        }

        return rectangleList;

    }

    private void insertDisc(Disc disc, int column) {
        int row = ROWS - 1;

        while (row >= 0) {
            if (getDiscIfPresent(row, column) == null)
                break;
            row--;
        }

        if (row < 0)
            return;

        insertedDiscsArray[row][column] = disc;
        insertedDiscPane.getChildren().add(disc);

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + (double) CIRCLE_DIAMETER / 4);

        int Row = row;

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + (double) CIRCLE_DIAMETER / 4);
        translateTransition.setOnFinished(actionEvent -> {
            isAllowedToInsert = true;

            if (gameEnded(Row, column)) {
                gameOver();
                return;
            }
            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);
        });

        translateTransition.play();
    }


    private void gameOver() {
        String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
        System.out.println("Winner is: " + winner);

        Alert alertDialog = new Alert(Alert.AlertType.INFORMATION);
        alertDialog.setTitle("Connect Four");
        alertDialog.setHeaderText("The Winner is: " + winner);
        alertDialog.setContentText("Want to play again?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No, Exit");

        alertDialog.getButtonTypes().setAll(yesButton,noButton);

        Platform.runLater(() -> {
            Optional<ButtonType> btnClicked = alertDialog.showAndWait();

            if (btnClicked.isPresent() && btnClicked.get() == yesButton) {
                //user typed yes so .....reset the game.
                resetGame();
            } else {
                //user typed no so ......exit the game.
                Platform.exit();
                System.exit(0);
            }
        });
    }

    private boolean gameEnded(int row, int column) {
        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)
                .mapToObj(r -> new Point2D(r, column))
                .collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(col -> new Point2D(row, col))
                .collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonalPoints1 = IntStream.rangeClosed(0,6)
                .mapToObj(i-> startPoint1.add(i, -i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonalPoints2 = IntStream.rangeClosed(0,6)
                .mapToObj(i-> startPoint2.add(i, i))
                .collect(Collectors.toList());

        return checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                || checkCombinations(diagonalPoints1) || checkCombinations(diagonalPoints2);
    }


    private boolean checkCombinations(List<Point2D> points) {
        int chain = 0;
        for (Point2D point : points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {
                chain++;
                if (chain == 4) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }
        return false;
    }

    public void resetGame() {
        insertedDiscPane.getChildren().clear();

        for (Disc[] discs : insertedDiscsArray) {
            Arrays.fill(discs, null);
        }

        isPlayerOneTurn = true;
        playerNameLabel.setText(PLAYER_ONE);
        createPlayground();
    }

    private Disc getDiscIfPresent (int row , int column) {
        if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0) {
            return null;
        }else{
            return insertedDiscsArray[row][column];
        }
    }

    private static class Disc extends Circle {
        private final boolean isPlayerOneMove;
        public Disc(boolean isPlayerOneMove) {
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius((double) CIRCLE_DIAMETER / 2);
            setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
            setCenterX((double) CIRCLE_DIAMETER / 2);
            setCenterY((double) CIRCLE_DIAMETER / 2);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

}