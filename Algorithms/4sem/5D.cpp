#pragma GCC optimize("Ofast")

#include <iostream>
#include <cmath>
#include <iomanip>

using namespace std;

struct Point {
    double x, y;
};

const double EPS = 1e-8;

int main() {
    Point center{};
    double r, a, b, c;
    cin >> center.x >> center.y >> r >> a >> b >> c;

    double alpha = a * a + b * b;
    double beta = 2 * (center.x * a * b - center.y * a * a + b * c);
    double gamma = 2 * center.x * a * c + pow(center.x * a, 2) + c * c
                   + pow(center.y * a, 2) - pow(r * a, 2);
    double D = beta * beta - 4 * alpha * gamma;

    if (D < 0) {
        cout << 0 << endl;
    } else if (abs(D) < EPS) {
        double y = -beta / (2 * alpha);
        double x = (b * (b * center.x - a * center.y) - a * c) / (pow(a,2) + pow(b, 2));
        cout << 1 << endl;
        cout << fixed << setprecision(8) << x << " " << fixed << setprecision(8) << y << endl;
    } else {
        D = sqrt(D);
        double y1 = (-beta - D) / (2 * alpha );
        double x1 = abs(a) < EPS ? 0 : -(b * y1 + c) / a;
        double y2 = (-beta + D) / (2 * alpha);
        double x2 = abs(a) < EPS ? 0 : -(b * y2 + c) / a;
        cout << 2 << endl;
        cout << fixed << setprecision(8) << x1 << " " << fixed << setprecision(8) << y1 << endl;
        cout << fixed << setprecision(8) << x2 << " " << fixed << setprecision(8) << y2 << endl;
    }
    return 0;
}