package com.piggydoom;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;

public class Controller {

    // injections, declarations & constants
    @FXML
    public Canvas canvasBG;
    @FXML
    public Canvas canvas;
    @FXML
    public Canvas canvasP;
    @FXML
    public StackPane stackPane;
    @FXML
    public Pane hitboxPane;
    @FXML
    public Label scoreLabel;
    @FXML
    public VBox gameOverCard;
    @FXML
    public ProgressBar replayProBar;
    @FXML
    public Label gameOverCardScore;

    Image dylanImg = new Image(getClass().getResource("/com/piggydoom/assets/dylan.png").toExternalForm());
    GraphicsContext ctx;
    GraphicsContext ctxBG;
    GraphicsContext ctxP;
    Random rand = new Random();
    int GPS = 32; // Ground Pixel Size
    int pipeWidth = 60;
    double dImgW = 84.45; // dylan img width
    double dImgH = 98.2; // dylan img height
    double v = 0; // velocity UPWARDS
    double prevDY = 0;
    double scoreMargin = 3.5;
    int score = -1;
    String scoreString;
    Circle hitbox = new Circle(200, 0, dImgH / 2.3);
    ImageView dylan = new ImageView(dylanImg);
    boolean gameStarted = false;
    int tickrate = 20;
    double replayTimerMs = 1200;
    boolean gameOverFlag = true;
    boolean sessionBegan = false;
    double pipePixelSize = pipeWidth / 8;
    boolean cooldown = false;

    public class Pipe {
        public double lowerPipeHeight;
        public double topPipeHeight;
        public double currentX;
        public double lowerPipeTopY;
        public double gap;

        public Pipe(double lowerPipeHeight, double topPipeHeight, double currentX, double lowerPipeTopY, double gap) {
            this.lowerPipeHeight = lowerPipeHeight;
            this.topPipeHeight = topPipeHeight;
            this.currentX = currentX;
            this.lowerPipeTopY = lowerPipeTopY;
            this.gap = gap;
        };

        @Override
        public String toString() {
            return "Pipe{" +
                    "lowerPipeHeight=" + lowerPipeHeight +
                    ", topPipeHeight=" + topPipeHeight +
                    ", currentX=" + currentX +
                    ", lowerPipeTopY=" + lowerPipeTopY +
                    ", gap=" + gap +
                    '}';
        }
    }

