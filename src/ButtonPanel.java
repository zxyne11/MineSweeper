import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;

public class ButtonPanel extends JPanel {
    private int rows;// 按钮有多少行
    private int cols;// 按钮有多少列
    private int margin = 5;// 按钮之间的空隙
    private double aspectRatio = 1.0;// 按钮长和宽之间
    private Color buttonColor = Color.WHITE;// 按钮的默认颜色
    private int minimumEmptyBorder = margin * 2;// 边框和按钮之间的最小空隙

    public int getMargin() {
        return margin;
    }

    public int getMinimumEmptyBorder() {
        return minimumEmptyBorder;
    }

    public ButtonPanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        // setBackground(panelColor);
    }

    public ButtonPanel(int rows, int cols, int margin, double aspectRatio, Color buttonColor, Color panelColor,
            int minimumEmptyBorder) {
        this.rows = rows;
        this.cols = cols;
        this.margin = margin;
        this.aspectRatio = aspectRatio;
        this.buttonColor = buttonColor;
        this.minimumEmptyBorder = minimumEmptyBorder;
        setBackground(panelColor);
    }

    @Override
    public void doLayout() {
        layoutButtons();
    }

    private void layoutButtons() {

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int buttonSize = (int) Math.min((panelWidth - (cols - 1) * margin - minimumEmptyBorder * 2) / (double) cols,
                (panelHeight - (rows - 1) * margin - minimumEmptyBorder * 2) / (double) rows);
        int buttonWidth = (int) (buttonSize * aspectRatio);
        int buttonHeight = buttonSize;

        int xOffset = (panelWidth - buttonSize * cols - (cols - 1) * margin) / 2;

        int yOffset = (panelHeight - buttonSize * rows - (rows - 1) * margin) / 2;
        int x = xOffset;
        int y = yOffset;

        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.setBounds(x, y, buttonWidth, buttonHeight);
            }
            x += buttonWidth + margin;
            if (x >= xOffset + buttonWidth * cols + (cols - 1) * margin) {
                x = xOffset;
                y += buttonHeight + margin;
            }
        }
    }

    void addButtons(JButton[] buttons) {
        for (int i = 0; i < rows * cols; i++) {
            buttons[i].setBackground(buttonColor);
            add(buttons[i]);
        }
    }

    void addButtons(Cell[][] cells) {
        for (int i = 0; i < cells.length; i++)
            for (int j = 0; j < cells[i].length; j++) {

                cells[i][j].button.setBackground(buttonColor);
                add(cells[i][j].button);
            }

    }

}

class ToolPanel extends JPanel implements Flow.Subscriber<Date> {

    private Timer timer;
    private int elapsedTime = 0;
    private int margin = 5;// 按钮之间的空隙
    private double aspectRatio = 2.5;// 按钮长和宽之间
    private Color buttonColor = new Color(255, 255, 255);// 按钮的默认颜色
    private Flow.Subscription subscription;
    private JLabel[] labels;
    private JButton restarButton;
    private JButton changeButton;
    private JButton suspendButton;

    // public int getElapsedTime(){
    // return elapsedTime;
    // }

    public ToolPanel() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                updateTimerLabel();
            }
        });

        /* 初始化和添加按钮 */
        restarButton = new JButton("重开一局");
        changeButton = new JButton("改变难度");
        suspendButton = new JButton("暂停");
        add(restarButton);
        add(changeButton);
        add(suspendButton);
        /* 初始化和添加标签 */
        labels = new JLabel[4];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel();
            add(labels[i]);
        }

        ImageIcon ico = new ImageIcon("image/flag.png");

        labels[0].setIcon(ico);
        // labels[1].setText("0/" + flagTotal);
        ico = new ImageIcon("image/colok.png");
        labels[2].setIcon(ico);
        labels[3].setText("00:00");

    }

    public void doLayout() {
        layoutButtons();

    }

    private void layoutButtons() {

        int panelHeight = getHeight();
        int buttonHeight = 60;// y
        int buttonWidth = (int) (buttonHeight * aspectRatio);// x
        int xOffset = 0;
        int yOffset = panelHeight - buttonHeight;
        int x = xOffset;
        int y = yOffset;

        // setBackground(panelColor);

        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.setBounds(x, y, buttonWidth, buttonHeight);
                y -= (margin + buttonHeight);
            }
        }

        int LabelHeight = 60;
        int LabelWidth = 60;
        xOffset = 0;
        yOffset = LabelHeight - 30;
        x = xOffset;
        y = yOffset;

        labels[0].setBounds(x + 3, y, (int) (LabelWidth * 0.8), (int) (LabelHeight * 0.8));
        ImageIcon ico = (ImageIcon) labels[0].getIcon();
        Image temp = ico.getImage().getScaledInstance((int) (LabelWidth * 0.8), (int) (LabelHeight * 0.8),
                Image.SCALE_DEFAULT);
        ico = new ImageIcon(temp);
        labels[0].setIcon(ico);

        x += 10;
        y += (int) (LabelHeight * 0.8);
        labels[1].setBounds(x, y, LabelWidth, 20);
        x -= 10;
        y += 15;
        labels[2].setBounds(x, y, LabelWidth, LabelHeight);
        x += 12;
        y += LabelHeight;
        labels[3].setBounds(x, y, LabelWidth, 20);
    }

    void addButtons(JButton[] buttons) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBackground(buttonColor);
            add(buttons[i]);
        }
    }

    private void updateTimerLabel() {
        int minutes = elapsedTime / 60;
        int second = elapsedTime % 60;
        // var d = new Date(elapsedTime);
        String s = String.format("%02d:%02d", minutes, second);
        labels[3].setText("" + s);
    }

    public void startTimer() {
        timer.start();
    }

    public void stopTimer() {
        timer.stop();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1);
    }

    @Override
    public void onNext(Date item) {

        if (item.getGameStatus() == GameStatus.START && !timer.isRunning()) {
            startTimer();
        }
        if ((item.getGameStatus() == GameStatus.WIN || item.getGameStatus() == GameStatus.FAIL) && timer.isRunning())
            stopTimer();

        labels[1].setText(item.toString());
        this.subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        // 出现异常时的处理
        throwable.printStackTrace();
        // 取消订阅
        this.subscription.cancel();
    }

    @Override
    public void onComplete() {
        System.out.println("over completed.");
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public JButton getRestarButton() {
        return restarButton;
    }

    public JButton getChangeButton() {
        return changeButton;
    }

    public JButton getSuspendButton() {
        return suspendButton;
    }

}

