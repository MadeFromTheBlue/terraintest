package blue.made.turrem.terraintest.mesh;

import blue.made.turrem.terraintest.Vector3d;
import blue.made.turrem.terraintest.ops.IMeshOp;

import java.util.LinkedList;

/**
 * Created by sam on 2/18/16.
 */
public class Face {
	public class OpList extends LinkedList<IMeshOp> {};

	public final GenVert[] verts = new GenVert[3];
	public final OpList[] childOps = new OpList[4];
	protected OpList ops;
	private final GenMesh mesh;

	public Face(GenVert a, GenVert b, GenVert c, GenMesh mesh) {
		this.verts[0] = a;
		this.verts[1] = b;
		this.verts[2] = c;
		this.mesh = mesh;
		for (int i = 0; i < 4; i++) {
			childOps[i] = new OpList();
		}
	}

	public GenMesh.Info info() {
		return mesh.info;
	}

	public OpList todo() {
		if (ops == null) {
			ops = new OpList();
		}
		return ops;
	}
}
