#ifndef MAINWINDOW_H
#define MAINWINDOW_H
#include "plot.h"

#include <Q3DTheme>
#include <QAction>
#include <QApplication>
#include <QCheckBox>
#include <QGroupBox>
#include <QHBoxLayout>
#include <QLabel>
#include <QMainWindow>
#include <QPushButton>
#include <QRadioButton>
#include <QSettings>
#include <QSlider>
#include <QWidget>
#include <QtDataVisualization>

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = nullptr);
    QCheckBox *boarder;

    QAction *m_act = new QAction();
    QSettings *m_set = new QSettings();

    int plt_type;
    float m_rangeMinX = -10.0f;
    float m_rangeMaxX = 10.0f;

    float m_rangeMinZ = -10.0f;
    float m_rangeMaxZ = 10.0f;

    QGroupBox *color;
    QGroupBox *m_plot;
    QGroupBox *mode;
    QGroupBox *lang;

    QCheckBox *grid;
    QCheckBox *lbl;
    QRadioButton *rq1;
    QRadioButton *rq2;
    QLabel *c_l;
    QLabel *r_l;
    QRadioButton *sin1;
    QRadioButton *sin2;
    QPushButton *bt1;
    QPushButton *bt2;
    QRadioButton *eng;
    QRadioButton *rus;

    ~MainWindow();
public slots:
    void show_grid(QVBoxLayout *layout, QtDataVisualization::Q3DTheme *theme, QGridLayout *c);
    void show_label_boarder(QVBoxLayout *layout,
                            QtDataVisualization::Q3DTheme *theme,
                            QGridLayout *c);
    void show_label(QVBoxLayout *layout, Plot *plot, QGridLayout *c);
    void set_color(QGroupBox *color, QtDataVisualization::Q3DSurface *graph);
    void select_mode(QtDataVisualization::Q3DSurface *graph, QGroupBox *mode);
    void select_plot(Plot *a, QGroupBox *mode);
    void c_range(QVBoxLayout *vLayout, Plot *a);
    void r_range(QVBoxLayout *vLayout, Plot *a);
    void set_language(QGroupBox *lang);
    void doSave();
    void create_new();
    void load();

private:
    Ui::MainWindow *ui;
};

#endif // MAINWINDOW_H
