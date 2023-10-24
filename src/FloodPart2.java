import java.util.ArrayList;
import java.util.Arrays;
import tester.Tester;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;

interface ICell {
  // Draws a cell in the flood it game
  public WorldImage drawCell(Color color, int cellSize);

  // Draws a brighter cell in the flood game
  public WorldImage drawBrighterCell(Color color, int cellSize);

  // True if given color is the same as the color of this ICell
  public boolean sameColor(Color otherColor);

  // 'Floods' the cell
  public void flood();

}

// Represents an empty cell beyond the game borders
class MtCell implements ICell {

  MtCell() {}

  // Empty case for drawing cells
  public WorldImage drawCell(Color color, int cellSize) {
    throw new RuntimeException("Cannot draw empty border cells");
  }

  // Empty case for drawing  brighter cells
  public WorldImage drawBrighterCell(Color color, int cellSize) {
    throw new RuntimeException("Cannot draw empty border cells");
  }

  // No such thing as same color in border cell
  public boolean sameColor(Color otherColor) {
    return false;
  }

  // Cannot flood border cell
  public void flood() {
    return;
  }

}

// Represents a single square of the game area
class ConsCell implements ICell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;

  ArrayList<ConsCell> floodList = new ArrayList<ConsCell>();
  // Properties of a cell
  Color color;
  boolean flooded;

  // The four adjacent cells to this cell
  ICell left;
  ICell top;
  ICell right;
  ICell bottom;



  // Standard colors
  Color red = new Color(217, 28, 60);
  Color orange = new Color(240, 117, 10);
  Color yellow = new Color(237, 211, 43);
  Color green = new Color(62, 212, 51);
  Color blue = new Color(34, 122, 230);
  Color purple = new Color(105, 48, 191);

  // Brighter colors
  Color redBright = new Color(217, 95, 116);
  Color orangeBright = new Color(242, 150, 70);
  Color yellowBright = new Color(242, 227, 126);
  Color greenBright = new Color(121, 212, 114);
  Color blueBright = new Color(98, 156, 227);
  Color purpleBright = new Color(139, 103, 194);

  // Regular constructor for ConsCell
  ConsCell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = false;
  }

  // Convenience constructor for ConsCell testing
  ConsCell(int x, int y, Color color, boolean flooded, ICell left, ICell top, ICell right,
      ICell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = false;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  // Draws a cell in the flood it game
  public WorldImage drawCell(Color color, int cellSize) {
    if (this.flooded) {
      return this.drawBrighterCell(color, cellSize);
    }
    else {
      return new RectangleImage(cellSize, cellSize, "solid", color);
    }
  }

  // Draws a brighter cell in the flood game
  public WorldImage drawBrighterCell(Color color, int cellSize) {
    return new RectangleImage(cellSize - (cellSize / 5), cellSize - (cellSize / 5),
        "solid", this.brighter(color)).overlayImages(new
            RectangleImage(cellSize, cellSize, "solid", color));
  }

  // True if given color is the same as the color of this ConsCell
  public boolean sameColor(Color otherColor) {
    return this.color.equals(otherColor);
  }

  // 'Floods' this ConsCell
  public void flood() {
    this.flooded = true;
  }

  // Finds the brighter version of a color
  public Color brighter(Color color) {
    if (color.equals(this.red)) {
      return this.redBright;
    }
    else if (color.equals(this.orange)) {
      return this.orangeBright;
    }
    else if (color.equals(this.yellow)) {
      return this.yellowBright;
    }
    else if (color.equals(this.green)) {
      return this.greenBright;
    }
    else if (color.equals(this.blue)) {
      return this.blueBright;
    }
    else {
      return this.purpleBright;
    }
  }

}

// Represents a Flood-It World
class FloodItWorld extends World {
  // the amount of rows/columns in a game
  int boardSize;

  // the amount of colors distributed through the game board
  int numColors;

  // the number of clicks used in this game
  int clicks;

  // the amount of clicks allowed to be made in the game
  int clicksAllowed;

