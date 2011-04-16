package asteroids;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Vector2f;
import processing.core.*;

public class Main extends PApplet{
	ArrayList asteroids;
	ArrayList bullets;
	Ship me;
	int health;
	static public boolean useColor;
	
	Color backgroundColor;
	
	double delta;
	long lastUpdateTime;
	float fpsCounter;
	static float _fps;
	
	int score;
	int visualScore;
	
	boolean up;
	boolean down;
	boolean left;
	boolean right;
	boolean paused;
	static boolean mouseControl;
	
	
	public void setup()
	{
		screenWidth = 1300;
		screenHeight = 600;
		Random ran = new Random();
		backgroundColor = new Color(ran.nextFloat(),ran.nextFloat(),ran.nextFloat(),1);
		useColor = true;
		health = -1;
		score = 0;
		visualScore = 0;
		size(screenWidth, screenHeight);
		smooth();
		mouseControl = false;
		frameRate(60);
		lastUpdateTime = 0;
		asteroids = new ArrayList();
		bullets = new ArrayList();
		delta = .017;
		lastUpdateTime = System.currentTimeMillis();
		_fps = 1;
		up = false;
		down = false;
		left = false;
		paused = true;
		right = false;
		for (int x = 0; x < 7; x++)
		{
			asteroids.add(new Asteroid(this, new Vector2f((float)Math.random()*screenWidth,(float)Math.random()*screenHeight), (int)(Math.random()*80+30)));
		}
		me = new Ship(this, new Vector2f(screenWidth/2, screenHeight/2));
		
		
	}
	

	public void update()
	{
		
		
		
		
		if (left)
			me.tempRotation-=2;
		if (right)
			me.tempRotation+=2;
		
		me.update(delta);	
		
		if (visualScore != score)
		{
			visualScore++;
		}
		
				
		if (!paused)
		{
			if (asteroids.size() < 7)
			{
					asteroids.add(new Asteroid(this, new Vector2f((float)Math.random()*screenWidth,(float)Math.random()*screenHeight), (int)(Math.random()*80+30)));	
			}
		}
				
		ArrayList discardedAsteroids = new ArrayList();
		ArrayList discardedBullets = new ArrayList();
		for (int x = 0; x < asteroids.size(); x++)
		{
			Asteroid tempAsteroid = (Asteroid)asteroids.get(x);
			tempAsteroid.update(delta);
			
			if (tempAsteroid.verticies.intersects(me.verticies.getBounds2D()))
			{
				health -= 10;
				discardedAsteroids.add(tempAsteroid);
			}
		}
		for (int x = 0; x < discardedAsteroids.size(); x++)
		{
			Asteroid tempAsteroid = (Asteroid)discardedAsteroids.get(x);
			asteroids.remove(tempAsteroid);	
		}
		discardedAsteroids = new ArrayList();
		
		for (int x = 0; x < bullets.size(); x++)
		{
			Bullets temp = (Bullets)bullets.get(x);
			temp.update(delta);
			if (temp.bulletx < 0 || temp.bulletx > screenWidth || temp.bullety < 0 || temp.bullety > screenHeight)
				discardedBullets.add(temp);
			for (int y = 0; y < asteroids.size(); y++)
			{
				Asteroid tempAsteroid = (Asteroid)asteroids.get(y);
				
				if (tempAsteroid.verticies.intersects(new Rectangle((int)temp.bulletx, (int)temp.bullety, 10, 10)))
				{
					tempAsteroid.health--;
					discardedBullets.add(temp);
					if (tempAsteroid.health <= 0)
					{
						discardedAsteroids.add(tempAsteroid);				
					}
				}
			}
		}
		
		for (int x = 0; x < discardedAsteroids.size(); x++)
		{
			Asteroid tempAsteroid = (Asteroid)discardedAsteroids.get(x);
			score += tempAsteroid.size;
			if (tempAsteroid.size > 30)
			{
				for (int i = 0; i < tempAsteroid.size/10; i++)
				{
					asteroids.add(new Asteroid(this, new Vector2f(tempAsteroid.position.x, tempAsteroid.position.y), tempAsteroid.size/2));
				}
			}
			asteroids.remove(tempAsteroid);
			
		}
		for (int x = 0; x < discardedBullets.size(); x++)
		{
			bullets.remove(discardedBullets.get(x));
		}
		
		if (health <= 0)
		{
			paused = true;
		}
	}
	
