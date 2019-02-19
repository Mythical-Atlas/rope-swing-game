package main;

public class Physics {
	public static double linearDelta2AngularDelta(double angle, double dx, double dy) { // converts linear momentum to angular momentum relative to a point (or, rather, relative to the angle of a point to an anchor point)
		/*
		
		okay so this is kinda difficult to explain so just bear with me
		
		this function is for a really specific sort of event, where you have 2 points: an anchor point and a moving point
		the moving point will typically be a player object in a game, so it will hereby be refered to as the player point
		
		in this situation, the player point has a speed, represented by a vector (dx, dy), and a position, represented by a vector (x, y)
		the anchor point does not move, but has a position
		
		this function converts the linear momentum of the player point relative to the anchor point to an angular speed, which is used to rotate the player point around the anchor point
		
		this seems super specific, because it is
		
		for instance, if the player point is traveling directly toward or away from the anchor when it "latches on," it doesn't have any rotational speed around the anchor
		however, if it is traveling perpendicular to a theoretical vector pointing from the anchor to the player, it keeps 100% of its original speed
		
		the main problem is all the in-betweens, so it can't just be handled by a bunch of if-statements
		
		because of this, the final angular speed must be calculated by a bunch of equations so it can handle anything in between 0 speed kept and all speed kept
		
		the backbone equation of it is called cross product, similar to dot product
		technically this can only be calculated with 3d vectors, but under the assumption that z is 0 (meaning its relative to a 2d plane), it can return a scalar value instead of a vector
		basically what this does is the closer the 2 vectors (speed of player point and position of player relative to anchor, where anchor is the origin) are to pointing in the same or opposite directions (them being parallel), it returns a value closer to 0
		so if the vectors are perpendicular, it returns a value closer to 1 or -1
		
		then it takes this value and multiplies it by the initial distance traveled using the linear speed, reducing the speed by how far away it is from being perfectly perpendicular
		
		another concept used is distance traveled with linear or angular velocity
		
		if the player has a speed of (1, 0), it travels 1 unit to the right
		if the player has a speed of (0, -1), it travels 1 unit up
		
		using radians, you can convert this to distance traveled around a circle's perimeter, so the actual distance traveled is the same
		
		*/
		
		double da = 0; // delta angular
		
		double l1 = Math.sqrt(dx * dx + dy * dy); // gets length/magnitude/hypotenuse of linear speed vector
		if(l1 != 0) {
			// normalizes linear speed vector
			double nx1 = dx / l1;
			double ny1 = dy / l1;
			
			// gets vector of distance from anchor to point
			double nx2 = Math.cos(angle);
			double ny2 = Math.sin(angle);
			
			double cp = (nx1 * ny2) - (ny1 * nx2); // cross product where z is implied 0 (cross product technically undefined in 2d space)
			
			da = l1 * cp; // reduces linear speed by how close vectors are to being parallel (0 = completely parallel, 1/-1 = completely perpendicular)
		}
		else {da = 0;} // prevents division by 0
		
		return(da); // returns the angular speed (distance traveled around the circle's perimeter)
	}
	
	public static double[] angularDelta2LinearDelta(double angle, double da) { // reverse of above function
		/*
		
		this one's much easier
		it simply takes the original angle between the player and anchor, rotates it by 90 degrees (pi/2 radians), and spits out the cosine and sine of it
		easy peasy
		
		*/
		
		double angle2 = angle - Math.PI / 2;
		
		if(angle2 > Math.PI * 2) {angle2 -= Math.PI * 2;}
		if(angle2 < 0) {angle2 += Math.PI * 2;}
		
		return(new double []{Math.cos(angle2) * da, Math.sin(angle2) * da});
	}
}
