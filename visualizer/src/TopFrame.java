/**
 * This file is a part of software for SamurAI 2013 game visualizer.
 *
 * Takashi Chikayama disclaims to the extent authorized by law any and
 * all warranties, whether express or implied, including, without
 * limitation, any implied warranties of merchantability or fitness for a
 * particular purpose
 * 
 * You assume responsibility for selecting the software to achieve your
 * intended results, and for the results obtained from your use of the
 * software. You shall bear the entire risk as to the quality and the
 * performance of the software.
 */

package samurai2013;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import sun.swing.table.DefaultTableCellHeaderRenderer;

/**
 * Top class of the SamurAI 2013 game visualizer
 * @author Takashi Chikayama
 */
public class TopFrame extends javax.swing.JFrame {
    // Tournament log
    private TournamentLog tournament;
    
    // Current game to visualize
    private int currentRound = 0;
    private int currentGame = 0;
    private VisualizedGameLog game;
    private int currentTurn = 0;
    private int numTurns = 0;

    private void setTurn(int turn) {
        turnSlider.setValue(turn);
    }

    private void forward() {
        if (currentTurn != numTurns) {
            setTurn(currentTurn + 1);
            displayPane.setSelectedComponent(gameStatePanel);
            repaint();
        }
    }

    private void backward() {
        if (currentTurn != 0) {
            setTurn(currentTurn - 1);
            displayPane.setSelectedComponent(gameStatePanel);
            repaint();
        }
    }
    
    private void nextGame() {
        if (currentGame != tournament.nGames) {
            setDisplayedGame(currentRound, currentGame+1);
        }
    }
    
    private void previousGame() {
        if (currentGame != 0) {
            setDisplayedGame(currentRound, currentGame-1);
        }
    }

    private void rewind() {
        setTurn(0);
        repaint();
    }

    /**
     * Painting the top frame
     */ 
    public void paint(Graphics g) {
        super.paint(g);
        if (game != null) {
            turnLabel.setText("Turn = " + currentTurn + "/" + game.maxTurns);
        }
    }
    
    // Button icon colors
    private static Color buttonColor = new Color(0x4080FF);
    private static Color stopColor = new Color(0xFF4040);
    
    private ImageIcon forwardIcon, backwardIcon, ffIcon, rewindIcon, pauseIcon;
    private ImageIcon teamIcons[] = new ImageIcon[4];
    
    private void prepareIcons() {
        Dimension d = new Dimension(30, 30);
        forwardIcon = IconBuilder.forward(d, buttonColor);
        backwardIcon = IconBuilder.backward(d, buttonColor);
        ffIcon = IconBuilder.fastForward(d, buttonColor);
        rewindIcon = IconBuilder.rewind(d, buttonColor);
        pauseIcon = IconBuilder.pause(d, stopColor);
        int h = teamName0.getHeight()/4;
        for (int t = 0; t != 4; t++){
            teamIcons[t] = IconBuilder.hex(h, VisualizedGameLog.agentColors[t]);
        }
    }

    private void setIcons() {
        rewindButton.setIcon(rewindIcon);
        backButton.setIcon(backwardIcon);
        stepButton.setIcon(forwardIcon);
        playPauseButton.setIcon(ffIcon);
        teamName0.setIcon(teamIcons[0]);
        teamName1.setIcon(teamIcons[1]);
        teamName2.setIcon(teamIcons[2]);
        teamName3.setIcon(teamIcons[3]);
    }

