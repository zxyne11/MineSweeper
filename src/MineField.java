import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.util.concurrent.SubmissionPublisher;

public class MineField extends SubmissionPublisher<Date> {
    private Cell[][] boardCells;
    int minesTotal;// 雷的数量
    int flagTotal;// 旗子的数量
    int flagMakred;
    int flagMakredTrue;
    boolean FirstClick;
    private GameStatus gameStatus;

    public MineField(int rows, int cols, int mineTotal) {
        setBoardCells(rows, cols);
        this.minesTotal = mineTotal;
        this.flagTotal = mineTotal;
        this.FirstClick = true;
        this.gameStatus = GameStatus.UNSTART;
    }

    private void setBoardCells(int rows, int cols) {

        /* 创建一个Cell[][]二维数组 */
        /* new Cell时,i, j,mine,flag,button都被初始化了 */
        /* numOfMinesAround,cellsAround还未被初始化 */
        boardCells = new Cell[rows][cols];
        for (int i = 0; i < boardCells.length; i++)
            for (int j = 0; j < boardCells[i].length; j++) {
                boardCells[i][j] = new Cell(i, j);
                boardCells[i][j].button.addMouseListener(new CellLeftClickLisenter(boardCells[i][j]));
                boardCells[i][j].button.addMouseListener(new CellRightClickLisenter(boardCells[i][j]));
                boardCells[i][j].button.addMouseListener(new MineFieldGameWin());
                boardCells[i][j].button.addComponentListener(new CellButtonFontAndIconAdapter());
            }

        /* 为每个Cell[i][j]设置 cellsAround */
        for (int i = 0; i < boardCells.length; i++)
            for (int j = 0; j < boardCells[i].length; j++) {

                if (i - 1 >= 0) {
                    if (j - 1 >= 0)
                        boardCells[i][j].setCellsAround(boardCells[i - 1][j - 1]);// 左上角

                    boardCells[i][j].setCellsAround(boardCells[i - 1][j]);// 上边

                    if (j + 1 < boardCells[i].length)
                        boardCells[i][j].setCellsAround(boardCells[i - 1][j + 1]);// 右上角
                }

                if (j - 1 >= 0)
                    boardCells[i][j].setCellsAround(boardCells[i][j - 1]);// 左边
                if (j + 1 < boardCells[i].length)
                    boardCells[i][j].setCellsAround(boardCells[i][j + 1]);// 右边

                if (i + 1 < boardCells.length) {
                    if (j - 1 >= 0)
                        boardCells[i][j].setCellsAround(boardCells[i + 1][j - 1]);// 左下角
                    boardCells[i][j].setCellsAround(boardCells[i + 1][j]);// 下边
                    if (j + 1 < boardCells[i].length)
                        boardCells[i][j].setCellsAround(boardCells[i + 1][j + 1]);// 右下角
                }

            }

    }

    public int getFlagTotal() {
        return flagTotal;
    }

    public int getFlagMakred() {
        return flagMakred;
    }

    public Cell[][] getBoardCells() {
        return boardCells;
    }

    public void initMineField() {
        Random r = new Random();
        int x = 0;
        int rows = boardCells.length;
        int cols = boardCells[0].length;
        /* 设置雷 */
        while (x < minesTotal) {
            int i = r.nextInt(rows);
            int j = r.nextInt(cols);

            if (!boardCells[i][j].isInit()) {
                boardCells[i][j].setMine(true);
                boardCells[i][j].setInit(true);
                x++;
            }

        }
        /* 为每个单元格设置了周围雷的数量 */
        for (Cell[] bc : boardCells)
            for (Cell c : bc) {
                c.setNumOfMinesAround();
            }

        for (Cell[] bc : boardCells) {
            for (Cell c : bc) {
                int x1 = c.isMine() ? 1 : 0;
                System.out.print(x1 + " ");
            }
            System.out.println("");
        }
    }

    public void displayMinesAndFlag() {
        for (Cell[] bc : boardCells)
            for (Cell c : bc) {
                /* 显示未被标记的雷 */
                if (c.isMine() && !c.isFlag()) {
                    c.setMineIcon();
                }
                /* 显示标记错误的雷 */
                if (!c.isMine() && c.isFlag()) {
                    c.button.setBackground(Color.RED);
                }
            }
    }

    public void removeCellsMouseListener() {
        for (Cell[] bc : boardCells)
            for (Cell c : bc) {
                MouseListener[] listeners = c.button.getMouseListeners();
                for (MouseListener listener : listeners) {
                    c.button.removeMouseListener(listener);
                }

            }
    }

