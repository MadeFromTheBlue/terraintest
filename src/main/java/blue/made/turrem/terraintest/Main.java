package blue.made.turrem.terraintest;

import blue.made.turrem.terraintest.mesh.GenMesh;

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
			GenMesh.Builder b = GenMesh.create();
			b.addVert(new Vector3d(0, 0, 0), 0);
			b.addVert(new Vector3d(1, 0, 0), 1);
			b.addVert(new Vector3d(0.5, 0.866025, 0), 2);
			b.addVert(new Vector3d(1.5, 0.866025, 0), 3);

			b.addFace(0, 1, 2);
			b.addFace(1, 2, 3);

			mesh = b.build();
		}

		(new File("testout/")).mkdir();
		for (int i = 0; i <= 10; i++) {
			if (i != 0) {
				GenMesh.Info geninfo = new GenMesh.Info();
				long t = System.nanoTime();
				mesh = mesh.run(geninfo);
				double dt = (System.nanoTime() - t) * 1e-6;
				System.out.printf("New Mesh #%d: %s in %.2fms\n", mesh.layer, geninfo, dt);
			}
		}
		PrintWriter out = new PrintWriter("testout/out" + ".obj");
		for (GenMesh.Face f : mesh.getFaces()) {
			out.printf("v %f %f %f\n", f.a.pos.x, f.a.pos.y, f.a.pos.z);
			out.printf("v %f %f %f\n", f.b.pos.x, f.b.pos.y, f.b.pos.z);
			out.printf("v %f %f %f\n", f.c.pos.x, f.c.pos.y, f.c.pos.z);
			out.printf("f -1 -2 -3\n");
		}
		out.close();
	}
}
