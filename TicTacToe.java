import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.concurrent.ThreadLocalRandom;

public class TicTacToe extends JFrame
      implements ActionListener {
   JButton b11, b21, b31,
         b12, b22, b32,
         b13, b23, b33;
   boolean myturn;
   BufferedReader br;
   BufferedWriter bw;
   Thread connection;
   Process prologProcess;
   int currGame = 0;
   boolean GameOver = false;
   boolean playNgames = false;
   int NGames = 0;
   String prolog;
   String ttt;
   int scoreX = 0;
   int scoreO = 0;
   boolean WinnerMessageShown = false;
   JLabel PlayerInfo;
   JLabel GameStats;
   JRadioButton option1;
   JPanel controlPanel;
   boolean player1Human = true;
   String filePath = "ttt-java-log.txt";
   FileWriter fileWriter;

   /**
    * Create a tic tac toe game,
    * prolog is the prolog command (e.g. "/opt/local/bin/swipl").
    * ttt is the locator for ttt.pl (e.g. "/javalib/TicTacToe/ttt.pl").
    */
   public TicTacToe(String prolog, String ttt) {
      this.prolog = prolog;
      this.ttt = ttt;
      try {
         fileWriter = new FileWriter(filePath);
         fileWriter.write("Game Started");
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      b11 = new JButton("");
      b21 = new JButton("");
      b31 = new JButton("");
      b12 = new JButton("");
      b22 = new JButton("");
      b32 = new JButton("");
      b13 = new JButton("");
      b23 = new JButton("");
      b33 = new JButton("");
      b11.setActionCommand("(1,1)."); // prolog reads pair term
      b21.setActionCommand("(2,1).");
      b31.setActionCommand("(3,1).");
      b12.setActionCommand("(1,2).");
      b22.setActionCommand("(2,2).");
      b32.setActionCommand("(3,2).");
      b13.setActionCommand("(1,3).");
      b23.setActionCommand("(2,3).");
      b33.setActionCommand("(3,3).");
      Font f = new Font("monospaced", Font.PLAIN, 64);
      b11.setFont(f);
      b21.setFont(f);
      b31.setFont(f);
      b12.setFont(f);
      b22.setFont(f);
      b32.setFont(f);
      b13.setFont(f);
      b23.setFont(f);
      b33.setFont(f);
      b11.addActionListener(this);
      b21.addActionListener(this);
      b31.addActionListener(this);
      b12.addActionListener(this);
      b22.addActionListener(this);
      b32.addActionListener(this);
      b13.addActionListener(this);
      b23.addActionListener(this);
      b33.addActionListener(this);
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(3, 3));
      panel.add(b11);
      panel.add(b21);
      panel.add(b31);
      panel.add(b12);
      panel.add(b22);
      panel.add(b32);
      panel.add(b13);
      panel.add(b23);
      panel.add(b33);

      controlPanel = new JPanel();
      controlPanel.setLayout(new GridLayout(7, 1)); // 3 rows for 3 buttons

      // Create three buttons for the control panel
      PlayerInfo = new JLabel("Player1 (X): human;   Player2 (O): Computer");

      JButton button1 = new JButton("New Game");
      button1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            // Implement the logic to restart the game
            writeToFile("New Human Vs. Computer game started");
            reset();
            setupHVC();
         }
      });
      JButton button2 = new JButton("start N games");
      button2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            // Create an input dialog to get a number from the user
            String input = JOptionPane.showInputDialog("Enter a number:");
            scoreX = 0;
            scoreO = 0;
            PlayerInfo.setText("Player1 (X): Computer;   Player2 (O): Computer");
            writeToFile("New Computer Vs. Computer game started");

            try {
               // Convert the input to an integer and save it in the global variable
               currGame = 0;
               NGames = Integer.parseInt(input);
               System.out.println(NGames);
               playNgames = true;
               cvc();
               // reset();
               // setupCVC();
               // for (int i = 0; i < NGames; i++) {
               // reset();
               // setupCVC();
               // }

            } catch (NumberFormatException ex) {
               JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
            }

         }

      });
      JButton button3 = new JButton("Button 3");
      button3.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            writeToFile("Reset");

            reset();
            myturn = false;
            try {
               Thread.sleep(100);
               try {
                  bw.write("(1,1)." + "\n");
                  System.out.println("custom");
                  bw.flush();

               } catch (Exception xx) {
                  System.out.println(xx);
               }
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }

         }
      });
      option1 = new JRadioButton("Player1 (X) is Human");
      JRadioButton option2 = new JRadioButton("Player1 (X) is Computer");
      option1.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
               // Radio button is selected
               player1Human = true;
               PlayerInfo.setText("Player1 (X): Human;   Player2 (O): Computer");
               writeToFile("First Player Changed to Human");

               System.out.println("Selected");
               // Add your action here
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
               // Radio button is deselected
               player1Human = false;
               PlayerInfo.setText("Player1 (X): Computer;   Player2 (O): Human");
               writeToFile("First Player Changed to Computer");

               System.out.println("Deselected");
               // Add your action here
            }
         }
      });
      option1.setSelected(true);
      // "<html>Line 1<br>Line 2<br>Line 3</html>"
      GameStats = new JLabel(
            "<html>CurrentGame:" + currGame + "/" + NGames + "<br>scores:<br>X:" + scoreX + "&emsp;O:" + scoreO);
      // Create a ButtonGroup to group the radio buttons
      ButtonGroup buttonGroup = new ButtonGroup();
      buttonGroup.add(option1);
      buttonGroup.add(option2);
      // Add ActionListeners to your control panel buttons if needed

      // Add the buttons to the control panel
      controlPanel.add(button1);
      controlPanel.add(button2);
      controlPanel.add(button3);
      controlPanel.add(PlayerInfo);
      controlPanel.add(GameStats);
      controlPanel.add(option1);
      controlPanel.add(option2);

      // Create a container panel to hold both the game panel and the control panel
      JPanel containerPanel = new JPanel();
      containerPanel.setLayout(new GridLayout(1, 2)); // 1 row, 2 columns

      // Add your game panel (panel) to the left side
      containerPanel.add(panel);

      // Add the control panel to the right side
      containerPanel.add(controlPanel);
      // this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE) ;
      this.setTitle("Tic Tac Toe: Txb180006");
      Border panelborder = BorderFactory.createLoweredBevelBorder();
      panel.setBorder(panelborder);
      this.getContentPane().add(containerPanel);
      this.setSize(800, 600);
      this.setLocation(900, 300);
      this.myturn = true;

      Connector connector = new Connector(54321);
      connector.start();

      Socket sock;
      try {
         sock = new Socket("127.0.0.1", 54321);
         br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
         bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
         bw.flush();
      } catch (Exception x) {
         System.out.println(x);
      }

      connection = new Thread() {
         public void run() {
            while (true) {
               try {
                  String s = br.readLine();
                  computer_move(s);
               } catch (Exception xx) {
                  System.out.println(xx);
               }
            }
         }
      };
      connection.start();
      Thread shows = new Thread() {
         public void run() {
            setVisible(true);
         }
      };
      EventQueue.invokeLater(shows);
      SwingUtilities.invokeLater(shows);

      // Start the prolog player

      try {
         prologProcess = Runtime.getRuntime().exec(prolog + " -f " + ttt);
      } catch (Exception xx) {
         System.out.println(xx);
      }

      // On closing, kill the prolog process first and then exit
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent w) {
            if (prologProcess != null)
               prologProcess.destroy();
            writeToFile("GAME OVER!!");
            try {
               fileWriter.close();
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            System.exit(0);
         }
      });
      try {
         Thread.sleep(100);
         try {
            bw.write("(1,1)." + "\n");
            System.out.println("custom");
            bw.flush();

         } catch (Exception xx) {
            System.out.println(xx);
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   // /opt/local/bin/swipl /javalib/TicTacToe/ttt.pl
   public static void main(String[] args) {
      String prolog = "/usr/bin/swipl";
      String ttt = "ttt.pl";
      boolean noargs = true;
      try {
         prolog = args[0];
         ttt = args[1];
         noargs = false;
      } catch (Exception xx) {
         System.out.println("usage: java TicTactoe  <where prolog>  <where ttt>");
      }
      if (noargs) {
         Object[] message = new Object[4];
         message[0] = new Label("  prolog command");
         message[1] = new JTextField(prolog);
         message[2] = new Label("  where ttt.pl ");
         message[3] = new JTextField(ttt);
         try {
            // int I = JOptionPane.showConfirmDialog(null, message, "Where are Prolog and
            // ttt.pl? ",
            // JOptionPane.OK_CANCEL_OPTION);
            // if (I == 2 | I == 1)
            // System.exit(0);
            // System.out.println(I);
            new TicTacToe(((JTextField) message[1]).getText().trim(), ((JTextField) message[3]).getText().trim());
         } catch (Exception yy) {
         }
      } else
         new TicTacToe(prolog, ttt);
   }

   void computer_move(String s) { // " x ## y '
      String[] c = s.split(",");
      int x = Integer.parseInt(c[0].trim()),
            y = Integer.parseInt(c[1].trim());
      // System.out.println(x+","+y) ;

      if (!GameOver) {
         if (!myturn) {
            writeToFile("Computer (O) takes step ("+x+","+y+")");

            if (x == 1) {
               if (y == 1)
                  b11.setText("O");
               else if (y == 2)
                  b12.setText("O");
               else if (y == 3)
                  b13.setText("O");
            } else if (x == 2) {
               if (y == 1)
                  b21.setText("O");
               else if (y == 2)
                  b22.setText("O");
               else if (y == 3)
                  b23.setText("O");
            } else if (x == 3) {
               if (y == 1)
                  b31.setText("O");
               else if (y == 2)
                  b32.setText("O");
               else if (y == 3)
                  b33.setText("O");
            }
            myturn = true;
            // try {
            // Thread.sleep(100);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
         } else {
            writeToFile("Computer (X) takes step ("+x+","+y+")");
            if (x == 1) {
               if (y == 1)
                  b11.setText("X");
               else if (y == 2)
                  b12.setText("X");
               else if (y == 3)
                  b13.setText("X");
            } else if (x == 2) {
               if (y == 1)
                  b21.setText("X");
               else if (y == 2)
                  b22.setText("X");
               else if (y == 3)
                  b23.setText("X");
            } else if (x == 3) {
               if (y == 1)
                  b31.setText("X");
               else if (y == 2)
                  b32.setText("X");
               else if (y == 3)
                  b33.setText("X");
            }
            myturn = false;
            // try {
            // Thread.sleep(100);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
         }
         if (winner()) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }

            if (playNgames && GameOver)
               cvc();

            // else
            // connection.stop();

         }
      }

      // else
      // myturn = true;
      // winner()
   }

   /**
    * Java player
    */
   public void actionPerformed(ActionEvent act) {
      if (!GameOver) {
         if (!myturn)
            return; // otherwise
         String s = ((JButton) act.getSource()).getText();
         if (!s.equals(""))
            return;
         ((JButton) (act.getSource())).setText("X");
         writeToFile("Human (X) takes step "+act.getActionCommand());
         try {
            bw.write(act.getActionCommand() + "\n");
            System.out.println(act.getActionCommand());
            bw.flush();
         } catch (Exception xx) {
            System.out.println(xx);
         }
         myturn = false;
         // if (winner())
         // connection.stop();
         winner();
      }
   }

   /**
    * Do we have a winner?
    */
   boolean winner() {
      return line(b11, b21, b31) ||
            line(b12, b22, b32) ||
            line(b13, b23, b33) ||
            line(b11, b12, b13) ||
            line(b21, b22, b23) ||
            line(b31, b32, b33) ||
            line(b11, b22, b33) ||
            line(b13, b22, b31);
   }

   /**
    * Are three buttons marked with same player?
    * If, so color the line and return true.
    */
   boolean line(JButton b, JButton c, JButton d) {
      if (!b.getText().equals("") && b.getText().equals(c.getText()) &&
            c.getText().equals(d.getText())) {
         GameOver = true;

         if (b.getText().equals("O")) {
            b.setBackground(Color.red);
            c.setBackground(Color.red);
            d.setBackground(Color.red);
            scoreO += 1;
            if (!WinnerMessageShown && !playNgames)
               JOptionPane.showMessageDialog(this, "Player 2 (O) won the game", "Winner Announcement",
                     JOptionPane.INFORMATION_MESSAGE);
            WinnerMessageShown = true;
         } else {
            b.setBackground(Color.green);
            c.setBackground(Color.green);
            d.setBackground(Color.green);
            scoreX += 1;
            if (!WinnerMessageShown && !playNgames && !myturn)
               JOptionPane.showMessageDialog(this, "Player 1 (X) won the game", "Winner Announcement",
                     JOptionPane.INFORMATION_MESSAGE);
            WinnerMessageShown = true;
         }

         return true;
      } else if (!b11.getText().equals("") &&
            !b12.getText().equals("") &&
            !b13.getText().equals("") &&
            !b21.getText().equals("") &&
            !b22.getText().equals("") &&
            !b23.getText().equals("") &&
            !b31.getText().equals("") &&
            !b32.getText().equals("") &&
            !b33.getText().equals("")) {
         GameOver = true;
         return true;
      }
      return false;
   }

   private void reset() {
      GameOver = false;
      WinnerMessageShown = false;
      b11.setText("");
      b21.setText("");
      b31.setText("");
      b12.setText("");
      b22.setText("");
      b32.setText("");
      b13.setText("");
      b23.setText("");
      b33.setText("");

      b11.setBackground(null);
      b21.setBackground(null);
      b31.setBackground(null);
      b12.setBackground(null);
      b22.setBackground(null);
      b32.setBackground(null);
      b13.setBackground(null);
      b23.setBackground(null);
      b33.setBackground(null);

      try {
         bw.flush();
         // br.close();
         bw.close();
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      // // Add any other game state reset logic here
      // connection.stop();

      prologProcess.destroy();
      myturn = true;
      // option1.setSelected(true);
      Socket sock;
      try {
         sock = new Socket("127.0.0.1", 54321);
         br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
         bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
      } catch (Exception x) {
         System.out.println(x);
      }

      // connection = new Thread() {
      // public void run() {
      // while (true) {
      // try {
      // String s = br.readLine();
      // computer_move(s);
      // } catch (Exception xx) {
      // System.out.println(xx);
      // }
      // }
      // }
      // };
      // connection.start();

      try {
         prologProcess = Runtime.getRuntime().exec(prolog + " -f " + ttt);
      } catch (Exception xx) {
         System.out.println(xx);
      }

      // On closing, kill the prolog process first and then exit

   }

   public void setupHVC() {
      playNgames = false;
      if (player1Human) {
         try {
            Thread.sleep(100);
            try {
               bw.write("(1,1)." + "\n");
               System.out.println("custom");
               bw.flush();

            } catch (Exception xx) {
               System.out.println(xx);
            }
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      } else {
         myturn = false;
         try {
            Thread.sleep(100);
            try {
               bw.write("(2,2)." + "\n");
               System.out.println("custom");
               bw.flush();

            } catch (Exception xx) {
               System.out.println(xx);
            }
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

   }

   public void setupCVC() {

      try {
         Thread.sleep(100);
         try {
            bw.write("(-1,-1)." + "\n");
            System.out.println("CVC");
            bw.flush();
            int x = ThreadLocalRandom.current().nextInt(1, 4);
            int y = ThreadLocalRandom.current().nextInt(1, 4);
            myturn = false;
            System.out.println("Ngames:\ncurrGame:" + currGame + "/" + NGames + "\nx:" + x + "   y:" + y + "\n scoreX: "
                  + scoreX + "    scoreO:" + scoreO);
            if (x == 1) {
               if (y == 1) {
                  b11.setText("X");
                  try {
                     bw.write("(1,1)." + "\n");
                     System.out.println("custom");
                     bw.flush();
                  } catch (Exception xx) {
                     System.out.println(xx);
                  }
               }

               else if (y == 2) {
                  b12.setText("X");
                  try {
                     bw.write("(1,2)." + "\n");
                     System.out.println("custom");
                     bw.flush();
                  } catch (Exception xx) {
                     System.out.println(xx);
                  }
               }

               else if (y == 3) {
                  b13.setText("X");
                  try {
                     bw.write("(1,3)." + "\n");
                     System.out.println("custom");
                     bw.flush();
                  } catch (Exception xx) {
                     System.out.println(xx);
                  }
               }
            } else if (x == 2) {
               if (y == 1) {
                  b21.setText("X");
                  try {
                     bw.write("(2,1)." + "\n");
                     System.out.println("custom");
                     bw.flush();
                  } catch (Exception xx) {
                     System.out.println(xx);
                  }
               }

               else if (y == 2) {
                  b22.setText("X");
                  try {
                     bw.write("(2,2)." + "\n");
                     System.out.println("custom");
                     bw.flush();
                  } catch (Exception xx) {
                     System.out.println(xx);
                  }
               }

               else if (y == 3) {
                  b23.setText("X");
                  try {
                     bw.write("(2,3)." + "\n");
                     System.out.println("custom");
                     bw.flush();
                  } catch (Exception xx) {
                     System.out.println(xx);
                  }
               }

            } else if (x == 3) {
               if (y == 1) {
                  b31.setText("X");
                  try {
                     bw.write("(3,1)." + "\n");
                     System.out.println("custom");
                     bw.flush();
                  } catch (Exception xx) {
                     System.out.println(xx);
                  }
               }

               else if (y == 2) {
                  b32.setText("X");
                  try {
                     bw.write("(3,2)." + "\n");
                     System.out.println("custom");
                     bw.flush();
                  } catch (Exception xx) {
                     System.out.println(xx);
                  }
               }

               else if (y == 3) {
                  b33.setText("X");
                  try {
                     bw.write("(3,3)." + "\n");
                     System.out.println("custom");
                     bw.flush();
                  } catch (Exception xx) {
                     System.out.println(xx);
                  }
               }

            }

         } catch (Exception xx) {
            System.out.println(xx);
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   public void cvc() {
      GameStats.setText(
            "<html>CurrentGame:" + currGame + "/" + NGames + "<br>scores:<br>X:" + scoreX + "&emsp;O:" + scoreO);

      if (currGame == NGames) {
         if (scoreO != 0 && scoreX != 0)
            if (scoreO > scoreX) {
               JOptionPane.showMessageDialog(controlPanel,
                     "⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⠀⠀⠀⠀\n" + //
                           "⢠⣤⣤⣤⣼⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣄⣤⣤⣠\n" + //
                           "⢸⠀⡶⠶⠾⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡷⠶⠶⡆⡼\n" + //
                           "⠈⡇⢷⠀⠀⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⠇⠀⢸⢁⡗\n" + //
                           "⠀⢹⡘⡆⠀⢹⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡸⠀⢀⡏⡼⠀\n" + //
                           "⠀⠀⢳⡙⣆⠈⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠇⢀⠞⡼⠁⠀\n" + //
                           "⠀⠀⠀⠙⣌⠳⣼⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣞⡴⣫⠞⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠈⠓⢮⣻⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡼⣩⠞⠉⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠉⠛⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠞⠋⠁⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠳⢤⣀⠀⠀⠀⠀⢀⣠⠖⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⡇⢸⡏⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠖⠒⠓⠚⠓⠒⣦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⣀⣠⣞⣉⣉⣉⣉⣉⣉⣉⣉⣉⣉⣙⣆⣀⡀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⡇ O is the winner    ⠀⡇⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠓⠲⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠖⠃⠀⠀⠀⠀⠀⠀\nGame Over\n\nPlayer2 (O) is the winner🏆🏆\n\nFinal Scores are:\nX:"
                           + scoreX + "  O:" + scoreO,
                     "Selection", JOptionPane.INFORMATION_MESSAGE);
            } else if (scoreX > scoreO) {
               JOptionPane.showMessageDialog(controlPanel,
                     "⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⠀⠀⠀⠀\n" + //
                           "⢠⣤⣤⣤⣼⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣄⣤⣤⣠\n" + //
                           "⢸⠀⡶⠶⠾⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡷⠶⠶⡆⡼\n" + //
                           "⠈⡇⢷⠀⠀⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⠇⠀⢸⢁⡗\n" + //
                           "⠀⢹⡘⡆⠀⢹⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡸⠀⢀⡏⡼⠀\n" + //
                           "⠀⠀⢳⡙⣆⠈⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠇⢀⠞⡼⠁⠀\n" + //
                           "⠀⠀⠀⠙⣌⠳⣼⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣞⡴⣫⠞⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠈⠓⢮⣻⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡼⣩⠞⠉⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠉⠛⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠞⠋⠁⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠳⢤⣀⠀⠀⠀⠀⢀⣠⠖⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⡇⢸⡏⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠖⠒⠓⠚⠓⠒⣦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⣀⣠⣞⣉⣉⣉⣉⣉⣉⣉⣉⣉⣉⣙⣆⣀⡀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⡇ x is the winner    ⠀⡇⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠓⠲⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠖⠃⠀⠀⠀⠀⠀⠀\nGame Over\n\nPlayer1 (X) is the winner\n\nFinal Scores are:\nX:"
                           + scoreX + "  O:" + scoreO,
                     "Selection", JOptionPane.INFORMATION_MESSAGE);
            } else {
               JOptionPane.showMessageDialog(controlPanel,
                     "⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⠀⠀⠀⠀\n" + //
                           "⢠⣤⣤⣤⣼⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣄⣤⣤⣠\n" + //
                           "⢸⠀⡶⠶⠾⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡷⠶⠶⡆⡼\n" + //
                           "⠈⡇⢷⠀⠀⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⠇⠀⢸⢁⡗\n" + //
                           "⠀⢹⡘⡆⠀⢹⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡸⠀⢀⡏⡼⠀\n" + //
                           "⠀⠀⢳⡙⣆⠈⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠇⢀⠞⡼⠁⠀\n" + //
                           "⠀⠀⠀⠙⣌⠳⣼⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣞⡴⣫⠞⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠈⠓⢮⣻⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡼⣩⠞⠉⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠉⠛⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠞⠋⠁⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠳⢤⣀⠀⠀⠀⠀⢀⣠⠖⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⡇⢸⡏⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠖⠒⠓⠚⠓⠒⣦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⣀⣠⣞⣉⣉⣉⣉⣉⣉⣉⣉⣉⣉⣙⣆⣀⡀⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⡇ x/O is the winner   ⠀⡇⠀⠀⠀⠀⠀⠀\n" + //
                           "⠀⠀⠀⠀⠀⠀⠀⠓⠲⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠶⠖⠃⠀⠀⠀⠀⠀⠀\nGame Over\n\nDRAW\n\nFinal Scores are:\nX:" + scoreX
                           + "  O:" + scoreO,
                     "Selection", JOptionPane.INFORMATION_MESSAGE);
            }

         scoreO = 0;
         scoreX = 0;
         return;
      } else {
         reset();
         setupCVC();
         currGame++;
      }
   }

   public void writeToFile(String s) {
      try {
         fileWriter.write(s);
         fileWriter.write("\n");
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}

/*
 * If Java player closes GUI, then Prolog process is terminated.
 * Java process monitors "win" status of both players, signals a win,
 * and closes the connector and prolog player.
 * Prolog justs plays given position.
 * Write all of this up; it is interesting.
 */
