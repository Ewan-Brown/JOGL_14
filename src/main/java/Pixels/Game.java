package Pixels;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES2.GL_COMPILE_STATUS;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;


//The Frame is the "window" that you see and click on
//The GLCanvas myCanvas is a component that is placed in the window, and as there is nothing else in the Frame, it fills the entire view
//Alot of th integer variables in this are pseudo-pointers for use with the native OpenGL code which is written in C, and only works with points.
public class Game extends JFrame implements GLEventListener, KeyListener, MouseListener {
    private GLCanvas myCanvas;
    private int rendering_program;
    private int vao[] = new int[1];
    private int vbo[] = new int[2];
    //Translations applied to objects, relative to camera. Positive values will push objects 'away' from camera
    //Not using the x and the y for now, maybe testing. set to 0.
    private float unusedX, unusedY, zDistFromCamera;
    private float rotX, rotY, rotZ;
    private Matrix3D pMat;
    public static BitSet keySet = new BitSet(256);
    ArrayList<Pixel> pixels = new ArrayList<Pixel>();
    int pixelCount = 10000;
    Random rand = new Random();
    Point2D lastPoint = new Point2D.Float(0,0);
//    Vector3D lastCalculatedVector = new Vector3D(0,0,0);
    float nearPlane = 0.1f;
    float farPlane = 1000f;
    float fov = 60.0f;
    float lastFX = 0.0f;
    float lastFY = 0.0f;
    float lastFZ = 0.0f;
    boolean clicked = false;

    //The 'entry' point for this code
    public static void main(String[] args) {
        //Calls the constructor below
        Game game = new Game();
        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                game.updatePixels();
            }
        }).start();
    }

    public void updatePixels(){
        for(Pixel p : pixels){
            p.update();
            if(Math.abs(p.x) > 1) {
                p.speedX = -p.speedX;
                p.x += p.speedX;
            }
            if(Math.abs(p.y) > 1) {
                p.speedY = -p.speedY;
                p.y += p.speedY;
            }
            if(Math.abs(p.z) > 1) {
                p.speedZ = -p.speedZ;
                p.z += p.speedZ;
            }
            float dX = p.x - lastFX;
            float dY = p.y - lastFY;
            float dZ = p.z - lastFZ;
            if(!keySet.get(KeyEvent.VK_E) && clicked) {
                p.speedX -= dX / 100f;
                p.speedY -= dY / 100f;
                p.speedZ -= dZ / 100f;
            }

        }
    }


    public void initializePixels(){
        for(int i = 0; i < pixelCount; i++){
            Pixel p = new Pixel(
                    rand.nextFloat()*2 - 1,
                    rand.nextFloat()*2 - 1,
                    rand.nextFloat()*2 - 1
            );
//            p.speedX = (rand.nextFloat()*2 - 1) / 200f;
//            p.speedY = (rand.nextFloat()*2 - 1) / 200f;
//            p.speedZ = (rand.nextFloat()*2 - 1) / 200f;

            p.rotSpeedX = (rand.nextFloat()*2 - 1);
            p.rotSpeedY = (rand.nextFloat()*2 - 1);
            p.rotSpeedZ = (rand.nextFloat()*2 - 1);

            pixels.add(p);
        }
    }

    private void doInput(){
        if(keySet.get(KeyEvent.VK_LEFT)){
            rotY -= 0.9f;
        }
        if(keySet.get(KeyEvent.VK_RIGHT)){
            rotY += 0.9;
        }
    }

    public Game(){
        initializePixels();
        setTitle("Game Game Game! Now in 3D");
        setSize(600,600);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        FPSAnimator animator = new FPSAnimator(60);
        animator.add(myCanvas);
        animator.start();
        this.add(myCanvas);
        myCanvas.addKeyListener(this);
        myCanvas.addMouseListener(this);
        setVisible(true);
        initializePixels();
    }

    public void init(GLAutoDrawable drawable){
        rendering_program = createShaderProgram();
        setupVertices();
        unusedX = 0.0f; unusedY = 0.0f; zDistFromCamera = 3.0f;
        rotX = 0.0f; rotY = 0.0f; rotZ = 0.0f;
        float aspect = (float)myCanvas.getWidth()/(float)myCanvas.getHeight();
        pMat = perspective(fov, aspect, nearPlane, farPlane);
    }
    private Matrix3D perspective(float fovy, float aspect, float n, float f){
        float q = 1.0f / (float) Math.tan(Math.toRadians(0.5f * fovy));
        float A = q / aspect;
        float B = (n + f) / (n- f);
        float C = (2.0f * n * f) / (n-f);
        Matrix3D r = new Matrix3D();
        r.setElementAt(0,0,A);
        r.setElementAt(1,1,q);
        r.setElementAt(2,2,B);
        r.setElementAt(3,2,-1.0f);
        r.setElementAt(2,3,C);
        r.setElementAt(3,3,0.0f);
        return r;
    }
    private Vector3D inverseThePerspective(Vector3D v){
        return v.mult(pMat.inverse());
    }
