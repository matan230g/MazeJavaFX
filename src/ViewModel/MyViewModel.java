package ViewModel;

import Model.IModel;
import javafx.scene.input.KeyCode;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public class MyViewModel extends Observable implements Observer {

    private IModel model;

    public MyViewModel(IModel model){
        this.model = model;
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            //Notify my observer (View) that I have changed
            setChanged();
            notifyObservers();
        }
    }



    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    public int[][] getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        //return characterPositionRowIndex;
        return model.getCharacterPositionRow();
    }

    public int getCharacterPositionColumn() {
        //return characterPositionColumnIndex;
        return model.getCharacterPositionColumn();
    }

    public int getGoalPositionRow() {
        return model.getGoalPositionRow();
    }

    public int getGoalPositionColumn() {
        return model.getGoalPositionColumn();
    }



}
