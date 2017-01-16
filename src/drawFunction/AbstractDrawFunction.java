package drawFunction;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public abstract class AbstractDrawFunction extends JPanel {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    //原点的坐标
    private int Ox = 0;
    private int Oy = 0;
    //鼠标的坐标
    private int mouthX = 0;
    private int mouthY = 0;
    //比率
    private int rate = 50;
    //描点法作图
    private Polygon p = new Polygon();
    //实现任意拖动的中间坐标（保存鼠标点击时的原点位置以实现拖动任意位置）
    private int tempX;
    private int tempY;
    
    
    protected abstract double f(double x);
    
    protected AbstractDrawFunction() {
        
        //鼠标移动显示坐标，拖动改变原点位置
        addMouseListener(new MouseAdapter() {
            //记录鼠标点击时（即拖动之前）的原点位置
            @Override
            public void mousePressed(MouseEvent e) {
                tempX = Ox;
                tempY = Oy;
            }
            
        });
        addMouseMotionListener(new MouseMotionListener() {
            
            @Override
            public void mouseMoved(MouseEvent e) {
                mouthX = e.getX();
                mouthY = e.getY();
                repaint();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                //不减去括号里的内容整个图形在拖动中会“瞬移”
                Ox = e.getX()-(mouthX-tempX);
                Oy = e.getY()-(mouthY-tempY);
                repaint();
            }
        });
        
        //滚轮滚动缩放
        addMouseWheelListener(new MouseWheelListener() {
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                if (rotation > 0)
                    rate++;
                else if(rate > 1)
                    rate--;
                repaint();
            }
        });
    }
    
    protected int changeNumberToPixel(double number) {
        return (int)(number * rate);
    }
    
    public void drawFunction() {
        p.reset();//每次重绘都要清空点，不然会出现神奇的bug
        
        for (double x = -Ox*1.0/rate; x <= (-Ox+getWidth())*1.0/rate; x += 1.0/rate) {
            p.addPoint(changeNumberToPixel(x) + Ox, Oy - changeNumberToPixel(f(x)));
        }  
    }
    
    protected void drawRectangularCoordinates(Graphics g) {
        
        //以下那些神奇的数字是为了美观而设计的（坐标轴的小箭头）
        //x轴
        g.drawLine(0, Oy, getWidth(), Oy);
        g.drawLine(getWidth() - 10, Oy + 5, getWidth(), Oy);
        g.drawLine(getWidth() - 10, Oy - 5, getWidth(), Oy);
//        for (int i = -Ox/rate; i <= (-Ox+getWidth())/rate; i++) {
//            if (i == 0)
//                continue;
//            g.drawString(i + "", Ox + i * rate, Oy);
//        }
        double dis = 50.0; //每50像素显示一个下标
        for (double i = dis/rate; i <= (-Ox+getWidth())*1.0/rate; i += dis/rate) {
            g.drawString(String.format("%.2f", i), Ox + (int)(i * rate), Oy);
        }
        for (double i = -dis/rate; i >= -Ox*1.0/rate; i -= dis/rate) {
            g.drawString(String.format("%.2f", i), Ox + (int)(i * rate), Oy);
        }
        
        //y轴
        g.drawLine(Ox, 0, Ox, getHeight());
        g.drawLine(Ox, 0, Ox - 5, 10);
        g.drawLine(Ox, 0, Ox + 5, 10);
//        for (int i = -Oy/rate; i <= (-Oy+getHeight())/rate; i++) {
//            if (i == 0)
//                continue;
//            g.drawString(-i + "", Ox, Oy + i * rate);
//        }
        for (double i = dis/rate; i <= (-Oy+getHeight())*1.0/rate; i += dis/rate) {
            g.drawString(String.format("%.2f", i), Ox, Oy + (int)(i * rate));
        }
        for (double i = -dis/rate; i >= -Oy*1.0/rate; i -= dis/rate) {
            g.drawString(String.format("%.2f", i), Ox, Oy + (int)(i * rate));
        }
        
        //原点     
        g.drawString("0", Ox, Oy);  
        
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (Ox == 0 || Oy == 0) {//加判断是防止鼠标拖动事件调用repaint后重设原点坐标
            Ox = getWidth() / 2;
            Oy = getHeight() / 2;
        }
        
        drawRectangularCoordinates(g);
        drawFunction();
        g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
        
        
        //TODO 以下方法体代码用于测试
        for (int i=0; i<p.npoints; i++)
            System.out.println(p.xpoints[i] + "," + p.ypoints[i]);
        g.drawString("原点: ("+Ox+","+Oy+")", 0, 10);
        g.drawString("鼠标: ("+mouthX+","+mouthY+")", 0, 25);
        g.drawString(String.format("鼠标的坐标: (%.3f,%.3f)", 
                (mouthX-Ox)*1.0/rate, (Oy-mouthY)*1.0/rate), 0, 40);
        g.drawString("点的个数: "+p.npoints, 0, 55);
        g.drawString("比率(像素:单位): "+rate, 0, 70);
    }
    
}
