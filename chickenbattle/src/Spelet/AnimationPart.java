package Spelet;

import network.Player;
import Screens.Application;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.FloatArray;


public class AnimationPart {
	public Mesh partMesh;
	public Matrix4 modelMatrix;
	public Texture texture;
	BoundingBox box;
	public float x;
	public float y;
	public float z;
	public float w, h, d;
	public float rotationX;
	public float rotationZ;
	public float rotationY;
	Matrix4 temp = new Matrix4();
	public AnimationPart(String str) {
		String[] strings = str.split(" ");
		// x,y,z,w,h,d,rotX,rotZ
		x = Float.parseFloat(strings[1]);
		y = Float.parseFloat(strings[2]);
		z = Float.parseFloat(strings[3]);
		w = Float.parseFloat(strings[4]);
		h = Float.parseFloat(strings[5]);
		d = Float.parseFloat(strings[6]);
		rotationX = Float.parseFloat(strings[7]);
		rotationY = Float.parseFloat(strings[8]);
		rotationZ = Float.parseFloat(strings[9]);
		FloatArray fa = new FloatArray();
		modelMatrix = new Matrix4();
		box = new BoundingBox();
		addTopFace(fa,0,0,0,w,h,d);
		addBotFace(fa,0,0,0,w,h,d);
		addLeftFace(fa,0,0,0,w,h,d);
		addRightFace(fa,0,0,0,w,h,d);
		addFrontFace(fa,0,0,0,w,h,d);
		addBackFace(fa,0,0,0,w,h,d);
		if (fa.size > 0) {
			partMesh = new Mesh(true, fa.size, 0,
					VertexAttributes.position, 
					VertexAttributes.normal,
					VertexAttributes.textureCoords);
			partMesh.setVertices(fa.items);
		}
		setTexture(new Texture(Gdx.files.internal("data/grassmap.png")));
		partMesh.calculateBoundingBox(box);
		updateModelMatrix();
	}
	public AnimationPart(float x, float y, float z, float w, float h, float d) {
		box = new BoundingBox();
		modelMatrix = new Matrix4();
		FloatArray fa = new FloatArray();
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.h = h;
		this.d = d;
		addTopFace(fa,0,0,0,w,h,d);
		addBotFace(fa,0,0,0,w,h,d);
		addLeftFace(fa,0,0,0,w,h,d);
		addRightFace(fa,0,0,0,w,h,d);
		addFrontFace(fa,0,0,0,w,h,d);
		addBackFace(fa,0,0,0,w,h,d);
		if (fa.size > 0) {
			partMesh = new Mesh(true, fa.size, 0,
					VertexAttributes.position, 
					VertexAttributes.normal,
					VertexAttributes.textureCoords);
			partMesh.setVertices(fa.items);
		}
		setTexture(new Texture(Gdx.files.internal("data/grassmap.png")));
		partMesh.calculateBoundingBox(box);
		updateModelMatrix();
	}
	public String toString() {
		String ret = "";
		// x,y,z,w,h,d,rotX,rotZ
		ret = x + " " + y + " " + z + " " +  w + " " + h + " " + d  + " " + rotationX  + " " + rotationZ;		
		return ret;
	}
	public void updateModelMatrix() {
		modelMatrix.setToTranslation(x,y,z);
		modelMatrix.rotate(1,0,0,rotationX);
		modelMatrix.rotate(0,0,1,rotationZ);
		modelMatrix.rotate(0,1,0,rotationY);
		partMesh.calculateBoundingBox(box);
		box.mul(modelMatrix);
	}
	public void updateModelMatrix(Matrix4 directionMatrix) {
		modelMatrix.set(directionMatrix);
		modelMatrix.translate(x,y,z);
		//modelMatrix.setToTranslation(x,y,z);
		modelMatrix.rotate(1,0,0,rotationX);
		modelMatrix.rotate(0,0,1,rotationZ);
		partMesh.calculateBoundingBox(box);
		box.mul(modelMatrix);
	}
	public void setTexture(Texture t) {
		texture = t;
	}
	
