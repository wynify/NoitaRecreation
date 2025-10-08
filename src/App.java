import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Materials.Material;
import Simulator.Simulator;
import World.World;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class App extends JPanel {
    private final int PIXEL_SIZE = 4;
    private final World world;
    private final Simulator simulator;

    private boolean leftMousePressed = false;
    private boolean rightMousePressed = false;

    private final Material[] drawMaterials = {
        Material.SAND,
        Material.WATER,
        Material.OIL,
        Material.ACID,
        Material.FIRE
    };

    private int selectedMaterialIndex = 0;

    private Material getSelectedMaterial() {
        return drawMaterials[selectedMaterialIndex];
    }

    public App() {
        this.world = new World(160, 120);
        this.simulator = new Simulator(world);

        for (int x = 60; x < 100; x++) {
            world.set(x, 10, Material.SAND);
        }

        for (int x = 0; x < world.width; x++) {
            for (int y = world.height - 20; y < world.height; y++) {
                world.set(x, y, Material.FLOOR);
            }
        }

        MouseAdapter mouseHandler = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                leftMousePressed = true;
                placeMaterial(e.getX(), e.getY(), getSelectedMaterial());
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                rightMousePressed = true;
                placeMaterial(e.getX(), e.getY(), getSelectedMaterial());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                leftMousePressed = false;
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                rightMousePressed = false;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (leftMousePressed) {
                placeMaterial(e.getX(), e.getY(), getSelectedMaterial());
            } else if (rightMousePressed) {
                placeMaterial(e.getX(), e.getY(), getSelectedMaterial());
            }
        }
    };

    this.addMouseListener(mouseHandler);
    this.addMouseMotionListener(mouseHandler);
    this.addMouseWheelListener(e -> {
        int notches = e.getWheelRotation();

        selectedMaterialIndex += notches;

        if(selectedMaterialIndex < 0) {
            selectedMaterialIndex = drawMaterials.length - 1;
        } else if(selectedMaterialIndex >= drawMaterials.length) {
            selectedMaterialIndex = 0;
        }
        
        System.out.println("Selected material: " + getSelectedMaterial()); // для отладки
    });

        new Thread(() -> {
            final int TARGET_FPS = 60;
            final long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;

            while (true) {
                long startTime = System.nanoTime();

                simulator.tick();
                repaint();

                long elapsed = System.nanoTime() - startTime;
                long sleepTime = (OPTIMAL_TIME - elapsed) / 1_000_000;

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    public void render(Graphics g) {
        for (int y = 0; y < world.height; ++y) {
            for (int x = 0; x < world.width; ++x) {
                Material m = world.grid[y][x].material;
                g.setColor(getColorFromMaterial(m));
                g.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }
    }

    private Color getColorFromMaterial(Material mat) {
        return switch (mat) {
            case AIR -> Color.BLACK;
            case STONE -> Color.GRAY;
            case SAND -> new Color(194, 178, 128);
            case WATER -> Color.BLUE;
            case FIRE -> Color.ORANGE;
            case FLOOR -> Color.GRAY;
            case OIL -> new Color(61, 61, 61);
            case ACID -> Color.GREEN;
        };
    }

    private void placeMaterial(int pixelX, int pixelY, Material material) {
        int x = pixelX / PIXEL_SIZE;
        int y = pixelY / PIXEL_SIZE;

        if (world.isBounds(x, y)) {
            world.set(x, y, material);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Noita Recreation");
        App app = new App();

        frame.setSize(new Dimension(640, 480));
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(app);
        frame.setVisible(true);
    }
}