  // the size of each cell in the game board
  int cellSize;

  // the array and arrangement of all cells
  ArrayList<ArrayList<ConsCell>> board;

  Color red = new Color(217, 28, 60);
  Color orange = new Color(240, 117, 10);
  Color yellow = new Color(237, 211, 43);
  Color green = new Color(62, 212, 51);
  Color blue = new Color(34, 122, 230);
  Color purple = new Color(105, 48, 191);

  // list of all possible colors to be used
  ArrayList<Color> colors = new ArrayList<Color>(
      Arrays.asList(red, orange, yellow, green, blue, purple));

  // list of length (numColors) of random colors that
  // makeCells() can chose randomly from
  ArrayList<Color> colorList = new ArrayList<Color>();

  // random variable
  Random rand;

  Color currentFloodColor;

  Color newFloodColor;


  // regular constructor for FloodItWorld
  FloodItWorld(int boardSize, int numColors) {
    this.boardSize = boardSize;
    this.numColors = numColors;

    // Because this random variable isn't seeded, the board will be
    // random for every test in ExamplesFlood
    this.rand = new Random();

    // Clicks allowed will be further expanded upon in part 2,
    // but this is a rough draft of varying the amount of clicks allowed
    // in accordance to the size of the board and the amount of colors used
    this.clicks = 0;
    for (int i = numColors; i > 0; i--) {
      this.clicksAllowed += i;
    }
    this.clicksAllowed += boardSize;

    // cellSize is dependent on boardSize as cells will need to be larger
    // if boardSize is smaller, and vice versa.
    // 600 is the pixel-space allowed in the game (as seen in makeScene())
    this.cellSize = 600 / this.boardSize;

    // Sets up numColors amount of possible random color choices
    for (int i = 6; i > 6 - numColors; i--) {
      this.colorList.add(this.colors.remove(rand.nextInt(i)));
    }

    // Arranges the cells needed for a board of a given size boardSize
    makeCells();

    this.currentFloodColor = this.board.get(0).get(0).color;

    this.newFloodColor = this.board.get(0).get(0).color;

    this.board.get(0).get(0).flood();

  }

  // convenience constructor for FloodItWorld testing
  // FOR CONSISTENCY PURPOSES IN TESTING,
  // THIS BOARD WILL ALWAYS BE 3x3 WITH 2 COLORS
  FloodItWorld(int seed) {
    this.boardSize = 3;
    this.numColors = 2;

    // Because this random variable is seeded, the board will be
    // consistent for every test in ExamplesFlood
    this.rand = new Random(seed);

    // Clicks allowed will be further expanded upon in part 2,
    // but this is a rough draft of varying the amount of clicks allowed
    // in accordance to the size of the board and the amount of colors used
    this.clicks = 0;
    for (int i = numColors; i > 0; i--) {
      this.clicksAllowed += i;
    }
    this.clicksAllowed += boardSize;

    // cellSize is dependent on boardSize as cells will need to be larger
    // if boardSize is smaller, and vice versa.
    // 600 is the pixel-space allowed in the game (as seen in makeScene())
    this.cellSize = 600 / this.boardSize;

    // Sets up numColors amount of possible random color choices
    for (int i = 6; i > 6 - numColors; i--) {
      this.colorList.add(this.colors.remove(rand.nextInt(i)));
    }

    // Arranges the cells needed for a board of a given size boardSize
    makeCells();

    this.currentFloodColor = this.board.get(0).get(0).color;

    this.newFloodColor = this.board.get(0).get(0).color;

    this.board.get(0).get(0).flood();

  }

  // Gets a random color from a list of colors within the parameter
  // of how many colors are allowed in this game
  public Color getRandomColor() {
    int colorIndex = rand.nextInt(numColors);
    return this.colorList.get(colorIndex);

  }

