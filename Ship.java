package asteroids;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Vector2f;
import processing.core.PApplet;


//Inherit methods and variables from our abstract entity
public class Ship extends AbstractEntity{
	
	public int size;	
	public int health;
	public float speed;
	public double tempRotation;
	public ArrayList smokeVectors;
	public ArrayList smokeTimers;
	
	//Default constructor
	public Ship(PApplet aParent, Vector2f aPosition)
	{
		//Initialize our parent with our current information;
		super(aParent, aPosition);
		
		//We know our ship is going to have 4 sides
		numberOfSides = 4;	
		//set our traveling speed
		speed = 120;
		
		//initialize our smoke faux particle emitter.
		smokeVectors = new ArrayList();
		smokeTimers = new ArrayList();
		
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
		randomSize = -10;
		mySize = new Double(randomSize);
		verticiesSizes.add(mySize);
		randomSize = 12;
		mySize = new Double(randomSize);
		verticiesSizes.add(mySize);		
		updatePolygon();		
	}
	
	
	
	public void update(double aDelta)
	{
		//Tell our parent to update.
		//This will do our wraping around the world
		//and updating our current rotation;
		super.update(aDelta);
		//get our ships bounds
		Rectangle2D tempRect = verticies.getBounds2D();
		//If we are not inside of the ship bounds with our mouse
		//Then our ship should move forward.
		if (!tempRect.contains(parent.mouseX, parent.mouseY))
		{
			//if we are using mouse control
			if (Main.mouseControl)
			{
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
			
			}
			
			//If we are not using mouse controls our keyboard will change our temprotation
			//This will make our ship still rotate even if the mouse mode is off
			
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
			
			
			//Rotate our ship with a speed of about 80 pixels/s
			//We multiply this by aDelta to make it so our ship
			//will move the same distance reguardless of how long
			//between frame redraws based on the scale of 1/s that has passed
			//this is how we deduce speed is in pixels/s
			if ((int)rotation < (int)tempRotation)  
			{
				rotation+=1.2*60*aDelta;
			}
			else
			{
				rotation-=1.2*60*aDelta;
			}
			
			//move distance by speed at our current rotational angle			
			double x = (float)(speed * Math.cos(Math.toRadians(rotation)));
			double y = (float)(speed * Math.sin(Math.toRadians(rotation)));
			
			//we have moved so lets make a new particle and throw it into our arrays to be rendered.
			//I added some variance to the positions to make it look like a thicker smoke.
			smokeVectors.add(new Vector2f((float)(position.x-3+Math.random()*6), (float)(position.y-3+Math.random()*6)));
			//set the timer to delete the smoke to 15 frames
			smokeTimers.add(new Integer(15));
			
			//move the distance by our delta again in order to do a smooth transition between any
			//fps.
			position.x += x * aDelta;
			position.y += y * aDelta;			
		}
		else
		{
			//if our mouse is inside the ship
			//do all of our rotation stuff just dont let the ship move
			//mouse means the ship is not allowed to move.
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
		
		
		//Set up our garbage collectors
		ArrayList discardedTimers = new ArrayList();
		ArrayList discardedVectors = new ArrayList();
		//loop through our smoke timers
		for (int x = 0; x < smokeTimers.size(); x++)
		{
			//get the current int from the timer.
			Integer tempInt = (Integer)smokeTimers.get(x);
			int time = tempInt.intValue();
			
			//subtract 1 from the time
			smokeTimers.set(x, new Integer(--time));
			//if the time is <= 0 then add those 2 corresponding
			//smoke effects to the trash
			if (tempInt.intValue() <= 0)
			{
				discardedTimers.add(smokeTimers.get(x));
				discardedVectors.add(smokeVectors.get(x));
				
			}
		}		
		
		//Remove the garbage of timers that have expired.
		smokeTimers.removeAll(discardedTimers);
		smokeVectors.removeAll(discardedVectors);
	}
	
	public void draw()
	{
		//Draw our smoke. we dont want a line around it
		parent.noStroke();				
		for (int x = 0; x < smokeVectors.size(); x++)
		{	
			//Get current time and set the color to yellow with an alpha
			//percent of timer to timer end. This makes our nice alpha out
			//Instead of an abrupt end to the smoke it dissiapates.
			Integer time = (Integer)smokeTimers.get(x);
			Color smokeColor = new Color(1f,1f,0f,(float)((float)time.intValue()/15));
			parent.fill(smokeColor.getRed(), smokeColor.getGreen(), smokeColor.getBlue(), smokeColor.getAlpha());
			Vector2f temp = (Vector2f)smokeVectors.get(x);
			parent.rect(temp.x, temp.y, 5, 5);
		}
		//reenable our stroke.
		parent.stroke(0);
		//draw our polygon shape
		super.draw();
		//Draw our current square indicator as to which rotation our ship
		//is attempting to obtain. This is just a visual thing no real purpose.
		parent.noStroke();	
		parent.rect((float)(Math.cos(Math.toRadians(tempRotation)) * 35) + position.x, ((float)Math.sin(Math.toRadians(tempRotation)) * 35) + position.y, 5, 5);
		parent.stroke(0);
		
	}

}
