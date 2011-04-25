package asteroids;

/**
Name: Chris Drury
Class: CSc 2310: Introduction to programming
Filename: Asteroid.java
Date written: April, 19, 2011

Description:
This class controls how our asteroids work. 
*/

import java.util.Random;
import javax.vecmath.Vector2f;
import processing.core.PApplet;



public class Asteroid extends AbstractEntity{
	
	//Public because I'm the only one coding this game
	//And its quicker than making helper methods.
	public Vector2f driftSpeed;	
	public int size;
	public int health;
	
	
	//Default constructor of our asteroid
	public Asteroid(PApplet aParent, Vector2f aPosition, int aSize)
	{
		//Tell our abstract entity parent the information it needs
		super(aParent, aPosition);
		//Psudeo random number generator. Used for our random
		//Asteroid creation
		Random ran = new Random();
		size = aSize;
		//Minimum number of sides is 5
		//Triangles don't look like asteroids
		numberOfSides = (int)(ran.nextDouble() * 10)+5;
		//Health per asteroid is 3
		health = 3;
		//Random angle for our new asteroid to drift
		double randomAngle = ran.nextDouble()*360;
		//set the drift speed of our asteroid
		//The speed varies from 0-180 pixel/s
		driftSpeed = new Vector2f((float)(Math.cos(Math.toRadians(randomAngle))*ran.nextDouble()*180), (float)(Math.sin(Math.toRadians(randomAngle))*ran.nextDouble()*180));
		//Randomly create our asteroid. We do 360 - one angle of our side
		//This is because we dont need to remake our 360 degree point as its
		//our 0 point as well. Then using the number of sides in our asteroid
		//Loop in a circle around our point creating random distances
		//for each angle.
		for (int x = 0; x < 360-(360/(numberOfSides)); x+= (360/(numberOfSides)))
		{
			//Distance from center is created randomly between
			//30 and the largest size it could be - 10
			double randomSize = (ran.nextDouble() * size-10)+30;
			//Array lists only store objects, not primitives
			Double mySize = new Double(randomSize);
			//Add distance to my array of distances.
			verticiesSizes.add(mySize);
		}
	}
	

	public void update(double aDelta)
	{
		//Update our parent to update our position information
		super.update(aDelta);
		//Drift the asteroid. This is set
		//To drift speed * delta to make a smooth move
		//Then * difficulty making it harder later because
		//The asteroids move faster.
		position.x += (float)(driftSpeed.x * Main.difficulty * aDelta);
		position.y += (float)(driftSpeed.y * Main.difficulty * aDelta);
	}
		
	public void draw()
	{	
		//No special drawing attributes to our asteroids
		//Just draw our verticies. Our parent does this
		//for us.
		super.draw();		
	}

}
