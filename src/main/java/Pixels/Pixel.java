package Pixels;

public class Pixel {
    float x;
    float y;
    float z;

    float speedX = 0;
    float speedY = 0;
    float speedZ = 0;

    public Pixel(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public void setSpeedZ(float speedZ) {
        this.speedZ = speedZ;
    }

    public void update(){
        x += speedX;
        y += speedY;
        z += speedZ;
    }
}
