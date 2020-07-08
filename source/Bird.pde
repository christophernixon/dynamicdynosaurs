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
  void updateWidthHeight(){
    if(image==flappingOne){
      this.width=42;
      this.height=22;
    }else if(image==flappingTwo){
      this.width=42;
      this.height=32;
    }
  }
  void draw() {
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