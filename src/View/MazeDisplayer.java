package View;

import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas {
    private final double ZOOM_FACTOR = 0.2;
    // Asset Properties
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty imageFileNameSolution = new SimpleStringProperty();
    private StringProperty ImageFileNameGoal = new SimpleStringProperty();
    private Image wallImage;
    private Image characterImage;
    private Image goalImage;
    private Image solutionImage;

    // Calculation Properties
    private boolean assetsLoaded;
    private int[][] maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private int goalPositionRow;
    private int goalPositionColumn;
    private ArrayList<AState> solution;
    private double zoom = 1;
    private double cellWidth, cellHeight;
    private double translateX, translateY;
    private int characterDirection = 1;

    public MazeDisplayer() {
        assetsLoaded = false;
    }

    private void loadAssets() {
        try {
            characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
            wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
            solutionImage = new Image(new FileInputStream(imageFileNameSolution.get()));
            goalImage = new Image(new FileInputStream(ImageFileNameGoal.get()));
            assetsLoaded = true;
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }
    }

    public void setMaze(int[][] maze) {
        this.maze = maze;
        redraw();
    }

    public void setSolution(ArrayList<AState> solution) {
        this.solution = solution;
    }

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw();
    }

    public void setGoalPosition(int row, int column) {
        goalPositionRow = row;
        goalPositionColumn = column;
        redraw();
    }

    public void redraw() {
        if (!assetsLoaded)
            loadAssets();
        if (maze != null) {
            GraphicsContext gc = getGraphicsContext2D();
            // Calculate dimensions
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            cellHeight = 1 * 1 *(canvasHeight / maze.length * zoom);
            cellWidth = 1 * 1 *(canvasWidth / maze[0].length * zoom);

            translateX = calculateTranslate(canvasWidth, cellWidth, characterPositionColumn);
            translateY = calculateTranslate(canvasHeight, cellHeight, characterPositionRow);

            gc.clearRect(0, 0, getWidth(), getHeight());

            //Draw
            drawMaze(gc, cellWidth, cellHeight);
            if (solution != null)
                drawSolution(gc, cellWidth, cellHeight);
            gc.drawImage(goalImage, goalPositionColumn * cellWidth + translateX, goalPositionRow * cellHeight + translateY, cellWidth, cellHeight);
            double characterOffsetX = characterDirection < 0 ? cellWidth : 0;
            gc.drawImage(characterImage, characterPositionColumn * cellWidth + translateX + characterOffsetX, characterPositionRow * cellHeight + translateY, cellWidth * characterDirection, cellHeight);
        }
    }

    private double calculateTranslate(double canvasSize, double cellSize, int characterPositionTile) {
        double characterPosition = characterPositionTile * cellSize + cellSize * 0.5; // Center of character
        double translate = -characterPosition + canvasSize / 2;
        double zoomedCanvasSize = canvasSize * zoom;
        double overflow = -zoomedCanvasSize - translate + canvasSize;
        if (overflow > 0)
            translate += overflow;

        if (translate > 0)
            translate = 0;

        return 1 * 1 *(translate);
    }

    private void drawMaze(GraphicsContext gc, double cellWidth, double cellHeight) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == 1) {
                    gc.drawImage(wallImage, j * cellWidth + translateX, i * cellHeight + translateY, cellWidth, cellHeight);
                } else {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(j * cellWidth + translateX, i * cellHeight + translateY, cellWidth, cellHeight);
                }
            }
        }
    }

    private void drawSolution(GraphicsContext gc, double cellWidth, double cellHeight) {
        for (AState state : solution) {
            int row, col;
            MazeState m = (MazeState) state;
            row = m.getPosition().getRowIndex();
            col = m.getPosition().getColumnIndex();
            if (row == goalPositionRow && col == goalPositionColumn || row == characterPositionRow && col == characterPositionColumn)
                continue; // Don't draw above character or goal

            gc.drawImage(solutionImage, col * cellWidth + translateX, row * cellHeight + translateY, cellWidth, cellHeight);
        }
    }

    public void cleanDraw() {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
    }

    public void resetZoom() {
        zoom = 1;
    }

    public void changeZoom(int delta) {
        double previousZoom = zoom;
        zoom += delta * ZOOM_FACTOR;

        if (zoom < 1)
            zoom = 1;

        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        cellHeight = 1 * 1 *(canvasHeight / maze.length * zoom);
        cellWidth = 1 * 1 *(canvasWidth / maze[0].length * zoom);

        if (cellWidth > 80 || cellHeight > 80)
            zoom = previousZoom; // Revert

        if (zoom != previousZoom)
            redraw();
    }

    public void setCharacterDirection(int characterDirection) {
        this.characterDirection = characterDirection;
    }

    public Position projectClickToTile(double x, double y) {
        int col = (int) ((x - translateX) / cellWidth);
        int row = (int) ((y - translateY) / cellHeight);
        return new Position(row, col);
    }

    //region Getters and Setters
    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public StringProperty imageFileNameCharacterProperty() {
        return ImageFileNameCharacter;
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public StringProperty imageFileNameWallProperty() {
        return ImageFileNameWall;
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameSolution() {
        return imageFileNameSolution.get();
    }

    public StringProperty imageFileNameSolutionProperty() {
        return imageFileNameSolution;
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.imageFileNameSolution.set(imageFileNameSolution);
    }

    public String getImageFileNameGoal() {
        return ImageFileNameGoal.get();
    }

    public StringProperty imageFileNameGoalProperty() {
        return ImageFileNameGoal;
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.ImageFileNameGoal.set(imageFileNameGoal);
    }
    //endregion
}
