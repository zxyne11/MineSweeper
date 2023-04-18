import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.util.*;

public class Cell {
    int numOfMinesAround;// 周围雷的数量
    int i, j;
    private boolean mine;
    private boolean flag;
    private boolean init;
    private boolean click;
    JButton button;
    private ArrayList<Cell> cellsAround;// 与其相交接的方块

    static Color buttonExternalColor = new Color(186, 189, 182);
    static Color buttonZeroColor = new Color(222, 222, 220);
    static Color buttonOneColor = new Color(221, 250, 195);
    static Color buttonTwoColor = new Color(236, 237, 191);
    static Color buttonThreeColor = new Color(237, 218, 180);
    static Color buttonFourColor = new Color(237, 195, 138);
    static Color buttonFiveColor = new Color(247, 161, 162);
    static Color buttonSixColor = new Color(247, 162, 129);
    static Color buttonSevenColor = new Color(255, 125, 96);
    static Color buttonEightColor = new Color(255, 50, 60);
    static Dimension minimumSize = new Dimension(25, 25);

    public Cell(int i, int j) {
        this.numOfMinesAround = 0;
        this.i = i;
        this.j = j;
        this.mine = false;
        this.flag = false;
        this.init = false;
        this.click = false;

        cellsAround = new ArrayList<>();
        button = new JButton();
        button.setBackground(buttonExternalColor);
    }

    /* J */
    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    /* I */
    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    /* 计算周围雷的数量 */
    public void setNumOfMinesAround() {

        for (Cell cell : cellsAround) {
            if (cell.isMine()) {
                numOfMinesAround++;
            }
        }
    }

    public int getNumOfMinesAround() {
        return numOfMinesAround;
    }

    /* Mine */
    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isMine() {
        return mine;
    }

    /* Flag */
    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /* Init */
    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isClick() {
        return click;
    }

    public void setClick(boolean click) {
        this.click = click;
    }

    public ArrayList<Cell> getCellsAround() {
        return cellsAround;
    }

    // -------------------------------------------------------------------------------------------
    public void setCellsAround(Cell c) {
        cellsAround.add(c);
    }

    /* 第一次点击时候，将周围的单元格的雷设置为flag,设置为初始化，设置周围单元格的颜色 */
    public void firstClickedCell() {
        /* 已经初始化，没有雷 */
        setInit(true);
        setMine(false);

        for (Cell c : cellsAround) {
            c.setInit(true);
            c.setMine(false);
        }
    }

    public void setMineIcon() {
        setImageIcon("image/mine.png");
    }

    public void setFalgIcon() {
        setImageIcon("image/flag.png");
    }

    private void setImageIcon(String fileName) {
        ImageIcon ico = new ImageIcon(fileName);
        Image temp = ico.getImage().getScaledInstance(button.getWidth() + 1, button.getHeight() + 1,
                Image.SCALE_DEFAULT);
        ico = new ImageIcon(temp);
        button.setIcon(ico);

    }

    public void setButtonTextAndBackground() {

        if (numOfMinesAround > 0)
            button.setText("" + numOfMinesAround);

        switch (numOfMinesAround) {
            case 0:
                button.setBackground(buttonZeroColor);
                break;
            case 1:
                button.setBackground(buttonOneColor);
                break;
            case 2:
                button.setBackground(buttonTwoColor);
                break;
            case 3:
                button.setBackground(buttonThreeColor);
                break;
            case 4:
                button.setBackground(buttonFourColor);
                break;
            case 5:
                button.setBackground(buttonFiveColor);
                break;
            case 6:
                button.setBackground(buttonSixColor);
                break;
            case 7:
                button.setBackground(buttonSevenColor);
                break;
            case 8:
                button.setBackground(buttonEightColor);
                break;
        }
        // int fontSize = (int) (button.getWidth() * 0.4 + 1);
        // button.setFont(new Font("Arial", Font.BOLD, fontSize));

    }

    public void setFirstCellsAroundNumOfOfMinesAround() {
        if (!isFlag() && !isClick()) {
            if (numOfMinesAround == 0) {
                setButtonTextAndBackground();
                setClick(true);
                for (Cell c1 : cellsAround) {
                    c1.setFirstCellsAroundNumOfOfMinesAround();
                }
            } else {
                setButtonTextAndBackground();
                setClick(true);
            }
        }

    }

}
