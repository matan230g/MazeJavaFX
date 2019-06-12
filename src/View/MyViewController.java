package View;


import ViewModel.MyViewModel;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.omg.PortableInterceptor.ACTIVE;

import java.util.Observable;
import java.util.Observer;

public class MyViewController implements Observer,IView{
    //Controls
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;

    //Properties - For Binding
    public StringProperty characterPositionRow = new SimpleStringProperty("1");
    public StringProperty characterPositionColumn = new SimpleStringProperty("1");

    @FXML
    private MyViewModel viewModel;
    private Scene mainScene;
    private Stage mainStage;

    public void initialize(MyViewModel viewModel, Stage mainStage, Scene mainScene) {
        this.viewModel = viewModel;
        this.mainScene = mainScene;
        this.mainStage = mainStage;
       // bindProperties();
        //setResizeEvent();
    }




    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            displayMaze(viewModel.getMaze());
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
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        btn_solveMaze.setDisable(false);
    }

    @FXML
    public void handleCloseButtonAction(ActionEvent event)throws Exception  {
        Parent root = FXMLLoader.load(getClass().getResource("exitBox.fxml"));
        Stage box=new Stage();
        box.setScene(new Scene(root, 500, 300));
        box.initModality(Modality.APPLICATION_MODAL);
        box.initStyle(StageStyle.UNDECORATED);
        box.show();
    }
    public void close(ActionEvent event){
        Platform.exit();
    }
    public void stay(ActionEvent event){
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
        // do what you have to do
    }
    public void about(ActionEvent event)throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("aboutBox.fxml"));
        Stage box=new Stage();
        box.setScene(new Scene(root, 500, 300));
        box.initModality(Modality.APPLICATION_MODAL);
        box.initStyle(StageStyle.UNDECORATED);
        box.show();
    }
    private void bindProperties() {
        lbl_rowsNum.textProperty().bind(this.characterPositionRow);
        lbl_columnsNum.textProperty().bind(this.characterPositionColumn);
    }
    public void generateMaze(ActionEvent event){
        int heigth = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());
        btn_generateMaze.setDisable(true);
        btn_solveMaze.setDisable(true);
        viewModel.generateMaze(width, heigth);
    }







}
