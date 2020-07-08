import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class T_rex_game extends PApplet {


boolean keyPressed;
boolean gameEnded;
boolean developMode;
boolean showHitboxes;
Player player;
ArrayList<Hazard> hazards;
ArrayList<Background> backgrounds;
ArrayList<Background> ground;
PFont font;
PFont font2;
int distance;
int highscore;
int deathCount;
int cloudCounter;
float acceleration;
public void setup() {
  frameRate(60); //default
  gameEnded=false;
  player = new Player();
  highscore = 0;
  deathCount=0;
  cloudCounter=0;
  developMode = false;
  showHitboxes = false;
  backgrounds = new ArrayList<Background>();
  ground = new ArrayList<Background>();
  hazards = new ArrayList<Hazard>();
  acceleration = 0.001f;
  ground.add(new Background(0, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  ground.add(new Background(GROUNDWIDTH, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  ground.add(new Background(2*GROUNDWIDTH, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  ground.add(new Background(3*GROUNDWIDTH, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  font = loadFont("AmericanTypewriter-Semibold-48.vlw");
  font2 = loadFont("Avenir-Light-20.vlw");
  
}
public void draw() {
  background(255);
  drawText();
  if (!gameEnded) {
    updateText();
    acceleration = acceleration+0.001f;
    updateGround();
    updateBackground();
    createHazards();
    updateHazards();
    player.playerIsRunning();
  }
  drawGround();
  drawBackground();
  drawHazards();
  player.draw(); 
  if (checkCollisions()&&developMode==false) {
    player.kill();
    gameEnded=true;
    hazards.remove(0);
    deathCount++;
    if (distance>highscore) highscore = distance;
  }
}
public void drawText() {
  textFont(font2);
  fill(0);
  text("HI             "+distance, SCREENWIDTH-135, 50);
  text("DEATHS "+deathCount, SCREENWIDTH-135, 30);
  if (highscore!=0) {
    text(highscore, SCREENWIDTH-110, 50);
  }
  if (gameEnded) {
    text("PRESS SPACE TO RESTART", (SCREENWIDTH/2)-(ENDGAMETEXTWIDTH/2), SCREENHEIGHT/2);
  }
}
public void updateText() {
  if (frameCount%6==0) {
    distance = (int)(distance+1+acceleration);
  }
}
public void keyPressed() {
  if (keyPressed == false) {
    if ((keyCode == DOWN)&&(!player.isPlayerJumping())) {
      player.duck();
      keyPressed = true;
    } else if ((keyCode == ' ')&&(!player.isPlayerJumping())) {
      if (player.isPlayerDead()) {
        player.revive();
        gameEnded=false;
        distance=0;
      } else {
        player.jump();
        keyPressed = true;
      }
    } else if (keyCode == CONTROL) {
      if(developMode) developMode = false;
      else developMode = true;
      keyPressed = true;
    } else if (key == BACKSPACE&&developMode) {
      player.kill();
      gameEnded=true;
      if (hazards.size()!=0) hazards.remove(0);
      deathCount++;
      if (distance>highscore) highscore = distance;
      keyPressed = true;
    } else if (keyCode == RIGHT&&developMode) {
      acceleration++;
      keyPressed = true;
    } else if (keyCode == LEFT&&developMode) {
      acceleration--;
      keyPressed = true;
    } else if (key == 'H'&&developMode) {
      showHitboxes = (showHitboxes==true) ? false : true;
      if (showHitboxes==true) player.showHitboxes();
      else if (showHitboxes==false) player.hideHitboxes();
      keyPressed = true;
    }
  }
}
public void keyReleased() {
  if (keyCode == DOWN) {
    player.erect();
  }
  keyPressed = false;
}
public void updateBackground() {
  for (Background background : backgrounds) {
    background.updateXpos((int)acceleration);
  }
}
public void drawBackground() {
  int cloudChance = 80;
  if (((distance%200 == 0)&&(distance>=200))||cloudCounter!=0) {
    if (++cloudCounter==80) cloudCounter=0;
    cloudChance = 2;
  }
  if ((int)(Math.random()*cloudChance+1)==cloudChance) {
    Background background = new Background(SCREENWIDTH, 
      (int)(Math.random()*SCREENHEIGHT-CLOUDMARGIN+1), CLOUDTYPE, CLOUDSPEED+acceleration/2);
    backgrounds.add(background);
  }
  for (Background background : backgrounds) {
    background.draw();
  }
}
public void updateGround() {
  for (Background background : ground) {
    background.updateXpos((int)acceleration);
  }
}
public void drawGround() {
  Background b1 = ground.get(0);
  Background b2 = ground.get(1);
  Background b3 = ground.get(2);
  Background b4 = ground.get(3);
  for (Background background : ground) {
    background.draw();
  }

  if (b1.isOffScreen()) {
    ground.remove(0);
    ground.add(0, new Background(b4.getXpos()+GROUNDWIDTH, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  }
  if (b2.isOffScreen()) {
    ground.remove(1);
    ground.add(1, new Background(b1.getXpos()+GROUNDWIDTH, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  }
  if (b3.isOffScreen()) {
    ground.remove(2);
    ground.add(2, new Background(b2.getXpos()+GROUNDWIDTH, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  }
  if (b4.isOffScreen()) {
    ground.remove(3);
    ground.add(3, new Background(b3.getXpos()+GROUNDWIDTH, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  }
  updateGroundBumps();
}
public void updateGroundBumps() {
  int rand = (int)(Math.random()*120+1);
  if (rand==120) { //1 in 120 chance, 1 every two seconds
    ground.add(new Background(SCREENWIDTH, SCREENHEIGHT-GROUNDBUMPMARGIN, GROUNDBUMPTYPE, STARTSPEED+acceleration));
  } else if (rand==119) {
    ground.add(new Background(SCREENWIDTH, SCREENHEIGHT-GROUNDBUMPMARGIN, GROUNDDIPTYPE, STARTSPEED+acceleration));
  }
}
public void createHazards() {
  if (hazards.size()==0 || hazards.get(hazards.size()-1).getXpos()<=((SCREENWIDTH-MINDISTANCEBETWEENHAZARDS)-(10*acceleration))) {
    if ((int)(Math.random()*60+1)==60) { //1 in 60 chance
      int randType = (int)(Math.random()*4+1);
      if (randType==4&&this.distance>=400) {
        Bird bird = new Bird(SCREENWIDTH, 0, (int)(Math.random()*3+1)+5, STARTSPEED+acceleration+0.5f);
        hazards.add(bird);
        if (showHitboxes)bird.showHitboxes();
      } else {
        Cactus cactus = new Cactus(SCREENWIDTH, 0, (int)(Math.random()*4+1), STARTSPEED+acceleration);
        hazards.add(cactus);
        if (showHitboxes) cactus.showHitboxes();
      }
    }
  }
}
public void updateHazards() {
  for (Hazard hazard : hazards) {
    hazard.updateXpos((int)acceleration);
  }
}
public void drawHazards() {
  for (int i=0; i<hazards.size(); i++) {
    Hazard hazard = hazards.get(i);
    if (!hazard.isOffScreen()) hazard.draw();
  }
  ListIterator<Hazard> iterator = hazards.listIterator();
  while (iterator.hasNext()) {
    if (iterator.next().isOffScreen()) iterator.remove();
  }
}
public boolean checkCollisions() {
  boolean hasCollided = false;
  for (Hazard hazard : hazards) {
    if (hazard.hasHit(player)) hasCollided = true;
  }
  return hasCollided;
}
class Background {
 float xpos;
  int ypos;
  int width;
  int height;
  int type;
  float speed;
  PImage image; 
  boolean showHitboxes;
  public void updateXpos(int acceleration){
   xpos=xpos-speed-acceleration; 
  }
  Background(){
  }
  Background(float xpos, int ypos, int type, float speed){
   this.xpos = xpos;
   this.ypos = ypos;
   this.type = type;
   this.speed = speed;
   loadWidthHeight();
   if(!loadCorrectImage()) println("false"); 
  }
  public float getXpos(){
    return xpos;
  }
  public void draw(){
   //updateXpos();
   if(type!=GROUNDTYPE&&type!=GROUNDBUMPTYPE&&type!=GROUNDDIPTYPE&&type!=CLOUDTYPE){
     if(showHitboxes){
     fill(244,60,244);
     rect(xpos,ypos,width,height);
     }
   }
   image(image, xpos, ypos); 
  }
  public void loadWidthHeight(){
    switch(type){
      case 1: this.width = 23; this.height = 46; break;
      case 2: this.width = 15; this.height = 33; break;
      case 3: this.width = 32; this.height = 33; break;
      case 4: this.width = 49; this.height = 33; break;
      case 5: this.width = GROUNDWIDTH; this.height = 10; break;
      case 9: this.width = 28; this.height = 11; break;
      case 10: this.width = 28; this.height = 11; break;
      case 11: this.width = 46; this.height = 13; break;
    }
  }
  public boolean isOffScreen(){
    boolean isOffScreen = false;
    if(this.xpos+this.width<=0) isOffScreen = true;
    return isOffScreen;
  }
  public boolean loadCorrectImage(){
   boolean isSucessful = true;
   switch(type){
     case 1: this.image = loadImage("Cactus1_large.png"); break;
     case 2: this.image = loadImage("Cactus1_small.png"); break;
     case 3: this.image = loadImage("Cactus2.png"); break;
     case 4: this.image = loadImage("Cactus3.png"); break;
     case 5: this.image = loadImage("Ground.png"); break;
     case 6: break;
     case 7: break;
     case 8: break;
     case 9: this.image = loadImage("Groundbump.png"); break;
     case 10: this.image = loadImage("Grounddip.png"); break;
     case 11: this.image = loadImage("Cloud.png"); break;
     default: isSucessful = false;
   }
   return isSucessful;
  }
   public void showHitboxes(){
    showHitboxes = true;
  }
}
interface BackgroundI {
  
}
class Bird extends Hazard {
  PImage flappingOne;
  PImage flappingTwo;
  int flapCounter;
  Bird(float xpos, int ypos, int type, float speed) {
    super(xpos, ypos, type, speed); 
    flappingOne = loadImage("Bird1.png");
    flappingTwo = loadImage("Bird2.png");
    image = flappingOne;
    switch(type){
      case HIGHBIRDTYPE: this.ypos = SCREENHEIGHT-HIGHBIRDMARGIN; break;
      case MEDIUMBIRDTYPE: this.ypos = SCREENHEIGHT-MEDIUMBIRDMARGIN; break;
      case LOWBIRDTYPE: this.ypos = SCREENHEIGHT-LOWBIRDMARGIN; break;
    }
    flapCounter = 0;
  }
  public void updateWidthHeight(){
    if(image==flappingOne){
      this.width=42;
      this.height=22;
    }else if(image==flappingTwo){
      this.width=42;
      this.height=32;
    }
  }
  public void draw() {
    if (++flapCounter%10==0) {
      image = ((image==flappingOne) ? flappingTwo : flappingOne);
    }
    updateWidthHeight();
    if(showHitboxes){
      fill(255,255,0);
      rect(xpos,ypos,width,height);
    }
    image(image, xpos, ypos);
  }
}
class Cactus extends Hazard {
  
  Cactus(float xpos, int ypos, int type, float speed){
   super(xpos,ypos,type,speed);
   loadCorrectImage();
   loadWidthHeight();
   updateYpos();
  }
  public void updateYpos(){
    switch(type){
      case 1: ypos = SCREENHEIGHT-SMALLCACTUSMARGIN; break;
      case 2: 
      case 3:
      case 4: ypos = SCREENHEIGHT-LARGECACTUSMARGIN; break;
    }
  }
  
}
class Hazard extends Background {
  Hazard(float xpos, int ypos, int type, float speed) {
    super(xpos, ypos, type, speed);
    setCorrectWidth();
  }
  public boolean hasHit(Player player) {
    boolean hasHit = false;
    if ((player.getYpos()+player.getHeight())>=ypos) { 
      if (player.getYpos()<=(ypos+height)) {
        if ((player.PLAYERXPOS+player.getWidth())>=xpos) {
          if (player.PLAYERXPOS<=(xpos+width)) {
            hasHit = true;
          }
        }
      }
    }
    return hasHit;
  }
 
  public void setCorrectWidth() {
    switch(type) {
    case 1: 
      this.width=23;
      break;
    case 2: 
      this.width=15;
      break;
    case 3: 
      this.width=42;
      break;
    case 4: 
      this.width=39;
      break;
    case 6:
      this.width=42;
      break;
    case 7:
      this.width=42;
      break;
    case 8:
      this.width=42;
      break;
    default: 
      this.width = 0;
    }
  }
  public boolean isOffScreen() {
    boolean isOffScreen = false;
    if (xpos+width<=0) isOffScreen=true;
    return isOffScreen;
  }
}
class Player {
  final int PLAYERXPOS = 30;
  final int JUMPHEIGHT = 100;
  final int DUCKEDHEIGHT = 26;
  final int DUCKEDWIDTH = 55;
  final int NORMALHEIGHT = 40;
  final int NORMALWIDTH = 40;
  private boolean isDucked;
  private boolean isIdle;
  private boolean isDead;
  private boolean isJumping;
  private boolean showHitboxes;
  private PImage idle;
  private PImage runningOne;
  private PImage runningTwo;
  private PImage duckedOne;
  private PImage duckedTwo;
  private PImage dead;
  private PImage currentImage;
  private int counter;
  private float jumpCounter;
  private float ypos;
  private int width;
  private int height;

  Player() {
    isDucked = false;
    isIdle = true;
    isDead = false;
    loadImages();
    currentImage = idle;

    //Load in all the PImages
  }
  public void draw() {
    updateYPos();
    updateHeightWidth();
    getCurrentImage();
    if(showHitboxes){
    fill(255,182,192);
    rect(PLAYERXPOS,ypos,width,height);
    }
    image(currentImage, PLAYERXPOS, ypos);
  }
  public void updateHeightWidth() {
    if (isDucked) {
      height = DUCKEDHEIGHT;
      width = DUCKEDWIDTH;
    } else {
      height = NORMALHEIGHT;
      width = NORMALWIDTH;
    }
  }
  public void loadImages() {
    idle = loadImage("T-rex_idle.png");
    runningOne = loadImage("T-rex_running1.png");
    runningTwo = loadImage("T-rex_running2.png");
    duckedOne = loadImage("T-rex_ducked1.png");
    duckedTwo = loadImage("T-rex_ducked2.png");
    dead = loadImage("T-rex_dead.png");
  }
  public void updateYPos() {
    if (isDucked) {
     ypos = SCREENHEIGHT-PLAYERYMARGIN+18;
    }
    else if (isJumping) {
      ypos = ypos-jumpCounter;
      jumpCounter = jumpCounter-0.8f;
      if (ypos>SCREENHEIGHT-PLAYERYMARGIN) isJumping=false;
    }
    else ypos = SCREENHEIGHT-PLAYERYMARGIN;
  }

  public void duck() {
    isDucked = true;
    counter = 0;
  }

  public float getYpos() {
    return ypos;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }
  public void playerIsRunning() {
    isIdle=false;
  }

  public void erect() {
    isDucked = false; 
    counter = 0;
  }

  public void jump() {
    isJumping = true;
    jumpCounter = 13;
  }
  public void revive(){
    isDead = false;
  }
  public void kill() {
    isDead = true;
  }
  public void showHitboxes(){
    showHitboxes = true;
  }
  public void hideHitboxes(){
   showHitboxes=false; 
  }
  public boolean isPlayerJumping(){
    return isJumping;
  }
  
  public boolean isPlayerDucked() {
    return isDucked;
  }
  
  public boolean isPlayerDead() {
    return isDead;
  }
  public void getCurrentImage() {
    if (isDucked) {
      if(counter==0) currentImage = duckedOne;
      counter++;
      if (counter%15==0) { //every 15 frames or .25 of second
        if (currentImage==duckedOne) currentImage = duckedTwo;
        else currentImage = duckedOne;//Don't know whether images can be compared in this way.
      } 
    } else if (isIdle) {
      currentImage = idle;
    } else if (isDead) {
      currentImage = dead;
    } else if (isJumping) {
      currentImage = idle;
    } else {
      if(counter==0) currentImage = runningOne;
      counter++;
      if (counter%8==0) {
        if (currentImage==runningOne) currentImage = runningTwo;
        else currentImage = runningOne;
      }
    }
  } 
}
final int GROUNDWIDTH = 544;
final int GROUNDTYPE = 5;
final int GROUNDMARGIN = 57;
final int HIGHBIRDTYPE = 6;
final int MEDIUMBIRDTYPE = 7;
final int LOWBIRDTYPE = 8;
final int HIGHBIRDMARGIN = 145;
final int MEDIUMBIRDMARGIN = 118;
final int LOWBIRDMARGIN = 80;
final int GROUNDBUMPTYPE = 9;
final int GROUNDDIPTYPE = 10;
final int GROUNDBUMPMARGIN = 61;
final int PLAYERYMARGIN = 93;
final int SMALLCACTUSMARGIN = 93;
final int LARGECACTUSMARGIN = 80;
final float STARTSPEED = 5.5f;
final int SCREENWIDTH = 1400;
final int MINDISTANCEBETWEENHAZARDS = 170;
final int CLOUDTYPE = 11;
final float CLOUDSPEED = 1.2f;
final int CLOUDMARGIN = 140;
final int SCREENHEIGHT = 200;
final int ENDGAMETEXTWIDTH = 250;
  public void settings() {  size(1400, 200); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "T_rex_game" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
