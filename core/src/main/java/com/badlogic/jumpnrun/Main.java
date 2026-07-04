package com.badlogic.jumpnrun;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

class Pair<X, Y> {
    public final X first;
    public final Y second;

    public Pair(X first, Y second) {
        this.first = first;
        this.second = second;
    }

    public X getFirst() {
        return first;
    }

    public Y getSecond() {
        return second;
    }
}

enum PlayerState { IDLE, WALKING, JUMPING, RUNNING, FALLING, LANDING}

/*
TODO
+ Have Raphie draw character
+ Fix jumping animation
 */

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    FitViewport viewport;


    SpriteBatch batch;

    Texture idleSheet, walkSheet, runSheet, jumpSheet, landSheet;

    Animation<TextureRegion> idleAnimation,
        walkAnimationLeft, walkAnimationRight,
        runAnimationLeft, runAnimationRight,
        currentAnimation,
        jumpAnimation, landAnimation;

    float stateTime;
    float characterX, characterY;
    float speedY;
    boolean isOnGround;

    PlayerState state;
    boolean previousStateWasAir;

    @Override
    public void create() {
        viewport = new FitViewport(16,9);

        batch = new SpriteBatch();

        // ----- Idle Animation -----
        idleSheet = new Texture("idleSheet.png");
        Pair<TextureRegion[], Animation<TextureRegion>> idleAnimationPair = createAnimation(idleSheet, 4, 1, .2f, 0);
        idleAnimation = idleAnimationPair.getSecond();

        // ----- Walking Animation -----
        walkSheet = new Texture("walkSheet.png");
        Pair<TextureRegion[], Animation<TextureRegion>> walkAnimationPair = createAnimation(walkSheet, 8, 1, .1f, 0);
        walkAnimationRight = walkAnimationPair.getSecond();
        walkAnimationLeft = flipAnimation(walkAnimationPair.getFirst(), 8, .1f);

        // ----- Running Animation -----
        runSheet = new Texture("runSheet.png");
        Pair<TextureRegion[], Animation<TextureRegion>> runAnimationPair = createAnimation(runSheet, 8, 1, .1f, 0);
        runAnimationRight = runAnimationPair.getSecond();
        runAnimationLeft = flipAnimation(runAnimationPair.getFirst(), 8, .1f);

        // ----- Jumping Animation -----
        jumpSheet = new Texture("jumpSheet.png");
        Pair<TextureRegion[], Animation<TextureRegion>> jumpAnimationPair = createAnimation(jumpSheet, 5, 1, .15f, 0);
        jumpAnimation = jumpAnimationPair.getSecond();
        jumpAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        // ----- Landing Animation -----
        landSheet = new Texture("landSheet.png");
        Pair<TextureRegion[], Animation<TextureRegion>> landAnimationPair = createAnimation(landSheet, 4, 1, 0.2f, 0);
        landAnimation = landAnimationPair.getSecond();
        landAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        stateTime = 0f;
        characterX = 5f;
        characterY = 0f;
        speedY = 0f;
        isOnGround = true;

        currentAnimation = idleAnimation;

        state = PlayerState.IDLE;
        previousStateWasAir = false;
    }

    private Pair<TextureRegion[], Animation<TextureRegion>> createAnimation(Texture sheet, int cols, int rows, float duration, int usedRow) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight() / rows);
        TextureRegion[] frames = new TextureRegion[cols];
        int index = 0;
        for (int i = 0; i < cols; i++) { frames[index++] = tmp[usedRow][i]; }
        return new Pair<>(frames, new Animation<>(duration, frames));
    }

    private Animation<TextureRegion> flipAnimation(TextureRegion[] animationFrames, int frames, float duration) {
        TextureRegion[] flippedFrames = new TextureRegion[frames];
        for (int i = 0; i < frames; i++) {
            flippedFrames[i] = new TextureRegion(animationFrames[i]);
            flippedFrames[i].flip(true, false);
        }
        return new Animation<>(duration, flippedFrames);
    }

    private void setAnimation(Animation<TextureRegion> newAnimation) {
        if (currentAnimation != newAnimation) {
            currentAnimation = newAnimation;
            stateTime = 0f;
        }
    }

    @Override
    public void resize(int width, int height) {
        //if window is minimized, wait for it to be normally sized again
        if(width <= 0 || height <= 0) return;

        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        float speedX = 3f;
        float delta = Gdx.graphics.getDeltaTime();

        boolean walkingLeft = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean walkingRight = Gdx.input.isKeyPressed(Input.Keys.D)  || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean running = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);

        // ----- Horizontal movement -----
        if (running && walkingLeft && isOnGround) {
            state = PlayerState.RUNNING;
            characterX -= 2 * speedX * delta;
            setAnimation(runAnimationLeft);
        } else if (running && walkingRight && isOnGround) {
            state = PlayerState.RUNNING;
            characterX += 2 * speedX * delta;
            setAnimation(runAnimationRight);
        } else if (walkingLeft && isOnGround) { //TODO add another block for jumping left/right
            state = PlayerState.WALKING;
            characterX -= speedX * delta;
            setAnimation(walkAnimationLeft);
        } else if (walkingRight && isOnGround) {
            state = PlayerState.WALKING;
            characterX += speedX * delta;
            setAnimation(walkAnimationRight);
        } else {
            state = PlayerState.IDLE;
            setAnimation(idleAnimation);
        }

        // ----- Jumping -----
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isOnGround) {
            state = PlayerState.JUMPING;
            previousStateWasAir = true;
            speedY = 20f;
            isOnGround = false;
            setAnimation(jumpAnimation);
        }

    }

    private void logic() {
        float delta = Gdx.graphics.getDeltaTime();
        float gravity = -50f;

        // ----- Apply gravity -----
        speedY += gravity * delta;
        characterY += speedY * delta;

        if (!isOnGround) {
            if (speedY > 0f) {
                if (state != PlayerState.JUMPING) {
                    setAnimation(jumpAnimation);
                    state = PlayerState.JUMPING;
                }
            } else {
                state = PlayerState.FALLING;
            }
        }

        if (characterY < 0f) { characterY = 0f; }

        // ----- Hit ground -----
        if (characterY == 0 && previousStateWasAir) {
            characterY = 0;
            speedY = 0;
            isOnGround = true;
            previousStateWasAir = false;

            if (state != PlayerState.LANDING) {
                setAnimation(landAnimation);
                state = PlayerState.LANDING;
            }
        }

        if (state == PlayerState.LANDING) {
            if (landAnimation.isAnimationFinished(stateTime) && stateTime > 0) {
                setAnimation(idleAnimation);
                state = PlayerState.IDLE;
                previousStateWasAir = false;
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.GRAY);
        viewport.apply();

        boolean looping = (state != PlayerState.JUMPING && state != PlayerState.LANDING);

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, looping);
        batch.draw(currentFrame, characterX, characterY,3,3);

        batch.end();

        stateTime += Gdx.graphics.getDeltaTime();
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        batch.dispose();
        idleSheet.dispose();
        walkSheet.dispose();
        runSheet.dispose();
        jumpSheet.dispose();
        landSheet.dispose();
    }
}
