package blue.made.turrem.terraintest.mesh;

import blue.made.turrem.terraintest.Vector;
import blue.made.turrem.terraintest.Vector3d;

import java.util.*;

/**
 * Created by sam on 2/16/16.
 */
public class GenMesh {
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
		public int facenum = 0;
		public int maxops = 0;

		public String toString() {
			return String.format("Faces:%d Ops:%d MaxOps: %d", facenum, opnum, maxops);
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
			mesh.faces.addAll(faces);
			return mesh;
		}
	}

	private ArrayList<Face> faces = new ArrayList<>();
	private GenMesh next = null;
	public final int layer;

	private GenMesh(int layer) {
		this.layer = layer;
	}

	private void add(Face f) {
		faces.add(f);
	}

	private void add(GenVert a, GenVert b, GenVert c) {
		faces.add(new Face(a, b, c));
	}

	private GenVert mid(GenVert a, GenVert b) {
		return new GenVert((Vector3d) Vector.mix(a.pos, b.pos, 0.5));
	}

	private GenMesh subdivide() {
		GenMesh out = new GenMesh(layer + 1);
		for (Face f : faces) {
			GenVert a = new GenVert(f.a.pos.clone());
			GenVert b = new GenVert(f.b.pos.clone());
			GenVert c = new GenVert(f.c.pos.clone());
			GenVert ab = mid(a, b);
			GenVert bc = mid(b, c);
			GenVert ca = mid(c, a);

			out.add(ab, bc, ca);
			out.add(a, ab, ca);
			out.add(b, ab, bc);
			out.add(c, bc, ca);
		}
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
			for (Face f : next.faces) {

			}
		}

		info.facenum = next.faces.size();
		return next;
	}

	public List<Face> getFaces() {
		return Collections.unmodifiableList(faces);
	}

	public static Builder create() {
		return new Builder();
	}
}
