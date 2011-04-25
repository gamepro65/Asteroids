package asteroids;

/**
Name: Chris Drury
Class: CSc 2310: Introduction to programming
Filename: Main.java
Date written: April, 19, 2011

Description:
This class runs the game logic and sets up the screen. 
*/

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import javax.vecmath.Vector2f;
import processing.core.*;

public class Main extends PApplet{
	
	//Asteroids and bullets array lists
	ArrayList asteroids;
	ArrayList bullets;
	
	//Instance of my ship
	Ship me;
	//My ingame health
	int health;
	//current difficulty multiplyer
	static public float difficulty;
	//Should we render with color?
	static public boolean useColor;
	//Current background color
	Color backgroundColor;
	
	//Hold our information for figuring
	//out our current fps. this includes a
	//Timer so we just update the info to our user
	//at a longer interval to make it easier to read
	double delta;
	long lastUpdateTime;
	float fpsCounter;
	static float _fps;
	
	//current score
	int score;
	//scoreboard score
	int visualScore;
	
	//Keyboard control bools
	boolean up;
	boolean left;
	boolean right;
	boolean paused;
	
	//should use mouse control or not
	static boolean mouseControl;
	
	//Setup our game.
	public void setup()
	{
		
		//Screen Width and Height
		screenWidth = 800;
		screenHeight = 600;
		//Psuedo random number generator
		Random ran = new Random();
		//make a new random backgroundColor
		backgroundColor = new Color(ran.nextFloat(),ran.nextFloat(),ran.nextFloat(),1);
		//Default to color on
		useColor = true;
		//Health -1 means our game starts paused/menu screen
		health = -1;
		score = 0;
		visualScore = 0;
		size(screenWidth, screenHeight);
		smooth();
		//set mouse control off by default
		mouseControl = false;
		//Set framerate we want processing to try
		//and achieve.
		frameRate(60);
		lastUpdateTime = 0;
		//difficulty multiplyer starts at 1;
		difficulty = 1;
		//initialize our array lists
		asteroids = new ArrayList();
		bullets = new ArrayList();
		delta = .017;
		lastUpdateTime = System.currentTimeMillis();
		_fps = 1;
		up = false;
		left = false;
		paused = true;
		right = false;
		//create my ship in the middle of the screen.
		me = new Ship(this, new Vector2f(screenWidth/2, screenHeight/2));
		
		
	}
	

