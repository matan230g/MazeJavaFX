package Model;

import Client.Client;
import Client.IClientStrategy;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import IO.MyDecompressorInputStream;
import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import test.RunCommunicateWithServers;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
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

    Server mazeGeneratingServer;
    Server solveSearchProblemServer;
    public void startServers() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
    }

    public void stopServers() {
        solveSearchProblemServer.stop();
        mazeGeneratingServer.stop();
    }

    private Maze maze;
    private int characterPositionRow;
    private int characterPositionColumn;

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
    public int getGoalPositionRow() {
        return maze.getGoalPosition().getRowIndex();
    }

    @Override
    public int getGoalPositionColumn() {
        return maze.getGoalPosition().getColumnIndex();
    }

    @Override
    public boolean isFinished() {
        return characterPositionColumn == maze.getGoalPosition().getColumnIndex() && characterPositionRow == maze.getGoalPosition().getRowIndex();
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

    @Override
    public void generateMaze(int rows, int cols) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[3000];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    setChanged(); //Raise a flag that I have changed
                    notifyObservers(maze); //Wave the flag so the observers will notice
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e2) {
            e2.printStackTrace();
        }
    }
    ArrayList<AState> mazeSolutionSteps = null;
    @Override
    public void solveMaze() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        maze.setStartPosition(new Position(characterPositionRow,characterPositionColumn));
                        toServer.writeObject(maze);
                        toServer.flush();
                        Solution mazeSolution = (Solution)fromServer.readObject();
                        System.out.println(String.format("Solution steps: %s", mazeSolution));
                        mazeSolutionSteps = mazeSolution.getSolutionPath();

                        setChanged();
                        notifyObservers();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public ArrayList<AState> getSolution() {
        return mazeSolutionSteps;
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
        if (maze.checkIfPositionValid(new Position(newRow, newCol), Maze.EMPTY_TILE)) {
            characterPositionRow = newRow;
            characterPositionColumn = newCol;
            setChanged();
            notifyObservers();
        }
    }
}