    ObservableList<Pipe> pipesArray = FXCollections.observableArrayList();

    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(tickrate), event -> {
        v -= 0.6;
        drawDylan(0.0, prevDY -= v);
        movePipes();
        moveBackground();

        if (gameOverFlag) {
            gameOver();
        }

        for (Pipe pipe : pipesArray) {
            Rectangle lowerPipeHitbox = new Rectangle(pipe.currentX, pipe.lowerPipeTopY, pipeWidth,
                    pipe.lowerPipeHeight);
            Rectangle topPipeHitbox = new Rectangle(pipe.currentX, 0, pipeWidth, pipe.topPipeHeight);
            hitboxPane.getChildren().add(lowerPipeHitbox);
            hitboxPane.getChildren().add(topPipeHitbox);
            if (hitbox.getBoundsInParent().intersects(lowerPipeHitbox.getBoundsInParent())
                    || hitbox.getBoundsInParent().intersects(topPipeHitbox.getBoundsInParent())
                    || prevDY > canvas.getHeight() || prevDY < -dImgH - GPS) {
                gameOverFlag = true;

            }
            ;
            hitboxPane.getChildren().remove(topPipeHitbox);
            hitboxPane.getChildren().remove(lowerPipeHitbox);

        }
    }));

    Timeline pipeTimeline = new Timeline(new KeyFrame(Duration.millis(tickrate * 63), event -> {
        createNewPipe(canvasP.getWidth());
    }));

    public void initialize() {
        hitboxPane.getChildren().add(hitbox);

        ctx = canvas.getGraphicsContext2D();
        ctxBG = canvasBG.getGraphicsContext2D();
        ctxP = canvasP.getGraphicsContext2D();

        canvas.widthProperty().bind(stackPane.widthProperty());
        canvas.heightProperty().bind(stackPane.heightProperty());
        canvasBG.widthProperty().bind(stackPane.widthProperty());
        canvasBG.heightProperty().bind(stackPane.heightProperty());
        canvasP.widthProperty().bind(stackPane.widthProperty());
        canvasP.heightProperty().bind(stackPane.heightProperty());
        javafx.application.Platform.runLater(() -> {
            sketchBackground();
            timeline.setCycleCount(Animation.INDEFINITE);
            pipeTimeline.setCycleCount(Animation.INDEFINITE);

        });
    }

    public void drawSky(double posX, double width) {
        double b = 0.90;
        for (int i = 0; i < canvasBG.getHeight() - GPS; i += GPS / 3) {
            ctxBG.setFill(Color.hsb(205, 0.65, b));
            ctxBG.fillRect(posX, i, width, i + GPS / 3);
            b -= (0.90 - 0.55) / ((canvasBG.getHeight() - GPS) / (GPS / 3));
        }
    }

    public void drawGroundPixel(Integer drawX) {
        ctxBG.setFill(Color.hsb(30, 0.77, 0.25));
        ctxBG.fillRect(drawX, canvasBG.getHeight() - GPS, GPS, GPS);
        for (int groundTexture = rand.nextInt(6); groundTexture <= 6; groundTexture++) {

            int textureSize = rand.nextInt((GPS / 5 - 2) + 1) + 2;
            int randomTextureX = Math.round(rand.nextInt((drawX + GPS - drawX) + 1) + drawX);
            double randomTextureY = Math.round(canvasBG.getHeight() - rand.nextInt((GPS - 0) + 1));

            int textureHue = rand.nextInt((45 - 28) + 1) + 28;
            double textureSat = (44 + rand.nextInt(32)) / 100.0;
            double textureBri = (12 + rand.nextInt(14)) / 100.0;

            ctxBG.setFill(Color.hsb(textureHue, textureSat, textureBri));
            ctxBG.fillRect(randomTextureX, randomTextureY, textureSize, textureSize);
        }
    }

    public void drawDylan(Double angle, Double Ypos) {
        ctx.clearRect(200 - dImgW / 2, 0, dImgW, canvas.getHeight());
        ctx.save();
        ctx.translate(200, Ypos);
        ctx.rotate(-angle);
        ctx.drawImage(dylanImg, -dImgW / 2, dImgH / 2, dImgW, dImgH);
        hitbox.setCenterY(Ypos + dImgH);
        ctx.restore();
    }

    public void sketchBackground() {
        ctxP.clearRect(0, 0, canvasP.getWidth(), canvasP.getHeight());
        drawSky(0, canvasBG.getWidth());
        for (int i = 0; i < Math.ceil(canvasBG.getWidth() / GPS) * GPS; i += GPS) {
            drawGroundPixel(i);
        }
        createNewPipe(600);
        prevDY = (canvas.getHeight() / 3.5);
        drawDylan(0.0, prevDY);
        updateScore();
    }

    public void drawPipe(double x, double y, double w, double h) {
        double pipeBri = 0.6;
        ctxP.setFill(Color.BLACK);
        ctxP.fillRect(x, y, pipePixelSize, h);
        for (double i = x + pipePixelSize; i < x + w - pipePixelSize; i += pipePixelSize) {
            ctxP.setFill(Color.hsb(120.0, 1, pipeBri));
            ctxP.fillRect(i, y, pipePixelSize, h);
            pipeBri += 0.05;
        }
        ctxP.setFill(Color.BLACK);
        ctxP.fillRect(x + pipeWidth - pipePixelSize, y, pipePixelSize, h);

        // ctxP.fillRect(x, y, w, h);
    }

    public void createNewPipe(double x) {
        double gap = ThreadLocalRandom.current().nextDouble(140, 280);
        double lowerPipeHeight = ThreadLocalRandom.current().nextDouble(30, canvasP.getHeight() - gap - GPS);
        double lowerPipeTopY = canvasP.getHeight() - GPS - lowerPipeHeight;
        double topPipeHeight = lowerPipeTopY - gap;
        pipesArray.add(new Pipe(lowerPipeHeight, topPipeHeight, x, lowerPipeTopY, gap));
        ctxP.setFill(Color.LIMEGREEN);

        drawPipe(x, lowerPipeTopY, pipeWidth, lowerPipeHeight);
        drawPipe(x, 0, pipeWidth, topPipeHeight);
    }

    public void jump() {
        if (v <= 0) {
            v = 3;
        }
        v += 5;
    }

    public void movePipes() {
        for (Pipe pipe : pipesArray) {
            if (pipe.currentX > -pipeWidth) {
                ctxP.clearRect(pipe.currentX, 0, pipeWidth, canvasP.getHeight() - GPS);

                ctxP.setFill(Color.LIMEGREEN);
                pipe.currentX -= 10;
                drawPipe(pipe.currentX, pipe.lowerPipeTopY, pipeWidth, pipe.lowerPipeHeight);
                drawPipe(pipe.currentX, 0, pipeWidth, pipe.lowerPipeTopY - pipe.gap);
            }

            if (pipe.currentX == 200 - pipeWidth) {
                updateScore();
            }

        }
        pipesArray.removeIf(pipe -> pipe.currentX <= -pipeWidth);
    }

    public void moveBackground() {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Rectangle2D viewport = new Rectangle2D(0, canvasBG.getHeight() - GPS, canvasBG.getWidth() + GPS, GPS);
        params.setViewport(viewport);
        WritableImage selectedImage = canvasBG.snapshot(params, null);

        ctxBG.drawImage(selectedImage, -10, canvasBG.getHeight() - GPS);
        drawGroundPixel((int) canvasBG.getWidth() - 10);
    }

    public void updateScore() {
        score += 1;
        scoreString = Integer.toString(score);
        scoreLabel.setText(scoreString);
    }

    public void gameOver() {
        cooldown = true;
        gameStarted = false;
        timeline.stop();
        pipeTimeline.stop();
        pipesArray.clear();
        gameOverCard.setVisible(true);
        gameOverCardScore.setText(scoreString);
        v = 0;
        replayProBar.setProgress(0);

        KeyValue keyValue = new KeyValue(replayProBar.progressProperty(), 1.0);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(replayTimerMs), keyValue);
        Timeline probarTimeline = new Timeline(keyFrame);
        probarTimeline.setCycleCount(1);
        probarTimeline.play();
        probarTimeline.setOnFinished(event -> {
            cooldown = false;
        });

    }
}
