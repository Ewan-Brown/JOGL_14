package DexTest;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

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
    //pointer to the 'rendering program' which contains the compiled fragment and vertex shaders
    private int rendering_program;

    //Not 100% sure how to organize these but the vao contains the vbos and the vbos contain your data for drawing
    //Still need work on figuring stuff out. for example if i have 10 circles to draw do i need 1 VAO containing 10 VBOs?
    //This happens to work for now
    private int vao[] = new int[1];
    private int vbo[] = new int[2];

    //The 'entry' point for this code
    public static void main(String[] args) {
        //Calls the constructor below
        new Code();
    }

    //Constructor creating an object of type "Game" which is really just a JFrame with light additions (as defined by the "Game extends JFrame")
    public Code(){

        //Some general window settings
        setTitle("4.1   T H E   C U B E");
        setSize(600,600);
        myCanvas = new GLCanvas();
        //Allows the canvas to receive calls from OpenGL upon things like window resizing and when the user tabs out of the window
        myCanvas.addGLEventListener(this);
        //Adds the canvas to "this" - being the 'Game' Object
        this.add(myCanvas);
        //Makes the 'Game'-window visible and does lots of magic. This is where openGL is initialized i believe
        setVisible(true);
    }

    //Called once, somewhere down the line from setVisible
    public void init(GLAutoDrawable drawable){
        //A reference to openGL 'context'. this is just one of a thousand ways they could have coded the way that the developer (me) intereacts with the openGL state machine
        GL4 gl = (GL4) GLContext.getCurrentGL();
        //Create that rendering program! only needs to be compiled once as nothing changes with it.
        rendering_program = createShaderProgram();
        //See method description
        setupVertices();
    }


    //Unused openGL methods declared by the EventListener interface. I will put useful stuff here later?
    public void dispose(GLAutoDrawable drawable) { }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

    //The real thing. This is called every frame and attaches the necessary components to the pipleline
    public void display(GLAutoDrawable drawable){
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        //Tells the statemachine/pipeline to use this rendering program and hence the fragment/vertex shaders in it
        //Theres alot of things in this that i could use but i'm only changing the frag/vert shaders
        gl.glUseProgram(rendering_program);


        //associate VBO with the corresponding vertex attribute in the vertex shader
        //sending my vbo to openGL to use. a bit of magic here
        gl.glBindBuffer(GL_ARRAY_BUFFER,vbo[0]);
        gl.glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
        gl.glEnableVertexAttribArray(0);

        //adjust some OpenGL settings
        //Allows the rendering of some models to be placed ONTOP of otheres. will be useful later when we have multiple things rendered
        //Remember these are 2D objects (infinitesemally flat surfaces) rendered directly in front of a 'camera' but they are all on the same plane
        gl.glEnable(GL_DEPTH_TEST);
        //Tells openGL how to decide which objects get 'priority' in being drawn
        gl.glDepthFunc(GL_LEQUAL);
//        gl.glPointSize(10.0f); //Added for fun ignore this
        //This is where i tell openGL okay i've set everything i need, take this and send it to the GPU
        //the GL_TRIANGLE_FAN part tells it HOW to render these series of points. in this case as a series of triangles ( so it takes the points in triplets)
        //not sure what second parameter is
        //3rd parameter is how many of these points you want to render. this way you could tell it to render half of the points you gave it. i have no idea why you'd want to do that
        gl.glDrawArrays(GL_TRIANGLE_FAN,0 ,div + 2);
//        gl.glDrawArrays(GL_POINTS,0 ,div+3);

    }

    //This binds the vao to openGL and 'places' the points i created (the circle) into the vbo
    // not really clear on everything here
    private void setupVertices(){
        GL4 gl = (GL4)GLContext.getCurrentGL();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length,vbo,0);

        gl.glBindBuffer(GL_ARRAY_BUFFER,vbo[0]);
        FloatBuffer vertBuff = Buffers.newDirectFloatBuffer(vertex_positions);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuff.limit()*4, vertBuff, GL_STATIC_DRAW);
    }
    //Takes the cube_vert.shader and cube_frag.shader files, reads, compiles them then places them into rendering_program
    //Yes rendering_program is an integer, but it's really a pointer value that points to the real rendering program object(or struct i guess) in openGL
    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];

        //Read from files
        String vShaderSource[] = readShaderSource("C:\\Users\\Ewan\\IdeaProjects\\JOGL_14\\src\\main\\java\\DexTest\\cube_vert.shader");
        String fShaderSource[] = readShaderSource("C:\\Users\\Ewan\\IdeaProjects\\JOGL_14\\src\\main\\java\\DexTest\\cube_frag.shader");


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
    //Array holding my points
    //Organized as x1,y1,z1,x2,y2,z2,x3,y3,z3...
    //However z is always 0 because they're all on the same plane
    static float vertex_positions[];
    //How many divisions the circle is made of
    static int div = 20;

    //Mediocre algebra to create a circle of points. Don't read into this too much
    //This is a static java block by the way, my just being lazy and not making a method for it.
    //This is called as soon as the java file is compiled
    {
        System.out.println("Generating vertexes");
        float r = 0.5f;
        vertex_positions = new float[(div) * 3 + 6];
        vertex_positions[0] = 0;
        vertex_positions[1] = 0;
        vertex_positions[2] = 0;
        for(int i = 1; i < div+1;i++){
//            vertex_positions[i*3] = (float)i/(float)(div+1);
//            vertex_positions[i*3 + 1] = (float)i/(float)(div+1);
            float angle = (float)Math.toRadians(i*(360f/(float)div));
            System.out.println(angle);
            vertex_positions[i*3] = r * (float)Math.cos(angle);
            vertex_positions[i*3 + 1] = r * (float)Math.sin(angle);
            vertex_positions[i*3 + 2] = 0;
            System.out.println(vertex_positions[i*2] + " " + vertex_positions[i*2 + 1]);
        }
        for(int i = 0; i < vertex_positions.length;i++)
        vertex_positions[div*3 + 3] = vertex_positions[3];
        vertex_positions[div*3 + 4] = vertex_positions[4];
        vertex_positions[div*3 + 5] = vertex_positions[5];

    }
}

