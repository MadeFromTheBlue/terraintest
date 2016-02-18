package blue.made.turrem.terraintest.mesh;

import blue.made.turrem.terraintest.Vector;
import blue.made.turrem.terraintest.Vector3d;

import java.util.Iterator;

/**
 * Created by doctorocclusion on 2/16/2016.
 */
public class VertModder {
	public class Modder {
		// DO NOT MODIFY
		public final GenVert center;
		// DO NOT MODIFY
		public final GenVert edge;

		// MODIFY
		public final GenVert mid;

		Modder(GenVert center, GenVert edge, GenVert mid) {
			this.mid = mid;
			this.center = center;
			this.edge = edge;
		}
	}

	private GenVert vert;
	public final int layer;

	public VertModder(GenVert vert, int layer) {
		this.vert = vert;
		this.layer = layer;
	}

	public GenVert getDown() {
		return vert.down;
	}

	public Iterator<Modder> getNeighbors() {
		final Iterator<GenVert.Link> it = vert.links.iterator();

		return new Iterator<Modder>() {
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public Modder next() {
				GenVert.Link edge = it.next();
				return new Modder(vert, edge.other, edge.edge.mid);
			}
		};
	}

	public Modder inDirection(Vector3d dir) {
		double len = dir.length();
		GenVert closest = null;
		GenVert mid = null;
		double cdot = -1;
		for (GenVert.Link e : vert.links) {
			double dot = Vector.dot(Vector.subtract(e.other.pos, vert.pos), dir);
			// dot is now equal to dir.length() * edge.len() * cos(theta)
			dot /= e.edge.len();
			dot /= len;
			// dot is now equal to cos(theta)
			if (dot > cdot) {
				mid = e.edge.mid;
				closest = e.other;
				cdot = dot;
			}
		}
		return new Modder(vert, closest, mid);
	}
}