    class CellButtonFontAndIconAdapter extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            if (e.getSource() instanceof JButton) {
                JButton button = (JButton) e.getSource();
                resizeFont(button);
                resizeIcon(button);
            }

        }

        private void resizeFont(JButton button) {
            int fontSize = (int) (button.getWidth() * 0.7);
            button.setFont(new Font("Arial", Font.BOLD, fontSize));
        }

        private void resizeIcon(JButton button) {
            ImageIcon icon = (ImageIcon) button.getIcon();
            if (icon != null) {
                Image temp = icon.getImage().getScaledInstance(button.getWidth() + 1, button.getHeight() + 1,
                        Image.SCALE_DEFAULT);
                icon = new ImageIcon(temp);
                button.setIcon(icon);
            }
        }
    }

    class CellLeftClickLisenter extends MouseAdapter {
        Cell cell;

        public CellLeftClickLisenter(Cell cell) {
            this.cell = cell;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) { // 左键
                if (!cell.isFlag()) {
                    if (FirstClick) {
                        gameStatus = GameStatus.START;
                        submit(new Date(flagTotal, flagMakred, gameStatus));
                        FirstClick = false;
                        cell.firstClickedCell();
                        initMineField();/* 开始初始化 */
                        cell.setFirstCellsAroundNumOfOfMinesAround();/* 必须在初始化后才调用这个方法 */

                        System.out.println("\n------------------------------------");
                        for (Cell[] bc : boardCells) {
                            for (Cell c : bc) {
                                int x1 = c.isClick() ? 1 : 0;
                                System.out.print(x1 + " ");
                            }
                            System.out.println("");
                        }

                    } else {
                        if (!cell.isClick()) {
                            if (!cell.isMine()) {

                                cell.setButtonTextAndBackground();
                                if (cell.getNumOfMinesAround() == 0) {
                                    cell.setFirstCellsAroundNumOfOfMinesAround();
                                }
                                cell.setClick(true);
                            } else {
                                /* 显示未被标记的雷 */
                                displayMinesAndFlag();
                                cell.button.setBackground(Color.RED);
                                gameStatus = GameStatus.FAIL;
                                submit(new Date(flagTotal, flagMakred, gameStatus));
                                close();
                                removeCellsMouseListener();// 让监听器失效
                            }

                        } else {
                            /* 周围旗子的数量和雷的数量相同就点开剩下的雷 */
                            int numOfFlagsAround = 0;
                            for (Cell c : cell.getCellsAround()) {
                                if (c.isFlag())
                                    numOfFlagsAround++;
                            }
                            if (numOfFlagsAround == cell.numOfMinesAround) {
                                setCellsAroundNumOfOfMinesAround();
                            }

                        }
                    }
                }
            }

        }

        private void setCellsAroundNumOfOfMinesAround() {
            for (Cell c : cell.getCellsAround()) {
                if (!c.isFlag() && !c.isClick()) {
                    if (c.isMine()) {
                        /* 显示未被标记的雷 */
                        displayMinesAndFlag();
                        c.button.setBackground(Color.RED);

                        /* 让监听器失效 */
                        removeCellsMouseListener();

                    } else {
                        if (c.numOfMinesAround == 0) {
                            c.setButtonTextAndBackground();
                            c.setClick(true);
                            for (Cell c1 : c.getCellsAround()) {
                                c1.setFirstCellsAroundNumOfOfMinesAround();
                            }
                        } else {
                            c.setButtonTextAndBackground();
                            c.setClick(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int submit(Date item) {
        // 打印出数据的内容
        // 调用父类中的submit方法
        return super.submit(item);
    }

    class CellRightClickLisenter extends MouseAdapter {
        Cell cell;

        public CellRightClickLisenter(Cell cell) {
            this.cell = cell;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (!cell.isClick()) {
                    if (!cell.isFlag()) {
                        cell.setFalgIcon();
                        cell.setFlag(true);
                        flagMakred++;
                        submit(new Date(flagTotal, flagMakred, gameStatus));
                        if (cell.isMine() == cell.isFlag())
                            flagMakredTrue++;
                    } else {
                        cell.button.setIcon(null);
                        flagMakred--;
                        submit(new Date(flagTotal, flagMakred, gameStatus));
                        if (cell.isMine() == cell.isFlag())
                            flagMakredTrue--;
                        cell.setFlag(false);
                    }
                }
            }
        }
    }

    class MineFieldGameWin extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            int sum = 0;
            if (flagMakredTrue == minesTotal) {
                for (Cell[] bc : boardCells)
                    for (Cell c : bc)
                        if (!c.isClick())
                            sum++;
                if (flagMakredTrue == sum) {
                    gameStatus = GameStatus.WIN;
                    submit(new Date(flagTotal, flagMakred, gameStatus));
                    close();
                    System.out.println("you win");
                    removeCellsMouseListener();
                }
            }
        }

    }

}

class Date {

    private int flagTotal;
    private int flagMakred;
    private GameStatus gameStatus;

    public Date(int flagTotal, int flagMakred, GameStatus gameStatus) {
        this.flagTotal = flagTotal;
        this.flagMakred = flagMakred;
        this.gameStatus = gameStatus;
    }

    public int getFlagTotal() {
        return flagTotal;
    }

    public int getFlagMakred() {
        return flagMakred;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    @Override
    public String toString() {
        return flagMakred + "/" + flagTotal;
    }

}

enum GameStatus {
    WIN, FAIL, START, UNSTART
}