	public void update()
	{		
		//If we are not using mouse controls
		if (!mouseControl)
		{
			//if we have left or right true,
			//increase our temp rotation.
			//Well let our ship handle how fast
			//its allowed to actually turn.
			if (left)
				me.tempRotation-=2;
			if (right)
				me.tempRotation+=2;
			//if up then we are accelerating
			if (up)
			{
				//if were less than max speed
				//then we can increase speed
				if (me.speed < me.maxSpeed)
					me.speed += 5;
			}
			else
			{
				//if we are not accelerating
				//divide our speed to create
				//a slowly drifting slowdown
				//to a stop.
				me.speed /= 1.05;
			}
			
		}
		//upadte my ship
		me.update(delta);	
		
		//If our visual score is not our real score then add
		//till we are equal. This will make a cool effect of
		//the score going up scrolling through all the numbers
		if (visualScore != score)
		{
			visualScore++;
		}
		
				
		//If we are not pasued, aka we are running
		if (!paused)
		{
			//if we have no asteroids on the screen
			if (asteroids.size() <= 0)
			{
				//create 3 new big ones
				for (int x = 0; x < 3; x++)
					asteroids.add(new Asteroid(this, 
							new Vector2f((float)Math.random()*screenWidth,
									(float)Math.random()*screenHeight), 150));	
				//increase our difficulty we have cleared the screen
				difficulty += .2;
			}
		}
		//Create temp arrays to hold our deleted objects
		ArrayList discardedAsteroids = new ArrayList();
		ArrayList discardedBullets = new ArrayList();
		
		//Loop through the asteroids
		for (int x = 0; x < asteroids.size(); x++)
		{
			//Assign our temp asteroid to an object
			//Then update that asteroid
			Asteroid tempAsteroid = (Asteroid)asteroids.get(x);
			tempAsteroid.update(delta);
			
			//If the asteroid is touching my ship
			if (tempAsteroid.verticies.intersects(me.verticies.getBounds2D()))
			{
				//Decrease health
				health -= 10;
				//Kill the asteroid, its taken enough damage
				//So add it to the asteroid discarded array
				discardedAsteroids.add(tempAsteroid);
			}
		}
		//Loop through the discarded asteroids
		for (int x = 0; x < discardedAsteroids.size(); x++)
		{
			Asteroid tempAsteroid = (Asteroid)discardedAsteroids.get(x);
			//completly remove the asteroid from the list
			//We could make it break down further but this
			//way we know our user wont be hit multiple times
			//for one measily mistake.
			asteroids.remove(tempAsteroid);	
		}
		//reinit the discarded asteroids array.
		discardedAsteroids = new ArrayList();
		
		//Loop through the bullets
		for (int x = 0; x < bullets.size(); x++)
		{
			//update the bullets
			Bullets temp = (Bullets)bullets.get(x);
			temp.update(delta);
			//if the bullet is outside the screen bounds, delete it
			if (temp.bulletx < 0 || temp.bulletx > screenWidth || temp.bullety < 0 || temp.bullety > screenHeight)
				discardedBullets.add(temp);
			//loop through the asteroids
			for (int y = 0; y < asteroids.size(); y++)
			{
				Asteroid tempAsteroid = (Asteroid)asteroids.get(y);
				
				//if the current bullet intersects the current asteroid
				if (tempAsteroid.verticies.intersects(new Rectangle((int)temp.bulletx, (int)temp.bullety, 10, 10)))
				{
					//Decrease the asteroids health
					tempAsteroid.health--;
					//remove the bullet from the list
					discardedBullets.add(temp);
					
					//Add the direction our bullet was going in
					//to the drift speed, this makes it seem as though
					//our shots are effecting its current
					//trajectory.
					tempAsteroid.driftSpeed.x += temp.speedx/20;
					tempAsteroid.driftSpeed.y += temp.speedy/20;
					
					//If the asteroid died of natural death
					//by bullets add it to the discard array.
					if (tempAsteroid.health <= 0)
					{
						discardedAsteroids.add(tempAsteroid);		
						
					}
				}
			}
		}
		
		//loop through our discarded asteroids
		for (int x = 0; x < discardedAsteroids.size(); x++)
		{
			Asteroid tempAsteroid = (Asteroid)discardedAsteroids.get(x);
			//increase our score based on the asteroids size.
			score += tempAsteroid.size;
			//if the size is > 50 we know its not at its 3rd stage yet
			if (tempAsteroid.size > 50)
			{
				//Create 3 new asteroids in this ones position
				//We make it at half the size this will keep up
				//with our asteroid level. we know stage 1 is 150
				//stage 2 is 75 stage 3 is 75/2 which is < than 50
				for (int i = 0; i < 3; i++)
				{
					asteroids.add(new Asteroid(this, new Vector2f(tempAsteroid.position.x, tempAsteroid.position.y), tempAsteroid.size/2));
				}
			}
			//remove the discarded asteroid from the list
			asteroids.remove(tempAsteroid);
			
		}
		//remove all bullets
		for (int x = 0; x < discardedBullets.size(); x++)
		{
			bullets.remove(discardedBullets.get(x));
		}
		
		//if health <= 0 then pause the game
		//forcing us into pause/main menu
		if (health <= 0)
		{
			paused = true;
		}
	}
	
