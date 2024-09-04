#include "mainwindow.h"
#include "plot.h"
#include "ui_mainwindow.h"

#include <QApplication>
#include <QCheckBox>
#include <QGroupBox>
#include <QHBoxLayout>
#include <QLabel>
#include <QPushButton>
#include <QRadioButton>
#include <QSlider>
#include <QStyle>
#include <QWidget>
#include <QtDataVisualization>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{}

void MainWindow::show_grid(QVBoxLayout *layout, QtDataVisualization::Q3DTheme *theme, QGridLayout *c)
{
    grid = new QCheckBox(QString("Show grid"));
    c->addWidget(grid);
    layout->addLayout(c);

    QObject::connect(grid, &QCheckBox::stateChanged, [=](int state) {
        if (state == Qt::Checked) {
            theme->setGridEnabled(true);
        } else {
            theme->setGridEnabled(false);
        }
    });
}

void MainWindow::show_label_boarder(QVBoxLayout *layout,
                                    QtDataVisualization::Q3DTheme *theme,
                                    QGridLayout *c)
{
    boarder = new QCheckBox(QString("Show label border"));
    c->addWidget(boarder);
    layout->addLayout(c);
    QObject::connect(boarder, &QCheckBox::stateChanged, [=](int state) {
        if (state == Qt::Checked) {
            theme->setLabelBackgroundEnabled(true);
        } else {
            theme->setLabelBackgroundEnabled(false);
        }
    });
}

void MainWindow::show_label(QVBoxLayout *layout, Plot *plot, QGridLayout *c)
{
    lbl = new QCheckBox(QString("Show label"));

    c->addWidget(lbl);
    layout->addLayout(c);

    QObject::connect(lbl, &QCheckBox::stateChanged, [=](int state) {
        if (state == Qt::Checked) {
            boarder->setEnabled(true);

            plot->graph->axisX()->setTitle(QStringLiteral("X"));
            plot->graph->axisX()->setTitleVisible(true);

            plot->graph->axisY()->setTitle(QStringLiteral("Y"));
            plot->graph->axisY()->setTitleVisible(true);

            plot->graph->axisZ()->setTitle(QStringLiteral("Z"));
            plot->graph->axisZ()->setTitleVisible(true);

            plot->graph->axisX()->setLabelFormat("%.1f");
            plot->graph->axisY()->setLabelFormat("%.1f");
            plot->graph->axisZ()->setLabelFormat("%.1f");
        } else {
            plot->graph->axisX()->setLabelFormat("");
            plot->graph->axisY()->setLabelFormat("");
            plot->graph->axisZ()->setLabelFormat("");
            plot->graph->axisX()->setTitleVisible(false);
            plot->graph->axisY()->setTitleVisible(false);
            plot->graph->axisZ()->setTitleVisible(false);
            boarder->setEnabled(false);
        }
    });
}

