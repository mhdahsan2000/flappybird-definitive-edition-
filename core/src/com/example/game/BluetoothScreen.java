package com.example.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class BluetoothScreen implements Screen{

    final MainGame gameSession;
    SpriteBatch group;
    Texture ScreenPicture;

    Texture Endsession;

    Texture[] birdPositions;
    Circle birdreach;
    int flappingstate = 0;
    float Objectbird = 0;

    float speedgame = 0;
    int points = 0;
    int pointsway = 0;
    BitmapFont Valuefnt;
    float dropvalue = 2;
    int finalstate = 0;

    Texture upperblock;
    Texture lowerblock;
    Random rndmvalue;
    Texture gamepad;

    float blockspace = 700;
    float maxBlockOst;
    float speedblock = 4;
    int blockcount = 4;
    float[] exclsblock = new float[blockcount];
    float[] blockost = new float[blockcount];
    float gapReach;

    int uploadCount;

    Rectangle[] UpperObjects;
    Rectangle[] LowerObjects;

    public BluetoothScreen(final MainGame maingame) {
        gameSession = maingame;

        // Get the current location of the device in latitude and longitude.
        gameSession.LLI.getLastLocation();

        // Gets the weather data from weather API.
        String weather = gameSession.WI.getWeather(gameSession.LLI.getLatitude(), gameSession.LLI.getLongitude());

        // Initialize all variables.
        group = new SpriteBatch();
        ScreenPicture = new Texture(gameSession.WI.setBackground(weather));
        Endsession = new Texture("gameover.png");
        //shapeRenderer = new ShapeRenderer();
        birdreach = new Circle();
        Valuefnt = new BitmapFont();
        Valuefnt.setColor(Color.WHITE);
        Valuefnt.getData().setScale(10);

        birdPositions = new Texture[2];
        birdPositions[0] = new Texture("bird.png");
        birdPositions[1] = new Texture("bird1.png");


        upperblock = new Texture("toptube.png");
        lowerblock = new Texture("bottomtube.png");
        gamepad = new Texture("gamepad.png");
        maxBlockOst = Gdx.graphics.getHeight() / 2 - blockspace / 2 - 100;
        rndmvalue = new Random();
        gapReach = Gdx.graphics.getWidth() * 3 / 4;
        UpperObjects = new Rectangle[blockcount];
        LowerObjects = new Rectangle[blockcount];

        uploadCount = 0;

        startGame();
    }

    public void startGame() {

        Objectbird = Gdx.graphics.getHeight() / 2 - birdPositions[0].getHeight() / 2;

        for (int i = 0; i < blockcount; i++) {

            blockost[i] = (rndmvalue.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - blockspace - 200);

            exclsblock[i] = Gdx.graphics.getWidth() / 2 - upperblock.getWidth() / 2 + Gdx.graphics.getWidth() + i * gapReach;

            UpperObjects[i] = new Rectangle();
            LowerObjects[i] = new Rectangle();

        }
    }

    @Override
    public void render (float delta) {

        group.begin();
        group.draw(ScreenPicture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // If the game is running.
        if (finalstate == 1) {
            // the
            if (exclsblock[pointsway] < Gdx.graphics.getWidth() / 2) {

                // Score points
                points++;

                Gdx.app.log("Score", String.valueOf(points));

                if (pointsway < blockcount - 1) {

                    pointsway++;

                } else {

                    pointsway = 0;

                }

            }
            if (gameSession.buttonOn == true) {

                speedgame = -30;
                // Set the button pressed state to false
                gameSession.buttonOn = false;

            }

            for (int i = 0; i < blockcount; i++) {

                if (exclsblock[i] < - upperblock.getWidth()) {

                    exclsblock[i] += blockcount * gapReach;
                    blockost[i] = (rndmvalue.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - blockspace - 200);

                } else {

                    exclsblock[i] = exclsblock[i] - speedblock;



                }
                group.draw(upperblock, exclsblock[i], Gdx.graphics.getHeight() / 2 + blockspace / 2 + blockost[i]);
                group.draw(lowerblock, exclsblock[i], Gdx.graphics.getHeight() / 2 - blockspace / 2 - lowerblock.getHeight() + blockost[i]);

                UpperObjects[i] = new Rectangle(exclsblock[i], Gdx.graphics.getHeight() / 2 + blockspace / 2 + blockost[i], upperblock.getWidth(), upperblock.getHeight());
                LowerObjects[i] = new Rectangle(exclsblock[i], Gdx.graphics.getHeight() / 2 - blockspace / 2 - lowerblock.getHeight() + blockost[i], lowerblock.getWidth(), lowerblock.getHeight());
            }

            if (Objectbird > 0) {

                speedgame = speedgame + dropvalue;
                Objectbird -= speedgame;

            } else {

                finalstate = 2;

            }

            // if the game is not started.
        } else if (finalstate == 0) {

            if (gameSession.buttonOn == true) {

                finalstate = 1;
                // Set the button pressed state to false
                gameSession.buttonOn = false;

            }

            // if it's gameover
        } else if (finalstate == 2) {

            // Update the score if the game has not updated.
            gameSession.FI.updateData(points, uploadCount);

            // Change the status to 'updated'
            uploadCount = 1;

            // Draw the gameover texture.
            group.draw(Endsession, Gdx.graphics.getWidth() / 2 - Endsession.getWidth() / 2, Gdx.graphics.getHeight() / 2 - Endsession.getHeight() / 2);

            // Goes back to main menu screen if the screen is touched again.
            if (gameSession.buttonOn == true) {
                gameSession.setScreen(new MainMenuScreen(gameSession));
                // Set the button pressed state to false
                gameSession.buttonOn = false;
                dispose();
            }
        }

        if (flappingstate == 0) {
            flappingstate = 1;
        } else {
            flappingstate = 0;
        }

        group.draw(birdPositions[flappingstate], Gdx.graphics.getWidth() / 2 - birdPositions[flappingstate].getWidth() / 2, Objectbird);
        group.draw(gamepad, Gdx.graphics.getWidth() - 200, 70);
        Valuefnt.draw(group, String.valueOf(points), 100, 200);


        birdreach.set(Gdx.graphics.getWidth() / 2, Objectbird + birdPositions[flappingstate].getHeight() / 2, birdPositions[flappingstate].getWidth() / 2);

        for (int i = 0; i < blockcount; i++) {

            if (Intersector.overlaps(birdreach, UpperObjects[i]) || Intersector.overlaps(birdreach, LowerObjects[i])) {

                finalstate = 2;

            }
        }
        group.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
