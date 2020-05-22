package Pixels;

import java.awt.event.KeyEvent;

public class Pixel {
    float x;
    float y;
    float z;

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


    public void update(){
        if(!Game.keySet.get(KeyEvent.VK_SPACE)) {
            x += speedX;
            y += speedY;
            z += speedZ;
            speedX -= speedX / 70f;
            speedY -= speedY / 70f;
            speedZ -= speedZ / 70f;
        }


        rotX += rotSpeedX;
        rotY += rotSpeedY;
        rotZ += rotSpeedZ;
    }
}
