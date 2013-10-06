package com.wasome.curio;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Game implements ApplicationListener {

	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		new LwjglApplication(new Game(), "Curio", 640, 480, false);
	}

}
