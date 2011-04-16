package asteroids;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Vector2f;
import processing.core.PApplet;



public class Asteroid extends AbstractEntity{

	private Vector2f driftSpeed;
	int size;
	int health;
	
	public Asteroid(PApplet aParent, Vector2f aPosition, int aSize)
	{
		super(aParent, aPosition);
		Random ran = new Random();
		size = aSize;
		verticiesSizes = new ArrayList();		
		numberOfSides = (int)(ran.nextDouble() * 10)+4;
		health = size/10;
		double randomAngle = ran.nextDouble()*360;
		driftSpeed = new Vector2f((float)(Math.cos(Math.toRadians(randomAngle))*ran.nextDouble()*180), (float)(Math.sin(Math.toRadians(randomAngle))*ran.nextDouble()*180));
		for (int x = 0; x < 360-(360/(numberOfSides)); x+= (360/(numberOfSides)))
		{
			double randomSize = (ran.nextDouble() * size-10)+15;
			Double mySize = new Double(randomSize);
			verticiesSizes.add(mySize);
		}
	}
	

	public void update(double aDelta)
	{
		super.update(aDelta);
		position.x += (float)(driftSpeed.x * aDelta);
		position.y += (float)(driftSpeed.y * aDelta);
	}
		
	public void draw()
	{	
		super.draw();		
	}

}