//    private Matrix3D getBig
    public void dispose(GLAutoDrawable drawable) { }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
    public void display(GLAutoDrawable drawable){
        doInput();
        GL4 gl = (GL4) GLContext.getCurrentGL();

        //Clear background
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        float[] bkg = {0.0f, 0.0f, 0.0f, 1.0f};
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL2ES3.GL_COLOR,0,bkgBuffer);
        gl.glUseProgram(rendering_program);

        //Camera and box translations. Are both really necessary...
        Matrix3D vMat = new Matrix3D();
        vMat.translate(-unusedX,-unusedY,-zDistFromCamera);
        vMat.rotate(rotX, rotY, rotZ);

        //Concat matrices

        //Connect these to uniform vars that the shaders can use
        int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(mv_loc, 1, false, vMat.getFloatValues(), 0);

        //Render Containing Cube
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); //Set wireframe
        gl.glDrawArrays(GL_TRIANGLES, 0 ,36);
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); //Disable Wireframe
        //PART 2 ==========================================
//        drawMiniCubes(gl);
        //END PART 2 ======================================


//        glu.gluUnProject();

        //TEMP REMOVE ME =============================================

//        System.out.println(pMat.toString());
//        IntBuffer viewportBuff = Buffers.newDirectIntBuffer(4);
        int[] viewPort = new int[4];
        gl.glGetIntegerv(GL_VIEWPORT, viewPort,0);
        GLU glu = GLU.createGLU();
        float[] objPos1 = new float[3];
        float[] objPos2 = new float[3];
        glu.gluUnProject((float)lastPoint.getX(),(float)lastPoint.getY(),0.1f,
                new Matrix3D().getFloatValues(),0,
                pMat.getFloatValues(),0,
                viewPort,0,
                objPos1,0);
        glu.gluUnProject((float)lastPoint.getX(),(float)lastPoint.getY(),1.0f,
                new Matrix3D().getFloatValues(),0,
                pMat.getFloatValues(),0,
                viewPort,0,
                objPos2,0);

        vMat = new Matrix3D();
        lastFX = (float)(objPos2[0]/objPos2[2] * -zDistFromCamera * Math.cos(Math.toRadians(rotY)));
        lastFY = objPos2[1]/objPos2[2] * zDistFromCamera;
        lastFZ = (float)(objPos2[0]/objPos2[2] * -zDistFromCamera * Math.sin(Math.toRadians(rotY)));

//        System.out.println(objPos2[0]/objPos2[2] * -zDistFromCamera * Math.sin(Math.toRadians(rotY)));
        vMat.translate(0,0,-zDistFromCamera);
        vMat.rotate(rotX, rotY, rotZ);
