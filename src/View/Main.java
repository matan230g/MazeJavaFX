package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    private static Stage primaryStage; // **Declare static Stage**
    private void setPrimaryStage(Stage stage) {
        Main.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return Main.primaryStage;
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model = new MyModel();
        model.startServers();
        FXMLLoader fxmlLoader = new FXMLLoader();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        setPrimaryStage(primaryStage);
        Scene scene=new Scene(root, 900, 900);
        scene.getStylesheets().add("View/MainStyle.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dangerous Dave");
        MyViewController view = fxmlLoader.getController();
        view.initialize(viewModel,primaryStage,scene);
        viewModel.addObserver(view);
        primaryStage.setOnCloseRequest(e -> e.consume() );
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
