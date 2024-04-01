
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import src.Display.HUD;
import src.Display.Minimap;
import src.Display.Renderer;
import src.Game.Game;
import src.Game.Sound;
import src.Math.Vec2;
import src.World.World;
import src.World.Entity.Bot;

public class Launcher {
    public static void main(String[] args) {

        File mapDir = new File("./res/maps");

        JFrame launcher = new JFrame("Braindead-2DS Launcher");
        launcher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launcher.setLayout(null);
        launcher.setSize(1000, 1000);

        launcher.getContentPane().setBackground(Color.LIGHT_GRAY);

        final JLabel gameLabel = new JLabel("Braindead 2DS Launcher");
        gameLabel.setFont(new Font("DynaPuff", Font.BOLD, 50));
        launcher.add(gameLabel);
        gameLabel.setBounds(20, 20, 800, 100);

        final JLabel mapSelectLabel = new JLabel("Select Map:");
        mapSelectLabel.setFont(new Font("DynaPuff", Font.BOLD, 20));
        launcher.add(mapSelectLabel);
        mapSelectLabel.setBounds(20, 80, 800, 100);

        DefaultListModel<String> mapOptionsList = new DefaultListModel<>();

        File []mapFiles = mapDir.listFiles();
        for(File map : mapFiles)
        {
            mapOptionsList.addElement(map.getName());
        }

        final JList<String> mapOptions = new JList<>(mapOptionsList);
        mapOptions.setSelectedIndex(0);
        mapOptions.setFont(new Font("DynaPuff", Font.BOLD, 14));
        mapOptions.setBounds(20, 150, 300, 200);
        
        launcher.add(mapOptions);

        final JLabel widthLabel = new JLabel("Display Width:");
        widthLabel.setFont(new Font("DynaPuff", Font.BOLD,14));
        widthLabel.setBounds(20, 350, 150, 30);
        launcher.add(widthLabel);

        final JTextField widthTextField = new JTextField("1280");
        widthTextField.setFont(new Font("DynaPuff", Font.BOLD, 14));
        widthTextField.setBounds(180, 355, 100, 20); 
        launcher.add(widthTextField);

        final JLabel heightLabel = new JLabel("Display Height:");
        heightLabel.setFont(new Font("DynaPuff", Font.BOLD, 14));
        heightLabel.setBounds(20, 370, 150, 30);
        launcher.add(heightLabel);

        final JTextField heightTextField = new JTextField("720");
        heightTextField.setFont(new Font("DynaPuff", Font.BOLD, 14));
        heightTextField.setBounds(180, 375, 100, 20); 
        launcher.add(heightTextField);

        final JLabel scaleLabel = new JLabel("World Scale:");
        scaleLabel.setFont(new Font("DynaPuff", Font.BOLD, 14));
        scaleLabel.setBounds(20, 390, 150, 30);
        launcher.add(scaleLabel);

        final JTextField scaleTextField = new JTextField("75");
        scaleTextField.setFont(new Font("DynaPuff", Font.BOLD, 14));
        scaleTextField.setBounds(180, 395, 100, 20); 
        launcher.add(scaleTextField);

        final JLabel TCountLabel = new JLabel("T Players:");
        TCountLabel.setFont(new Font("DynaPuff", Font.BOLD, 14));
        TCountLabel.setBounds(20, 410, 150, 30);
        launcher.add(TCountLabel);

        final JTextField TCountTextField = new JTextField("5");
        TCountTextField.setFont(new Font("DynaPuff", Font.BOLD, 14));
        TCountTextField.setBounds(180, 415, 100, 20); 
        launcher.add(TCountTextField);

        final JLabel CTCountLabel = new JLabel("CT Players:");
        CTCountLabel.setFont(new Font("DynaPuff", Font.BOLD, 14));
        CTCountLabel.setBounds(20, 430, 150, 30);
        launcher.add(CTCountLabel);

        final JTextField CTCountTextField = new JTextField("5");
        CTCountTextField.setFont(new Font("DynaPuff", Font.BOLD, 14));
        CTCountTextField.setBounds(180, 435, 100, 20); 
        launcher.add(CTCountTextField);

        final String []teamChoices = {"Terrorists", "Counter Terrorists"};
        final JComboBox<String> teamChoicesBox = new JComboBox<>(teamChoices);
        teamChoicesBox.setFont(new Font("DynaPuff", Font.BOLD, 14));
        launcher.add(teamChoicesBox);
        teamChoicesBox.setBounds(20, 460, 250, 20);

        final String []difficultyChoices = {"Easy", "Normal", "Hard", "Impossible"};
        final JComboBox<String> difficultyChoicesBox = new JComboBox<>(difficultyChoices);
        difficultyChoicesBox.setFont(new Font("DynaPuff", Font.BOLD, 14));
        launcher.add(difficultyChoicesBox);
        difficultyChoicesBox.setBounds(20, 482, 250, 20);

        final JCheckBox soundEnableBox = new JCheckBox("Enable Audio");
        launcher.add(soundEnableBox);
        soundEnableBox.setFont(new Font("DynaPuff", Font.BOLD, 14));
        soundEnableBox.setSelected(true);
        soundEnableBox.setBounds(20, 505, 250, 30);

        final JCheckBox minimapEnableBox = new JCheckBox("Enable Minimap");
        launcher.add(minimapEnableBox);
        minimapEnableBox.setFont(new Font("DynaPuff", Font.BOLD, 14));
        minimapEnableBox.setSelected(true);
        minimapEnableBox.setBounds(20, 525, 250, 30);

        final JCheckBox hudEnableBox = new JCheckBox("Enable HUD");
        hudEnableBox.setFont(new Font("DynaPuff", Font.BOLD, 14));
        launcher.add(hudEnableBox);
        hudEnableBox.setSelected(true);
        hudEnableBox.setBounds(20, 545, 250, 30);

        final JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("DynaPuff", Font.BOLD, 14));
        launcher.add(startButton);
        startButton.setBounds(20, 600, 200, 50);
        