    /**
     * Creates new form TopFrame
     */
    private TopFrame() {
        initComponents();
        prepareIcons();
        setIcons();
        siegePauseButton.setSelected(false);
        siegePauseButtonActionPerformed(null);
        siegeStopButton.setSelected(false);
        siegeStopButtonActionPerformed(null);
        syzygyPauseButton.setSelected(false);
        syzygyPauseButtonActionPerformed(null);
        syzygyStopButton.setSelected(false);
        syzygyStopButtonActionPerformed(null);
        setSize(new Dimension(1024, 768));
        teamsPanel.setVisible(false);
        JPanel fieldPanel = new JPanel() {
            int prevTurn = -1;
            int prevSiege = -1;
            BufferedImage img = null;
            Dimension prevSize = null;
            GameLog prevGame = null;

            public void paint(Graphics g) {
                Dimension currentSize = getSize();
                if (game != null) {
                    if (game != prevGame
                            || prevTurn != currentTurn
                            || !currentSize.equals(prevSize)) {
                        img = game.fieldImage(getWidth(), getHeight(), currentTurn);
                        prevTurn = currentTurn;
                        prevSize = currentSize;
                    }
                    if (img != null) g.drawImage(img, 0, 0, null);
                } else {
                    g.fillRect(0, 0, WIDTH, HEIGHT);
                }
            }
        };
        fieldPanel.setPreferredSize(new Dimension(32767, 32767));
        fieldPanel.setMinimumSize(new Dimension(128, 128));

        JPanel scorePanel = new JPanel() {
            int prevTurn = -1;
            BufferedImage img;
            Dimension prevSize;
            public void paint(Graphics g) {
                Dimension currentSize = getSize();
                if (game != null
                        && (prevTurn != currentTurn
                        || !currentSize.equals(prevSize))) {
                    img = game.scoreImage(getWidth(), getHeight(), currentTurn);
                    prevTurn = currentTurn;
                    g.drawImage(img, 0, 0, null);
                }
            }
        };
        scorePanel.setMaximumSize(new Dimension(32767, 30));
        scorePanel.setMinimumSize(new Dimension(128, 30));
        scorePanel.setPreferredSize(new Dimension(1024, 30));

        gameStatePanel.add(fieldPanel, 0);
        gameStatePanel.add(scorePanel, 1);
        
        rankingScrollPanel.setPreferredSize(new Dimension(32767, 32767));
        rankingScrollPanel.setMinimumSize(new Dimension(128, 128));
        
        logFileChooser.setDialogTitle("Open SamurAI Game Log");
        logFileChooser.setFileFilter(new FileNameExtensionFilter("SamurAI game log", "samuraiLog"));
        String directoryPath = System.getenv("SAMURAILOGDIR");
        if (directoryPath != null) {
            File directory = new File(directoryPath);
            logFileChooser.setCurrentDirectory(directory);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        logFileChooser = new javax.swing.JFileChooser();
        messagePopup = new javax.swing.JDialog();
        messageLabel = new javax.swing.JLabel();
        messageOKButton = new javax.swing.JButton();
        optionSettingDialog = new javax.swing.JDialog();
        optionsPanel = new javax.swing.JPanel();
        speedSlider = new javax.swing.JSlider();
        speedLabel = new javax.swing.JLabel();
        transStopButton = new javax.swing.JToggleButton();
        siegeStopButton = new javax.swing.JToggleButton();
        siegeThreshSlider = new javax.swing.JSlider();
        siegeThreshLabel = new javax.swing.JLabel();
        siegePauseButton = new javax.swing.JToggleButton();
        siegePauseTimeSlider = new javax.swing.JSlider();
        siegePauseTimeLabel = new javax.swing.JLabel();
        perHexelPauseSlider = new javax.swing.JSlider();
        perHexelPauseLabel = new javax.swing.JLabel();
        syzygyStopButton = new javax.swing.JToggleButton();
        syzygyPauseButton = new javax.swing.JToggleButton();
        syzygyPauseTimeSlider = new javax.swing.JSlider();
        syzygyPauseTimeLabel = new javax.swing.JLabel();
        optionCloseButton = new javax.swing.JButton();
        controlPanel = new javax.swing.JPanel();
        openButton = new javax.swing.JButton();
        gameSelectionPanel = new javax.swing.JPanel();
        roundLabel = new javax.swing.JLabel();
        roundNumberSpinner = new javax.swing.JSpinner();
        numberOfRoundsLabel = new javax.swing.JLabel();
        gameNoLabel = new javax.swing.JLabel();
        gameNumberSpinner = new javax.swing.JSpinner();
        numberOfGamesLabel = new javax.swing.JLabel();
        playPanel = new javax.swing.JPanel();
        rewindButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        stepButton = new javax.swing.JButton();
        playPauseButton = new javax.swing.JButton();
        turnSlider = new javax.swing.JSlider();
        turnLabel = new javax.swing.JLabel();
        optionsButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767), new java.awt.Dimension(32767, 32767));
        teamsPanel = new javax.swing.JPanel();
        teamName0 = new javax.swing.JLabel();
        teamName1 = new javax.swing.JLabel();
        teamName2 = new javax.swing.JLabel();
        teamName3 = new javax.swing.JLabel();
        displayPane = new javax.swing.JTabbedPane();
        rankingPanel = new javax.swing.JPanel();
        rankingLabel = new javax.swing.JLabel();
        rankingScrollPanel = new javax.swing.JScrollPane();
        rankingTable = new javax.swing.JTable();
        gameStatePanel = new javax.swing.JPanel();

        messageLabel.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        messageLabel.setText("message");
        messageLabel.setMaximumSize(new java.awt.Dimension(32767, 32767));
        messagePopup.getContentPane().add(messageLabel, java.awt.BorderLayout.CENTER);

