import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class MineSweepGui {
    MineSweepFrame frame; // 窗口
    ButtonPanel panelMain;// 开始面板
    GamePanel panelGame;// 游戏面板
    MinePanel panelMine;
    ToolPanel panelTool;
    JPanel panelCustomize;// 自定义面板
    Color panelBackground;

    public MineSweepGui() {
        frame = new MineSweepFrame();
        panelMain = new ButtonPanel(2, 2);
        panelGame = new GamePanel();
        panelBackground = new Color(250, 250, 250);
    }
    public void go(){
        mainMenu();
    }

    private void mainMenu() {
        JButton[] buttons = { new JButton("<html>8 x 8<br>10个雷</html>"), new JButton("<html>16 x 16<br>40个雷</html>"),
                new JButton("<html>30 x 16<br>99个雷</html>"), new JButton("<html> &nbsp &nbsp ?<br>自定义</html>") };

        /* 为了正常显示数字 */
        UIManager.put("Button.border", BorderFactory.createLineBorder(Color.black, 0));
        buttons[0].addActionListener(new ButtonListener(8, 8, 10));
        buttons[1].addActionListener(new ButtonListener(16, 16, 40));
        buttons[2].addActionListener(new ButtonListener(16, 30, 99));
        buttons[3].addActionListener(new Button3Listener());

        panelMain.addButtons(buttons);
        panelMain.setBackground(panelBackground);

        frame.setContentPane(panelMain);
        frame.setVisible(true);

    }

    private void gameMenu(int rows, int cols, int mineTotal) {

        panelMine = new MinePanel(rows, cols);// 雷区主体
        panelTool = new ToolPanel();
        panelMine.setLayout(null);
        JScrollPane scrollPane = new JScrollPane(panelMine);
        MineField mf = new MineField(rows, cols, mineTotal);
        // JButton[] buttons = { new JButton("重开一局"), new JButton("改变难度"), new
        // JButton("暂停") };

        mf.subscribe(panelTool);
        mf.submit(new Date(mineTotal, 0, GameStatus.UNSTART));

        panelMine.addButtons(mf.getBoardCells());
        panelMine.setBackground(panelBackground);

        panelTool.getChangeButton().addActionListener(new ChangeButtonListener());
        panelTool.getRestarButton().addActionListener(new RestartButtonListener(cols, rows, mineTotal));
        panelTool.getSuspendButton().addActionListener(new SuspendButtonListener());
        panelTool.setBackground(panelBackground);

        panelGame.setBackground(panelBackground);
        panelGame.add(panelTool);
        panelGame.add(scrollPane);

        frame.setContentPane(panelGame);
        frame.setVisible(true);

    }

    class ChangeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.setContentPane(panelMain);
            panelMain.revalidate();// 当窗口大小改变时，panelMain能正常显示
            panelGame.removeAll();
        }

    }

    class RestartButtonListener implements ActionListener {

        public RestartButtonListener(int cols, int rows, int mineTotal) {
            this.cols = cols;
            this.rows = rows;
            this.mineTotal = mineTotal;
        }

        int cols;
        int rows;
        int mineTotal;

        @Override
        public void actionPerformed(ActionEvent e) {
            panelGame.removeAll();
            gameMenu(rows, cols, mineTotal);
        }
    }

    class SuspendButtonListener implements ActionListener {
        private boolean start = true;

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            if (start) {
                start = false;
                button.setText("开始");
                panelTool.stopTimer();
                System.out.println("get: " + panelTool.getElapsedTime());
                panelMine.suspendPanel();
            } else {
                start = true;
                button.setText("暂停");
                panelTool.startTimer();
                System.out.println("get: " + panelTool.getElapsedTime());
                panelMine.startPanle();
            }

        }

    }

    class ButtonListener implements ActionListener {
        private int row;
        private int col;
        private int mineTotal;

        public ButtonListener(int row, int col, int mineTotal) {
            this.row = row;
            this.col = col;
            this.mineTotal = mineTotal;
        }

        public void actionPerformed(ActionEvent e) {
            gameMenu(row, col, mineTotal);
        }

    }

    class Button3Listener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            /*显示自定义的面板 还未实现 */
        }

    }

}