        launcher.setVisible(true);
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(mapOptions.getSelectedIndex() == -1) 
                {
                    JOptionPane.showMessageDialog(null, "Select a map before starting game!", "Map not Selected", 0);
                    return;
                }

                launcher.setVisible(false);

                double botRecoil = (difficultyChoicesBox.getSelectedIndex() - 4) * -1;
                botRecoil *= Math.pow(botRecoil, 2);

                double botSpeed = (difficultyChoicesBox.getSelectedIndex() + 1) / 70.0;

                start (
                    Integer.parseInt(scaleTextField.getText()),
                    soundEnableBox.isSelected(),
                    mapDir.getAbsolutePath() + "/" + mapOptions.getSelectedValue(),
                    Integer.parseInt(widthTextField.getText()),
                    Integer.parseInt(heightTextField.getText()),
                    minimapEnableBox.isSelected(),
                    hudEnableBox.isSelected(),
                    Integer.parseInt(TCountTextField.getText()),
                    Integer.parseInt(CTCountTextField.getText()),
                    teamChoicesBox.getSelectedItem().equals("Terrorists"),
                    botRecoil,
                    botSpeed
                );
            }
        });

    }


    public static void start(int scale, boolean soundEnable, String filepath, int width, int height, boolean minimapEnable, boolean hudEnable, int TCount, int CTCount, boolean team, double botRecoil, double botSpeed) {
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Renderer.segmentSizePx = scale;
        Sound.soundEnable = soundEnable;
        File file = new File(filepath);

        int window_width = width;
        int window_height = height;
        mainFrame.setSize(window_width, window_height);

        Minimap.MMapEnable = minimapEnable;
        HUD.HUDEnable = hudEnable;

        Game.CTCount = CTCount;
        Game.TCount = TCount;

        Bot.recoil = botRecoil;
        Bot.speed = botSpeed;

        mainFrame.setContentPane(new Game(window_width, window_height, new World(file.getAbsolutePath()), team));
        mainFrame.setVisible(true);
    }
}