  // Sets up 2D arrayList of cells for the game with random cell colors
  // EFFECT: adds cells to this.board
  void makeCells() {
    // Initializes board
    this.board = new ArrayList<ArrayList<ConsCell>>();

    // Adds random-colored cells with indexes that sequence as so: 0, 1, 2, 3, ...
    for (int row = 0; row < boardSize; row++) {
      ArrayList<ConsCell> rowList = new ArrayList<ConsCell>();
      for (int col = 0; col < boardSize; col++) {
        Color randomColor = getRandomColor();
        rowList.add(new ConsCell(row, col, randomColor, false));
      }
      this.board.add(rowList);
    }

    // Sets up adjacent cells and makes border cells into MtCell()'s
    for (int col = 0; col < boardSize; col++) {
      for (int row = 0; row < boardSize; row++) {
        ConsCell cell = this.board.get(row).get(col);
        if (row > 0) {
          cell.left = this.board.get(row - 1).get(col);
        }
        else {
          cell.left = new MtCell();
        }
        if (col > 0) {
          cell.top = this.board.get(row).get(col - 1);
        }
        else {
          cell.top = new MtCell();
        }
        if (row < boardSize - 1) {
          cell.right = this.board.get(row + 1).get(col);
        }
        else {
          cell.right = new MtCell();
        }
        if (col < boardSize - 1) {
          cell.bottom = this.board.get(row).get(col + 1);
        }
        else {
          cell.bottom = new MtCell();
        }
      }
    }
  }

  // Draws the state of the world
  public WorldScene makeScene() {
    // The empty canvas to work off
    WorldScene scene = new WorldScene(900, 950);

    // The black background that outlines the cells in the game
    WorldImage background = new RectangleImage(624, 624, OutlineMode.SOLID, Color.black);

    // Flood-It header
    WorldImage header = new TextImage("FLOOD-IT", 45, FontStyle.BOLD, Color.black);

    // The counter displayed that shows how many clicks have been used compare to how
    // many are allowed to be used
    WorldImage numClicksText = new TextImage(
        Integer.toString(clicks) + "/" + Integer.toString(clicksAllowed), 30, FontStyle.BOLD_ITALIC,
        Color.black);

    // The sub-text underneath the counter
    WorldImage numClicksSubText = new TextImage(
        "Try to solve the puzzle before you max out your clicks!", 20, FontStyle.BOLD, Color.black);

    // Empty white box to cover up sub-text
    WorldImage rectangleBlock = new RectangleImage(900, 25, OutlineMode.SOLID, Color.white);

    // Winning sub-text
    WorldImage youWon = new TextImage(
        "You win! Press 'r' to restart with this boardsize.", 20, FontStyle.BOLD, Color.black);

    // Losing sub-text
    WorldImage youLost = new TextImage(
        "Sorry, you ran out of clicks. Press 'r' to restart with this boardsize.",
        20, FontStyle.BOLD, Color.black);

    // Button to cycle through boardsizes: 24, 20, 15, 12, 8
    WorldImage changeBoardSizeButton = new RectangleImage(100, 40,
        OutlineMode.SOLID, new Color(217, 53, 41));

    // Text overlayed on the button
    WorldImage changeBoardSizeText = new TextImage("New Size",
        15, FontStyle.BOLD, Color.white);

    // Button to cycle through color counts: 6, 5, 4, 3, 2
    WorldImage changeColorsButton = new RectangleImage(100, 40,
        OutlineMode.SOLID, new Color(33, 138, 61));

    // Text overlayed on the button
    WorldImage changeColorsText = new TextImage("New Colors",
        14, FontStyle.BOLD, Color.white);

    // Places everything above onto the canvas
    scene.placeImageXY(background, 375, 380);
    scene.placeImageXY(header, 375, 35);
    scene.placeImageXY(numClicksText, 375, 725);
    scene.placeImageXY(numClicksSubText, 375, 765);
    scene.placeImageXY(changeBoardSizeButton, 113, 40);
    scene.placeImageXY(changeBoardSizeText, 113, 40);
    scene.placeImageXY(changeColorsButton, 637, 40);
    scene.placeImageXY(changeColorsText, 637, 40);

    // Checks if the board has been fully flooded within the amount of clicks allowed
    // Places winning text
    if (this.clicks <= this.clicksAllowed && this.succesfulFlood()) {
      scene.placeImageXY(rectangleBlock, 375, 765);
      scene.placeImageXY(youWon, 375, 765);

    }

    // Checks if the amount of clicks used exceeds the amount allowed
    // Places winning text
    if (this.clicks >= this.clicksAllowed) {
      scene.placeImageXY(rectangleBlock, 375, 765);
      scene.placeImageXY(youLost, 375, 765);
    }

    // Places all cells on the board centered in the black background
    for (ArrayList<ConsCell> arr : board) {
      for (ConsCell c : arr) {
        scene.placeImageXY(c.drawCell(c.color, this.cellSize),
            (cellSize * (c.x + 1)) + 75 - (cellSize / 2),
            (cellSize * (c.y + 1)) + 80 - (cellSize / 2));
      }
    }

    return scene;
  }

