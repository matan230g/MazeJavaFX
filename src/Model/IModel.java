package Model;

import javafx.scene.input.KeyCode;



public interface IModel {
    //Maze
    void generateMaze(int rows, int columns);
    void close();
    void moveCharacter(KeyCode movement) ;
    int[][] getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();




}
