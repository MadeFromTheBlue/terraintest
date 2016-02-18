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

	public static class Info {
		public long opnum = 0;
		public int vertnum = 0;
		public int facenum = 0;
		public int edgenum = 0;

		public String toString() {
			return String.format("Verts:%d Faces:%d Edges:%d Ops:%d", vertnum, facenum, edgenum, opnum);
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
			return build(0);
		}

		public GenMesh build(int layer) {
			GenMesh mesh = new GenMesh(layer);
			mesh.verts.addAll(verts.values());
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
	public final int layer;

	private GenMesh(int layer) {
		this.layer = layer;
	}

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

	private void connectNewVerts(GenVert a, GenVert b, GenVert c) {
		Face f = new Face(a, b, c);
		faces.add(f);
		createNewEdge(f.a, f.b);
		createNewEdge(f.b, f.c);
		createNewEdge(f.c, f.a);
	}

	/**
	 * Returns the edge the given verts if it already exists, otherwise it is created and added
	 */
	private Edge createEdge(GenVert a, GenVert b) {
		Edge e = new Edge(a, b);
		Edge got = edges.get(e);
		if (got == null) {
			got = e;
			createNewEdge(e);
		}
		return got;
	}

	private Edge getEdge(GenVert a, GenVert b) {
		return edges.get(new Edge(a, b));
	}

	private Edge createNewEdge(GenVert a, GenVert b) {
		return createNewEdge(new Edge(a, b));
	}

	private Edge createNewEdge(Edge e) {
		edges.put(e, e);
		e.a.links.add(new GenVert.Link(e, e.b));
		e.b.links.add(new GenVert.Link(e, e.a));
		return e;
	}

	private GenMesh subdivide() {
		GenMesh out = new GenMesh(layer + 1);
		System.out.println("\tSubdivide...duplicate verts");
		for (GenVert v : this.verts) {
			// duplicate verts to next layer
			GenVert down = new GenVert(v.pos.clone());
			out.verts.add(down);
			v.down = down;
		}
		System.out.println("\tSubdivide...midpoints and side edges");
		for (Edge e : this.edges.values()) {
			// create midpoints
			GenVert mid = new GenVert((Vector3d) Vector.mix(e.a.pos, e.b.pos, 0.5));
			e.mid = mid;
			out.verts.add(mid);
			// link midpoint
			out.createNewEdge(e.a.down, mid);
			out.createNewEdge(e.b.down, mid);
		}
		System.out.println("\tSubdivide...fill faces and inner edges");
		for (Face f : faces) {
			// fill faces
			// get face edges
			Edge ab = getEdge(f.a, f.b);
			Edge bc = getEdge(f.b, f.c);
			Edge ca = getEdge(f.c, f.a);
			// create middle face with edges
			out.connectNewVerts(ab.mid, bc.mid, ca.mid);
			// edges should already exist, just add faces
			out.faces.add(new Face(f.a.down, ab.mid, ca.mid));
			out.faces.add(new Face(f.b.down, ab.mid, bc.mid));
			out.faces.add(new Face(f.c.down, bc.mid, ca.mid));
		}
		System.out.println("\tSubdivide...done");
		return out;
	}

	public GenMesh run() {
		return run(null);
	}

	public GenMesh run(Info info) {
		if (info == null)
			info = new Info();

		if (next == null) {
			next = this.subdivide();
			for (GenVert v : this.verts) {
				VertModder modder = new VertModder(v, layer);
				while (!v.ops.isEmpty()) {
					info.opnum++;
					v.ops.poll().run(modder);
				}
			}
		}

		info.vertnum = next.verts.size();
		info.facenum = next.faces.size();
		info.edgenum = next.edges.size();
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