  // When the mouse is pressed, it checks if the mouse's location is within the bounds of
  // the game board. If it is, the cell color associated with the mouse's location will be updated
  // to flood the board
  //
  // onMousePressed is also used to check if one of the buttons was pressed. If one of of them was,
  // it cycles their boardSize/numColors
  public void onMousePressed(Posn p) {

    // New Size button
    if (p.x >= 63 && p.x <= 163 && p.y >= 20 && p.y <= 60) {
      if (this.boardSize == 24) {
        boardSize = 20;
      } else if (this.boardSize == 20) {
        boardSize = 15;
      } else if (this.boardSize == 15) {
        boardSize = 12;
      } else if (this.boardSize == 12) {
        boardSize = 8;
      } else {
        boardSize = 24;
      }

      // Properties to reset with a new game board
      this.clicks = 0;
      this.makeCells();
      this.currentFloodColor = this.board.get(0).get(0).color;
      this.newFloodColor = this.board.get(0).get(0).color;
      this.board.get(0).get(0).flood();
      this.clicksAllowed = 0;
      for (int i = numColors; i > 0; i--) {
        this.clicksAllowed += i;
      }
      this.clicksAllowed += boardSize;
      this.cellSize = 600 / this.boardSize;


    }

    // New Colors button
    if (p.x >= 587 && p.x <= 687 && p.y >= 20 && p.y <= 60) {
      if (this.numColors == 6) {
        numColors = 5;
      } else if (this.numColors == 5) {
        numColors = 4;
      } else if (this.numColors == 4) {
        numColors = 3;
      } else if (this.numColors == 3) {
        numColors = 2;
      } else {
        numColors = 6;
      }

      // Properties to reset with a new game board
      this.clicks = 0;
      this.makeCells();
      this.currentFloodColor = this.board.get(0).get(0).color;
      this.newFloodColor = this.board.get(0).get(0).color;
      this.board.get(0).get(0).flood();

    }

    // Flooding sequence
    if (clicks < clicksAllowed) {

      // Out of bounds of game board Posn check
      if (p.x < 75 || p.x >= 675 || p.y < 80 || p.x >= 680) {
        return;
      }

      ConsCell clicked = this.posnToCell(p);

      if (clicked.flooded || clicked.color.equals(currentFloodColor)) {
        return;
      }

      this.clicks++;
      this.newFloodColor = clicked.color;

      this.updateCells();
    }

  }

