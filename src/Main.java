import Model.MyModel;
import View.MyViewController;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model = new MyModel();
        model.startServers();
        FXMLLoader fxmlLoader = new FXMLLoader();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        Parent root = fxmlLoader.load(getClass().getResource("/fxml/MyView.fxml").openStream());
        Scene scene=new Scene(root, 900, 900);
        scene.getStylesheets().add("/css/MainStyle.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dangerous Dave");
        MyViewController view = fxmlLoader.getController();
        view.initialize(viewModel,primaryStage,scene);
        viewModel.addObserver(view);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            try{
            view.handleCloseButtonAction(null);}
            catch(Exception e){e.printStackTrace();}

        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
