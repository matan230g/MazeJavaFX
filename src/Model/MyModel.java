package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import javafx.scene.input.KeyCode;

import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public class MyModel extends Observable implements IModel {

    private ExecutorService threadPool = Executors.newCachedThreadPool();


    public void startServers() {

    }

    public void stopServers() {

    }
    private Maze maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private int goalPositionRow;
    private int goalPositionColumn;



    private Maze generateRandomMaze(int width, int height) {
        MyMazeGenerator mg=new MyMazeGenerator();
         maze=mg.generate(width,height);
         characterPositionColumn=maze.getStartPosition().getColumnIndex();
         characterPositionRow=maze.getStartPosition().getRowIndex();
         goalPositionRow=maze.getGoalPosition().getRowIndex();
         goalPositionColumn=maze.getGoalPosition().getColumnIndex();

        return maze;
    }

    @Override
    public int[][] getMaze() {
        return maze.getMazeMatrix();
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    @Override
    public void close() {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Model Functionality">
    @Override
    public void generateMaze(int width, int height) {
        //Generate maze
        threadPool.execute(() -> {
            try {
                Thread.sleep(1000);
                maze = generateRandomMaze(width,height);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged(); //Raise a flag that I have changed
            notifyObservers(maze); //Wave the flag so the observers will notice
        });
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        int newRow = characterPositionRow;
        int newCol = characterPositionColumn;

        switch (movement) {
            case UP:
                newRow--;
                break;
            case DOWN:
                newRow++;
                break;
            case RIGHT:
                newCol++;
                break;
            case LEFT:
                newCol--;
                break;
            case HOME:
                newRow = 0;
                newCol = 0;
        }
        if(maze.checkIfPositionValid(new Position(newRow,newCol), Maze.EMPTY_TILE)) {
            characterPositionRow = newRow;
            characterPositionColumn = newCol;

            setChanged();
            notifyObservers();
        }
    }
}
