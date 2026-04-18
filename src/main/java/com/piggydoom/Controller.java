package com.piggydoom;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import java.util.Random;

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

    Image dylanImg = new Image(getClass().getResource("/com/piggydoom/assets/dylan.png").toExternalForm());
    GraphicsContext ctx;
    GraphicsContext ctxBG;
    GraphicsContext ctxP;
    Random rand = new Random();
    int GPS = 32; // Ground Pixel Size
    int pipeWidth = 60;
    double dImgW = 84.45; // dylan img width
    double dImgH = 98.2; // dylan img height
    double threshold = 120.0;
    double v = 0; // velocity UPWARDS
    double prevDY = 0;

    public class Pipe {
        public double lowerPipeHeight;
        public double topPipeHeight;
        public double currentX;
        public double lowerPipeTopY;

        public Pipe(double lowerPipeHeight, double topPipeHeight, double currentX, double lowerPipeTopY) {
            this.lowerPipeHeight = lowerPipeHeight;
            this.topPipeHeight = topPipeHeight;
            this.currentX = currentX;
            this.lowerPipeTopY = lowerPipeTopY;
        };

        @Override
        public String toString() {
            return "Pipe{" +
                    "lowerPipeHeight=" + lowerPipeHeight +
                    ", topPipeHeight=" + topPipeHeight +
                    ", currentX=" + currentX +
                    ", lowerPipeTopY=" + lowerPipeTopY +
                    '}';
        }
    }

    ObservableList<Pipe> pipesArray = FXCollections.observableArrayList();

    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
        v -= 0.4;
        drawDylan(0.0, prevDY -= v);
        movePipes();
    }));

    public void initialize() {
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
            timeline.play();
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
        ctxBG.setFill(Color.hsb(30, 0.77, 0.09));
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
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        ctx.save();
        ctx.translate(200, Ypos);
        ctx.rotate(-angle);
        ctx.drawImage(dylanImg, -dImgW / 2, dImgH / 2, dImgW, dImgH);
        prevDY = Ypos;
        ctx.restore();
    }

    public void sketchBackground() {
        drawSky(0, canvasBG.getWidth());
        for (int i = 0; i < Math.ceil(canvasBG.getWidth() / GPS) * GPS; i += GPS) {
            drawGroundPixel(i);
        }

        drawDylan(0.0, (canvas.getHeight() / 3.5));
    }

    public void createNewPipe() {
        double lowerPipeHeight = ThreadLocalRandom.current().nextDouble(30, canvasP.getHeight() - threshold - GPS);
        double lowerPipeTopY = canvasP.getHeight() - GPS - lowerPipeHeight;
        pipesArray.add(new Pipe(lowerPipeHeight, 10.0, canvasP.getWidth(), lowerPipeTopY));
        ctxP.setFill(Color.LIMEGREEN);

        ctxP.fillRect(canvasP.getWidth(), lowerPipeTopY, pipeWidth, lowerPipeHeight);
        ctxP.fillRect(canvasP.getWidth(), 0, pipeWidth, lowerPipeTopY - threshold);
    }

    public void jump() {
        if (v <= 0) {
            v = 3;
        }
        v += 3;
    }

    public void movePipes() {
        for (Pipe pipe : pipesArray) {
            ctxP.clearRect(pipe.currentX, 0, pipeWidth, canvasP.getHeight() - GPS);

            ctxP.setFill(Color.LIMEGREEN);
            pipe.currentX -= 10;
            ctxP.fillRect(pipe.currentX, pipe.lowerPipeTopY, pipeWidth, pipe.lowerPipeHeight);
            ctxP.fillRect(pipe.currentX, 0, pipeWidth, pipe.lowerPipeTopY - threshold);
        }
    }
}
