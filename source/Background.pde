class Background {
 float xpos;
  int ypos;
  int width;
  int height;
  int type;
  float speed;
  PImage image; 
  boolean showHitboxes;
  void updateXpos(int acceleration){
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
  float getXpos(){
    return xpos;
  }
  void draw(){
   //updateXpos();
   if(type!=GROUNDTYPE&&type!=GROUNDBUMPTYPE&&type!=GROUNDDIPTYPE&&type!=CLOUDTYPE){
     if(showHitboxes){
     fill(244,60,244);
     rect(xpos,ypos,width,height);
     }
   }
   image(image, xpos, ypos); 
  }
  void loadWidthHeight(){
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
  boolean isOffScreen(){
    boolean isOffScreen = false;
    if(this.xpos+this.width<=0) isOffScreen = true;
    return isOffScreen;
  }
  boolean loadCorrectImage(){
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
   void showHitboxes(){
    showHitboxes = true;
  }
}