package P2_2;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.GL_POINTS;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;

public class Code extends JFrame implements GLEventListener {
    private int rendering_program;
    private int vao[] = new int[1];


    private GLCanvas canvas;

    public static void main(String[] args) {
        new Code();
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

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(rendering_program);
        gl.glPointSize(70.0f); //Added for fun
        gl.glDrawArrays(GL_POINTS,0,1);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }
    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        String vShaderSource[] =
            {
                "#version 430    \n",
                "void main(void)   \n",
                "{ gl_Position = vec4(0.0,0.0,0.0,1.0); } \n",
           };

        String fShaderSource[] =
            {
                "#version 430    \n" ,
                "out vec4 color;  \n",
                "void main(void)   \n" ,
                "{ color = vec4(sin(gl_FragCoord.x/5.0),0.0,cos(gl_FragCoord.y/5.0),1.0); } \n", //Edited for fun
            };

        int vShader = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
        gl.glShaderSource(vShader,3,vShaderSource,null,0);
        gl.glCompileShader(vShader);

        int fShader = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader,4,fShaderSource,null,0);
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