void MainWindow::set_color(QGroupBox *color, QtDataVisualization::Q3DSurface *graph)
{
    bt1 = new QPushButton;
    bt2 = new QPushButton;
    QHBoxLayout *tmp = new QHBoxLayout;

    color->setFixedSize(300, 400);

    bt1->setFixedSize(100, 250);
    bt2->setFixedSize(100, 250);

    bt1->setStyleSheet("background: qlineargradient(x1:0, y1:0, x2:0, y2:1, stop:0 #fde725, "
                       "stop:0.25 #5ec962, stop:0.5 #21918c, stop:0.75 #3b528b);");
    QObject::connect(bt1, &QPushButton::clicked, [=]() {
        QLinearGradient gr;
        gr.setColorAt(0.0, QColor(68, 1, 84));
        gr.setColorAt(0.25, QColor(59, 82, 139));
        gr.setColorAt(0.45, QColor(33, 145, 140));
        gr.setColorAt(0.65, QColor(94, 201, 98));
        gr.setColorAt(1.0, QColor(253, 231, 37));

        if (plt_type == 1) {
            graph->seriesList().at(0)->setBaseGradient(gr);
            graph->seriesList().at(0)->setColorStyle(
                QtDataVisualization::Q3DTheme::ColorStyleRangeGradient);
        } else {
            graph->seriesList().at(1)->setBaseGradient(gr);
            graph->seriesList().at(1)->setColorStyle(
                QtDataVisualization::Q3DTheme::ColorStyleRangeGradient);
        }
    });

    tmp->addWidget(bt1);
    bt2->setStyleSheet("background: qlineargradient(x1:0, y1:0, x2:0, y2:1, stop:0.75 #FFD700, "
                       "stop:0.5 #FF8C00, stop:0.25 #FF4500, stop:0 #8B0000);");
    QObject::connect(bt2, &QPushButton::clicked, [=]() {
        QLinearGradient gr;
        gr.setColorAt(0.0, QColor(255, 215, 00));
        gr.setColorAt(0.33, QColor(255, 140, 0));
        gr.setColorAt(0.67, QColor(255, 69, 0));
        gr.setColorAt(1.0, QColor(139, 0, 0));

        if (plt_type == 1) {
            graph->seriesList().at(0)->setBaseGradient(gr);
            graph->seriesList().at(0)->setColorStyle(
                QtDataVisualization::Q3DTheme::ColorStyleRangeGradient);
        } else {
            graph->seriesList().at(1)->setBaseGradient(gr);
            graph->seriesList().at(1)->setColorStyle(
                QtDataVisualization::Q3DTheme::ColorStyleRangeGradient);
        }
    });
    tmp->addWidget(bt2);

    color->setLayout(tmp);
}
void MainWindow::select_mode(QtDataVisualization::Q3DSurface *graph, QGroupBox *mode)
{
    rq1 = new QRadioButton(QString("Item"));
    rq2 = new QRadioButton(QString("No selection"));

    mode->setFixedSize(300, 200);

    QVBoxLayout *tmp = new QVBoxLayout;
    QObject::connect(rq1, &QRadioButton::toggled, [=](bool state) {
        if (state) {
            graph->setSelectionMode(QtDataVisualization::QAbstract3DGraph::SelectionItem);
        }
    });

    tmp->addWidget(rq1);

    QObject::connect(rq2, &QRadioButton::toggled, [=](bool state) {
        if (state) {
            graph->setSelectionMode(QtDataVisualization::QAbstract3DGraph::SelectionNone);
        }
    });

    tmp->addWidget(rq2);
    mode->setLayout(tmp);
}

void MainWindow::select_plot(Plot *a, QGroupBox *plot)
{
    sin1 = new QRadioButton(QString("Sinc1"));
    sin2 = new QRadioButton(QString("Sinc2"));

    plot->setFixedSize(300, 200);

    QVBoxLayout *tmp = new QVBoxLayout;
    QObject::connect(sin1, &QRadioButton::toggled, [=](bool state) {
        if (state) {
            a->graph->seriesList().at(0)->setVisible(true);
            a->graph->seriesList().at(1)->setVisible(false);
            plt_type = 1;
        }
    });

    tmp->addWidget(sin1);

    QObject::connect(sin2, &QRadioButton::toggled, [=](bool state) {
        if (state) {
            a->graph->seriesList().at(1)->setVisible(true);
            a->graph->seriesList().at(0)->setVisible(false);
            plt_type = 2;
        }
    });

    tmp->addWidget(sin2);

    plot->setLayout(tmp);
}

