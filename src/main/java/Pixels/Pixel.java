package Pixels;

import java.awt.event.KeyEvent;

public class Pixel {
    private float x;
    private float y;
    private float z;

    public float getX() {
        return x;
//        return x + (float)Math.sin(x)/20f;
    }

    public float getY() {
        return y;
//        return y + (float)Math.sin(y)/20f;
    }

    public float getZ() {
        return z;
//        return z + (float)Math.sin(z)/20f;
    }

    float speedX = 0;
    float speedY = 0;
    float speedZ = 0;

    float rotX = 0;
    float rotY = 0;
    float rotZ = 0;

    float rotSpeedX = 0;
    float rotSpeedY = 0;
    float rotSpeedZ = 0;

    public Pixel(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void applyPull(float fX, float fY, float fZ, float force){
        float dX = x - fX;
        float dY = y - fY;
        float dZ = z - fZ;
        float dist = (float) Math.sqrt(Math.pow(dX,2) + Math.pow(dY,2) + Math.pow(dZ,2));
        speedX -= dist * (((float)Math.random()-0.5f)*0.4f + 1) * dX / 200f;
        speedY -= dist * (((float)Math.random()-0.5f)*0.4f + 1) * dY / 200f;
        speedZ -= dist * (((float)Math.random()-0.5f)*0.4f + 1) * dZ / 200f;
    }

    public void update(){
        if(!Game.keySet.get(KeyEvent.VK_SPACE)) {
            x += speedX;
            y += speedY;
            z += speedZ;
            speedX -= speedX / 100f;
            speedY -= speedY / 100f;
            speedZ -= speedZ / 100f;
        }

        if(Math.abs(x) > 1) {
            speedX = -speedX;
            x += speedX;
        }
        if(Math.abs(y) > 1) {
            speedY = -speedY;
            y += speedY;
        }
        if(Math.abs(z) > 1) {
            speedZ = -speedZ;
            z += speedZ;
        }


        rotX += rotSpeedX;
        rotY += rotSpeedY;
        rotZ += rotSpeedZ;
    }
}
