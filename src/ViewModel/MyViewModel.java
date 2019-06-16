package ViewModel;

import Model.IModel;
import algorithms.search.AState;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public class MyViewModel extends Observable implements Observer {

    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            //Notify my observer (View) that I have changed
            setChanged();
            notifyObservers();
        }
    }

    public void generateMaze(int rows, int cols) {
        if (rows % 2 == 0)
            rows--;
        if (cols % 2 == 0)
            cols--;

        model.generateMaze(rows, cols);
    }

    public void solveMaze() {
        model.solveMaze();
    }
    public ArrayList<AState> getSolution() {
        return model.getSolution();
    }

    public void moveCharacter(KeyCode movement) {
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

    public boolean isFinished() {
        return model.isFinished();
    }
    public void close(){model.close();}

    public void openFile(File file) {
        model.openFile(file);
    }
    public void saveMaze(String path){
        model.saveMaze(path);
    }
}
