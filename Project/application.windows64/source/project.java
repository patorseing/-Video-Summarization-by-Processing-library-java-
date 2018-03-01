import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*; 
import javax.swing.border.*; 
import processing.video.*; 
import java.util.Random; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class project extends PApplet {








class Text{
    boolean frm_active=false;
    boolean go_active_uni=false;
    boolean go_active_non=false; 
    int count = 0; 
    
    JFrame frame;
    Container con;
    
    JLabel videofilefill = new JLabel("path of video: ");
    JTextField videofile = new JTextField();
    //videofile.setText("/Users/napatcholthaipanich/Documents/Processing/VideoSummariz/Beauty_n_Beast.mov");
    
    JLabel startfill = new JLabel("start (sec): ");
    JTextField start = new JTextField();
    
    JLabel endfill = new JLabel("end (sec): ");
    JTextField end = new JTextField();
    
    JLabel timefill = new JLabel("framerate: ");
    JTextField time = new JTextField();
    
    JButton uni, non, go;
    String video;
    int startT, endT, framerate;
    
    public void setup() {
      frame = new JFrame("Video Summarization");
      con = frame.getContentPane();
      con.setLayout(new GridLayout(11,1));
      
      videofile.setPreferredSize(new Dimension(490,30));
      videofilefill.setLabelFor(videofile);
      
      Border border = videofilefill.getBorder();
      Border margin = new EmptyBorder(0,10,0,0);
      videofilefill.setBorder(new CompoundBorder(border, margin));
      
      start.setPreferredSize(new Dimension(490,30));
      startfill.setLabelFor(start);
      border = startfill.getBorder();
      startfill.setBorder(new CompoundBorder(border, margin));
      
      end.setPreferredSize(new Dimension(490,30));
      endfill.setLabelFor(start);
      border = endfill.getBorder();
      endfill.setBorder(new CompoundBorder(border, margin));
      
      uni = new JButton("uniform");
      non = new JButton("non-uniform");
      
      con.add(videofilefill);
      con.add(videofile);
      con.add(startfill);
      con.add(start);
      con.add(endfill);
      con.add(end);
      con.add(uni);
      con.add(non);
      
      uni.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
               count = 1;
               time.setPreferredSize(new Dimension(490,30));
               timefill.setLabelFor(time);
               
               Border border = timefill.getBorder();
               Border margin = new EmptyBorder(0,12,0,0);
               timefill.setBorder(new CompoundBorder(border, margin));
               
               go = new JButton("go");
               con.add(timefill);
               con.add(time);
               con.add(go);
               frm_active=false;
               go.addActionListener(new ActionListener() { 
                  public void actionPerformed(ActionEvent e) { 
                    go_active_uni = true;
                    startT = Integer.parseInt(start.getText());
                    endT = Integer.parseInt(end.getText());
                    framerate = Integer.parseInt(time.getText());
                    video = videofile.getText();
                }
            } );
            }
      } ); 
      
      non.addActionListener(new ActionListener() { 
        public void actionPerformed(ActionEvent e) { 
                    go_active_non = true;
                    video = videofile.getText();
                    startT = Integer.parseInt(start.getText());
                    endT = Integer.parseInt(end.getText());
                }
            } );
    }
    
    public void draw() {
      if (!frm_active) {
        frame.setVisible(true);
        frame.setSize(600,500);
        frm_active=true;
      }
      background(200);
    }
}

Text input;
Movie theMov;
float begin = 2;
float start = 2;
float end;
int i = 0;
int count = 0;
float framerate;
float dur;

int ccount = 0;
int numFrames =0;
PImage images[];
PImage sum;
int row,col;
boolean unidone = false;
boolean nondone = false;

public void setup() {
  
  input = new Text();
  input.setup();
}

public void draw(){
  input.draw();
  
  if(unidone){
    sum = loadImage("Out_uniform.jpg");
    image(sum,0,0);
    return;
  }
  if(nondone){
    sum = loadImage("Out_non_uniform.jpg");
    image(sum,0,0);
    return;
  }
  
  begin = input.startT;
  end = input.endT;
  if(input.go_active_uni == true){
      framerate = input.framerate;
      count++;
      if(count==1){
        theMov = new Movie(this, input.video);
        theMov.loop();
      }
      if(theMov.duration()<end){
        theMov.stop();
      }
      if(end == 0){
        end = theMov.duration();
      }
      framerate = input.framerate;
      if(framerate == 0){
        framerate = 24;
      }
      dur = (end - begin)/framerate;
      if (theMov.available()) {
        theMov.read();
        theMov.jump(start);
        start = start+begin+dur;
        image(theMov, 0, 0);
        save("img"+i+".jpg");
        i++;
        if(start>=end){
          theMov.stop();
        }
      }
      else{
        theMov.stop();
        images = new PImage[i];
        if(ccount==0){
          clear();
          ccount++;
        }
        for(numFrames=0;numFrames<i;numFrames++){
          images[numFrames] = loadImage("img"+numFrames+".jpg");
        }
        int c=0;
        int lower =(int)sqrt(i)+1;
        for(row=0;row<lower;row++){
        for(col=0;col<lower;col++){
          if(c<i){
            image(images[c], col*width/lower,row*height/lower,width/lower, height/lower);
            c++;
          }
          else{
            save("Out_uniform.jpg");
            input.go_active_uni = false;
            unidone = true;
          }
        }
      }
      }
    }
    Random rand = new Random();
    if(input.go_active_non == true){
      framerate = rand.nextInt(50) + 1;
      count++;
      if(count==1){
        theMov = new Movie(this, input.video);
        theMov.loop();
      }
      if(theMov.duration()<end){
        theMov.stop();
      }
      if(end == 0){
        end = theMov.duration();
      }
      if(framerate == 0){
        framerate = 24;
      }
      dur = (end - begin)/framerate;
      if (theMov.available()) {
        theMov.read();
        theMov.jump(start);
        start = start+begin+dur;
        image(theMov, 0, 0);
        save(i+"img.jpg");
        i++;
        if(start>=end){
          theMov.stop();
        }
      }
      else{
        theMov.stop();
        images = new PImage[i];
        if(ccount==0){
          clear();
          ccount++;
        }
        for(numFrames=0;numFrames<i;numFrames++){
          images[numFrames] = loadImage(numFrames+"img.jpg");
        }
        int c=0;
        int lower =(int)sqrt(i)+1;
        for(row=0;row<lower;row++){
        for(col=0;col<lower;col++){
          if(c<i){
            image(images[c], col*width/lower,row*height/lower,width/lower, height/lower);
            c++;
          }
          else{
            save("Out_non_uniform.jpg");
            input.go_active_uni = false;
            nondone = true;
          }
        }
      }
      }
    }
  }
  public void settings() {  size(640, 480); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Project_u5888138_u5888205" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
