package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyCompressorOutputStream;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.Configurations;
import algorithms.mazeGenerators.Maze;

import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import java.util.Observable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Aviadjo on 6/14/2017.
 */
public class MyModel extends Observable implements IModel {
    private static final Logger LOG = LogManager.getLogger(MyModel.class);

    private ExecutorService threadPool;

    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    private ArrayList<AState> mazeSolutionSteps = null;

    private Maze maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private int characterDirection = 1;

    public MyModel() {
        threadPool = Executors.newCachedThreadPool();
    }

    public void startServers() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        LOG.debug("Starting maze generation server");
        mazeGeneratingServer.start();
        LOG.debug("Starting maze solution server");
        solveSearchProblemServer.start();
    }

    private void stopServers() {
        solveSearchProblemServer.stop();
        mazeGeneratingServer.stop();
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
    public int getGoalPositionRow() {
        return maze.getGoalPosition().getRowIndex();
    }

    @Override
    public int getGoalPositionColumn() {
        return maze.getGoalPosition().getColumnIndex();
    }

    @Override
    public boolean isFinished() {
        if (maze == null)
            return false;

        return characterPositionColumn == maze.getGoalPosition().getColumnIndex() && characterPositionRow == maze.getGoalPosition().getRowIndex();
    }

    @Override
    public void close() {
        LOG.debug("Closing instance");
        stopServers();
        threadPool.shutdown();
        LogManager.shutdown();
        Platform.exit();
    }

    @Override
    public void generateMaze(int rows, int cols) {
        LOG.debug("Generating maze");
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                private final Logger SERVER_LOG = LogManager.getLogger("ServerLog");

                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        String generatorName = Configurations.getMazeGenerator().getClass().getName();
                        SERVER_LOG.info(String.format("Generating maze of size [%d,%d], using algorithm [%s]", rows, cols, generatorName.substring(generatorName.lastIndexOf('.') + 1)));
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[rows * cols + 100];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        characterPositionColumn = maze.getStartPosition().getColumnIndex();
                        characterPositionRow = maze.getStartPosition().getRowIndex();
                        SERVER_LOG.info(String.format("Maze generated. Start position: %s, Goal position: %s", maze.getStartPosition(), maze.getGoalPosition()));
                    } catch (Exception e) {
                        LOG.catching(e);
                    }

                    setChanged(); //Raise a flag that I have changed
                    notifyObservers(maze); //Wave the flag so the observers will notice
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e2) {
            LOG.catching(e2);
        }
    }

    @Override
    public void solveMaze() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                private final Logger SERVER_LOG = LogManager.getLogger("ServerLog");

                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        maze.setStartPosition(new Position(characterPositionRow, characterPositionColumn));
                        SERVER_LOG.info(String.format("Solving maze of size [%d,%d], using algorithm [%s]", maze.getNumOfRows(), maze.getNumOfColumns(), Configurations.getSearchingAlgorithm().getName()));
                        toServer.writeObject(maze);
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject();
                        SERVER_LOG.info(String.format("Found solution of size [%d]", mazeSolution.getSolutionPath().size()));
                        System.out.println(String.format("Solution steps: %s", mazeSolution));
                        mazeSolutionSteps = mazeSolution.getSolutionPath();

                        setChanged();
                        notifyObservers();
                    } catch (Exception e) {
                        LOG.catching(e);
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e2) {
            LOG.catching(e2);
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
            case NUMPAD8:
                newRow--;
                updatePosition(newRow, newCol);
                break;
            case NUMPAD2:
                newRow++;
                updatePosition(newRow, newCol);
                break;
            case NUMPAD6:
                newCol++;
                updatePosition(newRow, newCol);
                break;
            case NUMPAD4:
                newCol--;
                updatePosition(newRow, newCol);
                break;

            case NUMPAD9:
                newCol++;
                updatePosition(newRow, newCol);
                newRow--;
                updatePosition(newRow, newCol);
                break;

            case NUMPAD7:
                newCol--;
                updatePosition(newRow, newCol);
                newRow--;
                updatePosition(newRow, newCol);
                break;

            case NUMPAD1:
                newCol--;
                updatePosition(newRow, newCol);
                newRow++;
                updatePosition(newRow, newCol);
                break;

            case NUMPAD3:
                newCol++;
                updatePosition(newRow, newCol);
                newRow++;
                updatePosition(newRow, newCol);
                break;
            case HOME:
                newRow = 0;
                newCol = 0;
                updatePosition(newRow, newCol);
        }

        setChanged();
        notifyObservers();
    }

    private void updatePosition(int newRow, int newCol) {
        if (maze.checkIfPositionValid(new Position(newRow, newCol), Maze.EMPTY_TILE)) {
            // Update solution
            updateSolution(newRow, newCol);

            // Update direction
            if (newCol > characterPositionColumn)
                characterDirection = 1;
            if (newCol < characterPositionColumn)
                characterDirection = -1;

            // Update position
            characterPositionRow = newRow;
            characterPositionColumn = newCol;
        }
    }

    void updateSolution(int row, int col) {
        if (mazeSolutionSteps == null)
            return;
        int currentIndex = -1;
        boolean advanced = false;

        for (int i = 1; i < mazeSolutionSteps.size(); i++) // Find current solution index
        {
            MazeState current = (MazeState) mazeSolutionSteps.get(i);
            MazeState previous = (MazeState) mazeSolutionSteps.get(i - 1);
            // Check if above step
            if (row == current.getPosition().getRowIndex() && col == current.getPosition().getColumnIndex()) {
                currentIndex = i;
                advanced = true;
                break;
            }

            // Check if between steps
            if (row == previous.getPosition().getRowIndex() && col == current.getPosition().getColumnIndex() ||
                    row == current.getPosition().getRowIndex() && col == previous.getPosition().getColumnIndex()) {
                currentIndex = i;
                advanced = true;
                break;
            }
        }

        while (currentIndex-- > 0)
            mazeSolutionSteps.remove(0);

        if (!advanced)
            addToSolution(row, col);
    }

    private void addToSolution(int row, int col) {
        MazeState newState = new MazeState(new Position(row, col));
        ArrayList<AState> newSolution = new ArrayList<>();
        newSolution.add(newState);
        newSolution.addAll(mazeSolutionSteps);
        mazeSolutionSteps = newSolution;
    }

    @Override
    public void openFile(File file) {
        if (file == null)
            return;

        LOG.info("Opening file - " + file.getPath());
        byte[] savedMazeBytes = new byte[10000];
        try {
            FileInputStream loadFile = new FileInputStream(file);
            InputStream in = new MyDecompressorInputStream(loadFile);
            in.read(savedMazeBytes);
            in.close();
            maze = new Maze(savedMazeBytes);
            characterPositionColumn = maze.getStartPosition().getColumnIndex();
            characterPositionRow = maze.getStartPosition().getRowIndex();
            setChanged();
            notifyObservers();
        } catch (IOException var7) {
            var7.printStackTrace();
        }
    }

    @Override
    public void saveMaze(String path) {
        LOG.info("Saving file - " + path);
        try {
            OutputStream out = new MyCompressorOutputStream(new FileOutputStream(path));
            out.write(maze.toByteArray());
            out.flush();
            out.close();
        } catch (IOException var8) {
            LOG.catching(var8);
        }
    }

    @Override
    public void resetSolution() {
        mazeSolutionSteps = null;
    }

    @Override
    public int getCharacterDirection() {
        return characterDirection;
    }
}
