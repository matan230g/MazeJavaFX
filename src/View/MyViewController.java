package View;


import Model.MyModel;
import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements Observer {
    //logger
    private static final Logger LOG = LogManager.getLogger(MyModel.class);
    //Controls
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public TimerLabel timer;
    public javafx.scene.control.MenuItem menuItem_save;
    public MediaPlayer mediaPlayer;

    private int num_click;

    //Properties - For Binding
    public StringProperty characterPositionRow = new SimpleStringProperty("1");
    public StringProperty characterPositionColumn = new SimpleStringProperty("1");
    public StringProperty goalPositionColumn = new SimpleStringProperty("1");
    public StringProperty goalPositionRow = new SimpleStringProperty("1");
    public ImageView victoryScreen;
    public StackPane mazeWrapper;
    public BorderPane board;


    @FXML
    private MyViewModel viewModel;
    private Scene mainScene;
    private Stage mainStage;


    public void initialize(MyViewModel viewModel, Stage mainStage, Scene mainScene) {
        this.viewModel = viewModel;
        this.mainScene = mainScene;
        this.mainStage = mainStage;
        mainStage.setMinHeight(400);
        mainStage.setMinWidth(400);
        bindProperties();
        setResizeEvent();
    }


    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            displayMaze(viewModel.getMaze(), viewModel.getGoalPositionRow(), viewModel.getGoalPositionColumn());
            btn_generateMaze.setDisable(false);
        }
    }

    public void setResizeEvent() {
        this.mainScene.widthProperty().addListener((observable, oldValue, newValue) -> {
            board.setPrefWidth(newValue.doubleValue());
        });

        this.mainScene.heightProperty().addListener((observable, oldValue, newValue) -> {
            board.setPrefHeight(newValue.doubleValue());
        });

        this.mazeWrapper.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Width: " + newValue);
            int newSize = viewModel.setWrapperWidth(newValue.intValue());
            resizeBoard(newSize);
            mazeDisplayer.redraw();
        });

        this.mazeWrapper.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Height: " + newValue);
            int newSize = viewModel.setWrapperHeight(newValue.intValue());
            resizeBoard(newSize);
            mazeDisplayer.redraw();
        });
    }

    private void resizeBoard(int newSize) {
        mazeDisplayer.setHeight(newSize);
        mazeDisplayer.setWidth(newSize);
        victoryScreen.setFitHeight(newSize);
        victoryScreen.setFitWidth(newSize);
    }

    public void newFile() {
        mazeDisplayer.cleanDraw();
        timer.stop();
        timer.reset();
        victoryScreen.setVisible(false);
        mazeDisplayer.setMaze(null);
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
        if (viewModel.isFinished() && timer.isRunning()) {
            LOG.info("Finished current maze! Time spent: " + timer.getTimeString() + " minutes");
            playMusic("/winSound.mp3", false, false);
            timer.stop();
            victoryScreen.setVisible(true);
        }
    }

    private void playMusic(String path, boolean loop, boolean mutable) {
        String mute_off = Configurations.prop.getProperty("mute");
        Media sound;
        if (mediaPlayer != null)
            mediaPlayer.stop();

        try {
            sound = new Media(getClass().getResource(path).toURI().toString());

            mediaPlayer = new MediaPlayer(sound);
            if (loop)
                mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
            if (mutable)
                mediaPlayer.setMute(mute_off.equals("true"));
            mediaPlayer.play();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
                getClass().getResource("/css/MainStyle.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            viewModel.close();
        }
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
        LOG.info("Displaying 'About' window");
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/aboutBox.fxml"));
        popABox(root);
        ImageView image = (ImageView) root.lookup("#dave_image");
        image.setImage(new Image(getClass().getResourceAsStream("/Images/dave.png")));

    }

    public void properties(ActionEvent event) throws Exception {
        LOG.info("Displaying 'Properties' window");
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Properties.fxml"));
        Stage box = popABox(root, 600, 400);
        box.setOnHiding(e -> {
            String mute_off = Configurations.prop.getProperty("mute");

            if (mediaPlayer != null)
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
        BooleanProperty muteProperty = new SimpleBooleanProperty(Configurations.prop.getProperty("mute").equals("true"));
        victoryScreen.setImage(new Image(getClass().getResourceAsStream("/Images/youWin.gif")));
        muteProperty.addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
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
            LOG.catching(e);
        }

        if (viewModel.generateMaze(rows, cols)) {
//            btn_generateMaze.setDisable(true);
//            btn_solveMaze.setDisable(true);
            mazeDisplayer.requestFocus();
            mazeDisplayer.resetZoom();
            resetSolution();
            mazeDisplayer.redraw();

            playMusic("/backgroundMusic.mp3", true, true);
            timer.start();
            victoryScreen.setVisible(false);
        } else {
            LOG.error("Invalid maze size was requested - showing alert dialog");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setContentText("Wrong input, maze size between 3 to 100");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/css/MainStyle.css").toExternalForm());
            dialogPane.getStyleClass().add("myDialog");
            alert.showAndWait();
        }
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if (!viewModel.isFinished()) {
            viewModel.keyPressed(keyEvent.getCode());
            updateDisplayAfterMove();
        }
        keyEvent.consume();
    }

    private void updateDisplayAfterMove() {
        ArrayList<AState> solution = viewModel.getSolution();
        mazeDisplayer.setSolution(solution);
        mazeDisplayer.setCharacterDirection(viewModel.getCharacterDirection());

        mazeDisplayer.redraw();
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        viewModel.keyReleased(keyEvent.getCode());
    }

    public void solveMaze(ActionEvent event) {
        if (viewModel.getSolution() != null) {
            LOG.info("Hiding solution");
            resetSolution();
        } else {
            LOG.info("Solution requested");
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
        if (num_click == 5) {
            try {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome  --kiosk https://www.playdosgames.com/play/dangerous-dave/"});
                num_click = 0;
            } catch (IOException e) {
                LOG.catching(e);
            }
        }
    }

    public void onScroll(ScrollEvent scrollEvent) {
        if (viewModel.isCtrlPressed())
            mazeDisplayer.changeZoom(scrollEvent.getDeltaY() > 0 ? 1 : -1);
    }


    public void mousePress(MouseEvent mouseEvent) {
        Bounds bounds = mazeDisplayer.localToScene(mazeDisplayer.getBoundsInLocal());
        double x = mouseEvent.getX() - bounds.getMinX();
        double y = mouseEvent.getY() - bounds.getMinY();
        Position clickPos = mazeDisplayer.projectClickToTile(x, y);
        viewModel.mousePress(clickPos);
    }

    public void mouseRelease(MouseEvent mouseEvent) {
        viewModel.mouseRelease();
        mazeDisplayer.redraw();
    }

    public void mouseMove(MouseEvent mouseEvent) {
        if (viewModel.isFinished())
            return;

        Bounds bounds = mazeDisplayer.localToScene(mazeDisplayer.getBoundsInLocal());
        double x = mouseEvent.getX() - bounds.getMinX();
        double y = mouseEvent.getY() - bounds.getMinY();
        Position mousePos = mazeDisplayer.projectClickToTile(x, y);
        boolean isDragActive = viewModel.mouseMove(mousePos);

        if (isDragActive)
            updateDisplayAfterMove();
    }

    public void openHelp() throws Exception {
        LOG.info("Displaying 'Help' window");
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/help.fxml"));
        popABox(root, 600, 400);
        ImageView image = (ImageView) root.lookup("#num_pad");
        image.setImage(new Image(getClass().getResourceAsStream("/Images/numeric_keypad.gif")));
    }
}
