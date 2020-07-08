import java.util.*;
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
void setup() {
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
  acceleration = 0.001;
  ground.add(new Background(0, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  ground.add(new Background(GROUNDWIDTH, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  ground.add(new Background(2*GROUNDWIDTH, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  ground.add(new Background(3*GROUNDWIDTH, SCREENHEIGHT-GROUNDMARGIN, GROUNDTYPE, STARTSPEED+acceleration));
  font = loadFont("AmericanTypewriter-Semibold-48.vlw");
  font2 = loadFont("Avenir-Light-20.vlw");
  size(1400, 200);
}
void draw() {
  background(255);
  drawText();
  if (!gameEnded) {
    updateText();
    acceleration = acceleration+0.001;
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
void drawText() {
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
void updateText() {
  if (frameCount%6==0) {
    distance = (int)(distance+1+acceleration);
  }
}
void keyPressed() {
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
void keyReleased() {
  if (keyCode == DOWN) {
    player.erect();
  }
  keyPressed = false;
}
void updateBackground() {
  for (Background background : backgrounds) {
    background.updateXpos((int)acceleration);
  }
}
void drawBackground() {
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
void updateGround() {
  for (Background background : ground) {
    background.updateXpos((int)acceleration);
  }
}
void drawGround() {
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
void updateGroundBumps() {
  int rand = (int)(Math.random()*120+1);
  if (rand==120) { //1 in 120 chance, 1 every two seconds
    ground.add(new Background(SCREENWIDTH, SCREENHEIGHT-GROUNDBUMPMARGIN, GROUNDBUMPTYPE, STARTSPEED+acceleration));
  } else if (rand==119) {
    ground.add(new Background(SCREENWIDTH, SCREENHEIGHT-GROUNDBUMPMARGIN, GROUNDDIPTYPE, STARTSPEED+acceleration));
  }
}
void createHazards() {
  if (hazards.size()==0 || hazards.get(hazards.size()-1).getXpos()<=((SCREENWIDTH-MINDISTANCEBETWEENHAZARDS)-(10*acceleration))) {
    if ((int)(Math.random()*60+1)==60) { //1 in 60 chance
      int randType = (int)(Math.random()*4+1);
      if (randType==4&&this.distance>=400) {
        Bird bird = new Bird(SCREENWIDTH, 0, (int)(Math.random()*3+1)+5, STARTSPEED+acceleration+0.5);
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
void updateHazards() {
  for (Hazard hazard : hazards) {
    hazard.updateXpos((int)acceleration);
  }
}
void drawHazards() {
  for (int i=0; i<hazards.size(); i++) {
    Hazard hazard = hazards.get(i);
    if (!hazard.isOffScreen()) hazard.draw();
  }
  ListIterator<Hazard> iterator = hazards.listIterator();
  while (iterator.hasNext()) {
    if (iterator.next().isOffScreen()) iterator.remove();
  }
}
boolean checkCollisions() {
  boolean hasCollided = false;
  for (Hazard hazard : hazards) {
    if (hazard.hasHit(player)) hasCollided = true;
  }
  return hasCollided;
}