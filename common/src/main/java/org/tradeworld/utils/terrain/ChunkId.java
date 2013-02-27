package org.tradeworld.utils.terrain;

/**
 * Identifies a specific terrain chunk.
 * Terrain is handled in chunks, e.g. when sending to clients.
 * Chunks have some fixed size, specified at application configuration time, for example 1*1 km.
 */
public final class ChunkId {
    public final int chunkX;
    public final int chunkY;

    public ChunkId(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChunkId chunkId = (ChunkId) o;

        if (chunkX != chunkId.chunkX) return false;
        if (chunkY != chunkId.chunkY) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = chunkX;
        result = 31 * result + chunkY;
        return result;
    }

    @Override
    public String toString() {
        return "Chunk{" + chunkX + ", " + chunkY + '}';
    }
}
