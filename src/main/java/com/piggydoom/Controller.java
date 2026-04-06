package com.piggydoom;

import java.io.IOException;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Random;

public class Controller {

    @FXML
    public Canvas canvas;

    @FXML 
    public StackPane stackPane;

    GraphicsContext ctx;
    Random rand = new Random();
    int GPS = 32; //Ground Pixel Size

    

    public void initialize(){
        ctx = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(stackPane.widthProperty());
        canvas.heightProperty().bind(stackPane.heightProperty());
        canvas.widthProperty().addListener(e -> resizeResponse());
        canvas.heightProperty().addListener(e -> resizeResponse());
    }

    public void resizeResponse(){
        for(int i = 0; i < Math.ceil(canvas.getWidth() / GPS) * GPS; i+= GPS){
            ctx.setFill(Color.hsb(30, 0.77, 0.09));
            ctx.fillRect(i, canvas.getHeight() - GPS, GPS, GPS);

                for(int groundTexture = rand.nextInt(6); groundTexture <= 6; groundTexture++){

                        int textureSize = rand.nextInt((GPS / 5 - 2) + 1) + 2;
                        int randomTextureX = rand.nextInt((i + GPS - i) + 1) + i;
                        double randomTextureY = canvas.getHeight() - rand.nextInt((GPS - 0) + 1);

                        int textureHue = rand.nextInt((45 - 28) + 1) + 28;
                        double textureSat = (44 + rand.nextInt(32)) / 100.0;
                        double textureBri = (12 + rand.nextInt(14)) / 100.0;
                        
                            ctx.setFill(Color.hsb(textureHue, textureSat, textureBri));
                            ctx.fillRect(randomTextureX , randomTextureY, textureSize, textureSize);
                }
        }    

        }
}
