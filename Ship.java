package asteroids;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Vector2f;
import processing.core.PApplet;



public class Ship extends AbstractEntity{
	
	int size;	
	int health;
	float speed;
	ArrayList smokeVectors;
	ArrayList smokeTimers;
	
	
	public Ship(PApplet aParent, Vector2f aPosition)
	{
		super(aParent, aPosition);
		Random ran = new Random();
		numberOfSides = 4;	
		speed = 120;
		
		smokeVectors = new ArrayList();
		smokeTimers = new ArrayList();
		
		objectColor = new Color(.99f,.99f,.99f,1);
		
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
		super.update(aDelta);
		Rectangle2D tempRect = verticies.getBounds2D();
		if (!tempRect.contains(parent.mouseX, parent.mouseY))
		{
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
			
			
			if (rotation-tempRotation > 180)
			{
				tempRotation += 360;
			}
			if (rotation-tempRotation < -180)
			{
				tempRotation -= 360;
			}
			
			if ((int)rotation < (int)tempRotation)  
			{
				rotation+=1.2*60*aDelta;
			}
			else
			{
				rotation-=1.2*60*aDelta;
			}
			
			double x = (float)(speed * Math.cos(Math.toRadians(rotation)));
			double y = (float)(speed * Math.sin(Math.toRadians(rotation)));

			smokeVectors.add(new Vector2f((float)(position.x-3+Math.random()*6), (float)(position.y-3+Math.random()*6)));
			smokeTimers.add(new Integer(15));
			
			position.x += x * aDelta;
			position.y += y * aDelta;			
		}
		else
		{
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
		ArrayList discardedTimers = new ArrayList();
		ArrayList discardedVectors = new ArrayList();
		for (int x = 0; x < smokeTimers.size(); x++)
		{
			Integer tempInt = (Integer)smokeTimers.get(x);
			int time = tempInt.intValue();
			smokeTimers.set(x, new Integer(--time));
			if (tempInt.intValue() <= 0)
			{
				discardedTimers.add(smokeTimers.get(x));
				discardedVectors.add(smokeVectors.get(x));
				
			}
		}		
		smokeTimers.removeAll(discardedTimers);
		smokeVectors.removeAll(discardedVectors);
	}
	
	public void draw()
	{
		
		parent.noStroke();				
		for (int x = 0; x < smokeVectors.size(); x++)
		{	
			Integer time = (Integer)smokeTimers.get(x);
			Color smokeColor = new Color(1f,1f,0f,(float)((float)time.intValue()/15));
			parent.fill(smokeColor.getRed(), smokeColor.getGreen(), smokeColor.getBlue(), smokeColor.getAlpha());
			Vector2f temp = (Vector2f)smokeVectors.get(x);
			parent.rect(temp.x, temp.y, 5, 5);
		}
		parent.stroke(0);
		super.draw();		
		
	}

}
