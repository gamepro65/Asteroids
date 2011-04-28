package asteroids;

/**
Name: Chris Drury
Class: CSc 2310: Introduction to programming
Filename: Ship.java
Date written: April, 19, 2011

Description:
This class controls how a ship acts. 
*/

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.vecmath.Vector2f;
import processing.core.PApplet;


//Inherit methods and variables from our abstract entity
public class Ship extends AbstractEntity{
	
	public int size;	
	public int health;
	public float speed;
	public float maxSpeed;
	public double tempRotation;
	public ArrayList smokeParticles;
	public Vector2f speedVector;
	public float spawningTimer;
	
	//Default constructor
	public Ship(PApplet aParent, Vector2f aPosition)
	{
		//Initialize our parent with our current information;
		super(aParent, aPosition);
		
		spawningTimer = 10;
		
		//We know our ship is going to have 4 sides
		numberOfSides = 4;	
		//set our traveling speed
		speed = 0;
		maxSpeed = 120;
		
		//Current speed vector of the ship
		speedVector = new Vector2f(0,0);
		
		//initialize our smoke particle emitter.
		smokeParticles = new ArrayList();
		
		//set our color to white.
		objectColor = new Color(.99f,.99f,.99f,1);
		
		//In order to reuse the polygon code from our asteroid we must
		//construct the distances before and figure out the distances
		//from the center in order to make a ship out of 4 points
		//To do this we go from 0 straight out 30 units.
		//then we know the next point will be to its bottom so we want
		//12 units out from the center to create the wing.
		//next is tricky we are at 180 degrees and this means we need to
		//use a negative number to actually go positive in the 0 degree
		//to make an indent into our polygon. The next angle is 270 which
		//will mirror our 90 angle of 12. With those values  put into our
		//sizes array we can update our polygon to construct our object shape out of our
		//predefined object with our ships rotation.
		double randomSize = 30;
		Double mySize = new Double(randomSize);
		verticiesSizes.add(mySize);	
		randomSize = 12;
		mySize = new Double(randomSize);
		verticiesSizes.add(mySize);	
		randomSize = 10;
		mySize = new Double(randomSize);
		verticiesSizes.add(mySize);
		randomSize = 12;
		mySize = new Double(randomSize);
		verticiesSizes.add(mySize);		
		updatePolygon();		
	}
	
	//rotate the ship using this method we can add smoke to
	//the "side thrusters" to give it a more realistic rotation
	public void rotate(float degrees)
	{
		//rotate the ship by the degrees
		rotation += degrees;
		//add 2 smoke particles, one on either side of the ship
		//make the color grey to look like smoke.
		//we take into account our current ship rotation so that
		//the particles spawn on top of the position.
		smokeParticles.add(new Particle(parent, 
				new Vector2f((float)(Math.cos(Math.toRadians(rotation+90)) * 7 + position.x),
				(float)(Math.sin(Math.toRadians(rotation+90)) * 7 +position.y)),
				.3f,
				new Color(0.3f,0.3f,0.3f,1f),
				5));

		smokeParticles.add(new Particle(parent, 
				new Vector2f((float)(Math.cos(Math.toRadians(rotation-90)) * 7 + position.x),
						(float)(Math.sin(Math.toRadians(rotation-90)) * 7 +position.y)),
						.3f,
						new Color(0.3f,0.3f,0.3f,1f),
						5));
	}
	