class GamePanel extends JPanel {

    private int margin = 5;// 按钮之间的空隙
    private int minimumEmptyBorder = margin * 2;// 边框和按钮之间的最小空隙
    private double aspectRatio = 0.8;
    // private JScrollPane jScrollPane;
    // private ToolPanel toolPanel;

    public GamePanel() {
    }

    public void doLayout() {

        layoutPanel();

    }

    private void layoutPanel() {

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int subPanelHeight = panelHeight - minimumEmptyBorder * 2;
        int subPanelWidth = panelWidth - minimumEmptyBorder * 2 - margin;
        int subPanelWidthOne = (int) (subPanelWidth * aspectRatio);
        int subPanelWidthTwo = (int) (subPanelWidth * (1 - aspectRatio));

        int xOffset = minimumEmptyBorder;
        int yOffset = minimumEmptyBorder;

        int x = xOffset;
        int y = yOffset;

        for (Component c : getComponents()) {

            if (c instanceof JScrollPane) {
                c.setBounds(x, y, subPanelWidthOne, subPanelHeight);
                MinePanel panel = (MinePanel) ((JScrollPane) c).getViewport().getView();
                panel.revalidate();
            }
            if (c instanceof ToolPanel) {
                c.setBounds(x + subPanelWidthOne + margin, y, subPanelWidthTwo, subPanelHeight);
            }

        }
    }
}

class MinePanel extends JPanel {

    private int rows;// 按钮有多少行
    private int cols;// 按钮有多少列
    private int margin = 2;// 按钮之间的空隙
    private JPanel suspendPanel;

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMargin() {
        return margin;
    }

    public MinePanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public MinePanel(int rows, int cols, Color panelColor) {
        this.rows = rows;
        this.cols = cols;
        setBackground(panelColor);
    }

    @Override
    public void doLayout() {
        layoutButtons();
        revalidate();
        repaint();
        if (suspendPanel != null) {
            suspendPanel.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    private void layoutButtons() {

        JViewport viewport = (JViewport) getParent(); // 获取对象D的引用
        Dimension viewportSize = viewport.getSize(); // 获取对象D的视口大小

        int width = viewportSize.width;// 获取
        int height = viewportSize.height;

        Dimension minimumButtonSize = Cell.minimumSize;

        int minimumPanelHeight = (int) (minimumButtonSize.getHeight() * rows + (rows - 1) * margin);
        int minimumPanelWidth = (int) (minimumButtonSize.getWidth() * cols + (rows - 1) * margin);

        width = (width + 2) > minimumPanelWidth ? width : minimumPanelWidth;
        height = (height + 2) > minimumPanelHeight ? height : minimumPanelHeight;
        setPreferredSize(new Dimension(width, height));

        int panelWidth = width;
        int panelHeight = height;

        int buttonSize = (int) Math.min((panelWidth - (cols - 1) * margin) / (double) cols,
                (panelHeight - (rows - 1) * margin) / (double) rows);

        int buttonWidth = buttonSize;
        int buttonHeight = buttonSize;

        int xOffset = (panelWidth - buttonSize * cols - (cols - 1) * margin) / 2;

        int yOffset = (panelHeight - buttonSize * rows - (rows - 1) * margin) / 2;
        int x = xOffset;
        int y = yOffset;

        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.setBounds(x, y, buttonWidth, buttonHeight);
            }
            x += buttonWidth + margin;
            if (x >= xOffset + buttonWidth * cols + (cols - 1) * margin) {
                x = xOffset;
                y += buttonHeight + margin;
            }
        }

    }

    public void suspendPanel() {
        suspendPanel = new JPanel();
        add(suspendPanel);
        suspendPanel.setBounds(0, 0, getWidth(), getHeight());
        suspendPanel.setBackground(new Color(186, 189, 182));
        setComponentZOrder(suspendPanel, 0);
        suspendPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });
    }

    public void startPanle() {
        remove(suspendPanel);
    }

    void addButtons(JButton[] buttons) {
        for (int i = 0; i < rows * cols; i++) {
            add(buttons[i]);
        }
    }

    void addButtons(Cell[][] cells) {
        for (int i = 0; i < cells.length; i++)
            for (int j = 0; j < cells[i].length; j++) {
                add(cells[i][j].button);
            }
    }

}

class MineSweepFrame extends JFrame {

    public MineSweepFrame() {

        /* 设置合适的大小 */
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        setSize((int) (screenWidth * 0.7), (int) (screenHeight * 0.6));

        setMinimumSize(new Dimension(900, 650));// 设置最小大小
        setLocationRelativeTo(null);// 设置JFrame的位置为屏幕中心
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setTitle("扫雷");
        // JLabel titleLabel = (JLabel) (getAccessibleContext().getAccessibleChild(0));
        // titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // titleLabel.setVerticalAlignment(SwingConstants.CENTER);
    }
}