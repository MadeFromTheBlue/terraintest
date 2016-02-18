package blue.made.turrem.terraintest.mesh;

import blue.made.turrem.terraintest.Vector3d;
import blue.made.turrem.terraintest.ops.IMeshOp;

import java.util.LinkedList;

/**
 * Created by doctorocclusion on 2/16/2016.
 */
public class GenVert {
	public Vector3d pos;
	private Vector3d norm;
	GenVert down;

	public GenVert(Vector3d pos) {
		this.pos = pos;
	}
}