	//Accelerate method. Will cause the ship to accelerate
	//at the current angle
	public void accelerate()
	{
		//If we are spwning, we have moved, 
		//thus we should not be protected any more.
		if (spawningTimer > 0)
			spawningTimer = 0;
		
		//Get what our speed addition would be if we do
		//in fact accelerate
		double x = (float)(speed * Math.cos(Math.toRadians(rotation)));
		double y = (float)(speed * Math.sin(Math.toRadians(rotation)));
		
		//The future speed thats about to be updated has a speed based
		//on the x and the y speed and finding the hypot as speed is the
		//length of the hypot.
		double currentSpeed = Math.hypot(speedVector.x+x, speedVector.y+y);
		
		//Get the max x and y speed we could use for the current angle
		double maxX = (float)(maxSpeed * Math.cos(Math.toRadians(rotation)));
		double maxY = (float)(maxSpeed * Math.sin(Math.toRadians(rotation)));
		
		//if our max speed in that direction is less
		//than our current speed, then we need to help the
		//user out a little bit. This just ensures that we
		//are not going too fast in one direction that our 
		//ship would never end up going close to our target direction.
		//This makes it less realistic however gives the user
		//a false sense of control because if a user is angry
		//they probably are not going to play the game for too long.
		//I'd rather sacrifice realism for more fun game play.
		if (Math.abs(maxX) < Math.abs(speedVector.x))
		{
			if (maxX < speedVector.x)
				speedVector.x -= 2;
			else
				speedVector.x += 2;
		}
		if (Math.abs(maxY) < Math.abs(speedVector.y))
		{
			if (maxY < speedVector.y)
				speedVector.y -= 2;
			else
				speedVector.y += 2;
		}		
		
		
		//if the updated vector speed is less than
		//the current speed then add the accelerate
		if(currentSpeed < maxSpeed)
		{
			//move distance by speed at our current rotational angle			
			speedVector.x += x;
			speedVector.y += y;
			
		}
		

		//if our ship is not destroied then
		//add a smoke particle because we have accelerated
		if (verticieParticles.size() == 0)
		{
			//we have moved so lets make a new particle and throw it into our arrays to be rendered.
			//I added some variance to the positions to make it look like a thicker smoke.
			smokeParticles.add(new Particle(parent,
						new Vector2f((float)(Math.cos(Math.toRadians(rotation+180)) * 15 + position.x-3+Math.random()*6),
						(float)(Math.sin(Math.toRadians(rotation+180)) * 15 +position.y-3+Math.random()*6)),
						.5f,
						new Color(1f,1f,0f,1f),
						5));
		}
		
		
	}
	
	public void update(double aDelta)
	{
		//Tell our parent to update.
		//This will do our wraping around the world
		//and updating our current rotation;
		super.update(aDelta);
		
		//if our spawning timer is still running
		//subtract time.
		if (spawningTimer > 0)
			spawningTimer -= aDelta;
		
		//get our ships bounds
		Rectangle2D tempRect = verticies.getBounds2D();
		//If we are not inside of the ship bounds with our mouse
		//Then our ship should move forward. Unless its
		//in non mouse mode in which then we need it to still
		//move.
		if (!tempRect.contains(parent.mouseX, parent.mouseY) || (!Main.mouseControl))
		{
			//if we are using mouse control
			if (Main.mouseControl)
			{
				speed = 20;
				//figure out the rotation to the mouse position
				//from the atan2 function with its xdis and ydis from
				//both points.
				float xdis = parent.mouseX - position.x;
				float ydis = parent.mouseY - position.y;
				double angleAradians = Math.atan2(ydis,xdis);			
			
				//convert to 360 degrees to make it easier to figure out.
				if (angleAradians < 0)
				{
					tempRotation = Math.toDegrees(angleAradians) + 360;
				}
				else
				{
					tempRotation = Math.toDegrees(angleAradians);
				}
				
				//Here we must check if the ship has to fly more than 180 degrees 
				//to get to the current rotation. If so we need to change the tempRotation
				//so that our ship will do the smaller than 180 degrees to go the shorter
				//rotational distance because our number is 0-360 we dont have a wrap around function
				//this fixes that.
				if (rotation-tempRotation > 180)
				{
					tempRotation += 360;
				}
				if (rotation-tempRotation < -180)
				{
					tempRotation -= 360;
				}
				//Rotate our ship with a speed of about 60 pixels/s
				//We multiply this by aDelta to make it so our ship
				//will move the same distance reguardless of how long
				//between frame redraws based on the scale of 1/s that has passed
				//this is how we deduce speed is in pixels/s
				if (Math.abs((int)rotation - (int)tempRotation) > 5)
				{
					if ((int)rotation < (int)tempRotation)  
					{
						if (Math.abs(360 * aDelta) > Math.abs(rotation - tempRotation))
							rotate((float)(rotation-tempRotation));
						else
							rotate((float)(360 * aDelta));					
					}
					else
					{
						if (Math.abs(-360 * aDelta) > Math.abs(rotation - tempRotation))
							rotate((float)(rotation-tempRotation));
						else
							rotate((float)(-360 * aDelta));
					}		
				}
				else
				{
					//If the ship is rotated close enough to our angle
					//then accelerate
					accelerate();
				}
			}			
		}
		else
		{
			//if our mouse is inside the ship
			//do all of our rotation stuff just dont let the ship move
			//mouse means the ship is not allowed to accelerate.
			//We can also take advantage of our invincibility mode
			//if we are spwning and havnt moved we can still shoot and not
			//lose invincibility. This is left for situations where its too
			//hard to get out of, we can atleast have 10 sec of free shooting
			float xdis = parent.mouseX - position.x;
			float ydis = parent.mouseY - position.y;
			double angleAradians = Math.atan2(ydis,xdis);		
			
			double tempRotation;
			
			if (angleAradians < 0)
			{
				tempRotation = Math.toDegrees(angleAradians) + 360;
			}
			else
			{
				tempRotation = Math.toDegrees(angleAradians);
			}
			
			if (rotation < 90 && tempRotation > 270)
				rotation += 360;
			
			if (rotation > 270 && tempRotation < 90)
				rotation -= 360;
			
			
				if ((int)rotation < (int)tempRotation)
				{
					rotation+=1.2*60*aDelta;
				}
				else
				{
					rotation-=1.2*60*aDelta;
				}
		}
		
		//move the distance by our delta again in order to do a smooth transition between any
		//fps.
		position.x += speedVector.x * aDelta;
		position.y += speedVector.y * aDelta;
		
		
		
		//Set up our garbage collectors
		ArrayList discardedParticles = new ArrayList();
		//loop through our smoke timers
		for (int x = 0; x < smokeParticles.size(); x++)
		{
			Particle tempParticle = (Particle)smokeParticles.get(x);
			//update our particles
			tempParticle.update(aDelta);
			
			//delete old ones
			if (tempParticle.delete)
			{
				discardedParticles.add(tempParticle);				
			}
		}		
		
		//Remove the garbage of timers that have expired.
		smokeParticles.removeAll(discardedParticles);
	}
	