  // Resets the game using the ‘r’ key to reset the game and create a new board
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.clicks = 0;
      this.makeCells();
      this.currentFloodColor = this.board.get(0).get(0).color;
      this.newFloodColor = this.board.get(0).get(0).color;
      this.board.get(0).get(0).flood();
    }
  }

  // Uses a Posn to find which cell  on the game board the mouse clicked
  public ConsCell posnToCell(Posn p) {
    int x = (p.x - 75) / cellSize;
    int y = (p.y - 80) / cellSize;

    return this.board.get(x).get(y);
  }

  // Floods the game board by checking if each cell is flooded, flooding neighboring
  // cells that also share the same color, and setting the new color to the cells
  // that are getting flooded
  public void updateCells() {

    for (ArrayList<ConsCell> arr : this.board) {
      for (ConsCell c : arr) {
        if (c.flooded) {
          c.color = this.newFloodColor;
          if (c.left.sameColor(c.color)) {
            c.left.flood();
          }
          if (c.top.sameColor(c.color)) {
            c.top.flood();
          }
          if (c.right.sameColor(c.color)) {
            c.right.flood();
          }
          if (c.bottom.sameColor(c.color)) {
            c.bottom.flood();
          }
        }
      }
    }
    this.currentFloodColor = this.newFloodColor;
  }

  // Checks if every cell in the board has been flooded
  public boolean succesfulFlood() {
    for (ArrayList<ConsCell> arr : this.board) {
      for (ConsCell c : arr) {
        if (!c.flooded) {
          return false;
        }
      }
    }
    return true;
  }

}

// Examples of Flood It Game
class ExamplesFlood {

  //Standard colors
  Color red = new Color(217, 28, 60);
  Color orange = new Color(240, 117, 10);
  Color yellow = new Color(237, 211, 43);
  Color green = new Color(62, 212, 51);
  Color blue = new Color(34, 122, 230);
  Color purple = new Color(105, 48, 191);

  // Brighter colors
  Color redBright = new Color(217, 95, 116);
  Color orangeBright = new Color(242, 150, 70);
  Color yellowBright = new Color(242, 227, 126);
  Color greenBright = new Color(121, 212, 114);
  Color blueBright = new Color(98, 156, 227);
  Color purpleBright = new Color(139, 103, 194);

  // example cells
  ConsCell redCell;
  ConsCell orangeCell;
  ConsCell yellowCell;
  ConsCell greenCell;
  ConsCell blueCell;
  ConsCell magentaCell;

  // example flood world
  FloodItWorld testFloodWorld;

  void initData() {
    // Initialize cells
    this.redCell = new ConsCell(0, 0, red, true,
        new MtCell(), new MtCell(), orangeCell, greenCell);
    this.orangeCell = new ConsCell(1, 0, orange, false,
        redCell, new MtCell(), yellowCell, blueCell);
    this.yellowCell = new ConsCell(2, 0, yellow, false,
        orangeCell, new MtCell(), new MtCell(), magentaCell);
    this.greenCell = new ConsCell(0, 1, green, false,
        new MtCell(), redCell, new MtCell(), blueCell);
    this.blueCell = new ConsCell(1, 1, blue, false,
        greenCell, orangeCell, magentaCell, new MtCell());
    this.magentaCell = new ConsCell(2, 1, purple, false,
        blueCell, yellowCell, new MtCell(), new MtCell());

    // Initializes the test world
    // From the convenience constructor purpose statement:
    // FOR CONSISTENCY PURPOSES IN TESTING,
    // THIS BOARD WILL ALWAYS BE 3x3 WITH 2 COLORS
    this.testFloodWorld = new FloodItWorld(1234);

  }

  ////////////////////////////////////////// ICell Tests //////////////////////////////////////////


  // tests the drawCell method
  void testDrawCell(Tester t) {
    // This method is does not mutate, but for a sanity check, all data is reinitialized
    this.initData();

    // Border cell case
    t.checkException(new RuntimeException("Cannot draw empty border cells"), new MtCell(),
        "drawCell", Color.red, testFloodWorld.cellSize);

    // All six possible cell colors
    t.checkExpect(redCell.drawCell(redCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize, testFloodWorld.cellSize,
            "solid", red));
    t.checkExpect(orangeCell.drawCell(orangeCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize, testFloodWorld.cellSize,
            "solid", orange));
    t.checkExpect(yellowCell.drawCell(yellowCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize, testFloodWorld.cellSize,
            "solid", yellow));
    t.checkExpect(greenCell.drawCell(greenCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize, testFloodWorld.cellSize,
            "solid", green));
    t.checkExpect(blueCell.drawCell(blueCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize, testFloodWorld.cellSize,
            "solid", blue));
    t.checkExpect(magentaCell.drawCell(magentaCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize, testFloodWorld.cellSize,
            "solid", purple));

  }

