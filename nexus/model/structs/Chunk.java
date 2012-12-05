package nexus.model.structs;

import nexus.model.generators.Perlin;
import nexus.model.renderable.Air;
import nexus.model.renderable.Solid;
import nexus.view.color.Colorist;
import nexus.view.color.Greyscale;

/**
 * 16x16 containers for Terrain
 * 
 * @author Lane Aasen <laneaasen@gmail.com>
 *
 */

public class Chunk {
	// this value should not need to be changed
	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;
	public static final int BIG_NUMBER = (int) Math.pow(2, 18);
	
	int x, z;
	Vector3 dilation;
	Block[][][] blocks;
	Colorist colorist;
	
	public Chunk(int x, int z, Vector3 dilation) {
		this.x = x;
		this.z = z;
		this.dilation = dilation;
		this.blocks = new Block[WIDTH][WIDTH][HEIGHT];
		this.colorist = new Greyscale(10.0f, 0.0f);
	}
	
	public void generate() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < WIDTH; j++) {
				float x = (float) this.x * Chunk.WIDTH + i;
				float z = (float) this.z * Chunk.WIDTH + j;
				float y = (int) dilation.y * ((Perlin.perlin2D(x * dilation.x + BIG_NUMBER, z * dilation.z + BIG_NUMBER) + 1) / 2) + 1;
				
				for (int k = 0; k < HEIGHT; k++) {
					if (k <= y) {
						this.blocks[i][j][k] = new Solid(new Vector3(x, (float) k, z), 1.0f, this.colorist);
					} else {
						this.blocks[i][j][k] = new Air(new Vector3(x, (float) k, z), 1.0f);	
					}
				}
			}
		}
		
		this.calcVisible();
	}
	
	/**
	 * Calculates visible sides of blocks and updates each block's mask accordingly
	 */
	public void calcVisible() {
		for (int x = 0; x < WIDTH; x++) {
			for (int z = 0; z < WIDTH; z++) {
				for (int y = 0; y < HEIGHT; y++) {
					if (x == 0 || x == WIDTH - 1 || z == 0 || z == WIDTH - 1 || y == 0 || y == HEIGHT - 1) {
						if (x == 0) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.left = true;
						} else if (x == WIDTH - 1) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.right = true;
						}
						
						if (y == 0) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.bottom = true;
						} else if (y == WIDTH - 1) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.top = true;
						}
						
						if (z == 0) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.near = true;
						} else if (z == WIDTH - 1) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.far = true;
						}
						
						if (y != 0 && y != HEIGHT - 1) {
							if (blocks[x][z][y + 1] instanceof Air) {
								blocks[x][z][y].mask.render = true;
								blocks[x][z][y].mask.top = true;
							}

							if (blocks[x][z][y - 1] instanceof Air) {
								blocks[x][z][y].mask.render = true;
								blocks[x][z][y].mask.bottom = true;
							}	
						}
						
					} else {
						if (blocks[x + 1][z][y] instanceof Air) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.right = true;
						}

						if (blocks[x - 1][z][y] instanceof Air) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.left = true;
						}

						if (blocks[x][z][y + 1] instanceof Air) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.top = true;
						}

						if (blocks[x][z][y - 1] instanceof Air) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.bottom = true;
						}
						
						if (blocks[x][z + 1][y] instanceof Air) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.far = true;
						}

						if (blocks[x][z - 1][y] instanceof Air) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.near = true;
						}
					}
				}
			}
		}
	}
	
	public void drawBlocks() {
		for (Block[][] a : blocks) {
			for (Block[] b : a) {
				for (Block block : b) {
					block.draw();
				}
			}
		}
	}
}