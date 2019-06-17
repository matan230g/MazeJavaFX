package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Position;
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
    private final int GAME_BOARD_PADDING = 20;

    private IModel model;
    private boolean isCtrlPressed;
    private boolean mouseDragActive;
    private int wrapperWidth, wrapperHeight;

    public MyViewModel(IModel model) {
        this.model = model;
        mouseDragActive = false;
        isCtrlPressed = false;
        wrapperHeight = 900;
        wrapperWidth = 700;
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            //Notify my observer (View) that I have changed
            setChanged();
            notifyObservers();
        }
    }

    public boolean generateMaze(int rows, int cols) {
        if (rows < 3 || cols < 3 || rows > 100 || cols > 100)
            return false;
        if (rows % 2 == 0)
            rows--;
        if (cols % 2 == 0)
            cols--;

        model.generateMaze(rows, cols);
        return true;
    }

    public void solveMaze() {
        model.solveMaze();
    }

    public ArrayList<AState> getSolution() {
        return model.getSolution();
    }

    public void keyPressed(KeyCode code) {
        model.moveCharacter(code);
        if (code == KeyCode.CONTROL)
            isCtrlPressed = true;
    }

    public void keyReleased(KeyCode code) {
        if (code == KeyCode.CONTROL)
            isCtrlPressed = false;
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

    public void close() {
        model.close();
    }

    public void openFile(File file) {
        model.openFile(file);
    }

    public void saveMaze(String path) {
        model.saveMaze(path);
    }

    public void resetSolution() {
        model.resetSolution();
    }

    /**
     * @return 1 = right, -1 = left
     */
    public int getCharacterDirection() {
        return model.getCharacterDirection();
    }

    public boolean isCtrlPressed() {
        return isCtrlPressed;
    }

    public void mousePress(Position position) {
        if (getCharacterPositionRow() == position.getRowIndex() && getCharacterPositionColumn() == position.getColumnIndex())
            mouseDragActive = true;
        if (mouseDragActive)
            System.out.println("DRAG ACTIVE");
    }

    public void mouseRelease() {
        if (mouseDragActive)
            System.out.println("DRAG FINISHED");
        mouseDragActive = false;
    }


    /**
     * @return Is Drag active
     */
    public boolean mouseMove(Position position) {
        if (mouseDragActive) {
            if (position.getRowIndex() > getCharacterPositionRow())
                model.moveCharacter(KeyCode.NUMPAD2);
            if (position.getRowIndex() < getCharacterPositionRow())
                model.moveCharacter(KeyCode.NUMPAD8);
            if (position.getColumnIndex() < getCharacterPositionColumn())
                model.moveCharacter(KeyCode.NUMPAD4);
            if (position.getColumnIndex() > getCharacterPositionColumn())
                model.moveCharacter(KeyCode.NUMPAD6);
        }

        return mouseDragActive;
    }

    public int setWrapperWidth(int newValue) {
        wrapperWidth = newValue;

        return Math.min(wrapperWidth, wrapperHeight) - GAME_BOARD_PADDING;
    }

    public int setWrapperHeight(int newValue) {
        wrapperHeight = newValue;

        return Math.min(wrapperWidth, wrapperHeight) - GAME_BOARD_PADDING;
    }
}
