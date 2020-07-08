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
  void draw() {
    updateYPos();
    updateHeightWidth();
    getCurrentImage();
    if(showHitboxes){
    fill(255,182,192);
    rect(PLAYERXPOS,ypos,width,height);
    }
    image(currentImage, PLAYERXPOS, ypos);
  }
  void updateHeightWidth() {
    if (isDucked) {
      height = DUCKEDHEIGHT;
      width = DUCKEDWIDTH;
    } else {
      height = NORMALHEIGHT;
      width = NORMALWIDTH;
    }
  }
  void loadImages() {
    idle = loadImage("T-rex_idle.png");
    runningOne = loadImage("T-rex_running1.png");
    runningTwo = loadImage("T-rex_running2.png");
    duckedOne = loadImage("T-rex_ducked1.png");
    duckedTwo = loadImage("T-rex_ducked2.png");
    dead = loadImage("T-rex_dead.png");
  }
  void updateYPos() {
    if (isDucked) {
     ypos = SCREENHEIGHT-PLAYERYMARGIN+18;
    }
    else if (isJumping) {
      ypos = ypos-jumpCounter;
      jumpCounter = jumpCounter-0.8;
      if (ypos>SCREENHEIGHT-PLAYERYMARGIN) isJumping=false;
    }
    else ypos = SCREENHEIGHT-PLAYERYMARGIN;
  }

  void duck() {
    isDucked = true;
    counter = 0;
  }

  float getYpos() {
    return ypos;
  }

  int getHeight() {
    return height;
  }

  int getWidth() {
    return width;
  }
  void playerIsRunning() {
    isIdle=false;
  }

  void erect() {
    isDucked = false; 
    counter = 0;
  }

  void jump() {
    isJumping = true;
    jumpCounter = 13;
  }
  void revive(){
    isDead = false;
  }
  void kill() {
    isDead = true;
  }
  void showHitboxes(){
    showHitboxes = true;
  }
  void hideHitboxes(){
   showHitboxes=false; 
  }
  boolean isPlayerJumping(){
    return isJumping;
  }
  
  boolean isPlayerDucked() {
    return isDucked;
  }
  
  boolean isPlayerDead() {
    return isDead;
  }
  void getCurrentImage() {
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