        messageOKButton.setText("OK");
        messageOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messageOKButtonActionPerformed(evt);
            }
        });
        messagePopup.getContentPane().add(messageOKButton, java.awt.BorderLayout.PAGE_END);

        optionSettingDialog.setTitle("Options: Rokkaku Conquest");
        optionSettingDialog.setMinimumSize(new java.awt.Dimension(226, 500));
        optionSettingDialog.setPreferredSize(null);
        optionSettingDialog.getContentPane().setLayout(new java.awt.FlowLayout());

        optionsPanel.setFocusable(false);
        java.awt.GridBagLayout optionsPanelLayout = new java.awt.GridBagLayout();
        optionsPanelLayout.columnWidths = new int[] {0};
        optionsPanelLayout.rowHeights = new int[] {0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0};
        optionsPanel.setLayout(optionsPanelLayout);

        speedSlider.setMinimum(10);
        speedSlider.setToolTipText("Play Speed");
        speedSlider.setValue(30);
        speedSlider.setFocusable(false);
        speedSlider.setMaximumSize(new java.awt.Dimension(32767, 32767));
        speedSlider.setMinimumSize(new java.awt.Dimension(103, 21));
        speedSlider.setPreferredSize(new java.awt.Dimension(150, 21));
        speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                speedSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        optionsPanel.add(speedSlider, gridBagConstraints);

        speedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        speedLabel.setText("step = 30 ms");
        speedLabel.setFocusable(false);
        speedLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speedLabel.setMaximumSize(new java.awt.Dimension(32767, 32767));
        speedLabel.setMinimumSize(new java.awt.Dimension(103, 21));
        speedLabel.setPreferredSize(new java.awt.Dimension(300, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        optionsPanel.add(speedLabel, gridBagConstraints);

        transStopButton.setText("Stop on Transconental");
        transStopButton.setMaximumSize(new java.awt.Dimension(32767, 32767));
        transStopButton.setMinimumSize(new java.awt.Dimension(150, 20));
        transStopButton.setPreferredSize(new java.awt.Dimension(150, 25));
        transStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transStopButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        optionsPanel.add(transStopButton, gridBagConstraints);

        siegeStopButton.setText("Stop on Big Sieges");
        siegeStopButton.setMaximumSize(new java.awt.Dimension(32767, 32767));
        siegeStopButton.setMinimumSize(new java.awt.Dimension(150, 20));
        siegeStopButton.setPreferredSize(new java.awt.Dimension(150, 25));
        siegeStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                siegeStopButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        optionsPanel.add(siegeStopButton, gridBagConstraints);

        siegeThreshSlider.setMinimum(1);
        siegeThreshSlider.setValue(5);
        siegeThreshSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                siegeThreshSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        optionsPanel.add(siegeThreshSlider, gridBagConstraints);

        siegeThreshLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        siegeThreshLabel.setText("threshold = 5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        optionsPanel.add(siegeThreshLabel, gridBagConstraints);

        siegePauseButton.setText("Pause on Sieges");
        siegePauseButton.setMaximumSize(new java.awt.Dimension(32767, 32767));
        siegePauseButton.setMinimumSize(new java.awt.Dimension(150, 20));
        siegePauseButton.setPreferredSize(new java.awt.Dimension(150, 25));
        siegePauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                siegePauseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        optionsPanel.add(siegePauseButton, gridBagConstraints);

        siegePauseTimeSlider.setFocusable(false);
        siegePauseTimeSlider.setMaximumSize(new java.awt.Dimension(32767, 32767));
        siegePauseTimeSlider.setMinimumSize(new java.awt.Dimension(103, 21));
        siegePauseTimeSlider.setPreferredSize(new java.awt.Dimension(150, 21));
        siegePauseTimeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                siegePauseTimeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        optionsPanel.add(siegePauseTimeSlider, gridBagConstraints);

        siegePauseTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        siegePauseTimeLabel.setText("pause = 100 ms");
        siegePauseTimeLabel.setFocusable(false);
        siegePauseTimeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        siegePauseTimeLabel.setMaximumSize(new java.awt.Dimension(32767, 32767));
        siegePauseTimeLabel.setMinimumSize(new java.awt.Dimension(103, 21));
        siegePauseTimeLabel.setPreferredSize(new java.awt.Dimension(300, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        optionsPanel.add(siegePauseTimeLabel, gridBagConstraints);

        perHexelPauseSlider.setFocusable(false);
        perHexelPauseSlider.setMaximumSize(new java.awt.Dimension(32767, 32767));
        perHexelPauseSlider.setMinimumSize(new java.awt.Dimension(103, 21));
        perHexelPauseSlider.setPreferredSize(new java.awt.Dimension(150, 21));
        perHexelPauseSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                perHexelPauseSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        optionsPanel.add(perHexelPauseSlider, gridBagConstraints);

        perHexelPauseLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        perHexelPauseLabel.setText("per hexel = 100 ms");
        perHexelPauseLabel.setFocusable(false);
        perHexelPauseLabel.setMaximumSize(new java.awt.Dimension(32767, 32767));
        perHexelPauseLabel.setMinimumSize(new java.awt.Dimension(103, 21));
        perHexelPauseLabel.setPreferredSize(new java.awt.Dimension(300, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        optionsPanel.add(perHexelPauseLabel, gridBagConstraints);

        syzygyStopButton.setText("Stop on Syzygies");
        syzygyStopButton.setMaximumSize(new java.awt.Dimension(32767, 32767));
        syzygyStopButton.setMinimumSize(new java.awt.Dimension(150, 20));
        syzygyStopButton.setPreferredSize(new java.awt.Dimension(150, 25));
        syzygyStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syzygyStopButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        optionsPanel.add(syzygyStopButton, gridBagConstraints);

        syzygyPauseButton.setText("Pause on Syzygies");
        syzygyPauseButton.setMaximumSize(new java.awt.Dimension(32767, 32767));
        syzygyPauseButton.setMinimumSize(new java.awt.Dimension(150, 20));
        syzygyPauseButton.setPreferredSize(new java.awt.Dimension(150, 25));
        syzygyPauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syzygyPauseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 24;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        optionsPanel.add(syzygyPauseButton, gridBagConstraints);

        syzygyPauseTimeSlider.setFocusable(false);
        syzygyPauseTimeSlider.setMaximumSize(new java.awt.Dimension(32767, 32767));
        syzygyPauseTimeSlider.setMinimumSize(new java.awt.Dimension(103, 21));
        syzygyPauseTimeSlider.setPreferredSize(new java.awt.Dimension(150, 21));
        syzygyPauseTimeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                syzygyPauseTimeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 26;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        optionsPanel.add(syzygyPauseTimeSlider, gridBagConstraints);

        syzygyPauseTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        syzygyPauseTimeLabel.setText("pause = 100 ms");
        syzygyPauseTimeLabel.setFocusable(false);
        syzygyPauseTimeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        syzygyPauseTimeLabel.setMaximumSize(new java.awt.Dimension(32767, 32767));
        syzygyPauseTimeLabel.setMinimumSize(new java.awt.Dimension(103, 21));
        syzygyPauseTimeLabel.setPreferredSize(new java.awt.Dimension(300, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        optionsPanel.add(syzygyPauseTimeLabel, gridBagConstraints);

        optionCloseButton.setText("Close");
        optionCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionCloseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 30;
        optionsPanel.add(optionCloseButton, gridBagConstraints);

        optionSettingDialog.getContentPane().add(optionsPanel);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rokkaku Conquest");
        setMinimumSize(new java.awt.Dimension(180, 130));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        controlPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        controlPanel.setFocusable(false);
        controlPanel.setMaximumSize(new java.awt.Dimension(160, 32767));
        controlPanel.setMinimumSize(new java.awt.Dimension(160, 130));
        controlPanel.setPreferredSize(new java.awt.Dimension(160, 300));
        java.awt.GridBagLayout controlPanelLayout = new java.awt.GridBagLayout();
        controlPanelLayout.columnWidths = new int[] {0};
        controlPanelLayout.rowHeights = new int[] {0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0};
        controlPanel.setLayout(controlPanelLayout);

        openButton.setText("Open");
        openButton.setFocusable(false);
        openButton.setPreferredSize(new java.awt.Dimension(80, 25));
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        controlPanel.add(openButton, gridBagConstraints);

        gameSelectionPanel.setFocusable(false);
        gameSelectionPanel.setLayout(new java.awt.GridBagLayout());

        roundLabel.setText("Round ");
        roundLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gameSelectionPanel.add(roundLabel, gridBagConstraints);

        roundNumberSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), null, null, Integer.valueOf(1)));
        roundNumberSpinner.setMinimumSize(new java.awt.Dimension(60, 30));
        roundNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 30));
        roundNumberSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                roundNumberSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gameSelectionPanel.add(roundNumberSpinner, gridBagConstraints);

        numberOfRoundsLabel.setText("/0");
        numberOfRoundsLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gameSelectionPanel.add(numberOfRoundsLabel, gridBagConstraints);

        gameNoLabel.setText("Game");
        gameNoLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gameSelectionPanel.add(gameNoLabel, gridBagConstraints);

        gameNumberSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), null, null, Integer.valueOf(1)));
        gameNumberSpinner.setMinimumSize(new java.awt.Dimension(60, 30));
        gameNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 30));
        gameNumberSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                gameNumberSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gameSelectionPanel.add(gameNumberSpinner, gridBagConstraints);

        numberOfGamesLabel.setText("/0");
        numberOfGamesLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gameSelectionPanel.add(numberOfGamesLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        controlPanel.add(gameSelectionPanel, gridBagConstraints);

        playPanel.setFocusable(false);
        playPanel.setMinimumSize(new java.awt.Dimension(150, 110));
        playPanel.setPreferredSize(new java.awt.Dimension(32767, 110));
        playPanel.setLayout(new java.awt.GridBagLayout());

        rewindButton.setBorder(null);
        rewindButton.setBorderPainted(false);
        rewindButton.setContentAreaFilled(false);
        rewindButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        rewindButton.setFocusable(false);
        rewindButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        rewindButton.setMaximumSize(new java.awt.Dimension(34, 34));
        rewindButton.setMinimumSize(new java.awt.Dimension(34, 34));
        rewindButton.setPreferredSize(new java.awt.Dimension(34, 34));
        rewindButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rewindButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        playPanel.add(rewindButton, gridBagConstraints);

        backButton.setBorder(null);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        backButton.setFocusable(false);
        backButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        backButton.setMaximumSize(new java.awt.Dimension(34, 34));
        backButton.setMinimumSize(new java.awt.Dimension(34, 34));
        backButton.setPreferredSize(new java.awt.Dimension(34, 34));
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        playPanel.add(backButton, gridBagConstraints);

        stepButton.setBorder(null);
        stepButton.setBorderPainted(false);
        stepButton.setContentAreaFilled(false);
        stepButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        stepButton.setFocusable(false);
        stepButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        stepButton.setMaximumSize(new java.awt.Dimension(34, 34));
        stepButton.setMinimumSize(new java.awt.Dimension(34, 34));
        stepButton.setPreferredSize(new java.awt.Dimension(34, 34));
        stepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        playPanel.add(stepButton, gridBagConstraints);

        playPauseButton.setBorder(null);
        playPauseButton.setBorderPainted(false);
        playPauseButton.setContentAreaFilled(false);
        playPauseButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        playPauseButton.setFocusable(false);
        playPauseButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        playPauseButton.setMaximumSize(new java.awt.Dimension(34, 34));
        playPauseButton.setMinimumSize(new java.awt.Dimension(34, 34));
        playPauseButton.setPreferredSize(new java.awt.Dimension(34, 34));
        playPauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPauseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        playPanel.add(playPauseButton, gridBagConstraints);

        turnSlider.setValue(0);
        turnSlider.setFocusable(false);
        turnSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                turnSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        playPanel.add(turnSlider, gridBagConstraints);

        turnLabel.setFont(new java.awt.Font("MS UI Gothic", 0, 14)); // NOI18N
        turnLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        turnLabel.setText("Turn = 0/0");
        turnLabel.setFocusable(false);
        turnLabel.setMaximumSize(new java.awt.Dimension(120, 16));
        turnLabel.setMinimumSize(new java.awt.Dimension(120, 16));
        turnLabel.setPreferredSize(new java.awt.Dimension(120, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        playPanel.add(turnLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        controlPanel.add(playPanel, gridBagConstraints);

        optionsButton.setText("show options");
        optionsButton.setFocusable(false);
        optionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        controlPanel.add(optionsButton, gridBagConstraints);

        filler1.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.weighty = 10.0;
        controlPanel.add(filler1, gridBagConstraints);

        teamsPanel.setFocusable(false);
        teamsPanel.setMinimumSize(new java.awt.Dimension(100, 80));
        teamsPanel.setLayout(new javax.swing.BoxLayout(teamsPanel, javax.swing.BoxLayout.Y_AXIS));

        teamName0.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        teamName0.setText("team0");
        teamName0.setFocusable(false);
        teamName0.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        teamName0.setMaximumSize(new java.awt.Dimension(32767, 32767));
        teamName0.setMinimumSize(new java.awt.Dimension(100, 18));
        teamName0.setPreferredSize(new java.awt.Dimension(150, 20));
        teamsPanel.add(teamName0);

        teamName1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        teamName1.setText("team1");
        teamName1.setFocusable(false);
        teamName1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        teamName1.setMaximumSize(new java.awt.Dimension(32767, 32767));
        teamName1.setMinimumSize(new java.awt.Dimension(100, 18));
        teamName1.setPreferredSize(new java.awt.Dimension(150, 20));
        teamsPanel.add(teamName1);

        teamName2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        teamName2.setText("team2");
        teamName2.setFocusable(false);
        teamName2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        teamName2.setMaximumSize(new java.awt.Dimension(32767, 32767));
        teamName2.setMinimumSize(new java.awt.Dimension(100, 18));
        teamName2.setPreferredSize(new java.awt.Dimension(150, 20));
        teamsPanel.add(teamName2);

        teamName3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        teamName3.setText("team3");
        teamName3.setFocusable(false);
        teamName3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        teamName3.setMaximumSize(new java.awt.Dimension(32767, 32767));
        teamName3.setMinimumSize(new java.awt.Dimension(100, 18));
        teamName3.setPreferredSize(new java.awt.Dimension(150, 20));
        teamsPanel.add(teamName3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        controlPanel.add(teamsPanel, gridBagConstraints);

        getContentPane().add(controlPanel);

        displayPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                displayPaneStateChanged(evt);
            }
        });

        rankingPanel.setLayout(new java.awt.GridBagLayout());

        rankingLabel.setFont(new java.awt.Font("Times New Roman", 3, 24)); // NOI18N
        rankingLabel.setForeground(new java.awt.Color(0, 0, 153));
        rankingLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        rankingLabel.setText("Rankings");
        rankingLabel.setMaximumSize(new java.awt.Dimension(32767, 32767));
        rankingLabel.setMinimumSize(new java.awt.Dimension(59, 28));
        rankingLabel.setPreferredSize(new java.awt.Dimension(32767, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        rankingPanel.add(rankingLabel, gridBagConstraints);

        rankingScrollPanel.setFocusable(false);
        rankingScrollPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                rankingScrollPanelComponentShown(evt);
            }
        });
        rankingScrollPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                keyEventHandler(evt);
            }
        });

        rankingTable.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        rankingTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Rank", "Points", "Rsum", "Asum", "Team Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        rankingTable.setFocusable(false);
        rankingTable.setRequestFocusEnabled(false);
        rankingTable.setRowHeight(28);
        rankingTable.setRowMargin(3);
        rankingTable.setRowSelectionAllowed(false);
        rankingTable.getTableHeader().setReorderingAllowed(false);
        rankingTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                keyEventHandler(evt);
            }
        });
        rankingScrollPanel.setViewportView(rankingTable);
        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        rankingTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        rankingTable.getColumnModel().getColumn(4).setPreferredWidth(320);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rankingPanel.add(rankingScrollPanel, gridBagConstraints);

        displayPane.addTab("Ranking", rankingPanel);

        gameStatePanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                keyEventHandler(evt);
            }
        });
        gameStatePanel.setLayout(new javax.swing.BoxLayout(gameStatePanel, javax.swing.BoxLayout.Y_AXIS));
        displayPane.addTab("Game FIeld", gameStatePanel);

        getContentPane().add(displayPane);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        int ret = logFileChooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                File file = logFileChooser.getSelectedFile();
                LogStream gameStream = new LogStream(file);
                tournament = new TournamentLog(gameStream);

                SpinnerNumberModel roundNumberModel = ((SpinnerNumberModel)roundNumberSpinner.getModel());
                roundNumberModel.setMinimum(1);
                roundNumberModel.setMaximum(tournament.nRounds+1);
                roundNumberModel.setValue(1);
                numberOfRoundsLabel.setText(" /" + tournament.nRounds);

                SpinnerNumberModel gameNumberModel = ((SpinnerNumberModel)gameNumberSpinner.getModel());
                gameNumberModel.setMinimum(1);
                gameNumberModel.setMaximum(tournament.nGames+1);
                gameNumberModel.setValue(1);
                numberOfGamesLabel.setText(" /" + tournament.nGames);
            } catch (Exception ex) {
                Logger.getLogger(TopFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            interruptPlay = true;
            setDisplayedGame(0, 0);
        }
    }//GEN-LAST:event_openButtonActionPerformed

    private void setDisplayedGame(int r, int g) {
        currentRound = r;
        if ((Integer)roundNumberSpinner.getValue() != r + 1) {
            ((SpinnerNumberModel)roundNumberSpinner.getModel()).setValue(r + 1);
        }
        currentGame = g;
        if (currentRound == tournament.nRounds) {
            ((SpinnerNumberModel)gameNumberSpinner.getModel()).setMinimum(0);
            ((SpinnerNumberModel)gameNumberSpinner.getModel()).setMaximum(0);            
        } else {
            ((SpinnerNumberModel)gameNumberSpinner.getModel()).setMaximum(tournament.nGames+1);
            if (currentRound == 0) {
                ((SpinnerNumberModel)gameNumberSpinner.getModel()).setMinimum(1);
            } else {
                ((SpinnerNumberModel)gameNumberSpinner.getModel()).setMinimum(0);
            }
        }
        if ((Integer)gameNumberSpinner.getValue() != g + 1) {
            ((SpinnerNumberModel)gameNumberSpinner.getModel()).setValue(g + 1);
        }
        if (currentRound != tournament.nRounds &&
                g >= 0 && g != tournament.nGames) {
            game = tournament.games[r][g];
            numTurns = game.numTurns;
            turnSlider.setMaximum(numTurns);
            setTurn(0);
            teamName0.setText(tournament.teamList[game.teams[0]]);
            teamName1.setText(tournament.teamList[game.teams[1]]);
            teamName2.setText(tournament.teamList[game.teams[2]]);
            teamName3.setText(tournament.teamList[game.teams[3]]);
            teamsPanel.setVisible(true);
        } else {
            game = null;
            currentGame = -1;
            teamsPanel.setVisible(false);
        }
        updateRankings();
        repaint();
        setFocusedComponent();
    }
    
    private int stepInterval = 100;

    private void updateSliderLabel(JLabel label, String name, int value, String unit) {
        label.setText(name + " = " + value + unit);
    }
    private boolean playing = false;
    private boolean interruptPlay = false;

    private void stepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepButtonActionPerformed
        forward();
    }//GEN-LAST:event_stepButtonActionPerformed

    private boolean stopOnSieges = false;
    private boolean pauseOnSieges = false;
    private int siegeStopThresh = 5;
    private int siegeBasicPause = 100;
    private int siegePerHexelPause = 100;
    private boolean stopOnSyzygies = false;
    private boolean pauseOnSyzygies = false;
    private int syzygyPause = 100;
    private boolean stopOnTrans = false;
    private boolean pauseOnTrans = false;
    private int transPause = 100;

    private void playPause() {
        if (game == null) return;
        if (!playing) {
            Thread playThread = new Thread() {

                public void run() {
                    Icon savedIcon = playPauseButton.getIcon();
                    playPauseButton.setIcon(pauseIcon);
                    playing = true;
                    interruptPlay = false;
                    while (!interruptPlay && currentTurn != numTurns) {
                        forward();
                        int syzygies = game.states[currentTurn].syzygies.length;
                        int sieges = game.states[currentTurn].siegesAtTurn;
                        int trans = game.states[currentTurn].trans.length;
                        int pauseTime = stepInterval;
                        if (syzygies != 0) {
                            if (stopOnSyzygies) {
                                break;
                            } else if (pauseOnSyzygies) {
                                pauseTime += syzygyPause;
                            }
                        }
                        if (sieges != 0) {
                            if (stopOnSieges && sieges >= siegeStopThresh) {
                                break;
                            } else if (pauseOnSieges) {
                                pauseTime += siegeBasicPause + sieges * siegePerHexelPause;
                            }
                        }
                        if (trans != 0) {
                            if (stopOnTrans) {
                                break;
                            } else if (pauseOnTrans) {
                                pauseTime += transPause;
                            }
                            
                        }
                        try {
                            Thread.sleep(pauseTime);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TopFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    playPauseButton.setIcon(savedIcon);
                    playing = false;
                }
            };
            if (currentTurn == numTurns) rewind();
            playThread.start();
        } else {
            interruptPlay = true;
        }
    }
    private void playPauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPauseButtonActionPerformed
        playPause();
    }//GEN-LAST:event_playPauseButtonActionPerformed

    private void speedSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_speedSliderStateChanged
        int v = speedSlider.getValue();
        stepInterval = (int) (10 * Math.pow(10.0, v / 50.0));
        updateSliderLabel(speedLabel, "step", stepInterval, "ms");
    }//GEN-LAST:event_speedSliderStateChanged

    private void rewindButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rewindButtonActionPerformed
        if (game == null) return;
        rewind();
    }//GEN-LAST:event_rewindButtonActionPerformed

    private void messageOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messageOKButtonActionPerformed
        messagePopup.setVisible(false);
    }//GEN-LAST:event_messageOKButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        backward();
    }//GEN-LAST:event_backButtonActionPerformed

    private void siegePauseTimeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_siegePauseTimeSliderStateChanged
        int v = siegePauseTimeSlider.getValue();
        siegeBasicPause = (int) (10 * Math.pow(10.0, v / 50.0));
        updateSliderLabel(siegePauseTimeLabel, "pause time", siegeBasicPause, "ms");
    }//GEN-LAST:event_siegePauseTimeSliderStateChanged

    private void perHexelPauseSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_perHexelPauseSliderStateChanged
        int v = perHexelPauseSlider.getValue();
        siegePerHexelPause = (int) (10 * Math.pow(10.0, v / 50.0));
        updateSliderLabel(perHexelPauseLabel, "per hexel", siegePerHexelPause, "ms");
    }//GEN-LAST:event_perHexelPauseSliderStateChanged

    private void turnSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_turnSliderStateChanged
        int t = turnSlider.getValue();
        if (t != currentTurn) {
            currentTurn = t;
            repaint();
        }
    }//GEN-LAST:event_turnSliderStateChanged

    private void optionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsButtonActionPerformed
        optionSettingDialog.setSize(optionSettingDialog.getPreferredSize());
        optionSettingDialog.setVisible(true);
    }//GEN-LAST:event_optionsButtonActionPerformed

    private void siegeThreshSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_siegeThreshSliderStateChanged
        siegeStopThresh = siegeThreshSlider.getValue();
        updateSliderLabel(siegeThreshLabel, "thresh", siegeStopThresh, "");
    }//GEN-LAST:event_siegeThreshSliderStateChanged

    private void transStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transStopButtonActionPerformed
        stopOnTrans = transStopButton.isSelected();
    }//GEN-LAST:event_transStopButtonActionPerformed

    private void siegeStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_siegeStopButtonActionPerformed
        stopOnSieges = siegeStopButton.isSelected();
        if (stopOnSieges && siegePauseButton.isSelected()) {
            siegePauseButton.setSelected(false);
        }
    }//GEN-LAST:event_siegeStopButtonActionPerformed

    private void siegePauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_siegePauseButtonActionPerformed
        pauseOnSieges = siegePauseButton.isSelected();
        if (pauseOnSieges && siegeStopButton.isSelected()) {
            siegeStopButton.setSelected(false);
        }
    }//GEN-LAST:event_siegePauseButtonActionPerformed

    private void syzygyStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syzygyStopButtonActionPerformed
        stopOnSyzygies = syzygyStopButton.isSelected();
        if (stopOnSyzygies && syzygyPauseButton.isSelected()) {
            syzygyPauseButton.setSelected(false);
        }
    }//GEN-LAST:event_syzygyStopButtonActionPerformed

    private void syzygyPauseTimeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_syzygyPauseTimeSliderStateChanged
        int v = syzygyPauseTimeSlider.getValue();
        syzygyPause = (int) (10 * Math.pow(10.0, v / 50.0));
        updateSliderLabel(syzygyPauseTimeLabel, "pause time", syzygyPause, "ms");
    }//GEN-LAST:event_syzygyPauseTimeSliderStateChanged

    private void syzygyPauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syzygyPauseButtonActionPerformed
        pauseOnSyzygies = syzygyPauseButton.isSelected();
        if (pauseOnSyzygies && syzygyStopButton.isSelected()) {
            syzygyStopButton.setSelected(false);
        }
    }//GEN-LAST:event_syzygyPauseButtonActionPerformed

    private void optionCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionCloseButtonActionPerformed
        optionSettingDialog.setVisible(false);
    }//GEN-LAST:event_optionCloseButtonActionPerformed

    private void roundNumberSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_roundNumberSpinnerStateChanged
        currentRound = (Integer)roundNumberSpinner.getValue() - 1;
        setDisplayedGame(currentRound, 0);
    }//GEN-LAST:event_roundNumberSpinnerStateChanged
    
    private void gameNumberSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_gameNumberSpinnerStateChanged
        int v = (Integer)gameNumberSpinner.getValue();
        if (v == 0) {
            currentRound -= 1;
            currentGame = tournament.nGames - 1;
            setDisplayedGame(currentRound, currentGame);            
        } else if (currentRound != tournament.nRounds) {
            currentGame =  v - 1;
            if (currentGame == tournament.nGames) {
                currentRound += 1;
                currentGame = 0;
            }
            setDisplayedGame(currentRound, currentGame);
        }
    }//GEN-LAST:event_gameNumberSpinnerStateChanged

    private TournamentLog prevTournament;
    private int prevRound;
    private int prevGame;
    private void updateRankings() {
        if (tournament != prevTournament ||
                currentRound != prevRound || currentGame != prevGame) {
            prevTournament = tournament;
            prevRound = currentRound;
            prevGame = currentGame;
            if (currentRound == tournament.nRounds) {
                rankingLabel.setText("Final Rankings");
            } else {
                rankingLabel.setText("Rankings before round " + (currentRound+1)
                        + " game " + (currentGame+1));
            }
            RankingEntry ranking[];
            if (currentGame <= 0) {
                if (currentRound == 0) {
                    ranking = tournament.initialRanking;
                } else {
                    ranking = tournament.games[currentRound-1][tournament.nGames-1].ranking;
                }
            } else {
                ranking = tournament.games[currentRound][currentGame-1].ranking;
            }
            String titles[] = { "Rank", "Points", "RSum", "ASum", "Team" };
            DefaultTableModel tm = new DefaultTableModel(titles, 0);
            for (RankingEntry e: ranking) {
                tm.addRow(new Object[] {
                    e.rank+1, e.points, e.ratioSum, e.areaSum, 
                    tournament.teamList[e.team]});
            }
            rankingTable.setModel(tm);
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            final Color bgColor[] = new Color[ranking.length];
            Arrays.fill(bgColor, Color.WHITE);
            GameLog thisGame = null;
            if (currentRound != tournament.nRounds) {
                thisGame = tournament.games[currentRound][currentGame];
            }
            for (int k = 0; k != ranking.length; k++) {
                int t = ranking[k].team;
                for (int j = 0; j != 4; j++) {
                    if (thisGame != null && thisGame.teams[j] == t) {
                        final Color teamColors[] = {
                            new Color(0xF0C0C0),
                            new Color(0xC0F0C0),
                            new Color(0xC0D0F0),
                            new Color(0xF0F0C0),
                        };
                        bgColor[k] = teamColors[j];
                    } else if (ranking[k].roundNumber == currentRound) {
                        // Already finished the game of this round
                        final Color lightGray = new Color(0xF4F4F4);
                        bgColor[k] = lightGray;
                    }
                }
            }
            DefaultTableCellRenderer coloringRenderer = new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean selected, boolean focus,
                        int row, int col) {
                    super.getTableCellRendererComponent(table, value, selected, focus, row, col);
                    setBackground(bgColor[row]);
                    return this;
                }
            };
            TableColumnModel tcm = rankingTable.getColumnModel();
            tcm.getColumn(0).setPreferredWidth(40);
            tcm.getColumn(0).setCellRenderer(rightRenderer);
            tcm.getColumn(1).setPreferredWidth(50);
            tcm.getColumn(1).setCellRenderer(rightRenderer);
            tcm.getColumn(2).setPreferredWidth(100);
            tcm.getColumn(2).setCellRenderer(rightRenderer);
            tcm.getColumn(3).setMaxWidth(70);
            tcm.getColumn(3).setCellRenderer(rightRenderer);
            tcm.getColumn(4).setPreferredWidth(500);
            tcm.getColumn(4).setCellRenderer(coloringRenderer);
            displayPane.setSelectedComponent(rankingPanel);
        }        
    }
    
    private void keyEventHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyEventHandler
        switch (evt.getKeyChar()) {
            case ' ':
                playPause();
                break;
            case 'f':
                forward();
                break;
            case 'b':
                backward();
                break;
            case 'r':
                rewind();
                break;
            case 'n':
                nextGame();
                break;
            case 'p':
                previousGame();
                break;
            case '?':
                messageLabel.setText(
                        "<html>Key Assignments" +
                        "<ul>" +
                        "<li><strong>Space</strong> to <strong>start/stop</strong> play</li>" +
                        "<li><strong>'f'/'b'</strong> to <strong>step foward/backward</strong> by one turn</li>" +
                        "<li><strong>'r'</strong> to <strong>rewind</strong> back to the start of the game</li>" +
                        "<li><strong>'n'/'p'</strong> to go to the <strong>next/prefvious game<strong></li>" +
                        "<li><strong>'?'</strong> to show this help message</li>" +
                        "</ul></html>");
                messagePopup.setSize(new Dimension(480,320));
                messagePopup.validate();
                messagePopup.setVisible(true);
                break;
        }
    }//GEN-LAST:event_keyEventHandler

    
    private void setFocusedComponent() {
        if (gameStatePanel.isShowing()) {
            gameStatePanel.requestFocusInWindow();
        } else {
            rankingTable.requestFocusInWindow();
        }
    }
    
    private void displayPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_displayPaneStateChanged
        setFocusedComponent();
    }//GEN-LAST:event_displayPaneStateChanged

    private void rankingScrollPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_rankingScrollPanelComponentShown
        updateRankings();
    }//GEN-LAST:event_rankingScrollPanelComponentShown
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TopFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TopFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TopFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TopFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TopFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JTabbedPane displayPane;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel gameNoLabel;
    private javax.swing.JSpinner gameNumberSpinner;
    private javax.swing.JPanel gameSelectionPanel;
    private javax.swing.JPanel gameStatePanel;
    private javax.swing.JFileChooser logFileChooser;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton messageOKButton;
    private javax.swing.JDialog messagePopup;
    private javax.swing.JLabel numberOfGamesLabel;
    private javax.swing.JLabel numberOfRoundsLabel;
    private javax.swing.JButton openButton;
    private javax.swing.JButton optionCloseButton;
    private javax.swing.JDialog optionSettingDialog;
    private javax.swing.JButton optionsButton;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JLabel perHexelPauseLabel;
    private javax.swing.JSlider perHexelPauseSlider;
    private javax.swing.JPanel playPanel;
    private javax.swing.JButton playPauseButton;
    private javax.swing.JLabel rankingLabel;
    private javax.swing.JPanel rankingPanel;
    private javax.swing.JScrollPane rankingScrollPanel;
    private javax.swing.JTable rankingTable;
    private javax.swing.JButton rewindButton;
    private javax.swing.JLabel roundLabel;
    private javax.swing.JSpinner roundNumberSpinner;
    private javax.swing.JToggleButton siegePauseButton;
    private javax.swing.JLabel siegePauseTimeLabel;
    private javax.swing.JSlider siegePauseTimeSlider;
    private javax.swing.JToggleButton siegeStopButton;
    private javax.swing.JLabel siegeThreshLabel;
    private javax.swing.JSlider siegeThreshSlider;
    private javax.swing.JLabel speedLabel;
    private javax.swing.JSlider speedSlider;
    private javax.swing.JButton stepButton;
    private javax.swing.JToggleButton syzygyPauseButton;
    private javax.swing.JLabel syzygyPauseTimeLabel;
    private javax.swing.JSlider syzygyPauseTimeSlider;
    private javax.swing.JToggleButton syzygyStopButton;
    private javax.swing.JLabel teamName0;
    private javax.swing.JLabel teamName1;
    private javax.swing.JLabel teamName2;
    private javax.swing.JLabel teamName3;
    private javax.swing.JPanel teamsPanel;
    private javax.swing.JToggleButton transStopButton;
    private javax.swing.JLabel turnLabel;
    private javax.swing.JSlider turnSlider;
    // End of variables declaration//GEN-END:variables
}