	public boolean contains(Vector3 vec) {
		Vector3 temp = new Vector3(vec);
		Matrix4 tempMat = new Matrix4(modelMatrix);
		tempMat.inv();
		temp.mul(tempMat);
		if (box.contains(temp)) {
			return true;
		}
		return false;
	}
	public void render(Application app, Player player) {
		//temp.setToTranslation(player.posX, player.posY,player.posZ);
		app.renderer.modelViewMatrix.set(app.cam.view);
		//app.renderer.modelViewMatrix.mul(temp);
		app.renderer.modelViewMatrix.mul(modelMatrix);
		app.renderer.charShader.setUniformMatrix("u_modelViewMatrix", app.renderer.modelViewMatrix);
		
		app.renderer.modelViewProjectionMatrix.set(app.cam.combined);
		app.renderer.modelViewProjectionMatrix.mul(temp);
		app.renderer.modelViewProjectionMatrix.mul(modelMatrix);
		app.renderer.charShader.setUniformMatrix("u_mvpMatrix", app.renderer.modelViewProjectionMatrix);
		
		app.renderer.normalMatrix.set(app.renderer.modelViewMatrix);
		app.renderer.charShader.setUniformMatrix("normalMatrix", app.renderer.normalMatrix);
		
		texture.bind(0);
		app.renderer.charShader.setUniformi("s_texture", 0);
		partMesh.render(app.renderer.charShader,GL20.GL_TRIANGLES);
	}
	
	public void addTopFace(FloatArray fa, int x, int y, int z, float w, float h, float d) {
		fa.add(-w/2+x); // x1
		fa.add(h/2+y); // y1
		fa.add(d/2+z); // z1
		fa.add(0); // Normal X
		fa.add(1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0f);
		fa.add(0.5f);

		fa.add(w/2+x); // x1
		fa.add(h/2+y); // y1
		fa.add(d/2+z); // z1
		fa.add(0); // Normal X
		fa.add(1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0.5f);

		fa.add(w/2+x); // x1
		fa.add(h/2+y); // y1
		fa.add(-d/2+z); // z1
		fa.add(0); // Normal X
		fa.add(1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0f);


		fa.add(w/2+x); // x1
		fa.add(h/2+y); // y1
		fa.add(-d/2+z); // z1
		fa.add(0); // Normal X
		fa.add(1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0f);

		fa.add(-w/2+x); // x1
		fa.add(h/2+y); // y1
		fa.add(-d/2+z); // z1
		fa.add(0); // Normal X
		fa.add(1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0f);
		fa.add(0f);

		fa.add(-w/2+x); // x1
		fa.add(h/2+y); // y1
		fa.add(d/2+z); // z1
		fa.add(0); // Normal X
		fa.add(1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0f);
		fa.add(0.5f);
	}
	public void addBotFace(FloatArray fa, int x, int y, int z, float w, float h, float d) {
		fa.add(-w/2+x);
		fa.add(-h/2+y);
		fa.add(-d/2+z);
		fa.add(0); // Normal X
		fa.add(-1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0);
		fa.add(1);
		
		fa.add(w/2+x);
		fa.add(-h/2+y);
		fa.add(-d/2+z);
		fa.add(0); // Normal X
		fa.add(-1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(1);

		fa.add(w/2+x);
		fa.add(-h/2+y);
		fa.add(d/2+z);
		fa.add(0); // Normal X
		fa.add(-1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0.5f);

		fa.add(w/2+x);
		fa.add(-h/2+y);
		fa.add(d/2+z);
		fa.add(0); // Normal X
		fa.add(-1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0.5f);

		fa.add(-w/2+x);
		fa.add(-h/2+y);
		fa.add(d/2+z);
		fa.add(0); // Normal X
		fa.add(-1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0f);
		fa.add(0.5f);

		fa.add(-w/2+x);
		fa.add(-h/2+y);
		fa.add(-d/2+z);
		fa.add(0); // Normal X
		fa.add(-1); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0);
		fa.add(1);

	}

