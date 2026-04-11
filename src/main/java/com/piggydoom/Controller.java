package com.piggydoom;

import java.io.IOException;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import java.util.Random;

public class Controller {

    @FXML
    public Canvas canvasBG;

    @FXML
    public Canvas canvas;

    @FXML
    public StackPane stackPane;

    Image dylanImg = new Image(getClass().getResource("/com/piggydoom/assets/dylan.png").toExternalForm());
    GraphicsContext ctx;
    GraphicsContext ctxBG;
    Random rand = new Random();
    int GPS = 32; // Ground Pixel Size
    double dImgW = 84.45;
    double dImgH = 98.2;
    double v = 0; // velocity UPWARDS
    double prevDY = 0;

    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
        v -= 0.4;
        drawDylan(0.0, prevDY -= v);

    }));

    public void initialize() {
        ctx = canvas.getGraphicsContext2D();
        ctxBG = canvasBG.getGraphicsContext2D();

        canvas.widthProperty().bind(stackPane.widthProperty());
        canvas.heightProperty().bind(stackPane.heightProperty());
        canvasBG.widthProperty().bind(stackPane.widthProperty());
        canvasBG.heightProperty().bind(stackPane.heightProperty());
        javafx.application.Platform.runLater(() -> {
            sketchBackground();
            var scene = stackPane.getScene();
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        });
    }

    public void drawSky() {
        double b = 0.90;
        for (int i = 0; i < canvasBG.getHeight() - GPS; i += GPS / 3) {
            ctxBG.setFill(Color.hsb(205, 0.65, b));
            ctxBG.fillRect(0, i, canvasBG.getWidth(), i + GPS / 3);
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
        drawSky();
        for (int i = 0; i < Math.ceil(canvasBG.getWidth() / GPS) * GPS; i += GPS) {
            drawGroundPixel(i);
        }

        drawDylan(0.0, (canvas.getHeight() / 3.5));
    }

    public void jump() {
        if (v <= 0) {
            v = 3;
        }
        v += 3;
    }

}
