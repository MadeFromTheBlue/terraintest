package blue.made.turrem.terraintest.mesh;

import blue.made.turrem.terraintest.Vector;
import blue.made.turrem.terraintest.Vector3d;
import blue.made.turrem.terraintest.ops.IMeshOp;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import java.util.*;

/**
 * Created by sam on 2/16/16.
 */
public class GenMesh {
	static class Edge {
		public GenVert a;
		public GenVert b;
		public GenVert mid;
		private double len = Double.NaN;

		private Edge(GenVert a, GenVert b) {
			this.a = a;
			this.b = b;
		}

		public double len() {
			if (len != Double.NaN)
				return len;
			len = Vector.subtract(a.pos, b.pos).length();
			return len;
		}

		public int hashCode() {
			return a.hashCode() ^ b.hashCode();
		}

		public boolean equals(Object obj) {
			if (obj instanceof Edge) {
				Edge other = (Edge) obj;
				return (other.a == this.a && other.b == this.b) || (other.b == this.a && other.a == this.b);
			}
			return false;
		}
	}

	public static class Face {
		public GenVert a;
		public GenVert b;
		public GenVert c;

		public Face(GenVert a, GenVert b, GenVert c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}

	public static class Builder {
		private HashMap<Integer, GenVert> verts = new HashMap<>();
		private ArrayList<Face> faces = new ArrayList<>();

		private Builder() {}

		public GenVert addVert(Vector3d vert, int id) {
			GenVert out = new GenVert(vert);
			verts.put(id, out);
			return out;
		}

		public void addFace(int a, int b, int c) {
			Face f = new Face(verts.get(a), verts.get(b), verts.get(c));
			if (f.a == null || f.b == null || f.c == null) {
				throw new IllegalArgumentException("One of the verts has not been added");
			}
			faces.add(f);
		}

		public GenMesh build() {
			GenMesh mesh = new GenMesh();
			for (Face f : faces) {
				mesh.connectVerts(f);
			}
			return mesh;
		}
	}

	private HashMap<Edge, Edge> edges = new HashMap<>(); // I cant use an Interner because they can't be iterated over
	private ArrayList<GenVert> verts = new ArrayList<>();
	private ArrayList<Face> faces = new ArrayList<>();
	private GenMesh next = null;

	private GenMesh() {}

	/**
	 * Creates and adds the edges and the face between the given verts
	 */
	private void connectVerts(GenVert a, GenVert b, GenVert c) {
		connectVerts(new Face(a, b, c));
	}

	private void connectVerts(Face f) {
		faces.add(f);
		createEdge(f.a, f.b);
		createEdge(f.b, f.c);
		createEdge(f.c, f.a);
	}

	/**
	 * Returns the edge the given verts if it already exists, otherwise it is created and added
	 */
	private Edge createEdge(GenVert a, GenVert b) {
		Edge e = new Edge(a, b);
		Edge got = edges.get(e);
		if (got == null) {
			edges.put(e, e);
			got = e;
		}
		return got;
	}

	private GenMesh subdivide() {
		GenMesh out = new GenMesh();
		for (Edge e : this.edges.values()) {
			// create midpoints
			GenVert mid = new GenVert((Vector3d) Vector.mix(e.a.pos, e.b.pos, 0.5));
			e.mid = mid;
			out.verts.add(mid);
		}
		for (GenVert v : this.verts) {
			// duplicate verts to next layer
			GenVert down = new GenVert(v.pos.clone());
			out.verts.add(down);
			v.down = down;
			// for all midpoints connected to the new center vert
			/*for (GenVert.Link e : v.links) {
				// add edge to center
				Edge add = out.createEdge(v, e.edge.mid);
				down.links.add(new GenVert.Link(add, e.edge.mid));
			}*/
		}
		for (Face f : faces) {
			// fill faces
			// get face edges
			Edge ab = createEdge(f.a, f.b);
			Edge bc = createEdge(f.b, f.c);
			Edge ca = createEdge(f.c, f.a);
			// create middle face with edges
			out.connectVerts(ab.mid, bc.mid, ca.mid);
			// edges should already exist, just add faces
			out.connectVerts(new Face(f.a, ab.mid, ca.mid));
			out.connectVerts(new Face(f.b, ab.mid, bc.mid));
			out.connectVerts(new Face(f.c, bc.mid, ca.mid));
		}
		return out;
	}

	public GenMesh run() {
		if (next == null) {
			next = this.subdivide();
			for (GenVert v : this.verts) {
				VertModder modder = new VertModder(v);
				while (!v.ops.isEmpty()) {
					v.ops.poll().run(modder);
				}
			}
		}
		return next;
	}

	public List<GenVert> getVerts() {
		return Collections.unmodifiableList(verts);
	}

	public List<Face> getFaces() {
		return Collections.unmodifiableList(faces);
	}

	public static Builder create() {
		return new Builder();
	}
}
