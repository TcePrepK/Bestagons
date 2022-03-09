package game;

import core.imageBuffers.ImageBuffer3D;
import elements.Element;
import elements.ElementRegistry;
import org.lwjgl.BufferUtils;
import toolbox.Maths;
import toolbox.Noise;
import toolbox.Points.Point3D;
import toolbox.Vector3D;

import java.nio.ByteBuffer;

import static core.GlobalVariables.*;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL30.GL_R8;

public class Chunk {
    private final Point3D pos;
    private final String id;

    private int minX, maxX, minY, maxY, minZ, maxZ;
    private int minXw, maxXw, minYw, maxYw, minZw, maxZw;

    private final Point3D chunkScale = new Point3D(mapChunkSize);
    private final Element[] grid = new Element[chunkScale.x * chunkScale.y * chunkScale.z];
    private final byte[] idGrid = new byte[chunkScale.x * chunkScale.y * chunkScale.z];

    private final ByteBuffer voxelBuffer = BufferUtils.createByteBuffer(mapChunkSize * mapChunkSize * mapChunkSize);
    private final ImageBuffer3D chunkBuffer = new ImageBuffer3D(chunkScale, 0, 0, GL_R8, GL_RED, GL_UNSIGNED_BYTE);
    private boolean updateBuffer = true;

//    private final int bitmaskSize = 4;
//    private final Point3D bitmaskScale = chunkScale.div(bitmaskSize);
//    private final int[] bitmaskGrid = new int[bitmaskScale.x * bitmaskScale.y * bitmaskScale.z];

    public Chunk(final int x, final int y, final int z, final String id) {
        pos = new Point3D(x, y, z).mult(mapChunkSize);
        this.id = id;

        chunkBuffer.create(null);

        generateTerrain(100);
        generateCaves(100);
//        generateSponge();

//        generateNoiseChunk();
    }

    public void generateTerrain(final float scale) {
        final float lightScale1 = 100;
        final float lightScale2 = 10;
        for (int offX = 0; offX < chunkScale.x; offX++) {
            final int finalX = pos.x + offX;
            for (int offZ = 0; offZ < chunkScale.z; offZ++) {
                final int finalZ = pos.z + offZ;

                final float height;
                if (pos.y < (2 * chunkViewDistance - 1) * mapChunkSize) {
                    height = mapChunkSize;
                } else {
                    height = (float) Math.abs(Noise.noise(finalX / scale, finalZ / scale)) * mapChunkSize;
                }

                for (int finalY = pos.y; finalY < pos.y + height; finalY++) {
                    final double noise1 = Noise.noise(finalX / lightScale1, finalY / lightScale1, finalZ / lightScale1) * 0.85;
                    final double noise2 = Noise.noise(finalX / lightScale2, finalY / lightScale2, finalZ / lightScale2) * 0.15;
                    if (noise1 + noise2 < 0.05) {
                        setElement(finalX, finalY, finalZ, ElementRegistry.getElementByName("Sand"));
                    } else {
                        setElement(finalX, finalY, finalZ, ElementRegistry.getElementByName("Dirt"));
                    }
                }
            }
        }
    }

    public void generateCaves(final float scale) {
        for (int offX = 0; offX < chunkScale.x; offX++) {
            final int finalX = pos.x + offX;
            for (int offY = 0; offY < chunkScale.y; offY++) {
                final int finalY = pos.y + offY;
                for (int offZ = 0; offZ < chunkScale.z; offZ++) {
                    final int finalZ = pos.z + offZ;

                    final float val = (float) Math.abs(Noise.noise(finalX / scale, finalY / scale, finalZ / scale));
                    if (val > 0.12) {
                        continue;
                    }

                    final Element element = getElement(finalX, finalY, finalZ);
                    if (element == null) {
                        continue;
                    }

                    setElement(finalX, finalY, finalZ, null);
                }
            }
        }
    }

    public void generateNoiseChunk() {
        final int noiseX = (int) (rand.nextFloat() * 1000);
        final int noiseY = (int) (rand.nextFloat() * 1000);
        final int noiseZ = (int) (rand.nextFloat() * 1000);
        final float scale1 = rand.nextFloat() * mapChunkSize * 2;
        final float scale2 = 50;
        for (int offX = 0; offX < chunkScale.x; offX++) {
            final int finalX = pos.x + offX;
            for (int offY = 0; offY < chunkScale.y; offY++) {
                final int finalY = pos.y + offY;
                for (int offZ = 0; offZ < chunkScale.z; offZ++) {
                    final int finalZ = pos.z + offZ;
                    final float val = (float) Math.abs(Noise.noise(finalX / scale1 + noiseX, finalY / scale1 + noiseY, finalZ / scale1 + noiseZ));

                    if (val < 0.5) {
                        continue;
                    }

                    if (Math.abs(Noise.noise(finalX / scale2, finalY / scale2, finalZ / scale2)) < 0.01) {
                        setElement(finalX, finalY, finalZ, ElementRegistry.getElementByName("Sand"));
                    } else {
                        setElement(finalX, finalY, finalZ, ElementRegistry.getElementByName("Dirt"));
                    }
                }
            }
        }
    }

