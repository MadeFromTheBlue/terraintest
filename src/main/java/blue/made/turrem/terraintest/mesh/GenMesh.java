package blue.made.turrem.terraintest.mesh;

import blue.made.turrem.terraintest.Vector;
import blue.made.turrem.terraintest.Vector3d;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import java.util.*;

/**
 * Created by sam on 2/16/16.
 */
public class GenMesh {
	public static class DebugData {
		public long opnum = 0;
		public int facenum = 0;
		public long subdms;
		public long opsms;

		public String toString() {
			return String.format("Faces:%d Ops:%d in Subd:%.3fs Ops:%.3fs", facenum, opnum, subdms / 1000., opsms / 1000.);
		}
	}

	public class Info {
		public int layer() {
			return GenMesh.this.layer;
		}
	}

	private static class Pair {
		final GenVert a;
		final GenVert b;
		GenVert mid;

		public Pair(GenVert a, GenVert b) {
			this.a = a;
			this.b = b;
		}

		public int hashCode() {
			return a.hashCode() ^ b.hashCode();
		}

		public boolean equals(Object obj) {
			if (obj instanceof Pair) {
				Pair other = (Pair) obj;
				return (this.a == other.a && this.b == other.b) || (this.a == other.b && this.b == other.a);
			}
			return false;
		}
	}

	public static class Builder {
		private HashMap<Integer, GenVert> verts = new HashMap<>();
		private GenMesh out;

		private Builder() {
			this(0);
		}

		private Builder(int layer) {
			out = new GenMesh(layer);
		}

		public void addVert(Vector3d vert, int id) {
			verts.put(id, new GenVert(vert));
		}

		public Face addFace(int a, int b, int c) {
			Face f = new Face(verts.get(a), verts.get(b), verts.get(c), out);
			if (f.verts[0] == null || f.verts[1] == null || f.verts[2] == null) {
				throw new IllegalArgumentException("One of the verts has not been added");
			}
			out.faces.add(f);
			return f;
		}

		public GenMesh get() {
			return out;
		}
	}

	private ArrayList<Face> faces = new ArrayList<>();
	private Interner<Pair> edges = Interners.newStrongInterner();
	public final int layer;
	public final Info info = new Info();

	private GenMesh(int layer) {
		this.layer = layer;
	}

	private Face add(Face f) {
		faces.add(f);
		return f;
	}

	private Face add(GenVert a, GenVert b, GenVert c) {
		Face f = new Face(a, b, c, this);
		faces.add(f);
		return f;
	}

	private GenVert mid(GenVert a, GenVert b) {
		Pair p = edges.intern(new Pair(a, b));
		if (p.mid == null) {
			p.mid = new GenVert(a);
			p.mid.add(b);
			p.mid.multiply(0.5);
		}
		return p.mid;
	}

	public GenMesh subdivide() {
		GenMesh out = new GenMesh(layer + 1);
		for (Face f : faces) {
			GenVert a = f.verts[0].next();
			GenVert b = f.verts[1].next();
			GenVert c = f.verts[2].next();
			GenVert ab = mid(a, b);
			GenVert bc = mid(b, c);
			GenVert ca = mid(c, a);

			out.add(ab, bc, ca).ops = f.childOps[3];
			out.add(a, ab, ca).ops = f.childOps[0];
			out.add(b, ab, bc).ops = f.childOps[1];
			out.add(c, bc, ca).ops = f.childOps[2];
		}
		return out;
	}

	public void run() {
		for (Face f : faces) {
			while (!f.ops.isEmpty()) {
				f.ops.poll().run(f);
			}
		}
	}

	public List<Face> getFaces() {
		return Collections.unmodifiableList(faces);
	}

	public static Builder create() {
		return new Builder();
	}
}
