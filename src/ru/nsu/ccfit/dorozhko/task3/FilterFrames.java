package ru.nsu.ccfit.dorozhko.task3;

import ru.nsu.ccfit.dorozhko.MainFrame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import java.util.Hashtable;
import java.util.logging.Filter;

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


        AbstractAction loadFile = new AbstractAction("Загрузить", MainFrame.createImageIcon("/images/document-open-folder.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                File f = null;
                try {
                    f = new File(new File("./src").getCanonicalPath());
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

        AbstractAction saveFile = new AbstractAction("Сохранить", MainFrame.createImageIcon("/images/document-export.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    JFileChooser fileopen = new JFileChooser();

                    File f = null;
                    try {
                        f = new File(new File("./src").getCanonicalPath());
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
                MainFrame.createImageIcon("/images/edit-select.png")) {
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

        //TODO: change
        AbstractAction gaussBlur = new AbstractAction("Размытие по Гауссу",
                MainFrame.createImageIcon("/images/gaussblur.png")) {
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
                MainFrame.createImageIcon("/images/draw-arrow-back.png")) {
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

        // TODO: filters

        AbstractAction watercolor = new AbstractAction("Акварель",
                MainFrame.createImageIcon("/images/watercolor.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    originImage.setImage3(Filters.sharpen(Filters.median(originImage.image2, 5)));
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(watercolor);
        watercolor.putValue(AbstractAction.SHORT_DESCRIPTION, "Акварель");

        AbstractAction blackWhiteThreshold = new AbstractAction("Черно-белое",
                MainFrame.createImageIcon("/images/blackWhite.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    final BufferedImage saved = deepCopy(originImage.image2);
                    int result = JOptionPane.showConfirmDialog(null, new JPanel() {
                        {
                            JSlider thresholdSlider = new JSlider(
                                    SwingConstants.HORIZONTAL,
                                    0, 255, 140);

                            thresholdSlider.setPaintLabels(true);

                            Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
                            table.put (0, new JLabel(String.valueOf(0)));
                            table.put (127, new JLabel(String.valueOf(127)));
                            table.put (255, new JLabel(String.valueOf(255)));
                            thresholdSlider.setLabelTable (table);

                            originImage.setImage3(Filters.blackWhite(saved, thresholdSlider.getValue()));
                            thresholdSlider.addChangeListener(new ChangeListener() {
                                @Override
                                public void stateChanged(ChangeEvent e) {
                                    JSlider source = (JSlider)e.getSource();
                                    originImage.setImage3(Filters.blackWhite(saved, source.getValue()));
                                }
                            });
                            add(thresholdSlider);
                        }
                    }, "Настройки фильтра Черно-белое", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.CANCEL_OPTION) {
                        originImage.setImage3(saved);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(blackWhiteThreshold);
        blackWhiteThreshold.putValue(AbstractAction.SHORT_DESCRIPTION, "Черно-белое");


        AbstractAction magnify = new AbstractAction("Увеличение в 2 раза",
                MainFrame.createImageIcon("/images/magnify.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    originImage.setImage3(Filters.gaussBlur(
                            Filters.magnifyX2(originImage.image2.getSubimage(64, 64, 128, 128))));
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(magnify);
        magnify.putValue(AbstractAction.SHORT_DESCRIPTION, "Увеличение в 2 раза");

        AbstractAction fsDithering = new AbstractAction("Флойд-Стейнберг", null) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    originImage.setImage3(Filters.fsDithering(originImage.image2));
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(fsDithering);
        fsDithering.putValue(AbstractAction.SHORT_DESCRIPTION, "Флойд-Стейнберг");


        AbstractAction parametrizedGaussBlur = new AbstractAction("Параметризованное размытие по Гауссу",
                MainFrame.createImageIcon("/images/blur.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    final BufferedImage saved = deepCopy(originImage.image2);
                    int result = JOptionPane.showConfirmDialog(null, new JPanel() {
                        {
                            JSlider blurRadius = new JSlider(
                                    SwingConstants.HORIZONTAL,
                                    1, 6, 3);

                            blurRadius.setPaintLabels(true);

                            Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
                            table.put (0, new JLabel(String.valueOf(0)));
                            table.put (3, new JLabel(String.valueOf(3)));
                            table.put (6, new JLabel(String.valueOf(6)));
                            blurRadius.setLabelTable(table);

                            originImage.setImage3(Filters.parametrizedGaussBlur(saved, blurRadius.getValue()));
                            blurRadius.addChangeListener(new ChangeListener() {
                                @Override
                                public void stateChanged(ChangeEvent e) {
                                    JSlider source = (JSlider) e.getSource();
                                    originImage.setImage3(Filters.parametrizedGaussBlur(saved, source.getValue()));
                                }
                            });
                            add(blurRadius);
                        }
                    }, "Настройки фильтра размытие по Гауссу", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.CANCEL_OPTION) {
                        originImage.setImage3(saved);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(parametrizedGaussBlur);
        parametrizedGaussBlur.putValue(AbstractAction.SHORT_DESCRIPTION, "Параметризованное размытие по Гауссу");

        AbstractAction grayScale = new AbstractAction("Градации серого",
                MainFrame.createImageIcon("/images/grey.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    originImage.setImage3(Filters.grayScale(originImage.image2));
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(grayScale);
        grayScale.putValue(AbstractAction.SHORT_DESCRIPTION, "Градации серого");


        AbstractAction negative = new AbstractAction("Негатив",
                MainFrame.createImageIcon("/images/negative.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    originImage.setImage3(Filters.negative(originImage.image2));
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(negative);
        negative.putValue(AbstractAction.SHORT_DESCRIPTION, "Негатив");

        AbstractAction orderedDithering = new AbstractAction("orderedDithering",
                MainFrame.createImageIcon("/images/ordered_dithering.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    originImage.setImage3(Filters.orderDithering(originImage.image2));
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        mainFrame.addAction(orderedDithering);
        orderedDithering.putValue(AbstractAction.SHORT_DESCRIPTION, "orderedDithering");



        AbstractAction roberts = new AbstractAction("Оператор Робертса",
                null) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    final BufferedImage saved = deepCopy(originImage.image2);
                    final BufferedImage roberts = Filters.roberts(saved);
                    originImage.setImage3(roberts);

                    int result = JOptionPane.showConfirmDialog(null, new JPanel() {
                        {
                            JSlider borderFeeling = new JSlider(
                                    SwingConstants.HORIZONTAL,
                                    0, 10, 1);

                            borderFeeling.setPaintLabels(true);

                            Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
                            table.put (0, new JLabel(String.valueOf(0)));
                            table.put (5, new JLabel(String.valueOf(5)));
                            table.put (10, new JLabel(String.valueOf(10)));
                            borderFeeling.setLabelTable(table);

                            borderFeeling.addChangeListener(new ChangeListener() {
                                @Override
                                public void stateChanged(ChangeEvent e) {
                                    JSlider source = (JSlider) e.getSource();
                                    originImage.setImage3(Filters.brightness(roberts, source.getValue()));
                                }
                            });
                            add(borderFeeling);
                        }
                    }, "Настройки фильтра Оператор Робертса", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.CANCEL_OPTION) {
                        originImage.setImage3(saved);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(roberts);
        roberts.putValue(AbstractAction.SHORT_DESCRIPTION, "Оператор Робертса");

        AbstractAction sobel = new AbstractAction("Оператор Собеля",
                null) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    final BufferedImage saved = deepCopy(originImage.image2);
                    final BufferedImage sobeled = Filters.sobel(saved);
                    originImage.setImage3(sobeled);

                    int result = JOptionPane.showConfirmDialog(null, new JPanel() {
                        {
                            JSlider borderFeeling = new JSlider(
                                    SwingConstants.HORIZONTAL,
                                    0, 10, 1);

                            borderFeeling.setPaintLabels(true);

                            Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
                            table.put (0, new JLabel(String.valueOf(0)));
                            table.put (5, new JLabel(String.valueOf(5)));
                            table.put (10, new JLabel(String.valueOf(10)));
                            borderFeeling.setLabelTable(table);

                            borderFeeling.addChangeListener(new ChangeListener() {
                                @Override
                                public void stateChanged(ChangeEvent e) {
                                    JSlider source = (JSlider) e.getSource();
                                    originImage.setImage3(Filters.brightness(sobeled, source.getValue()));
                                }
                            });
                            add(borderFeeling);
                        }
                    }, "Настройки фильтра Оператор Собеля", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.CANCEL_OPTION) {
                        originImage.setImage3(saved);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(sobel);
        sobel.putValue(AbstractAction.SHORT_DESCRIPTION, "Оператор Собеля");

        AbstractAction stamp = new AbstractAction("Штамп",
                MainFrame.createImageIcon("/images/stamp.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originImage.image2 != null) {
                    originImage.setImage3(Filters.stamp(originImage.image2));
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Не выделена область для обработки",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        mainFrame.addAction(stamp);
        stamp.putValue(AbstractAction.SHORT_DESCRIPTION, "Штамп");


        JMenu operationMenu = new JMenu("Операции задания №3");
        mainFrame.addMenu(operationMenu);

        operationMenu.add(selectionMode);
        operationMenu.add(gaussBlur);
        operationMenu.add(saveFilterResult);
        operationMenu.add(watercolor);
        operationMenu.add(blackWhiteThreshold);
        operationMenu.add(magnify);
        operationMenu.add(fsDithering);
        operationMenu.add(parametrizedGaussBlur);
        operationMenu.add(grayScale);
        operationMenu.add(negative);
        operationMenu.add(orderedDithering);
        operationMenu.add(roberts);
        operationMenu.add(sobel);
        operationMenu.add(stamp);


        AbstractAction contacts = new AbstractAction("Контакты", MainFrame.createImageIcon("/images/contacts.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(mainFrame, "name: Dorozhko Anton \n mail: dorozhko.a@gmail.com\n ");
            }
        };
        contacts.putValue(AbstractAction.SHORT_DESCRIPTION, "Как со мной связаться ^_^");
        mainFrame.addAction(contacts);
        mainFrame.getAboutMenu().add(contacts);

        AbstractAction aboutAction = new AbstractAction("О программе", MainFrame.createImageIcon("/images/help.png")) {
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

        AbstractAction exitAction = new AbstractAction("Выход", MainFrame.createImageIcon("/images/cross.png")) {
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