	public void mousePressed()
	{
		//If we are not paused then fire
		//Because we are using mouse mode
		//Our ship still should fire forwards
		//assuming we only have a gun on the front
		//For the extra credit however this would change
		//to be based on the rotation of the current
		//mouse position.
		if (!paused && bullets.size() < 3)
		{
			//In my version my ship only shoots forward
			//As thats how I imagined it.
			//So here we take our bullets starting position
			//This is going to be our current ship location +
			//The x and y offset that the front of our ship is at
			//based on the current ship rotation. We know by using
			//our predefined ship that our front is 30 pixels away
			//from the center of our position. So we make our starting 
			//there.
			float startx = me.position.x + (float)(Math.cos(Math.toRadians(me.rotation)) * 30);
			float starty = me.position.y + (float)(Math.sin(Math.toRadians(me.rotation)) * 30);
			//add the bullet to the list the end location is going to be just 
			//50 pixels away from the center as thats a point that will definatly
			//be infront of the ship making our bullet fire straight from the front
			bullets.add(new Bullets(this, (int)startx, (int)starty, (int)me.position.x+(int)(Math.cos(Math.toRadians(me.rotation))*50), (int)me.position.y+(int)(Math.sin(Math.toRadians(me.rotation))*50), 5));
		
			
			//For the sake of extra credit here is the code to make
			//The ship fire in the direction of the mouse.
			//Comment out the code above to enable this feature
			//and uncomment the next few lines.
			/*
				//So here we take our bullets starting position
				//This is going to be our current ship location +
				//The x and y offset that the front of our ship is at
				//based on the current ship rotation. We know by using
				//our predefined ship that our front is 30 pixels away
				//from the center of our position. So we make our starting 
				//there.
				float startx = me.position.x + (float)(Math.cos(Math.toRadians(me.rotation)) * 30);
				float starty = me.position.y + (float)(Math.sin(Math.toRadians(me.rotation)) * 30);
				//Our bullet class is set up to find the
				//angles between the start and finish point
				//for us so all we need to do is tell it just that
				//the start and finish.
			 	bullets.add(new Bullets(this, (int)startx, (int)starty, mouseX, mouseY, 5));
			 
			*/
			
		}
	}
	
	public void keyPressed()
	{
		//keycode 37 = left arrow
		if (keyCode == 37)
		{
			left = true;
		}
		//39 = right arrow
		if (keyCode == 39)
		{
			right = true;
		}
		//32 = spacebar
		if (keyCode == 32)
		{
			//if less than 3 bullets and not paused
			//add new bullet infront of the ship.
			if (!paused && bullets.size() < 3)
			{
				float startx = me.position.x + (float)(Math.cos(Math.toRadians(me.rotation)) * 30);
				float starty = me.position.y + (float)(Math.sin(Math.toRadians(me.rotation)) * 30);
				bullets.add(new Bullets(this, (int)startx, (int)starty, (int)me.position.x+(int)(Math.cos(Math.toRadians(me.rotation))*50), (int)me.position.y+(int)(Math.sin(Math.toRadians(me.rotation))*50), 5));
			}
		}
		//38 = up
		if (keyCode == 38)
		{
			up = true;
		}
		
		//key t is used to toggle color
		//on/off
		if (key == 't')
		{
			if (useColor)
				useColor = false;
			else
				useColor = true;
		}
		
		//key m is used to toggle mouseMode
		//on/off
		if (key == 'm')
		{
			if (mouseControl)
				mouseControl = false;
			else
				mouseControl = true;
		}
			
		//10 = enter
		if (keyCode == 10)
		{
			//If paused
			if (paused)
			{
				//unpause
				paused = false;
				//if health <= 0 reset the game
				if (health <= 0)
				{
					//remove any asteroids from previous games
					asteroids.clear();
					//create 3 asteroids
					for (int x = 0; x < 3; x++)
					{
						asteroids.add(new Asteroid(this, new Vector2f((float)Math.random()*screenWidth,(float)Math.random()*screenHeight), 75/2));
					}
					//reset score
					score = 0;
					//reset random background color
					Random ran = new Random();
					backgroundColor = new Color(ran.nextFloat(),ran.nextFloat(),ran.nextFloat(),1);
					//reset health
					health = 100;
					//reset difficulty
					difficulty = 1;
				}
			}
			else
			{
				//pause the game
				paused = true;
			}
		}
	}
	
