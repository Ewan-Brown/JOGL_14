package P4_1_Revised;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import graphicslib3D.GLSLUtils;
import graphicslib3D.Matrix3D;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Scanner;
import java.util.Vector;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES2.GL_COMPILE_STATUS;


//The Frame is the "window" that you see and click on
//The GLCanvas myCanvas is a component that is placed in the window, and as there is nothing else in the Frame, it fills the entire view
//Alot of th integer variables in this are pseudo-pointers for use with the native OpenGL code which is written in C, and only works with points.
public class Code extends JFrame implements GLEventListener {
    private GLCanvas myCanvas;
    private int rendering_program;
    private int vao[] = new int[1];
    private int vbo[] = new int[2];
    private float cameraX, cameraY, cameraZ;
    private float cubeLocX, cubeLocY, cubeLocZ;
    private GLSLUtils util = new GLSLUtils();
    private Matrix3D pMat;

    //The 'entry' point for this code
    public static void main(String[] args) {
        //Calls the constructor below
//        new Game(20);
        new Code(30);
//        new Game(40);
        new Code(60);
        new Code(75);

    }

    public Code(int fps){
        setTitle("4.1 Revised " + fps);
        setSize(600,600);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        this.add(myCanvas);
        setVisible(true);
        FPSAnimator animator = new FPSAnimator(myCanvas, fps);
        animator.start();
    }

    public void init(GLAutoDrawable drawable){
        GL4 gl = (GL4) GLContext.getCurrentGL();
        rendering_program = createShaderProgram();
        setupVertices();
        cameraX = 0.0f; cameraY = 0.0f; cameraZ = 8.0f;
        cubeLocX = 0.0f; cubeLocY = -2.0f; cubeLocZ = 0.0f;
        float aspect = (float)myCanvas.getWidth()/(float)myCanvas.getHeight();
        pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);
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
    public void dispose(GLAutoDrawable drawable) { }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
    public void display(GLAutoDrawable drawable){
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        float bkg[] = {0.0f,0.0f,0.0f,1.0f};
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL2ES3.GL_COLOR,0,bkgBuffer);
        gl.glUseProgram(rendering_program);

        double t = (double)System.currentTimeMillis() / 10000.0d;

        Matrix3D vMat = new Matrix3D();
        vMat.translate(-cameraX,-cameraY,-cameraZ);
        Matrix3D mMat = new Matrix3D();
//        mMat.translate(cubeLocX,cubeLocY,cubeLocZ);
        mMat.translate(Math.sin(300*t) * 2.0, Math.sin(300*t) * 2.0, 0);
//        mMat.rotate(1500*t,1500*t,1500*t);

        mMat.scale(1.5,1.5,1.5);

        Matrix3D mvMat = new Matrix3D();
        mvMat.concatenate(vMat);
        mvMat.concatenate(mMat);

        int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvMat.getFloatValues(), 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_TRIANGLES, 0 ,36);
    }

    public void setupVertices(){
        GL4 gl = (GL4) GLContext.getCurrentGL();
        float[] vertex_positions = {
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
        FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertex_positions);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);
    }
    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];

        //Read from files
        String vShaderSource[] = readShaderSource("C:\\Users\\Ewan\\IdeaProjects\\JOGL_14\\src\\main\\java\\P4_1_Revised\\cube_vert.shader");
        String fShaderSource[] = readShaderSource("C:\\Users\\Ewan\\IdeaProjects\\JOGL_14\\src\\main\\java\\P4_1_Revised\\cube_frag.shader");


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
        System.out.println(vertCompiled[0] + " " + fragCompiled[0]);
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
            program[i] = (String) lines.elementAt(i) + "\n";
        }
        return program;
    }
}