	public void draw()
	{
		
		//Draw our smoke. we dont want a line around it
		if (Main.useColor)
			parent.noStroke();		
		if (verticieParticles.size() == 0)
		{
			for (int x = 0; x < smokeParticles.size(); x++)
			{	
				Particle currentParticle = (Particle)smokeParticles.get(x);
				currentParticle.draw();			
			}
		}
		//reenable our stroke.
		parent.stroke(0);
		
		//if we are spwning
		if (spawningTimer > 0)
		{
			//set text to white/black depending on mode
			if (!Main.useColor)
				parent.fill(255);
			else
				parent.fill(0);
			
			//say the timer count down
			parent.text((int)spawningTimer, position.x, position.y-30);
			
			//Every other second make the ship blink
			if ((int)spawningTimer % 2 == 0)
			{
				objectColor = new Color(objectColor.getRed(), objectColor.getGreen(), objectColor.getBlue(), 255/2);
			}
			else
			{
				objectColor = new Color(objectColor.getRed(), objectColor.getGreen(), objectColor.getBlue(), 255);
			}
		}
		else
		{
			//set the color as normal.
			objectColor = new Color(objectColor.getRed(), objectColor.getGreen(), objectColor.getBlue(), 255);
		}
	
		//draw our polygon shape
		super.draw();
		
		//if we are using mouse control and our ship is alive
		if (Main.mouseControl && !destroied)
		{
			//set text to white/black depending on mode
			if (!Main.useColor)
				parent.fill(255);
			else
				parent.fill(0);
			//Draw our current square indicator as to which rotation our ship
			//is attempting to obtain. This is just a visual thing no real purpose.
			//parent.noStroke();	
			parent.rect((float)(Math.cos(Math.toRadians(tempRotation)) * 35) + position.x, ((float)Math.sin(Math.toRadians(tempRotation)) * 35) + position.y, 5, 5);
			//parent.stroke(0);
		}
		
	}

}
