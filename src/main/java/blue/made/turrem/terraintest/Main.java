package blue.made.turrem.terraintest;

import blue.made.turrem.terraintest.mesh.Face;
import blue.made.turrem.terraintest.mesh.GenMesh;
import blue.made.turrem.terraintest.ops.Perlin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by doctorocclusion on 2/16/2016.
 */
public class Main {
    public static void main(String[] args) throws IOException {
		GenMesh mesh;
		{
			Perlin p = new Perlin(0.5);
			GenMesh.Builder b = GenMesh.create();
			b.addVert(new Vector3d(0, 0, 0), 0);
			b.addVert(new Vector3d(1, 0, 0), 1);
			b.addVert(new Vector3d(0.5, 0.866025, 0), 2);
			b.addVert(new Vector3d(1.5, 0.866025, 0), 3);

			b.addFace(0, 1, 2).todo().add(p);
			b.addFace(1, 2, 3).todo().add(p);

			mesh = b.get();
		}

		(new File("testout/")).mkdir();
		mesh.run();
		for (int i = 0; i < 8; i++) {
			long t = System.nanoTime();
			mesh = mesh.subdivide();
			mesh.run();
			double dt = (System.nanoTime() - t) * 1e-6;
			System.out.printf("New Mesh #%d: Tris:%d in %.1fms\n", mesh.layer, mesh.getFaces().size(), dt);
		}
		PrintWriter out = new PrintWriter("testout/out" + ".obj");
		for (Face f : mesh.getFaces()) {
			out.printf("v %f %f %f\n", f.verts[0].x, f.verts[0].y, f.verts[0].z);
			out.printf("v %f %f %f\n", f.verts[1].x, f.verts[1].y, f.verts[1].z);
			out.printf("v %f %f %f\n", f.verts[2].x, f.verts[2].y, f.verts[2].z);
			out.printf("f -1 -2 -3\n");
		}
		out.close();
	}
}
