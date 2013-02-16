package org.tradeworld.core;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import org.tradeworld.utils.StringUtils;

public class Client implements ApplicationListener {
	Texture texture;
	SpriteBatch batch;
	
	@Override
	public void create () {
		texture = new Texture(Gdx.files.internal("images/placeholder2.png"));
		batch = new SpriteBatch();

        System.out.println(StringUtils.testString());
    }

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(texture, 100, 100);
		batch.end();
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	}
}
