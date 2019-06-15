package View;


import ViewModel.MyViewModel;
import algorithms.search.AState;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyViewController implements Observer, IView {
    //Controls
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public  MediaPlayer mediaPlayer;

    //Properties - For Binding
    public StringProperty characterPositionRow = new SimpleStringProperty("1");
    public StringProperty characterPositionColumn = new SimpleStringProperty("1");
    public StringProperty goalPositionColumn = new SimpleStringProperty("1");
    public StringProperty goalPositionRow = new SimpleStringProperty("1");

    @FXML
    private MyViewModel viewModel;
    private Scene mainScene;
    private Stage mainStage;

    public void initialize(MyViewModel viewModel, Stage mainStage, Scene mainScene) {
        this.viewModel = viewModel;
        this.mainScene = mainScene;
        this.mainStage = mainStage;
        //bindProperties();
        //setResizeEvent();
    }
    public void openOpition(ActionEvent event){


    }


    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            displayMaze(viewModel.getMaze(), viewModel.getGoalPositionRow(), viewModel.getGoalPositionColumn());
            btn_generateMaze.setDisable(false);
        }
    }

    public void setResizeEvent() {
        this.mainScene.widthProperty().addListener((observable, oldValue, newValue) -> {
            //mazeDisplayer.redraw();
            System.out.println("Width: " + newValue);
        });

        this.mainScene.heightProperty().addListener((observable, oldValue, newValue) -> {
            //mazeDisplayer.redraw();
            System.out.println("Height: " + newValue);
        });
    }

    public void displayMaze(int[][] maze, int goalRow, int goalCol) {
        mazeDisplayer.setMaze(maze);
        mazeDisplayer.setGoalPosition(goalRow, goalCol);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        btn_solveMaze.setDisable(false);
        if(viewModel.isFinished()){
            playMusic("resources/winSound.mp3");


        }
    }

    private void playMusic(String path) {
        Media sound = new Media(new File(path).toURI().toString());
        this.mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }


    @FXML
    public void handleCloseButtonAction(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("exitBox.fxml"));
        popABox(root);
    }

    public void close(ActionEvent event) {
        Platform.exit();
    }

    public void stay(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
        // do what you have to do
    }

    public void about(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("aboutBox.fxml"));
        popABox(root);
    }
    public void properties(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Properties.fxml"));
        popABox(root,600,400);
    }


    private void popABox(Parent root) {
       popABox(root,500,300);

    }

    private void popABox(Parent root,double width,double height) {
        Stage box = new Stage();
        box.setScene(new Scene(root, width, height));
        box.initModality(Modality.APPLICATION_MODAL);
        box.initStyle(StageStyle.UNDECORATED);
        box.showAndWait();

    }

    private void bindProperties() {
        lbl_rowsNum.textProperty().bind(this.characterPositionRow);
        lbl_columnsNum.textProperty().bind(this.characterPositionColumn);

    }

    public void generateMaze(ActionEvent event) {
        int rows = Integer.valueOf(txtfld_rowsNum.getText());
        int cols = Integer.valueOf(txtfld_columnsNum.getText());
        btn_generateMaze.setDisable(true);
        btn_solveMaze.setDisable(true);
        viewModel.generateMaze(rows, cols);

        mazeDisplayer.requestFocus();
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }
    public void change(KeyEvent e){
        mazeDisplayer.changeSize();

    }
    public void solveMaze(ActionEvent event){
        viewModel.solveMaze();
        ArrayList<AState> sol= viewModel.getSolution();
        mazeDisplayer.drawSol(sol);


    }


    public void shutDown() {

    }
}