void MainWindow::set_language(QGroupBox *lang)
{
    eng = new QRadioButton(QString("English"));
    rus = new QRadioButton(QString("Русский"));

    lang->setFixedSize(300, 200);

    QVBoxLayout *tmp = new QVBoxLayout;
    QObject::connect(eng, &QRadioButton::toggled, [=](bool state) {
        if (state) {
            mode->setTitle(QString("Selection mode"));
            m_plot->setTitle(QString("Plot"));
            color->setTitle(QString("Custom gradient"));
            lang->setTitle(QString("Set language"));
            grid->setText(QString("Show grid"));
            boarder->setText(QString("Show label border"));
            lbl->setText(QString("Show label"));
            rq1->setText(QString("Item"));
            rq2->setText(QString("No selection"));
            r_l->setText(QString("Row range"));
            c_l->setText(QString("Column range"));
        }
    });

    tmp->addWidget(eng);

    QObject::connect(rus, &QRadioButton::toggled, [=](bool state) {
        if (state) {
            mode->setTitle(QString("Выбор режима"));
            m_plot->setTitle(QString("График"));
            color->setTitle(QString("Градиент"));
            lang->setTitle(QString("Выберите язык"));
            grid->setText(QString("Сетка"));
            boarder->setText(QString("Границы разметки"));
            lbl->setText(QString("Разметка"));
            rq1->setText(QString("Выделение"));
            rq2->setText(QString("Без выделения"));
            r_l->setText(QString("Диапазон строк"));
            c_l->setText(QString("Диапазон столбцов"));
        }
    });

    tmp->addWidget(rus);
    lang->setLayout(tmp);
}

void MainWindow::c_range(QVBoxLayout *layout, Plot *a)
{
    int sampleCountX = 50;
    float sampleMin = -10.0f;
    float sampleMax = 10.0f;
    float m_stepX = (sampleMax - sampleMin) / float(sampleCountX - 1);

    c_l = new QLabel(QString("Column range"));
    c_l->setFixedHeight(50);

    QSlider *s2 = new QSlider(Qt::Horizontal);
    QSlider *s1 = new QSlider(Qt::Horizontal);
    s1->setMinimum(0);
    s1->setMaximum(sampleCountX - 2);
    s1->setValue(0);
    s1->setMaximumSize(350, 50);

    connect(s1, &QSlider::valueChanged, this, [=]() {
        m_rangeMinX = sampleMin + (s1->value() * m_stepX);
        a->graph->axisX()->setMin(m_rangeMinX);
    });

    s2->setMinimum(1);
    s2->setMaximum(sampleCountX - 1);
    s2->setValue(sampleCountX - 1);
    s2->setMaximumSize(350, 50);

    connect(s2, &QSlider::valueChanged, this, [=]() {
        m_rangeMaxX = sampleMin + (s2->value() * m_stepX);
        a->graph->axisX()->setMax(m_rangeMaxX);
    });

    layout->addWidget(c_l);
    layout->addWidget(s1);
    layout->addWidget(s2);
}

void MainWindow::r_range(QVBoxLayout *layout, Plot *a)
{
    int sampleCountZ = 50;
    float sampleMin = -10.0f;
    float sampleMax = 10.0f;
    float m_stepZ = (sampleMax - sampleMin) / float(sampleCountZ - 1);

    r_l = new QLabel(QString("Row range"));
    r_l->setFixedHeight(50);

    QSlider *s2 = new QSlider(Qt::Horizontal);
    QSlider *s1 = new QSlider(Qt::Horizontal);
    s1->setMinimum(0);
    s1->setMaximum(sampleCountZ - 2);
    s1->setValue(0);
    s1->setMaximumSize(350, 50);

    connect(s1, &QSlider::valueChanged, this, [=]() {
        m_rangeMinZ = sampleMin + (s1->value() * m_stepZ);
        a->graph->axisZ()->setMin(m_rangeMinZ);
    });

    s2->setMinimum(1);
    s2->setMaximum(sampleCountZ - 1);
    s2->setValue(sampleCountZ - 1);
    s2->setMaximumSize(350, 50);

    connect(s2, &QSlider::valueChanged, this, [=]() {
        m_rangeMaxZ = sampleMin + (s2->value() * m_stepZ);
        a->graph->axisZ()->setMax(m_rangeMaxZ);
    });
    layout->addWidget(r_l);
    layout->addWidget(s1);
    layout->addWidget(s2);
}

