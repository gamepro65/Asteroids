package asteroids;

import processing.core.PApplet;

public class Bullets {
	
	double bulletx, bullety, crosshairx, crosshairy;
	double speedx, speedy;
	PApplet parent;
	
	//Default constructor
	public Bullets(PApplet papp, int x, int y, int crosshairx, int crosshairy, int speed)
	{
		parent = papp;
		bulletx = x;
		bullety = y;
		//finds missing sides and angles
		//This will auto find the direction the bullet
		//needs to travel.
		double xdis = crosshairx - x;
		double ydis = crosshairy - y;
		double angleAradians = Math.atan2(ydis,xdis);
		double rotation = Math.toDegrees(angleAradians);
		//creates the points to add to next frame
		//This is a way of presetting our increments
		//That way we dont have to do more math fucntions
		//Each cycle.
		speedx = (float)(speed * 60 * Math.cos(Math.toRadians(rotation)));
		speedy = (float)(speed * 60 * Math.sin(Math.toRadians(rotation)));
		
		
	}
	public void update(double aDelta)
	{
		//Increment our location times delta
		//To make smooth transitions
		bulletx += speedx * aDelta;
		bullety += speedy * aDelta;
	}
	
	public void draw()
	{
		//Fill the rect with a greyish white.
		parent.fill(155);
		//render rect in the center of the pisition.
		parent.rectMode(parent.CENTER);
		//draw rect
		parent.rect((float)bulletx, (float)bullety, 3, 3);
		
	}

}
