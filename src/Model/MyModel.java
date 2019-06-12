package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
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

    //<editor-fold desc="Servers">
    public void startServers() {

    }

    public void stopServers() {

    }
    //</editor-fold>

    //<editor-fold desc="Character">
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    //</editor-fold>

    //<editor-fold desc="Maze">
    private int[][] maze = { // a stub...
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1},
            {0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1},
            {1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1},
            {1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1},
            {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1},
            {1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1}
    };

    private int[][] generateRandomMaze(int width, int height) {
        MyMazeGenerator mg=new MyMazeGenerator();
        Maze m=mg.generate(width,height);

        return maze;
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    @Override
    public int[][] getMaze() {
        return maze;
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
        switch (movement) {
            case UP:
                characterPositionRow--;
                break;
            case DOWN:
                characterPositionRow++;
                break;
            case RIGHT:
                characterPositionColumn++;
                break;
            case LEFT:
                characterPositionColumn--;
                break;
            case HOME:
                characterPositionRow = 0;
                characterPositionColumn = 0;
        }
        setChanged();
        notifyObservers(/*Can forward an Object*/);
    }
    //</editor-fold>

}