	public void mousePressed()
	{
		if (!paused)
		{
			float startx = me.position.x + (float)(Math.cos(Math.toRadians(me.rotation)) * 30);
			float starty = me.position.y + (float)(Math.sin(Math.toRadians(me.rotation)) * 30);
			bullets.add(new Bullets(this, (int)startx, (int)starty, (int)me.position.x+(int)(Math.cos(Math.toRadians(me.rotation))*50), (int)me.position.y+(int)(Math.sin(Math.toRadians(me.rotation))*50), 5));
		}
	}
	
	public void keyPressed()
	{
		System.out.println(keyCode);
		if (keyCode == 37)
		{
			left = true;
		}
		if (keyCode == 39)
		{
			right = true;
		}
		if (keyCode == 32)
		{
			if (!paused)
			{
				float startx = me.position.x + (float)(Math.cos(Math.toRadians(me.rotation)) * 30);
				float starty = me.position.y + (float)(Math.sin(Math.toRadians(me.rotation)) * 30);
				bullets.add(new Bullets(this, (int)startx, (int)starty, (int)me.position.x+(int)(Math.cos(Math.toRadians(me.rotation))*50), (int)me.position.y+(int)(Math.sin(Math.toRadians(me.rotation))*50), 5));
			}
		}
		if (key == 't')
		{
			if (useColor)
				useColor = false;
			else
				useColor = true;
		}
			
		if (keyCode == 10)
		{
			if (paused)
			{
				paused = false;
				if (health <= 0)
				{
					asteroids.clear();
					for (int x = 0; x < 7; x++)
					{
						asteroids.add(new Asteroid(this, new Vector2f((float)Math.random()*screenWidth,(float)Math.random()*screenHeight), (int)(Math.random()*30+30)));
					}
					score = 0;
					Random ran = new Random();
					backgroundColor = new Color(ran.nextFloat(),ran.nextFloat(),ran.nextFloat(),1);
					health = 100;
				}
			}
			else
				paused = true;
		}
	}
	
	public void keyReleased()
	{
		if (keyCode == 37)
		{
			left = false;
		}
		if (keyCode == 39)
		{
			right = false;
		}
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[] {"--present", "Main"});		
	}
	
	public void draw()
	{
		
		long currentTime = System.currentTimeMillis();
		delta = (currentTime - lastUpdateTime);
		delta = (1/(1000/delta));
		fpsCounter += delta;
        if(fpsCounter > 0.25f) {
            fpsCounter = 0;
            _fps = (int)((1/delta));
        }						
		lastUpdateTime = currentTime;
		
		if (useColor)
		{
			background(backgroundColor.getRGB());
		}
		else
		{
			background(204);
		}
		
		fill(0);
		textAlign(CENTER);
		
		text("Score: " + visualScore, screenWidth/2, screenHeight-70);
		
		if (!paused)
		{
			update();
			
			text("Press 'Enter' To Pause", 150, screenHeight-70);
			text("Press 'T' To Toggle Color Mode", 400, screenHeight-70);
			text("FPS: " + _fps, screenWidth-200, 10);
		}
		else
		{
			if (health < 0)
			{				
				text("Press 'Enter' To Play.", screenWidth/2, (screenHeight/2)-20);
				
			}
			else
			{
				text("Paused.", screenWidth/2, screenHeight/2);
				text("Press 'Enter' To Resume", 150, screenHeight-70);
			}
			text("Press 'T' To Toggle Color Mode", 300, screenHeight-70);
			text("FPS: " + _fps, screenWidth-200, 10);
			
		}		
		
		for (int x = 0; x < asteroids.size(); x++)
		{
			Asteroid temp = (Asteroid)asteroids.get(x);
			temp.draw();
			temp.rotation+=.2;
		}
		
		
		for (int x = 0; x < bullets.size(); x++)
		{
			Bullets temp = (Bullets)bullets.get(x);
			temp.draw();
		}
		me.draw();
		if (health > 0)
		{
			if (useColor)
			fill(255, 0, 0);
			else
			{
				fill(204);
			}
			rect(10, 10, (((float)screenWidth-30)*((float)health/100)), 20);
		}
		
		
	}
	
}
