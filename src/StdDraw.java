import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *  The {@code StdDraw} class provides a basic capability for
 *  creating drawings with your programs. It uses a simple graphics model that
 *  allows you to create drawings consisting of points, lines, squares, 
 *  circles, and other geometric shapes.
 *  <p>
 *  <b>Getting started.</b> To use this class, you must have {@code StdDraw.class}
 *  in your Java classpath. If you used our autoinstaller, you should be all set.
 *  Otherwise, download {@code stdlib.jar} and add it to your Java classpath.
 *  <p>
 *  <b>Basic drawing commands.</b>
 *  Here are the methods you'll use most often:
 *  <ul>
 *  <li> {@link #line(double x0, double y0, double x1, double y1)}
 *  <li> {@link #point(double x, double y)}
 *  <li> {@link #circle(double x, double y, double radius)}
 *  <li> {@link #filledCircle(double x, double y, double radius)}
 *  <li> {@link #square(double x, double y, double radius)}
 *  <li> {@link #filledSquare(double x, double y, double radius)}
 *  <li> {@link #text(double x, double y, String text)}
 *  </ul>
 *  <p>
 *  <b>Color and pen control.</b>
 *  The default drawing color is {@link Color#BLACK}. You can change
 *  the drawing color with {@link #setPenColor(Color color)}.
 *  The default pen radius is 0.005 and is not affected by coordinate scaling.
 *  <p>
 *  <b>Canvas size and coordinate system.</b>
 *  By default, all drawing takes place in a 512-by-512 canvas.
 *  The coordinate system is standard with (0, 0) at the lower left.
 *  You can change the canvas size with {@link #setCanvasSize(int width, int height)}
 *  and the coordinate system with {@link #setXscale(double min, double max)}
 *  and {@link #setYscale(double min, double max)}.
 *  <p>
 *  <b>Animation.</b>
 *  Double buffering is enabled by default, which eliminates flicker.
 *  You can turn on the animation loop with {@link #enableDoubleBuffering()}
 *  and control the animation speed with {@link #show()}.
 *  <p>
 *  <b>Keyboard and mouse interaction.</b>
 *  Standard drawing supports keyboard and mouse interaction.
 *  Use {@link #addKeyListener(KeyListener listener)} for keyboard
 *  and {@link #addMouseListener(MouseListener listener)} for mouse.
 *  <p>
 *  <b>Advanced features.</b>
 *  Standard drawing also includes facilities for text, color, pictures,
 *  and saving drawings to a file.
 *  <p>
 *  <b>Corresponding Python and MATLAB packages.</b>
 *  This class is a Java version of stddraw.py and stddraw.m,
 *  with a few minor differences in the API.
 *  <p>
 *  Graphics library for drawing and visualization.
 *
 *  
 */
public final class StdDraw implements ActionListener, MouseListener, MouseMotionListener, KeyListener {

    // pre-defined colors
    public static final Color BLACK      = Color.BLACK;
    public static final Color BLUE       = Color.BLUE;
    public static final Color CYAN      = Color.CYAN;
    public static final Color DARK_GRAY = Color.DARK_GRAY;
    public static final Color GRAY      = Color.GRAY;
    public static final Color GREEN     = Color.GREEN;
    public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;
    public static final Color MAGENTA   = Color.MAGENTA;
    public static final Color ORANGE    = Color.ORANGE;
    public static final Color PINK      = Color.PINK;
    public static final Color RED       = Color.RED;
    public static final Color WHITE     = Color.WHITE;
    public static final Color YELLOW    = Color.YELLOW;

    // default colors
    private static final Color DEFAULT_PEN_COLOR   = BLACK;
    private static final Color DEFAULT_CLEAR_COLOR = WHITE;

    // current pen color
    private static Color penColor;

    // default canvas size is DEFAULT_SIZE-by-DEFAULT_SIZE
    private static final int DEFAULT_SIZE = 512;
    private static int width  = DEFAULT_SIZE;
    private static int height = DEFAULT_SIZE;

    // default coordinate system
    private static final double DEFAULT_XMIN = 0.0;
    private static final double DEFAULT_XMAX = 1.0;
    private static final double DEFAULT_YMIN = 0.0;
    private static final double DEFAULT_YMAX = 1.0;
    private static double xmin, xmax, ymin, ymax;

    // default pen radius
    private static final double DEFAULT_PEN_RADIUS = 0.002;

    // current pen radius
    private static double penRadius;

    // show we draw immediately or wait until show()?
    private static boolean defer = false;

    // boundary of drawing canvas, 5% border
    private static final double BORDER = 0.05;
    private static final double DEFAULT_XMIN_BORDER = BORDER;
    private static final double DEFAULT_XMAX_BORDER = 1.0 - BORDER;
    private static final double DEFAULT_YMIN_BORDER = BORDER;
    private static final double DEFAULT_YMAX_BORDER = 1.0 - BORDER;

    // for animation
    private static final int DEFAULT_ANIMATION_DELAY = 100;
    private static int animationDelay = DEFAULT_ANIMATION_DELAY;

    // double buffering
    private static boolean doubleBuffering = true;
    private static BufferedImage offscreenImage, onscreenImage;
    private static Graphics2D offscreen, onscreen;

    // singleton for callbacks
    private static StdDraw std = new StdDraw();

    // the frame
    private static JFrame frame;

    // mouse state
    private static boolean mousePressed = false;
    private static double mouseX = 0;
    private static double mouseY = 0;

    // keyboard state
    private static final Set<Integer> keysPressed = new HashSet<Integer>();

    // not instantiable
    private StdDraw() { }

    // static initializer
    static { init(); }

    /**
     * Set the window size to the default size 512-by-512 pixels.
     * This method must be called before any other commands.
     */
    public static void setCanvasSize() {
        setCanvasSize(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /**
     * Set the window size to w-by-h pixels.
     * This method must be called before any other commands.
     *
     * @param w the width as a number of pixels
     * @param h the height as a number of pixels
     * @throws IllegalArgumentException if the width or height is 0 or negative
     */
    public static void setCanvasSize(int w, int h) {
        if (w < 1 || h < 1) throw new IllegalArgumentException("width and height must be positive");
        width = w;
        height = h;
        init();
    }

    // init
    private static void init() {
        if (frame != null) frame.setVisible(false);
        frame = new JFrame();
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen  = onscreenImage.createGraphics();
        setXscale();
        setYscale();
        setPenColor();
        setPenRadius();
        clear();

        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);

        // frame stuff
        ImageIcon icon = new ImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);

        frame.setContentPane(draw);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closes all windows
        // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // closes only current window
        frame.setTitle("Standard Draw");
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);

        // add listeners
        frame.addMouseListener(std);
        frame.addMouseMotionListener(std);
        frame.addKeyListener(std);
        draw.addMouseListener(std);
        draw.addMouseMotionListener(std);
        draw.addKeyListener(std);

        // default coordinate system
        setXscale();
        setYscale();
    }

    /**
     * Set the x-scale to the default range.
     */
    public static void setXscale() {
        setXscale(DEFAULT_XMIN, DEFAULT_XMAX);
    }

    /**
     * Set the y-scale to the default range.
     */
    public static void setYscale() {
        setYscale(DEFAULT_YMIN, DEFAULT_YMAX);
    }

    /**
     * Set the x-scale (a 10% border is added to the values)
     * @param min the minimum value of the x-scale
     * @param max the maximum value of the x-scale
     */
    public static void setXscale(double min, double max) {
        double size = max - min;
        xmin = min - BORDER * size;
        xmax = max + BORDER * size;
    }

    /**
     * Set the y-scale (a 10% border is added to the values).
     * @param min the minimum value of the y-scale
     * @param max the maximum value of the y-scale
     */
    public static void setYscale(double min, double max) {
        double size = max - min;
        ymin = min - BORDER * size;
        ymax = max + BORDER * size;
    }

    // helper functions that scale from user coordinates to screen coordinates and back
    private static double  scaleX(double x) { return width  * (x - xmin) / (xmax - xmin); }
    private static double  scaleY(double y) { return height * (ymax - y) / (ymax - ymin); }
    private static double factorX(double w) { return w * width  / Math.abs(xmax - xmin);  }
    private static double factorY(double h) { return h * height / Math.abs(ymax - ymin);  }
    private static double   userX(double x) { return xmin + x * (xmax - xmin) / width;    }
    private static double   userY(double y) { return ymax - y * (ymax - ymin) / height;   }

    /**
     * Clear the screen to the default color (white).
     */
    public static void clear() {
        clear(DEFAULT_CLEAR_COLOR);
    }

    /**
     * Clear the screen to the given color.
     * @param color the Color to make the background
     */
    public static void clear(Color color) {
        offscreen.setColor(color);
        offscreen.fillRect(0, 0, width, height);
        offscreen.setColor(penColor);
        draw();
    }

    /**
     * Get the current pen radius.
     */
    public static double getPenRadius() { return penRadius; }

    /**
     * Set the pen size to the default.
     */
    public static void setPenRadius() { setPenRadius(DEFAULT_PEN_RADIUS); }

    /**
     * Set the radius of the pen to the given size.
     * @param r the radius of the pen
     * @throws IllegalArgumentException if r is negative
     */
    public static void setPenRadius(double r) {
        if (r < 0) throw new IllegalArgumentException("pen radius must be nonnegative");
        penRadius = r;
        float width = (float) (2.0 * r);
        BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        // BasicStroke stroke = new BasicStroke(width);
        offscreen.setStroke(stroke);
    }

    /**
     * Get the current pen color.
     */
    public static Color getPenColor() { return penColor; }

    /**
     * Set the pen color to the default color (black).
     */
    public static void setPenColor() { setPenColor(DEFAULT_PEN_COLOR); }

    /**
     * Set the pen color to the given color. The available pen colors are
     * BLACK, BLUE, CYAN, DARK_GRAY, GRAY, GREEN, LIGHT_GRAY, MAGENTA,
     * ORANGE, PINK, RED, WHITE, and YELLOW.
     * @param color the Color to make the pen
     */
    public static void setPenColor(Color color) {
        if (color == null) throw new IllegalArgumentException();
        penColor = color;
        offscreen.setColor(penColor);
    }

    /**
     * Set the pen color to the given RGB color.
     * @param red the amount of red (between 0 and 255)
     * @param green the amount of green (between 0 and 255)
     * @param blue the amount of blue (between 0 and 255)
     * @throws IllegalArgumentException if the color arguments are out of range
     */
    public static void setPenColor(int red, int green, int blue) {
        if (red   < 0 || red   >= 256) throw new IllegalArgumentException("red must be between 0 and 255");
        if (green < 0 || green >= 256) throw new IllegalArgumentException("green must be between 0 and 255");
        if (blue  < 0 || blue  >= 256) throw new IllegalArgumentException("blue must be between 0 and 255");
        setPenColor(new Color(red, green, blue));
    }

    /**
     * Turn on or off antialiasing. Antialiasing is on by default.
     * @param on whether to turn on antialiasing
     */
    public static void setAntialiasing(boolean on) {
        if (on)
            offscreen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        else
            offscreen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    /**
     * Draw a point at (x, y).
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    public static void point(double x, double y) {
        double xs = scaleX(x);
        double ys = scaleY(y);
        double r = penRadius;
        float scaledPenRadius = (float) (r * Math.min(width, height));

        // a point is a filled circle
        offscreen.fill(new Ellipse2D.Double(xs - scaledPenRadius/2, ys - scaledPenRadius/2, scaledPenRadius, scaledPenRadius));
        draw();
    }

    /**
     * Draw a line from (x0, y0) to (x1, y1).
     * @param x0 the x-coordinate of the starting point
     * @param y0 the y-coordinate of the starting point
     * @param x1 the x-coordinate of the destination point
     * @param y1 the y-coordinate of the destination point
     */
    public static void line(double x0, double y0, double x1, double y1) {
        offscreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
        draw();
    }

    /**
     * Draw a circle of radius r, centered on (x, y).
     * @param x the x-coordinate of the center of the circle
     * @param y the y-coordinate of the center of the circle
     * @param r the radius of the circle
     * @throws IllegalArgumentException if the radius of the circle is negative
     */
    public static void circle(double x, double y, double r) {
        if (r < 0) throw new IllegalArgumentException("circle radius must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) point(x, y);
        else offscreen.draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draw a filled circle of radius r, centered on (x, y).
     * @param x the x-coordinate of the center of the circle
     * @param y the y-coordinate of the center of the circle
     * @param r the radius of the circle
     * @throws IllegalArgumentException if the radius of the circle is negative
     */
    public static void filledCircle(double x, double y, double r) {
        if (r < 0) throw new IllegalArgumentException("circle radius must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) point(x, y);
        else offscreen.fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draw a square of side length 2r, centered on (x, y).
     * @param x the x-coordinate of the center of the square
     * @param y the y-coordinate of the center of the square
     * @param r radius is half the length of any side
     * @throws IllegalArgumentException if r is negative
     */
    public static void square(double x, double y, double r) {
        if (r < 0) throw new IllegalArgumentException("square side length must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) point(x, y);
        else offscreen.draw(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draw a filled square of side length 2r, centered on (x, y).
     * @param x the x-coordinate of the center of the square
     * @param y the y-coordinate of the center of the square
     * @param r radius is half the length of any side
     * @throws IllegalArgumentException if r is negative
     */
    public static void filledSquare(double x, double y, double r) {
        if (r < 0) throw new IllegalArgumentException("square side length must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) point(x, y);
        else offscreen.fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draw a polygon with the given (x[i], y[i]) coordinates.
     * @param x an array of all the x-coordindates of the polygon
     * @param y an array of all the y-coordindates of the polygon
     */
    public static void polygon(double[] x, double[] y) {
        int n = x.length;
        GeneralPath path = new GeneralPath();
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));
        for (int i = 0; i < n; i++)
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));
        path.closePath();
        offscreen.draw(path);
        draw();
    }

    /**
     * Draw a filled polygon with the given (x[i], y[i]) coordinates.
     * @param x an array of all the x-coordindates of the polygon
     * @param y an array of all the y-coordindates of the polygon
     */
    public static void filledPolygon(double[] x, double[] y) {
        int n = x.length;
        GeneralPath path = new GeneralPath();
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));
        for (int i = 0; i < n; i++)
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));
        path.closePath();
        offscreen.fill(path);
        draw();
    }

    /**
     * Draw a text string centered at (x, y).
     * @param x the x-coordinate of the center of the text
     * @param y the y-coordinate of the center of the text
     * @param text the text to draw
     */
    public static void text(double x, double y, String text) {
        if (text == null) throw new IllegalArgumentException();
        offscreen.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics metrics = offscreen.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = metrics.stringWidth(text);
        int hs = metrics.getDescent();
        offscreen.drawString(text, (float) (xs - ws/2.0), (float) (ys + hs));
        draw();
    }

    /**
     * Draw a text string centered at (x, y) with the given font.
     * @param x the x-coordinate of the center of the text
     * @param y the y-coordinate of the center of the text
     * @param text the text to draw
     * @param font the font to use
     */
    public static void text(double x, double y, String text, Font font) {
        if (text == null) throw new IllegalArgumentException();
        if (font == null) throw new IllegalArgumentException();
        offscreen.setFont(font);
        FontMetrics metrics = offscreen.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = metrics.stringWidth(text);
        int hs = metrics.getDescent();
        offscreen.drawString(text, (float) (xs - ws/2.0), (float) (ys + hs));
        draw();
    }

    /**
     * Enable double buffering. All subsequent calls to 
     * drawing methods such as {@code line()}, {@code circle()},
     * and {@code square()} will be deferred until the next call
     * to {@code show()}. This includes clearing the screen, so
     * you only need to call {@code show()} once per frame, not
     * once per drawing command.
     */
    public static void enableDoubleBuffering() {
        defer = true;
    }

    /**
     * Disable double buffering. All subsequent calls to 
     * drawing methods such as {@code line()}, {@code circle()},
     * and {@code square()} will be displayed on screen when called.
     * This is the default.
     */
    public static void disableDoubleBuffering() {
        defer = false;
    }

    /**
     * Show the drawing on screen and wait for a specified time.
     * It is necessary to call this method to make the drawing appear.
     */
    public static void show() {
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
        try {
            Thread.sleep(animationDelay);
        } catch (InterruptedException e) {
            System.out.println("Error sleeping");
        }
    }

    // draw onscreen if defer is false
    private static void draw() {
        if (!defer) show();
    }

    /**
     * Save the drawing to a file in a supported format.
     * The supported formats are PNG, JPEG, and GIF.
     * @param filename the name of the file with one of the required suffixes
     */
    public static void save(String filename) {
        if (filename == null) throw new IllegalArgumentException();
        File file = new File(filename);
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);

        // png files
        if (suffix.toLowerCase().equals("png")) {
            try {
                ImageIO.write(onscreenImage, suffix, file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // need to change from ARGB to RGB for JPEG
        // reference: http://archives.java.sun.com/cgi-bin/wa?A2=ind0404&L=java2d-interest&D=0&P=2727
        else if (suffix.toLowerCase().equals("jpg")) {
            try {
                BufferedImage saveImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                saveImage.createGraphics().drawImage(onscreenImage, 0, 0, null);
                ImageIO.write(saveImage, suffix, file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            System.out.println("Invalid image file type: " + suffix);
        }
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // intentionally empty
    }

    /**
     * Add a listener for mouse events.
     * @param listener the mouse listener
     */
    public static void addMouseListener(MouseListener listener) {
        frame.addMouseListener(listener);
    }

    /**
     * Add a listener for keyboard events.
     * @param listener the keyboard listener
     */
    public static void addKeyListener(KeyListener listener) {
        frame.addKeyListener(listener);
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // intentionally empty
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
        mouseX = userX(e.getX());
        mouseY = userY(e.getY());
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // intentionally empty
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // intentionally empty
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = userX(e.getX());
        mouseY = userY(e.getY());
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = userX(e.getX());
        mouseY = userY(e.getY());
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // intentionally empty
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    /**
     * Test if the mouse is currently being pressed.
     * @return true or false
     */
    public static boolean isMousePressed() {
        return mousePressed;
    }

    /**
     * Get the current x-coordinate of the mouse.
     * @return the current x-coordinate of the mouse
     */
    public static double mouseX() {
        return mouseX;
    }

    /**
     * Get the current y-coordinate of the mouse.
     * @return the current y-coordinate of the mouse
     */
    public static double mouseY() {
        return mouseY;
    }

    /**
     * Test if the given key is currently being pressed.
     * @param keycode the key to test
     * @return true or false
     */
    public static boolean isKeyPressed(int keycode) {
        return keysPressed.contains(keycode);
    }

    /**
     * Create a menu bar for the application.
     * @return the menu bar
     */
    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem1 = new JMenuItem(" Save...   ");
        menuItem1.addActionListener(std);
        menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menu.add(menuItem1);
        return menuBar;
    }

}
