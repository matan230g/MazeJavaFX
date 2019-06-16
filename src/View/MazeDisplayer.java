package View;

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
    // Assets
    private Image wallImage;
    private Image characterImage;
    private Image goalImage;

    // Properties
    private boolean assetsLoaded;
    private int[][] maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private int goalPositionRow;
    private int goalPositionColumn;
    private ArrayList<AState> solution;

    public MazeDisplayer() {
        assetsLoaded = false;
    }

    private void loadAssets()
    {
        try {
            wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
            characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
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
        if(!assetsLoaded)
            loadAssets();
        if (maze != null) {
            GraphicsContext gc = getGraphicsContext2D();
            // Calculate dimensions
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = Math.floor(canvasHeight / maze.length);
            double cellWidth = Math.floor(canvasWidth / maze[0].length);

            gc.clearRect(0, 0, getWidth(), getHeight());

            //Draw
            drawMaze(gc, cellWidth, cellHeight);
            if (solution != null)
                drawSolution(gc, cellWidth, cellHeight);
            gc.drawImage(goalImage, goalPositionColumn * cellWidth, goalPositionRow * cellHeight, cellWidth, cellHeight);
            gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
        }
    }

    private void drawMaze(GraphicsContext gc, double cellWidth, double cellHeight) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == 1) {
                    gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
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
            gc.setFill(Color.GREEN);
            gc.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
        }
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameGoal = new SimpleStringProperty();

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public String getImageFileNameGoal() {
        return ImageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.ImageFileNameGoal.set(imageFileNameGoal);
    }
    //endregion

    public void changeSize() {
        this.setScaleX(150);
        this.setScaleY(150);
    }

    public void cleanDraw() {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
    }
}
