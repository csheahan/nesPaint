import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Font;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.StringBuilder;
import java.io.IOException;
import java.io.File;

public class nesPaint extends JApplet {

  public static void main(String[] args) {
    JFrame window = new JFrame("NES Background Painter");
    SimplePaintPanel content = new SimplePaintPanel();

    window.setContentPane(content);
    window.setSize(454, 372);
    window.setLocation(0, 0);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);
  }
  
  public static class SimplePaintPanel extends JPanel implements MouseListener, MouseMotionListener {  
    
    private int[][][] paintArr;  /* 16x16 array of 8x8 (64) pixels */
    private int[] currSelected;  /* Coordinates of the currently selected square */
    private int[][] outputArr;   /* Array of which tiles are where on canvas */

    private boolean dragging;     /* If the user is currently dragging */
    private boolean hasDragged;   /* If the user has dragged the mouse, used for painting */
    private Graphics paintBrush;  /* For grabbing the current graphics and drawing */

    SimplePaintPanel() {
      setBackground(Color.WHITE);

      dragging = false;
      hasDragged = false;
      paintBrush = null;

      paintArr = new int[16][16][64];
      currSelected = new int[2];
      outputArr = new int[32][30];
      initializeArrs();

      addMouseListener(this);
      addMouseMotionListener(this);

      getFilePathAndLoadArray();
    }

    public void initializeArrs() {
      currSelected[0] = -1;
      currSelected[1] = -1;

      for (int i = 0; i < outputArr.length; i++) {
        for (int j = 0; j < outputArr[i].length; j++) {
          outputArr[i][j] = 0;
        }
      }
    }

    /**
     * Requests the .chr file from the user and then loads it into the 
     * paintArr array 
     */
    public void getFilePathAndLoadArray() {
      /* Get Directory */
      String currDir = System.getProperty("user.dir");
      JFileChooser fc = new JFileChooser();
      fc.setCurrentDirectory(new File(currDir));
      int returnVal = fc.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        FileInputStream fin = null;
        try {
          fin = new FileInputStream(file);

          int ARRSIZE = 8;
          byte[] arrOne = new byte[ARRSIZE];
          byte[] arrTwo = new byte[ARRSIZE];

          /* Offset the file to ignore the sprite section and go straight to the 
           * background section. Basically a seek inline forloop */
          for (int offs = 0; offs <= 0xff; offs++, fin.read(arrOne), fin.read(arrTwo));

          /* tile will determine which background tile */
          for (int tile = 0; tile <= 0xff && fin.read(arrOne) != -1 && fin.read(arrTwo) != -1; tile++) {
            /* arrByte will be the current byte off arrOne and arrTwo */
            for (int arrByte = 0; arrByte < arrOne.length; arrByte++) {
              int levelOne = tile / 16;
              int levelTwo = tile % 16;
              int levelThree = 0;
              int hexOne, hexTwo, val = 0;

              /* Since a byte = 8 bits = 2 hex digits, hex is which hex digit*/
              for (int hex = 1; hex >= 0; hex--) {
                /* bin is the current bit int the hex number */
                for (int bin = 3; bin >= 0; bin--) {
                  val = 0;
                  levelThree = 0;

                  /* Shift and mask to get 4 bits */
                  hexOne = (((int)arrOne[arrByte] >> (hex * 4)) & 0xF);
                  hexTwo = (((int)arrTwo[arrByte] >> (hex * 4)) & 0xF);

                  /* With the new hex num, mask then shift to get the bit.
                   * I multiply the second bit value by 2 to have 4 unique
                   * values: 0 (both 0), 1 (One 1 and Two 0), 2 (One 0 and
                   * Two 1), and 3 (One 1 and Two 1) */
                  val += ((hexOne & (int)Math.pow(2, bin)) >> bin);
                  val += (((hexTwo & (int)Math.pow(2, bin)) >> bin) * 2);

                  levelThree += ((1 - hex) * 4);
                  levelThree += (3 - bin);
                  levelThree += (arrByte * 8);

                  paintArr[levelOne][levelTwo][levelThree] = val;
                }
              }
            }
          }
        }
        catch (Exception e) {
          System.out.println("---Exception found in method: getFilePath");
          e.printStackTrace();
        }
        finally {
          if (fin != null) {
            try {
              fin.close();
            }
            catch (IOException e) {
              System.out.println("---IOException found in method: getFilePath");
              System.out.println("---Could not close file properly");
              e.printStackTrace();
            }
          }
        }
      }
      else {
      }
    }

    /**
     * Draws the paint area to left of canvas for selection
     * of tile
     */
    public void drawPaintArea(Graphics g) {
      int topLeftX = 278;
      int topLeftY = 10;
      int offX = 0;
      int offY = 0;
      int color = 0;

      for (int i = 0; i < paintArr.length; i++) {
        for (int j = 0; j < paintArr[i].length; j++) {
          offX = (j + 1) + (j * 8);
          offY = (i + 1) + (i * 8);
          for (int k = 0; k < 8; k++) {
            for (int l = 0; l < 8; l++) {
              color = paintArr[i][j][(k * 8) + l];
              switch (color) {
                case (0) : { g.setColor(Color.RED); break; }
                case (1) : { g.setColor(Color.BLUE); break; }
                case (2) : { g.setColor(Color.GREEN); break; }
                case (3) : { g.setColor(Color.ORANGE); break; }
                default : { g.setColor(Color.DARK_GRAY); }
              }
              g.fillRect(topLeftX + offX + l, topLeftY + offY + k, 1, 1);
            }
          }
        }
      }
    }

    /**
     * Paints the currently selected tile on the canvas
     */
    public void paintCurrOnCanvas(int x, int y, Graphics g) {
      if (currSelected[0] == -1 || currSelected[1] == -1) {
        return;
      }

      int offX = 11 + (x * 8);
      int offY = 11 + (y * 8);
      int color;

      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          color = paintArr[currSelected[1]][currSelected[0]][(i * 8) + j];
          switch (color) {
            case (0) : { g.setColor(Color.RED); break; }
            case (1) : { g.setColor(Color.BLUE); break; }
            case (2) : { g.setColor(Color.GREEN); break; }
            case (3) : { g.setColor(Color.ORANGE); break; }
            default : { g.setColor(Color.DARK_GRAY); }
          }
          g.fillRect(offX + j, offY + i, 1, 1);
        }
      }

      outputArr[x][y] = (currSelected[1] << 4) + currSelected[0];
    }
    
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      /* Canvas */
      g.setColor(Color.DARK_GRAY);
      g.fillRect(10, 10, 258, 242);

      /* Background for the paint */
      g.setColor(Color.BLACK);
      g.fillRect(278, 10, 144, 144);
      drawPaintArea(g);

      /* Current Selection */
      g.setColor(Color.BLACK);
      g.setFont(new Font("TimesRoman", Font.PLAIN, 10));
      g.drawString("Currently Selected: ", 278, 174);
      g.fillRect(384, 165, 10, 10);

      /* Output Button */
      g.drawString("Output to textfile", 148, 278);
      g.drawRect(144, 266, 94, 14);
    }

    /**
     * Outputs the current canvas state to a file named
     *    "output.txt"
     * in the directory of the program formatted for 
     * immediate placement in an .asm file
     */
    public  void outputToTextFile() {
      FileOutputStream fout = null;
      File file;
      StringBuilder build = null;
      try {
        file = new File("output.txt");
        fout = new FileOutputStream(file);
        build = new StringBuilder();
        for (int i = 0; i < 30; i++) {
          for (int j = 0; j < 2; j++) {
            build.append("  .db ");
            for (int k = 0; k < 16; k++) {
              build.append("$" + intToFormattedHex(outputArr[(j * 16) + k][i]));
              if (k < 15) {
                build.append(",");
              }
            }
            if (j == 0) {
              build.append("  ; Row " + i);
            }
            build.append("\n");
          }
          build.append("\n");
        }
        build.append("\n");
        fout.write(build.toString().getBytes());
        fout.flush();
      }
      catch (Exception e) {
        System.out.println("---Exception found in method: outputToTextFile");
        e.printStackTrace();
      }
      finally {
        if (fout != null) {
          try {
            fout.close();
          }
          catch (IOException e) {
            System.out.println("---IOException found in method: outputToTextFile");
            System.out.println("---Could not close file properly");
            e.printStackTrace();
          }
        }
      }
    }

    public  String intToFormattedHex(int i) {
      String out = "";
      if (i <= 0xf) {
        out += "0";
      }
      out += Integer.toHexString(i);
      return out.toUpperCase();
    }

    public void mousePressed(MouseEvent e) {
      if (dragging == true) {
        return;
      }

      int x = e.getX();
      int y = e.getY();

      /* Check if canvas was clicked */
      if (x >= 10 && x <= 268) {
        /* x is in range of canvas */
        if (y >= 10 && y <= 252) {
          /* y is in range of canvas,
           * therefore user clicked in canvas */
          dragging = true;
          hasDragged = false;
          paintBrush = getGraphics();
        }
      }

      /* Check if switching paint */
      if (x >= 278 && x <= 422) {
        /* x in range of paint */
        if (y >= 10 && y <= 154) {
          /* y in range of paint 
           * therefore user is selecting a tile */
          handleSelectPaint(x, y);
        }
      }

      /* Check if want to output data */
      if (x >= 144 && x <= 238) {
        if (y >= 266 && y <= 280) {
          outputToTextFile();
        }
      }
    }

    public void mouseDragged(MouseEvent e) {
      if (dragging == false) {
        return;
      }

      hasDragged = true;

      int x = e.getX();
      int y = e.getY();

      adjustAndPaint(x, y);
    }

    public void handleSelectPaint(int x, int y) {
      int trueX = x - 278;
      int trueY = y - 10;

      if (trueX == 144)
        trueX--;
      if (trueY == 144)
        trueY--;
      trueX /= 9;
      trueY /= 9;

      currSelected[0] = trueX;
      currSelected[1] = trueY;

      paintBrush = getGraphics();

      int offX = 385;
      int offY = 166;
      int color = 0;

      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          color = paintArr[currSelected[1]][currSelected[0]][(i * 8) + j];
          switch (color) {
            case (0) : { paintBrush.setColor(Color.RED); break; }
            case (1) : { paintBrush.setColor(Color.BLUE); break; }
            case (2) : { paintBrush.setColor(Color.GREEN); break; }
            case (3) : { paintBrush.setColor(Color.ORANGE); break; }
            default : { paintBrush.setColor(Color.DARK_GRAY); }
          }
          paintBrush.fillRect(offX + j, offY + i, 1, 1);
        }
      }

      paintBrush.dispose();
      paintBrush = null;
    }

    public void mouseReleased(MouseEvent e) {
      if (dragging == false)
        return;

      if (!hasDragged) {
        int x = e.getX();
        int y = e.getY();

        adjustAndPaint(x, y);
      }

      dragging = false;
      paintBrush.dispose();
      paintBrush = null;
    }

    /**
     * Adjusts an x and y coordinate and rounds to get the
     * x and y of the matrix
     */
    public void adjustAndPaint(int x, int y) {
      /* FIX IF OUT OF BOUNDS */
        if (x <= 10)
          x = 11;
        if (x >= 268)
          x = 267;
        if (y <= 10)
          y = 11;
        if (y >= 252)
          y = 251;

        /* Get the the [x][y]th tile */
        x -= 11;
        y -= 11;
        if (x == 256)
          x--;
        if (y == 240)
          y--;
        x /= 8;
        y /= 8;

        paintCurrOnCanvas(x, y, paintBrush);
    }

    /* Methods requested by interfaces MouseListener and MouseMotionListener */
    public void mouseClicked(MouseEvent e) { return; }
    public void mouseEntered(MouseEvent e) { return; }
    public void mouseExited(MouseEvent e) { return; }
    public void mouseMoved(MouseEvent e) { return; }
  }
}