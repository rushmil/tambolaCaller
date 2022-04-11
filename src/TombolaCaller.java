import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TombolaCaller {
  // frame
  JFrame frame;

  // panels
  JPanel caller = new JPanel();
  JPanel table = new JPanel();
  static JPanel list = new JPanel();

  // colors
  static Color dominant = Color.decode("#6f5cff");
  static Color secondary = Color.decode("#978ab6");
  static Color accent = Color.decode("#fbf8ff");

  // audio
  static String[] audioPath = new String[90];
  static AudioInputStream audioInputStream;

  //labels for table
  static JLabel[] tableNum = new JLabel[90];

  //list vars
  static JTextField textBar = new JTextField();
  static JButton submit = new JButton();
  static JLabel[] listLabel = new JLabel[0];
  static JButton[] listButton = new JButton[0];

  // backend
  static backend BackEnd = new backend();
  boolean playing = false;
  static int[] randNumbers = BackEnd.getNumbers();

  // number
  static JLabel num = new JLabel("0", SwingConstants.CENTER);

  // buttons
  JButton pause = new JButton();
  JButton resetB = new JButton("Reset");

  // timer
  final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  ScheduledFuture<?> t;

  public static void main(String[] args) {
    new TombolaCaller();
  }

  public TombolaCaller() {
    initialize();
    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
  }

  private void initialize() {
    frame = new JFrame();
    frame.setVisible(true);
    frame.setSize(1280, 720);
    frame.setTitle("Tombola Caller");
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(null);
    caller.setLayout(null);
    caller.setBounds(0, 0, 354, 720);
    caller.setBackground(dominant);

    // numbers
    num.setBounds(2, 58, 353, 278);
    num.setForeground(accent);
    num.setFont(new Font("Roboto", Font.PLAIN, 291));
    caller.add(num);

    // audio path set
    for (int i = 0; i < 90; i++) {
      if (String.valueOf(i).length() == 1 && i != 9) {
        audioPath[i] = "resources/audio/en_num_0" + String.valueOf(i + 1) + "-converted.wav";
      } else {
        audioPath[i] = "resources/audio/en_num_" + String.valueOf(i + 1) + "-converted.wav";
      }
    }

    //initializing table
      for (int i = 0; i < tableNum.length; i++) {
        tableNum[i] = new JLabel("0");
        tableNum[0].setBounds(60, 20, 40, 40);
        tableNum[0].setText("1");
        tableNum[i].setOpaque(true);
        tableNum[i].setHorizontalAlignment(SwingConstants.CENTER);
        tableNum[i].setVerticalAlignment(0);
        tableNum[i].setBackground(secondary);
        tableNum[i].setForeground(accent);
        tableNum[i].setFont(new Font("Roboto", Font.PLAIN, 24));
        tableNum[i].setBorder(BorderFactory.createLineBorder(accent));
        boolean right = i % 10 != 0;
        if (i != 0) {
          tableNum[i].setText(String.valueOf(Integer.parseInt(tableNum[i-1].getText())+1));
          if (right) {
            tableNum[i].setBounds(tableNum[i - 1].getX() + 86, tableNum[i-1].getY(), 40, 40);
          } else {
            tableNum[i].setBounds(tableNum[0].getX(), tableNum[i-1].getY() + 45, 40, 40);
          }
        }
        table.add(tableNum[i]);
      }

    // buttons
    try {
      Image img = ImageIO.read(getClass().getResource("resources/play.png"));
      pause.setIcon(new ImageIcon(img));
    } catch (Exception e) {
      System.out.println(e);
    }
    pause.setBounds(135, 462, 100, 100);
    pause.setBackground(null);
    pause.setBorderPainted(false);
    pause.setMnemonic('p');
    pause.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            playing = !playing;
              if (playing) {
                t =
                    executorService.scheduleAtFixedRate(
                        TombolaCaller::nextNum, 0, 4, TimeUnit.SECONDS);
              } else {
                t.cancel(false);
              }
          }
        });
    caller.add(pause);

    resetB.setBounds(68, 593, 205, 45);
    resetB.setBackground(secondary);
    resetB.setForeground(accent);
    resetB.setFont(new Font("Roboto", Font.PLAIN, 36));

     resetB.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            BackEnd.reset(num, randNumbers);
            for (int i = 0; i < tableNum.length; i++) {
              tableNum[i].setBackground(secondary);
            }
          }
    });

    caller.add(resetB);

    frame.getContentPane().add(caller);

    //table frame
    table.setLayout(null);
    table.setBounds(354, 0, 927, 460);
    table.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    table.setBackground(secondary);
    frame.getContentPane().add(table);

    //list
    list.setLayout(null);
    list.setBounds(354, 460, 927, 259);
    list.setBackground(dominant);

    textBar.setBounds(111, 10, 539, 25);
    list.add(textBar);

    submit.setBounds(694, 8, 100, 29);
    submit.setText("add");
    submit.setBackground(secondary);
    submit.setForeground(accent);
    submit.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                listLabel = Arrays.copyOf(listLabel, listLabel.length + 1);
                listButton = Arrays.copyOf(listButton, listButton.length + 1);
                addContent(textBar.getText());
                textBar.setText("");
              }
            });
    list.add(submit);

    frame.getContentPane().add(list);
    frame.getRootPane().setDefaultButton(submit);
  }

  static void addContent(String text) {
    if (listLabel.length == 1) {
      listLabel[0] = new JLabel(text);
      listLabel[0].setBounds(100, 58, 159, 30);
      listLabel[0].setForeground(accent);
      listLabel[0].setFont(new Font("Roboto", Font.BOLD, 19));
      list.add(listLabel[0]);
      list.revalidate();
      list.repaint();
    } else {
      listLabel[listLabel.length - 1] = new JLabel(text);
      listLabel[listLabel.length - 1].setForeground(accent);
      listLabel[listLabel.length - 1].setFont(new Font("Roboto", Font.BOLD, 19));
      if (listLabel.length % 6 != 0) {
        listLabel[listLabel.length - 1].setBounds(
                listLabel[listLabel.length - 2].getX() + 129,
                listLabel[listLabel.length - 2].getY(),
                159,
                30);
      } else {
        listLabel[listLabel.length - 1].setBounds(
                listLabel[0].getX(), listLabel[listLabel.length - 2].getY() + 50, 159, 30);
      }
      list.add(listLabel[listLabel.length - 1]);
      list.revalidate();
      list.repaint();
    }
    if (listButton.length == 1) {
      listButton[0] = new JButton();
      try {
        Image img = ImageIO.read(new File("src/resources/remove.png"));
        listButton[0].setIcon(new ImageIcon(img));
      } catch (IOException e) {
        e.printStackTrace();
      }
      listButton[0].setBackground(null);
      listButton[0].setBorderPainted(false);
      listButton[0].setBounds(150, 56, 33, 33);
      list.add(listButton[0]);
      list.revalidate();
      list.repaint();
    } else {
      listButton[listButton.length - 1] = new JButton();
      try {
        Image img = ImageIO.read(new File("src/resources/remove.png").getAbsoluteFile());
        listButton[listButton.length - 1].setIcon(new ImageIcon(img));
      } catch (IOException e) {
        e.printStackTrace();
      }
      listButton[listButton.length-1].setBackground(null);
      listButton[listButton.length-1].setBorderPainted(false);
      if (listButton.length % 6 != 0) {
        listButton[listButton.length - 1].setBounds(
                listButton[listButton.length - 2].getX() + 129,
                listButton[listButton.length - 2].getY(),
                33,
                33);
      } else {
        listButton[listButton.length - 1].setBounds(
                listButton[0].getX(),
                listButton[listButton.length - 2].getY() + 50,
                33,
                33);
      }
      list.add(listButton[listButton.length - 1]);
      list.revalidate();
      list.repaint();
    }

    //buttons listener
    for (int i = 0; i < listButton.length; i++) {
      int finalI = i;
      listButton[i].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          listLabel[finalI].setText("<html><strike>" + text + "</strike></html");
        }
      });
    }
  }

  public static void nextNum() {
    num.setText(String.valueOf(randNumbers[0]));
    try {
      audioInputStream =
          AudioSystem.getAudioInputStream(TombolaCaller.class.getResourceAsStream(audioPath[Integer.parseInt(num.getText()) - 1]));
      Clip clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      clip.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
    tableNum[Integer.parseInt(num.getText()) - 1].setBackground(dominant);
    randNumbers = Arrays.copyOfRange(randNumbers, 1, randNumbers.length);
  }
}
