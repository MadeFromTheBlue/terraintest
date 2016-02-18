package blue.made.turrem.terraintest.ops;

import blue.made.turrem.terraintest.Vector3d;
import blue.made.turrem.terraintest.mesh.Face;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by sam on 2/18/16.
 */
public class Perlin implements IMeshOp {
	public static Random rand = new Random();

	public final double scale;

	public Perlin(double scale) {
		this.scale = scale;
	}

	@Override
	public void run(Face a) {
		double dz = (2 * rand.nextDouble() - 1) * scale;
		for (Vector3d v : a.verts) {
			v.z += dz;
		}
		Perlin next = new Perlin(scale * 0.5);
		for (Face.OpList c : a.childOps) {
			c.add(next);
		}
	}
}