  // tests the drawBrighterCell method
  void testDrawBrighter(Tester t) {
    // This method is does not mutate, but for a sanity check, all data is reinitialized
    this.initData();

    // Border cell case
    t.checkException(new RuntimeException("Cannot draw empty border cells"), new MtCell(),
        "drawBrighterCell", Color.red, testFloodWorld.cellSize);

    // All six possible cell colors
    t.checkExpect(redCell.drawBrighterCell(redCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize - (testFloodWorld.cellSize / 5),
            testFloodWorld.cellSize - (testFloodWorld.cellSize / 5), "solid",
            redCell.brighter(redCell.color)).overlayImages(new RectangleImage(
                testFloodWorld.cellSize, testFloodWorld.cellSize, "solid", redCell.color)));
    t.checkExpect(orangeCell.drawBrighterCell(orangeCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize - (testFloodWorld.cellSize / 5),
            testFloodWorld.cellSize - (testFloodWorld.cellSize / 5), "solid",
            orangeCell.brighter(orangeCell.color)).overlayImages(new RectangleImage(
                testFloodWorld.cellSize, testFloodWorld.cellSize, "solid", orangeCell.color)));
    t.checkExpect(yellowCell.drawBrighterCell(yellowCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize - (testFloodWorld.cellSize / 5),
            testFloodWorld.cellSize - (testFloodWorld.cellSize / 5), "solid",
            yellowCell.brighter(yellowCell.color)).overlayImages(new RectangleImage(
                testFloodWorld.cellSize, testFloodWorld.cellSize, "solid", yellowCell.color)));
    t.checkExpect(greenCell.drawBrighterCell(greenCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize - (testFloodWorld.cellSize / 5),
            testFloodWorld.cellSize - (testFloodWorld.cellSize / 5), "solid",
            greenCell.brighter(greenCell.color)).overlayImages(new RectangleImage(
                testFloodWorld.cellSize, testFloodWorld.cellSize, "solid", greenCell.color)));
    t.checkExpect(blueCell.drawBrighterCell(blueCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize - (testFloodWorld.cellSize / 5),
            testFloodWorld.cellSize - (testFloodWorld.cellSize / 5), "solid",
            blueCell.brighter(blueCell.color)).overlayImages(new RectangleImage(
                testFloodWorld.cellSize, testFloodWorld.cellSize, "solid", blueCell.color)));
    t.checkExpect(magentaCell.drawBrighterCell(magentaCell.color, testFloodWorld.cellSize),
        new RectangleImage(testFloodWorld.cellSize - (testFloodWorld.cellSize / 5),
            testFloodWorld.cellSize - (testFloodWorld.cellSize / 5), "solid",
            magentaCell.brighter(magentaCell.color)).overlayImages(new RectangleImage(
                testFloodWorld.cellSize, testFloodWorld.cellSize, "solid", magentaCell.color)));
  }

  // tests the brighter method
  void testBrighter(Tester t) {
    // This method is does not mutate, but for a sanity check, all data is reinitialized
    this.initData();

    t.checkExpect(redCell.brighter(red), redBright);
    t.checkExpect(orangeCell.brighter(orange), orangeBright);
    t.checkExpect(yellowCell.brighter(yellow), yellowBright);
    t.checkExpect(greenCell.brighter(green), greenBright);
    t.checkExpect(blueCell.brighter(blue), blueBright);
    t.checkExpect(magentaCell.brighter(purple), purpleBright);

  }

