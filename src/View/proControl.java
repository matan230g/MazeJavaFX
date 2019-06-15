package View;

import Server.Configurations;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Window;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class proControl implements Initializable {
    @FXML
    ComboBox<String> generatingAlgorithm;
    @FXML
    ComboBox<String> searchingAlgorithm;
    @FXML
    Button apply;
    @FXML
    Button cancel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StringProperty mazeTypeProperty = new SimpleStringProperty(Configurations.prop.getProperty("MazeGenerator"));
        StringProperty searchTypeProperty = new SimpleStringProperty(Configurations.prop.getProperty("searchingAlgorithm"));
        searchingAlgorithm.promptTextProperty().bind(searchTypeProperty);
        generatingAlgorithm.promptTextProperty().bind(mazeTypeProperty);
        searchingAlgorithm.getItems().addAll(
                "BreadthFirstSearch",
                "BestFirstSearch",
                "DepthFirstSearch"
        );
        generatingAlgorithm.getItems().addAll("EmptyMazeGenerator","MyMazeGenerator");
        apply.setOnAction(e->{
            applyAction();
            Window window=((Node)(e.getSource())).getScene().getWindow();
            window.hide();
        });
        cancel.setOnAction(e->{
            Window window=((Node)(e.getSource())).getScene().getWindow();
            window.hide();
        });
    }
    private void applyAction(){
        if (generatingAlgorithm.getValue()!=null){
           // generatingAlgorithm.setPromptText(generatingAlgorithm.getValue());
            Configurations.prop.setProperty("MazeGenerator",generatingAlgorithm.getValue());}
        if (searchingAlgorithm.getValue()!=null){
            //searchingAlgorithm.setPromptText(searchingAlgorithm.getValue());
            Configurations.prop.setProperty("searchingAlgorithm",searchingAlgorithm.getValue());}
      updatePro();
    }

    private void updatePro() {
        try{
            OutputStream out=new FileOutputStream("resources/config.properties");
            try {
                Configurations.prop.store(out,"update");
            }

            catch(IOException e){
                e.printStackTrace();

            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}