//        vMat.translate(objPos2[0]/objPos2[2] * -zDistFromCamera * Math.cos(Math.toRadians(rotY)),
//                objPos2[1]/objPos2[2] * zDistFromCamera,
//                objPos2[0]/objPos2[2] * -zDistFromCamera * Math.sin(Math.toRadians(rotY)));
//        vMat.translate(lastCalculatedVector.getX(), lastCalculatedVector.getY(),0);
        drawMiniCubes(gl);
    }

    public void drawMiniCubes(GL4 gl){
        for(Pixel p : pixels) {
            Matrix3D vMat = new Matrix3D();
            vMat.translate(-unusedX, -unusedY, -zDistFromCamera);
            vMat.rotate(rotX,rotY,rotZ);
//            mMat = new Matrix3D();

//            mvMat.concatenate(mMat);
            vMat.translate(p.x,p.y,p.z);
            vMat.rotate(p.rotX,p.rotY,p.rotZ);
            vMat.scale(0.015, 0.015, 0.015);

            //Connect these to uniform vars that the shaders can use
            int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
            int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
            gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
            gl.glUniformMatrix4fv(mv_loc, 1, false, vMat.getFloatValues(), 0);

            //Render Containing Cube
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);

            gl.glEnable(GL_DEPTH_TEST);
            gl.glDepthFunc(GL_LEQUAL);
//            gl.glDrawArrays(GL_TRIANGLES, 0, 36);
            gl.glDrawArrays(GL_POINTS, 0, 1);
        }
    }


    public void setupVertices(){
        GL4 gl = (GL4) GLContext.getCurrentGL();
        float[] cube_vertices = {
                -1.0f,-1.0f,-1.0f, -1.0f,-1.0f, 1.0f, -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f,-1.0f, -1.0f,-1.0f,-1.0f, -1.0f, 1.0f,-1.0f,
                1.0f,-1.0f, 1.0f, -1.0f,-1.0f,-1.0f, 1.0f,-1.0f,-1.0f,
                1.0f, 1.0f,-1.0f, 1.0f,-1.0f,-1.0f, -1.0f,-1.0f,-1.0f,
                -1.0f,-1.0f,-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f,-1.0f,
                1.0f,-1.0f, 1.0f, -1.0f,-1.0f, 1.0f, -1.0f,-1.0f,-1.0f,
                -1.0f, 1.0f, 1.0f, -1.0f,-1.0f, 1.0f, 1.0f,-1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,-1.0f,-1.0f, 1.0f, 1.0f,-1.0f,
                1.0f,-1.0f,-1.0f, 1.0f, 1.0f, 1.0f, 1.0f,-1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f,-1.0f, -1.0f, 1.0f,-1.0f,
                1.0f, 1.0f, 1.0f, -1.0f, 1.0f,-1.0f, -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,-1.0f, 1.0f
        };
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(cube_vertices);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);
    }
    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];

        //Read from files
        String vShaderSource[] = readShaderSource("C:\\Users\\Ewan\\IdeaProjects\\JOGL_14\\src\\main\\java\\Pixels\\shaders\\cube_vert.shader");
        String fShaderSource[] = readShaderSource("C:\\Users\\Ewan\\IdeaProjects\\JOGL_14\\src\\main\\java\\Pixels\\shaders\\cube_frag.shader");


        //Load and compile shaders
        int vShader = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
        gl.glShaderSource(vShader,vShaderSource.length,vShaderSource,null,0);
        gl.glCompileShader(vShader);


        int fShader = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader,fShaderSource.length,fShaderSource,null,0);
        gl.glCompileShader(fShader);

        //Attach shaders to rendering program
        gl.glGetShaderiv(vShader,GL_COMPILE_STATUS, vertCompiled,0);
        gl.glGetShaderiv(fShader,GL_COMPILE_STATUS, fragCompiled,0);
//        System.out.println(vertCompiled[0] + " " + fragCompiled[0]);
        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);
        //delete left over shaders (?)
        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }
    //Simple java to read from file
    private String[] readShaderSource(String filename){
        Vector<String> lines = new Vector<String>();
        Scanner sc;
        try {
            sc = new Scanner( new File(filename));
        } catch(IOException e){
            System.err.println("IOException when reading shader files " + e);
            return null;
        }
        while(sc.hasNext()){
            lines.addElement(sc.nextLine());
        }
        String[] program = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++){
            program[i] =  lines.elementAt(i) + "\n";
        }
        return program;
    }

    //Takes a click point and transforms from pixel location to  domain of [-1,+1]
    private Point2D.Float normalizePixelPoint(Point2D screenPoint){
//        float x = (float)(screenPoint.getX()/myCanvas.getWidth() * 2) - 1;
//        float y = -((float)(screenPoint.getY()/myCanvas.getHeight() * 2) - 1); //adjust for y being flipped in swing!
        return new Point2D.Float((float)screenPoint.getX(),(float)screenPoint.getY());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keySet.set(e.getKeyCode(),true);
        if(e.getKeyCode() == KeyEvent.VK_R){
            rotY = 0;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        keySet.set(e.getKeyCode(),false);
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        Point2D normalizedPoint = normalizePixelPoint(e.getPoint());
        lastPoint = normalizedPoint;
        clicked = true;
        System.out.println(normalizedPoint);
//        lastCalculatedVector = inverseThePerspective(new Vector3D(normalizedPoint.getX(),normalizedPoint.getY(), nearPlane));
//        System.out.println(lastCalculatedVector);
//        System.out.println(normalizedPoint);
//        System.out.println();
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        clicked = false;
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {

    }
}