    public void generateSponge() {
        for (int offX = 0; offX < chunkScale.x; offX++) {
            for (int offY = 0; offY < chunkScale.y; offY++) {
                for (int offZ = 0; offZ < chunkScale.z; offZ++) {
                    final Vector3D normPos = new Vector3D(pos.x + offX, pos.y + offY, pos.z + offZ).div(world.getWorldScale().toVector3D());

                    int iter = 0;
                    boolean hit = true;
                    Point3D voxel = normPos.mult(3).sub(1).toPoint3D();
                    while (iter <= 4) {
                        final Point3D absVoxel = voxel.abs();
                        if (absVoxel.x + absVoxel.y + absVoxel.z <= 1) {
                            hit = false;
                            break;
                        }

                        iter++;

                        final float power = (float) Math.pow(3, iter);
                        final Vector3D location = normPos.mult(power).floor();
                        voxel = location.mod(3).sub(1).toPoint3D();
                    }

                    if (hit) {
                        setElement(pos.x + offX, pos.y + offY, pos.z + offZ, ElementRegistry.getElementByName("Dirt"));
                    }
                }
            }
        }
    }

    public void updateBuffer() {
        if (!updateBuffer) {
            return;
        }

        voxelBuffer.flip();
        chunkBuffer.updatePixels(voxelBuffer);
        voxelBuffer.clear();

        updateBuffer = false;
    }

    public void awakeGrid(final int x, final int y, final int z) {
        minXw = Math.min(minXw, x - 1);
        minYw = Math.min(minYw, y - 1);
        minZw = Math.min(minZw, z - 1);

        maxXw = Math.max(maxXw, x + 2);
        maxYw = Math.max(maxYw, y + 2);
        maxZw = Math.max(maxZw, z + 2);

//        if (world.getChunkUpdateList().contains(this)) {
//            return;
//        }
//
//        world.getChunkUpdateList().add(this);
    }

    public void awakeGrid(final Point3D pos) {
        awakeGrid(pos.x, pos.y, pos.z);
    }

    public void updateRect(final boolean updatedThisFrame) {
        minX = (int) Maths.clamp(minXw, pos.x, pos.x + chunkScale.x);
        minY = (int) Maths.clamp(minYw, 0, chunkScale.y);
        minZ = (int) Maths.clamp(minZw, pos.z, pos.z + chunkScale.z);
        maxX = (int) Maths.clamp(maxXw, pos.x, pos.x + chunkScale.x);
        maxY = (int) Maths.clamp(maxYw, 0, chunkScale.y);
        maxZ = (int) Maths.clamp(maxZw, pos.z, pos.z + chunkScale.z);

        if (updatedThisFrame) {
            minXw = pos.x + chunkScale.x;
            minYw = chunkScale.y;
            minZw = pos.z + chunkScale.z;
            maxXw = pos.x;
            maxYw = 0;
            maxZw = pos.z;
        } else {
            minXw = 0;
            minYw = 0;
            minZw = 0;
            maxXw = 0;
            maxYw = 0;
            maxZw = 0;
        }
    }

    public int getMinHeight(final int x, final int z) {
        int currentHeight = 0;
        while (currentHeight < chunkScale.y) {
            final Element e = getElement(x, currentHeight, z);
            if (e == null) {
                return currentHeight;
            }

            currentHeight++;
        }

        return currentHeight;
    }

    public void setElement(final int x, final int y, final int z, final Element e) {
        if (outBounds(x, y, z)) {
            return;
        }

        final int idx = getIDX(x - pos.x, y - pos.y, z - pos.z);
        grid[idx] = e;
        idGrid[idx] = (byte) (e == null ? 0 : e.getId());

        voxelBuffer.put(idx, (byte) (e == null ? 0 : e.getId()));

        updateBuffer = true;
    }

    public void setElement(final Point3D pos, final Element e) {
        setElement(pos.x, pos.y, pos.z, e);
    }

    public Element getElement(final int x, final int y, final int z) {
        if (outBounds(x, y, z)) {
            return null;
        }

        return grid[getIDX(x - pos.x, y - pos.y, z - pos.z)];
    }

    public Element getElement(final Point3D pos) {
        return getElement(pos.x, pos.y, pos.z);
    }

    public boolean outBounds(final int x, final int y, final int z) {
        return (x < pos.x || x >= pos.x + chunkScale.x || y < pos.y || y >= pos.y + chunkScale.y || z < pos.z || z >= pos.z + chunkScale.z);
    }

    public int getIDX(final int x, final int y, final int z) {
        return x + (y * chunkScale.x) + (z * chunkScale.x * chunkScale.y);
    }

    public ImageBuffer3D getChunkBuffer() {
        return chunkBuffer;
    }

    public int getHeight() {
        return chunkScale.y;
    }

    public int getX() {
        return pos.x;
    }

    public int getZ() {
        return pos.z;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public String getId() {
        return id;
    }

    public boolean shouldUpdateBuffer() {
        return updateBuffer;
    }
}
    
