class Hazard extends Background {
  Hazard(float xpos, int ypos, int type, float speed) {
    super(xpos, ypos, type, speed);
    setCorrectWidth();
  }
  boolean hasHit(Player player) {
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
 
  void setCorrectWidth() {
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
  boolean isOffScreen() {
    boolean isOffScreen = false;
    if (xpos+width<=0) isOffScreen=true;
    return isOffScreen;
  }
}