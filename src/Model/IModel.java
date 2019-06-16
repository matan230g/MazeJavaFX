package Model;

import algorithms.search.AState;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.ArrayList;


public interface IModel {
    //Maze
    void generateMaze(int rows, int columns);
    void close();
    void moveCharacter(KeyCode movement) ;
    int[][] getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();

    int getGoalPositionRow();
    int getGoalPositionColumn();

    boolean isFinished();

    void solveMaze();
    ArrayList<AState> getSolution();

    void openFile(File file);
    void saveMaze(String path);

    void resetSolution();

    int getCharacterDirection();
}