  // tests the flood method
  public void testFlood(Tester t) {
    initData();

    // Test that a cell is correctly flooded
    this.redCell.flood();
    t.checkExpect(this.redCell.flooded, true);

    // Test that a flooded cell remains flooded
    this.redCell.flood();
    t.checkExpect(this.redCell.flooded, true);

    // Test that an unflooded cell remains unflooded
    t.checkExpect(this.orangeCell.flooded, false);
  }



  ////////////////////////////////////// FloodItWorld Tests ///////////////////////////////////////

  // tests the getRandomColor method
  void testGetRandomColor(Tester t) {
    // Tests to see if random selected colors are valid colors in this coloList
    this.initData();
    Color color1 = testFloodWorld.getRandomColor();
    t.checkExpect(this.testFloodWorld.colorList.contains(color1), true);

    Color color2 = testFloodWorld.getRandomColor();
    t.checkExpect(this.testFloodWorld.colorList.contains(color2), true);

    Color color3 = testFloodWorld.getRandomColor();
    t.checkExpect(this.testFloodWorld.colorList.contains(color3), true);

    Color color4 = testFloodWorld.getRandomColor();
    t.checkExpect(this.testFloodWorld.colorList.contains(color4), true);

  }

  // tests the makeCells method
  void testMakeCells(Tester t) {
    // Initializes world and cell data
    this.initData();

    testFloodWorld.makeCells();

    // Tests to see if row/column sizes are equal to the amount
    // of cells made in each row/column
    t.checkExpect(testFloodWorld.board.size(), 3);
    t.checkExpect(testFloodWorld.board.get(0).size(), 3);
    t.checkExpect(testFloodWorld.board.get(2).size(), 3);

    // Check that all cells are initialized with valid colors
    for (ArrayList<ConsCell> row : testFloodWorld.board) {
      for (ConsCell cell : row) {
        t.checkExpect(testFloodWorld.colorList.contains(cell.color), true);
      }
    }

    // Check that all cells are initialized with correct coordinates
    for (int row = 0; row < testFloodWorld.board.size(); row++) {
      for (int col = 0; col < testFloodWorld.board.get(row).size(); col++) {
        t.checkExpect(testFloodWorld.board.get(row).get(col).x, row);
        t.checkExpect(testFloodWorld.board.get(row).get(col).y, col);
      }
    }

  }

  //Tests the method makeScene() by reconstructing the entire makeScene method
  // and comparing the
  // reconstructed version to the method call on testFloodWorld
  void testMakeScene(Tester t) {
    // This method is does not mutate, but for a sanity check, all data is
    // reinitialized
    this.initData();

    WorldScene expectedScene = new WorldScene(900, 950);
    WorldImage background = new RectangleImage(624, 624, OutlineMode.SOLID, Color.black);
    WorldImage header = new TextImage("FLOOD-IT", 45, FontStyle.BOLD, Color.black);
    WorldImage numClicksText = new TextImage(Integer.toString(0) + "/" + Integer.toString(6), 30,
        FontStyle.BOLD_ITALIC, Color.black);
    WorldImage numClicksSubText = new TextImage(
        "Try to solve the puzzle before you max out your clicks!", 20, FontStyle.BOLD, Color.black);
    WorldImage changeBoardSizeButton = new RectangleImage(100, 40, OutlineMode.SOLID,
        new Color(217, 53, 41));
    WorldImage changeBoardSizeText = new TextImage("New Size", 15, FontStyle.BOLD, Color.white);
    WorldImage changeColorsButton = new RectangleImage(100, 40, OutlineMode.SOLID,
        new Color(33, 138, 61));
    WorldImage changeColorsText = new TextImage("New Colors", 14, FontStyle.BOLD, Color.white);

    // Places everything above onto the canvas
    expectedScene.placeImageXY(background, 375, 380);
    expectedScene.placeImageXY(header, 375, 35);
    expectedScene.placeImageXY(numClicksText, 375, 725);
    expectedScene.placeImageXY(numClicksSubText, 375, 765);
    expectedScene.placeImageXY(changeBoardSizeButton, 113, 40);
    expectedScene.placeImageXY(changeBoardSizeText, 113, 40);
    expectedScene.placeImageXY(changeColorsButton, 637, 40);
    expectedScene.placeImageXY(changeColorsText, 637, 40);

    // Places all cells on the board centered in the black background
    for (ArrayList<ConsCell> arr : testFloodWorld.board) {
      for (ConsCell c : arr) {
        expectedScene.placeImageXY(c.drawCell(c.color, this.testFloodWorld.cellSize),
            (testFloodWorld.cellSize * (c.x + 1)) + 75 - (testFloodWorld.cellSize / 2),
            (testFloodWorld.cellSize * (c.y + 1)) + 80 - (testFloodWorld.cellSize / 2));
      }
    }

    // Test if the reconstruction above is correct to what is expected
    t.checkExpect(this.testFloodWorld.makeScene(), expectedScene);
  }

