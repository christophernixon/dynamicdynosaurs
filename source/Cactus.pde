class Cactus extends Hazard {
  
  Cactus(float xpos, int ypos, int type, float speed){
   super(xpos,ypos,type,speed);
   loadCorrectImage();
   loadWidthHeight();
   updateYpos();
  }
  void updateYpos(){
    switch(type){
      case 1: ypos = SCREENHEIGHT-SMALLCACTUSMARGIN; break;
      case 2: 
      case 3:
      case 4: ypos = SCREENHEIGHT-LARGECACTUSMARGIN; break;
    }
  }
  
}