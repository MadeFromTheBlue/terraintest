package blue.made.turrem.terraintest.mesh;

import blue.made.turrem.terraintest.Vector3d;
import blue.made.turrem.terraintest.ops.IMeshOp;

import java.util.LinkedList;

/**
 * Created by doctorocclusion on 2/16/2016.
 */
public class GenVert {
	public static class Link {
		public final GenMesh.Edge edge;
		public final GenVert other;

		public Link(GenMesh.Edge edge, GenVert other) {
			this.edge = edge;
			this.other = other;
		}
	}
	public Vector3d pos;
	private Vector3d norm;
	GenVert down;
	LinkedList<Link> links = new LinkedList<>();
	public LinkedList<IMeshOp> ops = new LinkedList<>();

	public GenVert(Vector3d pos) {
		this.pos = pos.clone();
	}
}