package View;


import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.search.AState;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyEvent;

import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements Observer, IView {
    //Controls
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.MenuItem menuItem_save;
    public MediaPlayer mediaPlayer;
    private int num_click;

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
        bindProperties();
        //setResizeEvent();
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

    public void newFile(ActionEvent event) {
        mazeDisplayer.cleanDraw();
        btn_solveMaze.setDisable(true);
        menuItem_save.setDisable(true);


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
        menuItem_save.setDisable(false);
        if (viewModel.isFinished()) {
            playMusic("resources/winSound.mp3", false, false);
        }
    }

    private void playMusic(String path, boolean loop, boolean mutable) {
        String mute_off=Configurations.prop.getProperty("mute");
        Media sound = new Media(new File(path).toURI().toString());
        if (mediaPlayer != null)
            mediaPlayer.stop();

        mediaPlayer = new MediaPlayer(sound);
        if (loop)
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
        if(mutable)
            mediaPlayer.setMute(mute_off.equals("true"));
        mediaPlayer.play();
    }


    @FXML
    public void handleCloseButtonAction(ActionEvent event) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UNDECORATED);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
        alert.setContentText("Are you sure you want to exit the application?");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("MainStyle.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            viewModel.close();
        }
//        Parent root = FXMLLoader.load(getClass().getResource("exitBox.fxml"));
//        popABox(root);
    }

    public void close(ActionEvent event) {
        viewModel.close();
        mainStage.close();
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
        Stage box = popABox(root, 600, 400);
        box.setOnHiding(e -> {
            String mute_off=Configurations.prop.getProperty("mute");

            if(mediaPlayer != null)
                mediaPlayer.setMute(mute_off.equals("true"));
        });
    }


    private void popABox(Parent root) {
        popABox(root, 400, 250);

    }

    private Stage popABox(Parent root, double width, double height) {
        Stage box = new Stage();
        box.setScene(new Scene(root, width, height));
        box.initModality(Modality.APPLICATION_MODAL);
        box.initStyle(StageStyle.UNDECORATED);
        box.show();

        return box;
    }

    private boolean validCols = true, validRows = true;

    private void bindProperties() {
//        lbl_rowsNum.textProperty().bind(this.characterPositionRow);
//        lbl_columnsNum.textProperty().bind(this.characterPositionColumn);
        BooleanProperty muteProperty= new SimpleBooleanProperty(Configurations.prop.getProperty("mute").equals("true"));
        muteProperty.addListener((observable, oldValue, newValue) -> {
           if(mediaPlayer!=null){mediaPlayer.stop();}
        });
        txtfld_columnsNum.textProperty().addListener((observable, oldValue, newValue) -> {
            validCols = newValue.length() > 0;
            btn_generateMaze.setDisable(!validRows || !validCols);
        });

        txtfld_rowsNum.textProperty().addListener((observable, oldValue, newValue) -> {
            validRows = newValue.length() > 0;
            btn_generateMaze.setDisable(!validRows || !validCols);
        });
    }

    public void generateMaze(ActionEvent event) {
        int rows = 0;
        int cols = 0;
        try {
            rows = Integer.valueOf(txtfld_rowsNum.getText());
            cols = Integer.valueOf(txtfld_columnsNum.getText());
        } catch (NumberFormatException e) {
            // TODO log
        }

        if (viewModel.generateMaze(rows, cols)) {
//            btn_generateMaze.setDisable(true);
//            btn_solveMaze.setDisable(true);
            mazeDisplayer.requestFocus();
            mazeDisplayer.resetZoom();
            resetSolution();
            mazeDisplayer.redraw();

            playMusic("resources/backgroundMusic.mp3", true, true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setContentText("Wrong input, maze size between 3 to 100");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("MainStyle.css").toExternalForm());
            dialogPane.getStyleClass().add("myDialog");
            alert.showAndWait();
        }
    }

    public void mouseTest(MouseEvent mouseEvent) {

    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if (!viewModel.isFinished()) {
            viewModel.moveCharacter(keyEvent.getCode());
            ArrayList<AState> solution = viewModel.getSolution();
            mazeDisplayer.setSolution(solution);
            mazeDisplayer.setCharacterDirection(viewModel.getCharacterDirection());

            mazeDisplayer.redraw();
        }
        keyEvent.consume();
    }

    public void solveMaze(ActionEvent event) {
        if (viewModel.getSolution() != null) {
            resetSolution();
        } else {
            viewModel.solveMaze();
            btn_solveMaze.setText("Hide Solution");
        }
        ArrayList<AState> solution = viewModel.getSolution();
        mazeDisplayer.setSolution(solution);
        mazeDisplayer.redraw();
    }

    private void resetSolution() {
        viewModel.resetSolution();
        mazeDisplayer.setSolution(viewModel.getSolution());
        btn_solveMaze.setText("Solve Maze");
    }


    public void openFile(ActionEvent event) {
        FileChooser fileChooser = getFileChooser();
        File file = fileChooser.showOpenDialog(mainStage);
        viewModel.openFile(file);
        mazeDisplayer.requestFocus();

    }

    public void saveMaze(ActionEvent event) {
        FileChooser fileChooser = getFileChooser();
        File file = fileChooser.showSaveDialog(mainStage);
        viewModel.saveMaze(file.getPath());

    }

    private FileChooser getFileChooser() {
        FileChooser fileChooser = new FileChooser();
        // Set Initial Directory to Desktop
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser;
    }
    public void backDoor() {
        num_click++;
        if(num_click==5) {
            try {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome  --kiosk https://www.playdosgames.com/play/dangerous-dave/"});
                num_click=0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void onScroll(ScrollEvent scrollEvent) {
        mazeDisplayer.changeZoom(scrollEvent.getDeltaY() > 0 ? 1 : -1);
    }
}
