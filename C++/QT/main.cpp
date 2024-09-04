#include "mainwindow.h"

int main(int argc, char **argv)
{
    QApplication app(argc, argv);
    QCoreApplication::setOrganizationName("settings");
    QSettings::setDefaultFormat(QSettings::IniFormat);
    QSettings::setPath(QSettings::IniFormat,
                       QSettings::UserScope,
                       QCoreApplication::applicationDirPath());

    MainWindow *window = new MainWindow();
    window->create_new();
    window->show();
    return app.exec();
}
