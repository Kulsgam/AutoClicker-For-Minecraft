import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import javax.swing.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main implements NativeMouseInputListener {
    public static final List<Long> leftClicks = new ArrayList<>();
    public static final List<Long> rightClicks = new ArrayList<>();
    public static final List<Long> leftAutoClicks = new ArrayList<>();
    public static final List<Long> rightAutoClicks = new ArrayList<>();
    public static int leftCPS = 0;
    public static int rightCPS = 0;
    public static int leftAutoCPS = 0;
    public static int rightAutoCPS = 0;
    public static Robot bot;
    public static int ignoreLeftClick = 0;
    public static int ignoreRightClick = 0;
    public static final boolean isWindows = System.getProperty("os.name").contains("Windows");
    public static int activationCPS = 7;
    public static int multiplier = 2;
    public static boolean leftClickEnable = true;
    public static boolean rightClickEnable = true;
    public static int leftClickDelay = 0;
    public static int rightClickDelay = 0;
    public static int leftDelayVariation = 0;
    public static int rightDelayVariation = 0;
    public static Timer timer = new Timer();

    static {
        try {
            bot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static int getRandomInteger(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }

    public void nativeMouseClicked(NativeMouseEvent e) {
        // System.out.println("Mouse Clicked: " + e.getClickCount());
    }

    public void nativeMousePressed(NativeMouseEvent e) {
        // System.out.println("Mouse Pressed: " + e.getButton());
    }

    public void nativeMouseReleased(NativeMouseEvent e) {
        if (e.getButton() == 1) {
            // System.out.println("Left click!");
            leftAutoClicks.add(System.currentTimeMillis());
            if (ignoreLeftClick > 0) {
                ignoreLeftClick--;
                return;
            }
            leftClicks.add(System.currentTimeMillis());
            if (leftClickEnable && leftCPS > activationCPS) {
                int delay = leftClickDelay + getRandomInteger(leftDelayVariation, -leftDelayVariation);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        for (int i = 0; i < multiplier - 1; i++) {
                            ignoreLeftClick += 1;
                            bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        }
                    }
                }, Math.max(delay, 0));
            }
        } else if (
                (isWindows && e.getButton() == 2) ||
                        (!isWindows && e.getButton() == 3)
        ) {
            // System.out.println("Right click!");
            rightAutoClicks.add(System.currentTimeMillis());
            if (ignoreRightClick > 0) {
                ignoreRightClick--;
                return;
            }
            rightClicks.add(System.currentTimeMillis());
            if (rightClickEnable && rightCPS > activationCPS) {
                int delay = rightClickDelay + getRandomInteger(rightDelayVariation, -rightDelayVariation);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        for (int i = 0; i < multiplier - 1; i++) {
                            ignoreRightClick += 1;
                            bot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                            bot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        }
                    }
                }, Math.max(delay, 0));
            }
        }
    }

    public void nativeMouseMoved(NativeMouseEvent e) {
        // System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
    }

    public void nativeMouseDragged(NativeMouseEvent e) {
        // System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
    }

    public static void main(String[] args) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        JFrame frame = new JFrame("AutoClicker");//creating instance of JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//using no layout managers
        frame.setLayout(new BorderLayout());
        JPanel infoPanel = new JPanel();
        JLabel info = new JLabel("");
        infoPanel.add(info);
        frame.add(infoPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        {
            JPanel enablePanel = new JPanel();
            JButton enableLeftClick = new JButton("Enable Auto Left Click");
            enableLeftClick.addActionListener(actionEvent -> {
                JButton button = (JButton) actionEvent.getSource();
                if (leftClickEnable) {
                    leftClickEnable = false;
                    button.setBackground(Color.RED);
                } else {
                    leftClickEnable = true;
                    button.setBackground(Color.GREEN);
                }
            });
            enableLeftClick.setBackground(Color.GREEN);
            enablePanel.add(enableLeftClick);
            JButton enableRightClick = new JButton("Enable Auto Right Click");
            enableRightClick.addActionListener(actionEvent -> {
                JButton button = (JButton) actionEvent.getSource();
                if (rightClickEnable) {
                    rightClickEnable = false;
                    button.setBackground(Color.RED);
                } else {
                    rightClickEnable = true;
                    button.setBackground(Color.GREEN);
                }
            });
            enableRightClick.setBackground(Color.GREEN);
            enablePanel.add(enableRightClick);
            buttonPanel.add(enablePanel, BorderLayout.NORTH);
        }
        {
            JPanel delayPanel = new JPanel();
            delayPanel.setLayout(new BorderLayout());
            {
                JPanel configDelayPanel = new JPanel();
                configDelayPanel.setLayout(new BorderLayout());
                {
                    JPanel panel = new JPanel();
                    JLabel label = new JLabel("Left Click Delay in ms (0)");
                    panel.add(label);
                    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
                    slider.addChangeListener(changeEvent -> {
                        label.setText("Left Click Delay in ms (" + slider.getValue() + ")");
                        Main.leftClickDelay = slider.getValue();
                    });
                    slider.setMajorTickSpacing(100);
                    slider.setPaintTicks(true);
                    slider.setPaintLabels(true);
                    panel.add(slider);
                    configDelayPanel.add(panel, BorderLayout.NORTH);
                }
                {
                    JPanel panel = new JPanel();
                    JLabel label = new JLabel("Right Click Delay in ms (0)");
                    panel.add(label);
                    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
                    slider.addChangeListener(changeEvent -> {
                        label.setText("Right Click Delay in ms (" + slider.getValue() + ")");
                        Main.rightClickDelay = slider.getValue();
                    });
                    slider.setMajorTickSpacing(100);
                    slider.setPaintTicks(true);
                    slider.setPaintLabels(true);
                    panel.add(slider);
                    configDelayPanel.add(panel, BorderLayout.SOUTH);
                }
                delayPanel.add(configDelayPanel, BorderLayout.NORTH);
            }
            {
                JPanel configDelayPanel = new JPanel();
                configDelayPanel.setLayout(new BorderLayout());
                {
                    JPanel panel = new JPanel();
                    JLabel label = new JLabel("Left Click Delay Variation in ms (0)");
                    panel.add(label);
                    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
                    slider.addChangeListener(changeEvent -> {
                        label.setText("Left Click Delay Variation in ms (" + slider.getValue() + ")");
                        Main.leftDelayVariation = slider.getValue();
                    });
                    slider.setMajorTickSpacing(100);
                    slider.setPaintTicks(true);
                    slider.setPaintLabels(true);
                    panel.add(slider);
                    configDelayPanel.add(panel, BorderLayout.NORTH);
                }
                {
                    JPanel panel = new JPanel();
                    JLabel label = new JLabel("Right Click Delay Variation in ms (0)");
                    panel.add(label);
                    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
                    slider.addChangeListener(changeEvent -> {
                        label.setText("Right Click Delay Variation in ms (" + slider.getValue() + ")");
                        Main.rightDelayVariation = slider.getValue();
                    });
                    slider.setMajorTickSpacing(100);
                    slider.setPaintTicks(true);
                    slider.setPaintLabels(true);
                    panel.add(slider);
                    configDelayPanel.add(panel, BorderLayout.SOUTH);
                }
                delayPanel.add(configDelayPanel, BorderLayout.SOUTH);
            }
            buttonPanel.add(delayPanel, BorderLayout.SOUTH);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);

        JPanel configPanel = new JPanel();
        BorderLayout layout = new BorderLayout();
        configPanel.setLayout(layout);
        {
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Multiplier");
            panel.add(label);
            JSlider multiplierSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 2);
            multiplierSlider.addChangeListener(changeEvent -> {
                JSlider slider = (JSlider) changeEvent.getSource();
                multiplier = slider.getValue();
            });
            multiplierSlider.setMajorTickSpacing(1);
            multiplierSlider.setPaintTicks(true);
            multiplierSlider.setPaintLabels(true);
            multiplierSlider.setSnapToTicks(true);
            panel.add(multiplierSlider);
            configPanel.add(panel, BorderLayout.NORTH);
        }
        {
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Activation CPS");
            panel.add(label);
            JSlider activationCPSSlider = new JSlider(JSlider.HORIZONTAL, 1, 15, 7);
            activationCPSSlider.addChangeListener(changeEvent -> {
                JSlider slider = (JSlider) changeEvent.getSource();
                activationCPS = slider.getValue();
            });
            activationCPSSlider.setMajorTickSpacing(2);
            activationCPSSlider.setMinorTickSpacing(1);
            activationCPSSlider.setPaintTicks(true);
            activationCPSSlider.setPaintLabels(true);
            activationCPSSlider.setSnapToTicks(true);
            panel.add(activationCPSSlider);
            configPanel.add(panel, BorderLayout.SOUTH);
        }
        frame.add(configPanel, BorderLayout.SOUTH);

        frame.setSize(450, 600);//400 width and 500 height
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;

        //Set the new frame location
        frame.setLocation(x, y);
        frame.setVisible(true);//making the frame visible

        LogManager.getLogManager().reset();

        // Get the logger for "org.jnativehook" and set the level to off.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        // Construct the example object.
        Main example = new Main();

        // Add the appropriate listeners.
        GlobalScreen.addNativeMouseListener(example);
        GlobalScreen.addNativeMouseMotionListener(example);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                leftCPS = 0;
                List<Long> outdated = new ArrayList<>();
                synchronized (leftClicks) {
                    for (Long click : leftClicks) {
                        if (click < currentTime - 1000) {
                            outdated.add(click);
                        } else {
                            leftCPS++;
                        }
                    }
                    leftClicks.removeAll(outdated);
                }


                rightCPS = 0;
                outdated.clear();
                synchronized (rightClicks) {
                    for (Long click : rightClicks) {
                        if (click < currentTime - 1000) {
                            outdated.add(click);
                        } else {
                            rightCPS++;
                        }
                    }
                    rightClicks.removeAll(outdated);
                }

                leftAutoCPS = 0;
                outdated.clear();
                synchronized (leftAutoClicks) {
                    for (Long click : leftAutoClicks) {
                        if (click < currentTime - 1000) {
                            outdated.add(click);
                        } else {
                            leftAutoCPS++;
                        }
                    }
                    leftAutoClicks.removeAll(outdated);
                }


                rightAutoCPS = 0;
                outdated.clear();
                synchronized (rightAutoClicks) {
                    for (Long click : rightAutoClicks) {
                        if (click < currentTime - 1000) {
                            outdated.add(click);
                        } else {
                            rightAutoCPS++;
                        }
                    }
                    rightAutoClicks.removeAll(outdated);
                }

                info.setText("" +
                        "<html>" +
                        "<div style='text-align: center; font-size: 15px; padding-top: 15px;'>" +
                        "Auto-Double-Clicker v69420" +
                        "</div>" +
                        "<div style='text-align: center; font-size: 10px; font-style: italic;'>" +
                        "made by VimHax" +
                        "</div>" +
                        "<div style='text-align: center; font-size: 15px; color: #ff0000;  padding-top: 15px; font-family: monospace;'>" +
                        "Left CPS: " + leftCPS +
                        "</div>" +
                        "<div style='text-align: center; font-size: 15px; color: #ff0000; font-family: monospace;'>" +
                        "Right CPS: " + rightCPS +
                        "</div>" +
                        "<div style='text-align: center; font-size: 15px; color: #0000ff;  padding-top: 15px; font-family: monospace;'>" +
                        "Auto-Left CPS: " + (leftClickEnable ? (leftCPS > activationCPS ? leftAutoCPS : "0") : "Disabled") +
                        "</div>" +
                        "<div style='text-align: center; font-size: 15px; color: #0000ff; font-family: monospace;'>" +
                        "Auto-Right CPS: " + (rightClickEnable ? (rightCPS > activationCPS ? rightAutoCPS : "0") : "Disabled") +
                        "</div>" +
                        "</html>"
                );
            }
        }, 0, 50);
    }
}
