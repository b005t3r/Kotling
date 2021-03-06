package com.kotling.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kotling.KotlingRendererDemo;
import com.kotling.TexturePatchAtlasDemo;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//new LwjglApplication(new LibGDXDemo(), config);
		//new LwjglApplication(new KotlingRendererDemo(), config);
		new LwjglApplication(new TexturePatchAtlasDemo(), config);
	}
}
