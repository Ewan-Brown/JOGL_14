package P2_4;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Scanner;
import java.util.Vector;

import static com.jogamp.opengl.GL.GL_POINTS;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;

public class Code extends JFrame implements GLEventListener {
    private int rendering_program;
    private int vao[] = new int[1];


    private GLCanvas canvas;

    public static void main(String[] args) {
        new Code();
    }

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


    public Code(){
        setTitle("Program 2.2 - the second");
        setSize(600,400);
        setLocation(200,200);
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        this.add(canvas);
        setVisible(true);
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        rendering_program = createShaderProgram();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    public void dispose(GLAutoDrawable drawable) {

    }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }
    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(rendering_program);
        gl.glPointSize(70.0f); //Added for fun
        gl.glDrawArrays(GL_POINTS,0,1);
    }


    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        String vShaderSource[] = readShaderSource("C:\\Users\\Ewan\\IdeaProjects\\JOGL_14\\src\\main\\java\\P2_4\\cube_vert.shader");

        String fShaderSource[] = readShaderSource("C:\\Users\\Ewan\\IdeaProjects\\JOGL_14\\src\\main\\java\\P2_4\\cube_frag.shader");

        int vShader = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
        gl.glShaderSource(vShader,vShaderSource.length,vShaderSource,null,0);
        gl.glCompileShader(vShader);

        int fShader = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader,fShaderSource.length,fShaderSource,null,0);
        gl.glCompileShader(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }
}
