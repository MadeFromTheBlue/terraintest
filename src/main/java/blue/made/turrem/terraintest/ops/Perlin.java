package blue.made.turrem.terraintest.ops;

import blue.made.turrem.terraintest.mesh.VertModder;

import java.util.Random;

/**
 * Created by doctorocclusion on 2/17/2016.
 */
public class Perlin implements IMeshOp {
    public static Random rand = new Random();

    @Override
    public void run(VertModder vert) {
        vert.getDown().pos.z += (rand.nextDouble() - 0.5) / (1 << vert.layer);
        vert.getDown().ops.add(this);
        vert.getNeighbors().forEachRemaining(modder -> {
            if (modder.mid.ops.isEmpty())
                modder.mid.ops.add(this);
        });
    }
}
