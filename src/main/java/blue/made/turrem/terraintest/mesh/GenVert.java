package blue.made.turrem.terraintest.mesh;

import blue.made.turrem.terraintest.Vector;
import blue.made.turrem.terraintest.Vector3d;

/**
 * Created by sam on 2/18/16.
 */
public class GenVert extends Vector3d {
	private GenVert next;

	GenVert(Vector pos) {
		super(pos);
	}

	GenVert() {
		super();
	}

	GenVert next() {
		if (next == null) {
			next = new GenVert(this);
		}
		return next;
	}
}
