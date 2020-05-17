package P2_1;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL2ES3.GL_COLOR;

public class Code extends JFrame implements GLEventListener {

    private GLCanvas canvas;

    public static void main(String[] args) {
        new Code();
    }

    public Code(){
        setTitle("Program 2.1 - the first");
        setSize(600,400);
        setLocation(200,200);
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        this.add(canvas);
        setVisible(true);
    }

    public void init(GLAutoDrawable drawable) {

    }

    public void dispose(GLAutoDrawable drawable) {

    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        float bkg[] = {1.0f, 0.0f, 0.0f, 1.0f};
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL_COLOR,0,bkgBuffer);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }
}
