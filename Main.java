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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
	
	ArrayList stars;
	
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
		stars = new ArrayList();
		//Screen Width and Height
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = (int)(dim.width);
		screenHeight = (int)(dim.height);
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
		
		//allows us to resize the frame
		frame.setResizable(true);
		size(screenWidth,screenHeight);
		frame.setSize(screenWidth,screenHeight); 
		frame.setLocation((dim.width/2 - screenWidth/2), (dim.height/2 - screenHeight/2));	
		
		smooth();
		//set mouse control off by default
		mouseControl = false;
		//Set framerate we want processing to try
		//and achieve.
		frameRate(60);
		lastUpdateTime = 0;
		//difficulty multiplyer starts at 1;
		difficulty = 1.3f;
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
		//lets make stars in our sky
		if (stars.size() < 100)
		{
			//add a new particle to our world that is small enough
			//look like a star
			Particle p = new Particle(this, new Vector2f((float)(Math.random() * screenWidth), 
														(float)(Math.random() * screenHeight)), 
														(float)(Math.random() * 20), 
														new Color(1f, 1f, 1f), 2);
			stars.add(p);
		}
		ArrayList discardedStars = new ArrayList();
		//Update our stars and delete dead ones
		for (int x = 0; x < stars.size(); x++)
		{
			Particle p = (Particle)stars.get(x);
			p.update(delta);
			if (p.delete)
			{
				discardedStars.add(p);
			}
		}
		stars.removeAll(discardedStars);
		
		//If we are not using mouse controls
		if (!mouseControl)
		{
			//if we have left or right true,
			//increase our rotation.
			//Well let our ship handle how fast
			//its allowed to actually turn.
			if (left)
				me.rotate((float)(-180 * delta));	
			if (right)
				me.rotate((float)(180 * delta));
			
			//if up then we are accelerating
			if (up)
			{
				me.speed = 5;
				me.accelerate();
			}
			
		}
		//update my ship
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
				//create new big asteroids based on the difficulty
				for (int x = 0; x < difficulty*2; x++)
				{
					//select random angle to position the new asteroid at
					double randomAngle = Math.random()*360;
					
					asteroids.add(new Asteroid(this, 
							new Vector2f((float)((screenWidth/2) + (Math.cos(Math.toRadians(randomAngle)) * screenWidth)),
										(float)((screenHeight/2) + (Math.sin(Math.toRadians(randomAngle)) * screenHeight))), 150));	
				}
				//increase our difficulty we have cleared the screen
				difficulty += .2;
			}
		}
		
		//Create temp arrays to hold our deleted objects
		ArrayList discardedAsteroids = new ArrayList();
		ArrayList discardedBullets = new ArrayList();
		
		
				
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
				if (tempAsteroid.verticies.intersects(new Rectangle((int)temp.bulletx, (int)temp.bullety, 10, 10)) && !tempAsteroid.destroied)
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
						//discardedAsteroids.add(tempAsteroid);		
						tempAsteroid.destroyObject();
						//increase our score based on the asteroids size.
						score += tempAsteroid.size;
						//if the size is > 50 we know its not at its 3rd stage yet
						if (tempAsteroid.size > 50)
						{
							//Create 2 new asteroids in this ones position
							//We make it at half the size this will keep up
							//with our asteroid level. we know stage 1 is 150
							//stage 2 is 75 stage 3 is 75/2 which is < than 50
							for (int i = 0; i < 2; i++)
							{
								asteroids.add(new Asteroid(this, new Vector2f(tempAsteroid.position.x, tempAsteroid.position.y), tempAsteroid.size/2));
							}
						}
					}
				}
			}
		}
		
		//if my ship is destroied and I have no particles left
		//reset my position to the middle of the screen
		//reset my spwning invincibilty to 10
		//and tell me the ship isnt destroied and isnt moving yet.
		if (me.destroied && me.verticieParticles.size() == 0)
		{
			me.position = new Vector2f(screenWidth/2, screenHeight/2);
			me.spawningTimer = 10;
			me.speedVector = new Vector2f(0,0);
			me.destroied = false;
		}
		
		//Loop through the asteroids
		for (int x = 0; x < asteroids.size(); x++)
		{
			//Assign our temp asteroid to an object
			//Then update that asteroid
			Asteroid tempAsteroid = (Asteroid)asteroids.get(x);
			tempAsteroid.update(delta);
			//if we are not dead
			if (!tempAsteroid.destroied)
			{
				//If the asteroid is touching my ship and im not dead or invincible
				if (tempAsteroid.verticies.intersects(me.verticies.getBounds2D()) && me.spawningTimer <= 0 && me.verticieParticles.size() == 0)
				{
					//Decrease health
					health -= 25;
					//set the asteroids health to 0
					tempAsteroid.health = 0;
					//destroy our object we hit it
					me.destroyObject();
					//if the size is > 50 we know its not at its 3rd stage yet
					if (tempAsteroid.size > 50)
					{
						//Create 2 new asteroids in this ones position
						//We make it at half the size this will keep up
						//with our asteroid level. we know stage 1 is 150
						//stage 2 is 75 stage 3 is 75/2 which is < than 50
						for (int i = 0; i < 2; i++)
						{
							asteroids.add(new Asteroid(this, new Vector2f(tempAsteroid.position.x, tempAsteroid.position.y), tempAsteroid.size/2));
						}
					}
					discardedAsteroids.add(tempAsteroid);
				}
			}
			else if (tempAsteroid.destroied && tempAsteroid.verticieParticles.size() == 0)
			{
				//we are dead and have no particles left
				//so we delete this asteroid now.
				discardedAsteroids.add(tempAsteroid);
			}
			
		}
		//delete asteroids and bullets
		asteroids.removeAll(discardedAsteroids);
		bullets.removeAll(discardedBullets);
		
		//if health <= 0  and our ship has been destroied
		//then pause the game
		//forcing us into pause/main menu
		if (health <= 0 && me.verticieParticles.size() == 0)
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
		if (!paused && bullets.size() < 3 && !me.destroied)
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
		if (keyCode == 107)
		{
			//+ hit so lets increase the screen size by 10
			screenWidth += 10;
			screenHeight += 10;
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			size(screenWidth,screenHeight);
			frame.setSize(screenWidth,screenHeight); 
			//keep the frame in the middle of the screen
			frame.setLocation((dim.width/2 - screenWidth/2), (dim.height/2 - screenHeight/2));
		}
		if (keyCode == 109)
		{
			//- hit so lets decrease the screen size by 10
			screenWidth -= 10;
			screenHeight -= 10;
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			size(screenWidth,screenHeight);
			frame.setSize(screenWidth,screenHeight); 
			//keep the frame in the middle of the screen
			frame.setLocation((dim.width/2 - screenWidth/2), (dim.height/2 - screenHeight/2));
		}
		
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
			if (!paused && bullets.size() < 3 && !me.destroied)
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
					//reset score
					score = 0;
					visualScore = 0;
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
		PApplet.main(new String[] {"--present", "asteroids.Main"});		
		
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
			//Gimmie black
			background(0);
		}
		//set no stroke and then draw our stars.
		noStroke();
		for (int x = 0; x < stars.size(); x++)
		{
			Particle p = (Particle)stars.get(x);
			p.draw();
		}
		stroke(0);
		
		
		//set text to white/black depending on mode
		if (!useColor)
			fill(255);
		else
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
			text("Press 'Enter' To Pause", 150, screenHeight-90);
			text("Press 'T' To Toggle Color Mode", 150, screenHeight-70);
			text("FPS: " + _fps, screenWidth-200, 10);
			text ("Press 'M' To Toggle Mouse Mode", 150, screenHeight-80);
			text("+/- Will resize your screen accordingly", 150, screenHeight - 100);
		}
		else
		{
			//if health is less than 0
			if (health < 0)
			{				
				//tell them to hit enter to play and toggles
				text("Press 'Enter' To Play", 150, screenHeight-90);
				text("Press 'T' To Toggle Color Mode", 150, screenHeight-70);
				text ("Press 'M' To Toggle Mouse Mode", 150, screenHeight-80);
				text("+/- Will resize your screen accordingly", 150, screenHeight - 100);
			}
			else
			{
				//Game is just paused so tell them that
				//Also mention how to resume and toggle switches
				text("Paused.", screenWidth/2, screenHeight/2);
				text("Press 'Enter' To Resume", 150, screenHeight-90);
				text("Press 'T' To Toggle Color Mode", 150, screenHeight-70);
				text ("Press 'M' To Toggle Mouse Mode", 150, screenHeight-80);
				text("+/- Will resize your screen accordingly", 150, screenHeight - 100);
		
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
				noFill();
			}
			//draw the current current health ammount across the top
			//as a percentage of the whole.
			rect(10, 10, (((float)screenWidth-30)*((float)health/100)), 20);
			//set text to white/black depending on mode
			if (!useColor)
				fill(255);
			else
				fill(0);
			text("Health", 30, 25);
		}
		
		
	}
	
}
