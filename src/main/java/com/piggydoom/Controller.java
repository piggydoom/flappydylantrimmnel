package com.piggydoom;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import java.util.Random;
import javafx.geometry.Bounds;

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

    Image dylanImg = new Image(getClass().getResource("/com/piggydoom/assets/dylan.png").toExternalForm());
    Image n0 = new Image(getClass().getResource("/com/piggydoom/assets/0.png").toExternalForm());
    Image n1 = new Image(getClass().getResource("/com/piggydoom/assets/1.png").toExternalForm());
    Image n2 = new Image(getClass().getResource("/com/piggydoom/assets/2.png").toExternalForm());
    Image n3 = new Image(getClass().getResource("/com/piggydoom/assets/3.png").toExternalForm());
    Image n4 = new Image(getClass().getResource("/com/piggydoom/assets/4.png").toExternalForm());
    Image n5 = new Image(getClass().getResource("/com/piggydoom/assets/5.png").toExternalForm());
    Image n6 = new Image(getClass().getResource("/com/piggydoom/assets/6.png").toExternalForm());
    Image n7 = new Image(getClass().getResource("/com/piggydoom/assets/7.png").toExternalForm());
    Image n8 = new Image(getClass().getResource("/com/piggydoom/assets/8.png").toExternalForm());
    Image n9 = new Image(getClass().getResource("/com/piggydoom/assets/9.png").toExternalForm());
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
    Circle hitbox = new Circle(200, 0, dImgH / 2);
    ImageView dylan = new ImageView(dylanImg);
    boolean gameStarted = false;
    int tickrate = 20;

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
    ObservableList<Image> numberSpriteArray = FXCollections.observableArrayList(n0, n1, n2, n3, n4, n5, n6, n7, n8, n9);

    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
        v -= 0.4;
        drawDylan(0.0, prevDY -= v);
        movePipes();

        for (Pipe pipe : pipesArray) {
            Rectangle lowerPipeHitbox = new Rectangle(pipe.currentX, pipe.lowerPipeTopY, pipeWidth,
                    pipe.lowerPipeHeight);
            Rectangle topPipeHitbox = new Rectangle(pipe.currentX, 0, pipeWidth, pipe.topPipeHeight);
            hitboxPane.getChildren().addAll(lowerPipeHitbox, topPipeHitbox);
            // System.out.println(lowerPipeHitbox);
            if (hitbox.getBoundsInParent().intersects(lowerPipeHitbox.getBoundsInParent())
                    || hitbox.getBoundsInParent().intersects(topPipeHitbox.getBoundsInParent())) {
                // System.out.println("collided");
            }
            ;
            hitboxPane.getChildren().remove(lowerPipeHitbox);
        }
    }));

    Timeline pipeTimeline = new Timeline(new KeyFrame(Duration.millis(tickrate * 63), event -> {
        createNewPipe();
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
        ctx.clearRect(200 - dImgW / 2, 0, dImgW, canvas.getHeight());
        ctx.save();
        ctx.translate(200, Ypos);
        ctx.rotate(-angle);
        ctx.drawImage(dylanImg, -dImgW / 2, dImgH / 2, dImgW, dImgH);
        hitbox.setCenterY(Ypos + dImgH);
        ctx.restore();
    }

    public void sketchBackground() {
        drawSky(0, canvasBG.getWidth());
        for (int i = 0; i < Math.ceil(canvasBG.getWidth() / GPS) * GPS; i += GPS) {
            drawGroundPixel(i);
        }

        drawDylan(0.0, (canvas.getHeight() / 3.5));
        updateScore();
        // dylan.setPreserveRatio(true);
        // dylan.setFitHeight(80);
        // hitbox.setCenterY(canvas.getHeight() / 3.5);
    }

    public void createNewPipe() {
        double gap = ThreadLocalRandom.current().nextDouble(140, 280);
        double lowerPipeHeight = ThreadLocalRandom.current().nextDouble(30, canvasP.getHeight() - gap - GPS);
        double lowerPipeTopY = canvasP.getHeight() - GPS - lowerPipeHeight;
        pipesArray.add(new Pipe(lowerPipeHeight, 10.0, canvasP.getWidth(), lowerPipeTopY, gap));
        ctxP.setFill(Color.LIMEGREEN);

        ctxP.fillRect(canvasP.getWidth(), lowerPipeTopY, pipeWidth, lowerPipeHeight);
        ctxP.fillRect(canvasP.getWidth(), 0, pipeWidth, lowerPipeTopY - gap);
    }

    public void jump() {
        if (v <= 0) {
            v = 3;
        }
        v += 3;
    }

    public void movePipes() {
        for (Pipe pipe : pipesArray) {
            if (pipe.currentX > -pipeWidth) {
                ctxP.clearRect(pipe.currentX, 0, pipeWidth, canvasP.getHeight() - GPS);

                ctxP.setFill(Color.LIMEGREEN);
                pipe.currentX -= 10;
                ctxP.fillRect(pipe.currentX, pipe.lowerPipeTopY, pipeWidth, pipe.lowerPipeHeight);
                ctxP.fillRect(pipe.currentX, 0, pipeWidth, pipe.lowerPipeTopY - pipe.gap);
            }

            if (pipe.currentX == 200 - pipeWidth) {
                updateScore();
            }

        }
        pipesArray.removeIf(pipe -> pipe.currentX <= -pipeWidth);
    }

    public void updateScore() {
        score += 1;
        scoreString = Integer.toString(score);
        // ctx.clearRect(0, 0, (scoreString.length() * 24) + scoreString.length() * scoreMargin, 41);
        // double drawNumX = scoreMargin;

        // for (int i = 0; i < scoreString.length(); i++) {
        //     // System.out.println(scoreString);

        //     int itterationIndex = Integer.parseInt(String.valueOf(scoreString.charAt(i)));
        //     System.out.println(scoreString);
        //     ctx.drawImage(numberSpriteArray.get(itterationIndex), drawNumX, scoreMargin);
        //     drawNumX += numberSpriteArray.get(itterationIndex).getWidth() + scoreMargin;
        // }
        scoreLabel.setText(scoreString);
    }


    // Source - https://stackoverflow.com/a/20979664
    // Posted by Stevantti
    // Retrieved 2026-04-22, License - CC BY-SA 3.0

    public static void showPopup() {
        Stage newStage = new Stage();
        VBox comp = new VBox();
        TextField nameField = new TextField("Name");
        TextField phoneNumber = new TextField("Phone Number");
        comp.getChildren().add(nameField);
        comp.getChildren().add(phoneNumber);

        Scene stageScene = new Scene(comp, 300, 300);
        newStage.setScene(stageScene);
        newStage.show();
    }

}