void MainWindow::create_new()
{
    Plot *plot = new Plot();

    plot->fillSin1Proxy();
    plot->m_ser1->setDataProxy(plot->m_data1);
    plot->graph->addSeries(plot->m_ser1);
    plt_type = 1;

    plot->fillSin2Proxy();
    plot->m_ser2->setDataProxy(plot->m_data2);
    plot->graph->addSeries(plot->m_ser2);
    plt_type = 1;
    plot->graph->seriesList().at(1)->setVisible(false);

    QHBoxLayout *hLayout = new QHBoxLayout;
    QVBoxLayout *vLayout = new QVBoxLayout;
    hLayout->addWidget(plot->container);
    QWidget *widget = new QWidget();
    QtDataVisualization::Q3DTheme *theme = plot->graph->activeTheme();
    QGridLayout *c = new QGridLayout();

    color = new QGroupBox(QString("Custom gradient"));
    mode = new QGroupBox(QString("Selection mode"));
    m_plot = new QGroupBox(QString("Plot"));
    lang = new QGroupBox(QString("Set language"));
    QMenuBar *menu_bar = new QMenuBar();

    theme->setGridEnabled(true);
    show_grid(vLayout, theme, c);
    show_label_boarder(vLayout, theme, c);
    show_label(vLayout, plot, c);
    select_plot(plot, m_plot);
    select_mode(plot->graph, mode);
    set_color(color, plot->graph);
    set_language(lang);

    vLayout->addWidget(m_plot);
    vLayout->addWidget(mode);
    vLayout->addWidget(lang);
    c_range(vLayout, plot);
    r_range(vLayout, plot);

    vLayout->addWidget(color);
    hLayout->addLayout(vLayout);

    m_act = new QAction(style()->standardIcon(QStyle::SP_DialogSaveButton), QString("&Save"), this);
    m_act->setShortcuts(QKeySequence::Save);
    m_act->setStatusTip(QString("Save state"));
    connect(m_act, &QAction::triggered, this, &MainWindow::doSave);
    QMenu *menu = menu_bar->addMenu(QString("&Settings"));

    QAction *m_load = new QAction(style()->standardIcon(QStyle::SP_BrowserReload),
                                  QString("&Load"),
                                  this);
    m_load->setShortcuts(QKeySequence::Refresh);
    m_load->setStatusTip(QString("Load"));
    connect(m_load, &QAction::triggered, this, &MainWindow::load);

    menu->addAction(m_load);
    menu->addAction(m_act);
    this->setMenuBar(menu_bar);
    QToolBar *tool_bar = addToolBar(QString("Toolbar"));
    tool_bar->addAction(m_act);
    tool_bar->addAction(m_load);
    this->addToolBar(tool_bar);
    widget->setLayout(hLayout);

    setCentralWidget(widget);
    resize(1080, 1080);
}

void MainWindow::doSave()
{
    m_set->beginGroup(QString("Settings"));
    m_set->setValue("show_grid", grid->isChecked());
    m_set->setValue("show_label", lbl->isChecked());
    m_set->setValue("show_border", boarder->isChecked());
    m_set->setValue("show_mode", mode->isChecked());
    m_set->setValue("sin1", sin1->isChecked());
    m_set->setValue("sin2", sin2->isChecked());
    m_set->setValue("english", eng->isChecked());
    m_set->setValue("russian", rus->isChecked());
    m_set->endGroup();
}

void MainWindow::load()
{
    m_set->beginGroup(QString("Settings"));
    lbl->setChecked(m_set->value("show_label", true).toBool());
    grid->setChecked(m_set->value("show_grid", true).toBool());
    boarder->setChecked(m_set->value("show_border", true).toBool());
    sin1->setChecked(m_set->value("sin1", true).toBool());
    sin2->setChecked(m_set->value("sin2", true).toBool());
    eng->setChecked(m_set->value("english", true).toBool());
    rus->setChecked(m_set->value("russian", true).toBool());
    m_set->endGroup();
}
MainWindow::~MainWindow()
{
    delete ui;
}
