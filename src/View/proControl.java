package View;

import Model.MyModel;
import Server.Configurations;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class proControl implements Initializable {
    //logger
    private static final Logger LOG = LogManager.getLogger(MyModel.class);
    @FXML
    ComboBox<String> generatingAlgorithm;
    @FXML
    ComboBox<String> searchingAlgorithm;
    @FXML
    CheckBox muteBox;
    @FXML
    Button apply;
    @FXML
    Button cancel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StringProperty mazeTypeProperty = new SimpleStringProperty(Configurations.prop.getProperty("MazeGenerator"));
        StringProperty searchTypeProperty = new SimpleStringProperty(Configurations.prop.getProperty("searchingAlgorithm"));
        BooleanProperty muteProperty= new SimpleBooleanProperty(Configurations.prop.getProperty("mute").equals("true"));
        searchingAlgorithm.promptTextProperty().bind(searchTypeProperty);
        generatingAlgorithm.promptTextProperty().bind(mazeTypeProperty);
        muteBox.selectedProperty().bindBidirectional(muteProperty);

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
        Configurations.prop.setProperty("mute",muteBox.isSelected()?"true":"false");
      updatePro();
    }

    private void updatePro() {
        try{
            OutputStream out=new FileOutputStream("resources/config.properties");
            try {
                Configurations.prop.store(out,"update");
            }

            catch(IOException e){
               LOG.catching(e);

            }
        }
        catch (FileNotFoundException e){
            LOG.catching(e);
        }
    }
}