  // Starts the game. In this test, a boardSize of 20 and a numColors of 6 are used
  // to create a 20x20 Flood-It game board distributing 6 random colors to 400 tiles
  void testBigBang(Tester t) {
    // boardSize must be a factor of 600 to fit the crop of the background
    // Possible boardSizes: 1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 20, 24, 25, 30, 40, 50,
    // 60, 75 ...
    int boardSize = 24;
    int numColors = 6;
    FloodItWorld scene = new FloodItWorld(boardSize, numColors);
    int sceneSizeX = 750;
    int sceneSizeY = 800;

    // Creates the game with a 750x800 canvas
    scene.bigBang(sceneSizeX, sceneSizeY, 0.05);

  }

  // Tests for the New Size Button
  void testOnMousePressedNewSize(Tester t) {
    FloodItWorld world = new FloodItWorld(123456);
    // Test if the board size changes to 24 after the New Size button is pressed
    // once
    world.onMousePressed(new Posn(113, 40));
    t. checkExpect(world.boardSize, 24);
    // Test if the board size changes to 20 after the New Size button is pressed
    // twice
    world. onMousePressed(new Posn(113, 40));
    t. checkExpect(world.boardSize, 20);
  }

  // tests the onKeyEvent method
  void testOnKeyEvent(Tester t) {
    initData();

    testFloodWorld.onKeyEvent("r");
    t.checkExpect(testFloodWorld.clicks, 0);
    t.checkExpect(testFloodWorld.board.get(0).get(0).flooded, true);
  }

  // tests the updateCells method
  void testUpdateCells(Tester t) {
    initData();

    // sets colors to be equal to top and asks if they're flooded
    // (cells that go through updateWorld will only be flooded if
    // this is true)

    t.checkExpect(testFloodWorld.board.get(1).get(0).flooded, false);

    testFloodWorld.board.get(1).get(0).color =
        testFloodWorld.board.get(0).get(0).color;
    testFloodWorld.updateCells();

    t.checkExpect(testFloodWorld.board.get(1).get(0).flooded, true);


    t.checkExpect(testFloodWorld.board.get(1).get(1).flooded, false);

    testFloodWorld.board.get(1).get(1).color =
        testFloodWorld.board.get(0).get(0).color;
    testFloodWorld.updateCells();

    t.checkExpect(testFloodWorld.board.get(1).get(1).flooded, true);


    t.checkExpect(testFloodWorld.board.get(2).get(1).flooded, false);

    testFloodWorld.board.get(2).get(1).color =
        testFloodWorld.board.get(0).get(0).color;
    testFloodWorld.updateCells();

    t.checkExpect(testFloodWorld.board.get(2).get(1).flooded, true);

  }

  // tests the succesfulFlood method
  void testSuccessfulFlood(Tester t) {
    initData();

    // board should not be succesfully flooded yet
    t.checkExpect(testFloodWorld.succesfulFlood(), false);

    // flood every cell
    for (ArrayList<ConsCell> arr : testFloodWorld.board) {
      for (ConsCell c : arr) {
        c.flood();
      }
    }

    // all cells should be flooded
    t.checkExpect(testFloodWorld.succesfulFlood(), true);

  }

}