package ru.nsu.ccfit.dorozhko.task3;

import javafx.scene.control.ToolBar;
import ru.nsu.ccfit.dorozhko.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Created by Anton on 25.04.2014.
 */
public class FilterFrames {
    OriginImage originImage = new OriginImage();

    /**
     * Ctor that manages adding menu items, actions, canvas to the mainFrame.
     * May be I should do Init() method instead.
     */
    public FilterFrames() {
        final MainFrame mainFrame = new MainFrame("Лабораторная работа №3");


        AbstractAction loadFile = new AbstractAction("Загрузить", MainFrame.createImageIcon("/images/open.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                File f = null;
                try {
                    f = new File(new File(".").getCanonicalPath());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                fileopen.setCurrentDirectory(f);

                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    try {
                        originImage.setBmPcontainer(new BMPcontainer(file));
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Ошибка открытия файла",
                                "Inane error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        };
        mainFrame.addAction(loadFile);
        loadFile.putValue(AbstractAction.SHORT_DESCRIPTION, "открыть bmp и показать в сжатом виде в области оригинал");

        AbstractAction saveFile = new AbstractAction("Сохранить", MainFrame.createImageIcon("/images/save.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    JFileChooser fileopen = new JFileChooser();
                    File f = null;
                    try {
                        f = new File(new File(".").getCanonicalPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    fileopen.setCurrentDirectory(f);

                    int ret = fileopen.showDialog(null, "Сохранить файл");
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        File file = fileopen.getSelectedFile();
                        try {

                            BMPcontainer.save(originImage.image2, file);


                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        mainFrame.addAction(saveFile);
        saveFile.putValue(AbstractAction.SHORT_DESCRIPTION, "сохранить область выделения в bmp");

        AbstractAction selectionMode = new AbstractAction("Выделение",
                MainFrame.createImageIcon("/images/selection.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image != null) {
                    originImage.switchSelectionMode(!originImage.isSelectionModeOn());
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не загружено изображение",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(selectionMode);
        selectionMode.putValue(AbstractAction.SHORT_DESCRIPTION, "выделить область на оригинале");

        AbstractAction gaussBlur = new AbstractAction("Размытие по Гауссу",
                MainFrame.createImageIcon("/images/gauss_blur.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    originImage.setImage3(Filters.gaussBlur(originImage.image2));
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(gaussBlur);
        gaussBlur.putValue(AbstractAction.SHORT_DESCRIPTION, "размытие выделения по Гауссу");

        AbstractAction saveFilterResult = new AbstractAction("Выделить фильтр",
                MainFrame.createImageIcon("/images/copy_fts.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image3 != null) {
                    originImage.copyFilterResultToSelection();
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Фильтр не применялся",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(saveFilterResult);
        saveFilterResult.putValue(AbstractAction.SHORT_DESCRIPTION, "копировать фильтр в выделение");

        JMenu operationMenu = new JMenu("Операции задания №3");
        mainFrame.addMenu(operationMenu);

        operationMenu.add(selectionMode);
        operationMenu.add(gaussBlur);
        operationMenu.add(saveFilterResult);

        AbstractAction contacts = new AbstractAction("Контакты", MainFrame.createImageIcon("/images/contact.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(mainFrame, "name: Dorozhko Anton \n mail: dorozhko.a@gmail.com\n ");
            }
        };
        contacts.putValue(AbstractAction.SHORT_DESCRIPTION, "Как со мной связаться ^_^");
        mainFrame.addAction(contacts);
        mainFrame.getAboutMenu().add(contacts);

        AbstractAction aboutAction = new AbstractAction("О программе", MainFrame.createImageIcon("/images/help_info2.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(mainFrame, "Лабораторная работа №3\n"
                        + "Поле \"Оригинал\" для загрузки файла.\n" +
                        "Поле \"Выделение\" для выделения квадрата 256х256 из оригинала\n" +
                        "Поле \"Фильтр\" отображает результат применения фильтра к области \"Выделение\"");
            }
        };
        aboutAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Кэп сообщает, что это краткое описание задания");
        mainFrame.addAction(aboutAction);
        mainFrame.getAboutMenu().add(aboutAction);

        AbstractAction exitAction = new AbstractAction("Выход", MainFrame.createImageIcon("/images/exit.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };

        mainFrame.addAction(exitAction);
        mainFrame.getFileMenu().add(loadFile);
        mainFrame.getFileMenu().add(saveFile);
        mainFrame.getFileMenu().add(exitAction);

        mainFrame.addCanvas(originImage);

        mainFrame.pack();
        mainFrame.setVisible(true);


    }

    class OriginImage extends JPanel {

        public void setBmPcontainer(BMPcontainer bmPcontainer) {
            this.bmPcontainer = bmPcontainer;
            image = BMPcontainer.compress(bmPcontainer);
            repaint();
        }

        private BMPcontainer bmPcontainer = null;
        private BufferedImage image = null;
        private BufferedImage image2 = null;
        private BufferedImage image3 = null;

        public boolean isSelectionModeOn() {
            return selectionModeOn;
        }

        private boolean selectionModeOn = false;

        private static final int BLOCK_SIZE = 256;

        int fieldWidth = BLOCK_SIZE;
        int fieldHeight = BLOCK_SIZE;

        OriginImage() {
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;


            for (int i = 0; i < 3; i++) {
                g2d.drawRect(i * fieldWidth, 0, fieldWidth, fieldHeight);
            }
            g2d.drawString("Оригинал", fieldWidth / 2, fieldHeight + 30);
            g2d.drawString("Выделение", fieldWidth * 3 / 2, fieldHeight + 30);
            g2d.drawString("Фильтр", fieldWidth * 5 / 2, fieldHeight + 30);


            if (image != null) {
                g2d.drawImage(image, null, 0, 0);
            }
            if (image2 != null) {
                g2d.drawImage(image2, null, fieldWidth, 0);
            }
            if (image3 != null) {
                g2d.drawImage(image3, null, fieldWidth * 2, 0);
            }


        }

        public void switchSelectionMode(boolean b) {

            if (b) {
                this.addMouseListener(selectionModeSwitchMouseListener);
            } else {
                this.removeMouseListener(selectionModeSwitchMouseListener);
            }
            selectionModeOn = b;
        }

        MouseMotionListener selectionMouseMotionListener;

        {
            selectionMouseMotionListener = new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (image != null) {
                        int rX = e.getX();
                        int rY = e.getY();

                        if (rX < image.getWidth() && rY < image.getHeight()) {

                            int x;
                            int y;

                            x = rX * bmPcontainer.getWidth() / image.getWidth();
                            y = rY * bmPcontainer.getHeight() / image.getHeight();

                            if (x <= BLOCK_SIZE / 2) {
                                if (y <= BLOCK_SIZE / 2) {
                                    image2 = bmPcontainer.getSubImage(0, 0, BLOCK_SIZE, BLOCK_SIZE);
                                } else if (y >= bmPcontainer.getHeight() - BLOCK_SIZE / 2) {
                                    image2 = bmPcontainer.getSubImage(0, bmPcontainer.getHeight() - BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                                } else {
                                    image2 = bmPcontainer.getSubImage(0, y - BLOCK_SIZE / 2, BLOCK_SIZE, BLOCK_SIZE);
                                }
                            } else if (x >= bmPcontainer.getWidth() - BLOCK_SIZE / 2) {
                                if (y <= BLOCK_SIZE / 2) {
                                    image2 = bmPcontainer.getSubImage(bmPcontainer.getWidth() - BLOCK_SIZE,
                                            0, BLOCK_SIZE, BLOCK_SIZE);
                                } else if (y >= bmPcontainer.getHeight() - BLOCK_SIZE / 2) {
                                    image2 = bmPcontainer.getSubImage(bmPcontainer.getWidth() - BLOCK_SIZE,
                                            bmPcontainer.getHeight() - BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                                } else {
                                    image2 = bmPcontainer.getSubImage(bmPcontainer.getWidth() - BLOCK_SIZE,
                                            y - BLOCK_SIZE / 2, BLOCK_SIZE, BLOCK_SIZE);
                                }
                            } else {
                                if (y <= BLOCK_SIZE / 2) {
                                    image2 = bmPcontainer.getSubImage(x - BLOCK_SIZE / 2,
                                            0, BLOCK_SIZE, BLOCK_SIZE);
                                } else if (y >= bmPcontainer.getHeight() - BLOCK_SIZE / 2) {
                                    image2 = bmPcontainer.getSubImage(x - BLOCK_SIZE / 2,
                                            bmPcontainer.getHeight() - BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                                } else {
                                    image2 = bmPcontainer.getSubImage(x - BLOCK_SIZE / 2,
                                            y - BLOCK_SIZE / 2, BLOCK_SIZE, BLOCK_SIZE);
                                }
                            }


                            repaint();
                        }
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {

                }
            };
        }

        MouseListener selectionModeSwitchMouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println(e.getLocationOnScreen());
                OriginImage.this.addMouseMotionListener(selectionMouseMotionListener);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println(e.getLocationOnScreen());
                OriginImage.this.removeMouseMotionListener(selectionMouseMotionListener);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };

        public void setImage3(BufferedImage image3) {
            this.image3 = image3;
            repaint();
        }


        public void copyFilterResultToSelection() {
            if (image3 != null) {
                image2 = FilterFrames.deepCopy(image3);
                repaint();

            }
        }


    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}
