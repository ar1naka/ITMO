#ifndef PLOT_H
#define PLOT_H

#include <QApplication>
#include <QWidget>
#include <QtDataVisualization>

class Plot
{
public:
    Plot();
    QtDataVisualization::Q3DSurface *graph = new QtDataVisualization::Q3DSurface();
    QWidget *container = QWidget::createWindowContainer(graph);
    QtDataVisualization::QSurface3DSeries *m_seties = new QtDataVisualization::QSurface3DSeries();
    QtDataVisualization::QSurfaceDataProxy *m_data1 = new QtDataVisualization::QSurfaceDataProxy();
    QtDataVisualization::QSurfaceDataProxy *m_data2 = new QtDataVisualization::QSurfaceDataProxy();

    QtDataVisualization::QSurface3DSeries *m_ser1 = new QtDataVisualization::QSurface3DSeries();
    QtDataVisualization::QSurface3DSeries *m_ser2 = new QtDataVisualization::QSurface3DSeries();

    void fillSin1Proxy();
    void fillSin2Proxy();

private:
};
#endif // PLOT_H