	public void addLeftFace(FloatArray fa, int x, int y, int z, float w, float h, float d) {
		fa.add(-w/2+x);
		fa.add(-h/2+y);
		fa.add(-d/2+z);
		fa.add(-1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0.5f);

		fa.add(-w/2+x);
		fa.add(-h/2+y);
		fa.add(d/2+z);
		fa.add(-1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(1);
		fa.add(0.5f);

		fa.add(-w/2+x);
		fa.add(h/2+y);
		fa.add(d/2+z);
		fa.add(-1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(1);
		fa.add(0);

		fa.add(-w/2+x);
		fa.add(h/2+y);
		fa.add(d/2+z);
		fa.add(-1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(1);
		fa.add(0);

		fa.add(-w/2+x);
		fa.add(h/2+y);
		fa.add(-d/2+z);
		fa.add(-1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0);

		fa.add(-w/2+x);
		fa.add(-h/2+y);
		fa.add(-d/2+z);
		fa.add(-1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0.5f);
	}

	public void addRightFace(FloatArray fa, int x, int y, int z, float w, float h, float d) {
		fa.add(w/2+x); // x1
		fa.add(-h/2+y); // y1
		fa.add(d/2+z); // z1
		fa.add(1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0.5f);

		fa.add(w/2+x); // x1
		fa.add(-h/2+y); // y1
		fa.add(-d/2+z); // z1
		fa.add(1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(1f);
		fa.add(0.5f);

		fa.add(w/2+x); // x1
		fa.add(h/2+y); // y1
		fa.add(-d/2+z); // z1
		fa.add(1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(1f);
		fa.add(0f);

		fa.add(w/2+x); // x1
		fa.add(h/2+y); // y1
		fa.add(-d/2+z); // z1
		fa.add(1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(1f);
		fa.add(0f);

		fa.add(w/2+x); // x1
		fa.add(h/2+y); // y1
		fa.add(d/2+z); // z1
		fa.add(1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0f);

		fa.add(w/2+x); // x1
		fa.add(-h/2+y); // y1
		fa.add(d/2+z); // z1
		fa.add(1); // Normal X
		fa.add(0); // Normal Y
		fa.add(0); // Normal Z
		fa.add(0.5f);
		fa.add(0.5f);
	}
	public void addFrontFace(FloatArray fa, int x, int y, int z, float w, float h, float d) {
		fa.add(-w/2+x); // x1
		fa.add(-h/2+y); // y1
		fa.add(d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(1); // Normal Z
		fa.add(0.5f); // u1
		fa.add(0.5f); // v1

		fa.add(w/2+x); // x2
		fa.add(-h/2+y); // y2
		fa.add(d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(1); // Normal Z
		fa.add(1f); // u2
		fa.add(0.5f); // v2

		fa.add(w/2+x); // x3
		fa.add(h/2+y); // y2
		fa.add(d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(1); // Normal Z
		fa.add(1f); // u3
		fa.add(0f); // v3

		fa.add(w/2+x); // x3
		fa.add(h/2+y); // y2
		fa.add(d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(1); // Normal Z
		fa.add(1f); // u3
		fa.add(0f); // v3

		fa.add(-w/2+x); // x4
		fa.add(h/2+y); // y4
		fa.add(d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(1); // Normal Z
		fa.add(0.5f); // u4
		fa.add(0f); // v4

		fa.add(-w/2+x); // x1
		fa.add(-h/2+y); // y1
		fa.add(d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(1); // Normal Z
		fa.add(0.5f); // u1
		fa.add(0.5f); // v1

	}
	public void addBackFace(FloatArray fa, int x, int y, int z, float w, float h, float d) {
		fa.add(w/2+x);
		fa.add(-h/2+y);
		fa.add(-d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(-1); // Normal Z
		fa.add(0.5f);
		fa.add(0.5f);

		fa.add(-w/2+x);
		fa.add(-h/2+y);
		fa.add(-d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(-1); // Normal Z
		fa.add(1f);
		fa.add(0.5f);

		fa.add(-w/2+x);
		fa.add(h/2+y);
		fa.add(-d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(-1); // Normal Z
		fa.add(1f);
		fa.add(0f);

		fa.add(-w/2+x);
		fa.add(h/2+y);
		fa.add(-d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(-1); // Normal Z
		fa.add(1f);
		fa.add(0f);

		fa.add(w/2+x);
		fa.add(h/2+y);
		fa.add(-d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(-1); // Normal Z
		fa.add(0.5f);
		fa.add(0f);

		fa.add(w/2+x);
		fa.add(-h/2+y);
		fa.add(-d/2+z);
		fa.add(0); // Normal X
		fa.add(0); // Normal Y
		fa.add(-1); // Normal Z
		fa.add(0.5f);
		fa.add(0.5f);
	}
}