	public void keyReleased()
	{
		//releasing the key removes the boolean
		//of that key
		if (keyCode == 37)
		{
			left = false;
		}
		if (keyCode == 39)
		{
			right = false;
		}
		if (keyCode == 38)
		{
			up = false;
		}
	}
	
	public static void main(String[] args) {
		//init the game
		PApplet.main(new String[] {"--present", "Main"});		
	}
	
	public void draw()
	{
		//update our delta based from current - lastTime
		long currentTime = System.currentTimeMillis();
		delta = (currentTime - lastUpdateTime);
		delta = (1/(1000/delta));
		fpsCounter += delta;
		//if its been a 1/4 second
		//update the fps time to display
        if(fpsCounter > 0.25f) {
            fpsCounter = 0;
            _fps = (int)((1/delta));
        }						
		lastUpdateTime = currentTime;
		
		//if we are using color set our bg color
		if (useColor)
		{
			background(backgroundColor.getRGB());
		}
		else
		{
			//Gimmie grey
			background(204);
		}
		
		
		//set text to white
		fill(0);
		//align text to center of position
		textAlign(CENTER);
		
		//draw score at middle of bottom of screen
		text("Score: " + visualScore, screenWidth/2, screenHeight-70);
		
		//if we are not paused
		if (!paused)
		{
			//update our game logic
			update();
			
			//draw information text including our fps, toggle switces
			text("Press 'Enter' To Pause", 100, screenHeight-90);
			text("Press 'T' To Toggle Color Mode", 100, screenHeight-70);
			text("FPS: " + _fps, screenWidth-200, 10);
			text ("Press 'M' To Toggle Mouse Mode", 100, screenHeight-80);
		}
		else
		{
			//if health is less than 0
			if (health < 0)
			{				
				//tell them to hit enter to play and toggles
				text("Press 'Enter' To Play", 100, screenHeight-90);
				text("Press 'T' To Toggle Color Mode", 100, screenHeight-70);
				text ("Press 'M' To Toggle Mouse Mode", 100, screenHeight-80);
			}
			else
			{
				//Game is just paused so tell them that
				//Also mention how to resume and toggle switches
				text("Paused.", screenWidth/2, screenHeight/2);
				text("Press 'Enter' To Resume", 100, screenHeight-90);
				text("Press 'T' To Toggle Color Mode", 100, screenHeight-70);
				text ("Press 'M' To Toggle Mouse Mode", 100, screenHeight-80);
		
			}
			//Display the current fps.
			text("FPS: " + _fps, screenWidth-200, 10);			
		}		
		
		//Loop through the asteroids, draw them,
		//and update their rotation to make them spin.
		for (int x = 0; x < asteroids.size(); x++)
		{
			Asteroid temp = (Asteroid)asteroids.get(x);
			temp.draw();
			temp.rotation+=.2;
		}
		
		
		//loop and draw the bullets
		for (int x = 0; x < bullets.size(); x++)
		{
			Bullets temp = (Bullets)bullets.get(x);
			temp.draw();
		}
		
		//Draw me.
		me.draw();
		//If health is > 0 aka we are in game
		if (health > 0)
		{
			//if use color color health bar
			if (useColor)
				fill(255, 0, 0);
			else
			{
				fill(204);
			}
			//draw the current current health ammount across the top
			//as a percentage of the whole.
			rect(10, 10, (((float)screenWidth-30)*((float)health/100)), 20);
		}
		
		
	}
	
}
