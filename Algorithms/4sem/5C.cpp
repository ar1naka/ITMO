#include <iostream>
#include <iomanip>
#include <cmath>

using namespace std;

const double EPS = 1E-9;

struct Point {
    double x, y;

    bool operator<(const Point& p) const {
        return x < p.x - EPS || (abs(x - p.x) < EPS && y < p.y - EPS);
    }

    friend ostream& operator<<(std::ostream& os, const Point& point) {
        return os << fixed << setprecision(8) << point.x << " " << point.y;
    }
};

struct line {
    double a, b, c;

    line() = default;
    line(const Point &p, const Point &q) {
        a = p.y - q.y;
        b = q.x - p.x;
        c = -a * p.x - b * p.y;
        double z = sqrt(a * a + b * b);
        if (abs(z) > EPS)
            a /= z, b /= z, c /= z;
    }


    double dist(const Point &p) const { return a * p.x + b * p.y + c; }
};


inline double det(double a, double b, double c, double d) { return a * d - b * c; }

inline bool between(double l, double r, double x) { return min(l, r) <= x + EPS && x <= max(l, r) + EPS;; }

inline bool intersect(double a, double b, double c, double d) {
    if (a > b) swap(a, b);
    if (c > d) swap(c, d);
    return max(a, c) <= min(b, d) + EPS;
}

void point_swap(Point& a, Point& b) {
    Point temp = a;
    a = b;
    b = temp;
}
bool solve(Point a, Point b, Point c, Point d, Point& left, Point& right) {
    if (!intersect(a.x, b.x, c.x, d.x) || !intersect(a.y, b.y, c.y, d.y)) {return false;}
    line m(a, b);
    line n(c, d);
    double zn = det(m.a, m.b, n.a, n.b);
    if (abs(zn) < EPS) {
        if (abs(m.dist(c)) > EPS || abs(n.dist(a)) > EPS) { return false;}
        if (b < a) { point_swap(a, b); }
        if (d < c) { swap(c, d);}
        left = max(a, c);
        right = min(b, d);
        return true;
    } else {
        double inv_zn = 1.0 / zn;
        left.x = right.x = -det(m.c, m.b, n.c, n.b) * inv_zn;
        left.y = right.y = -det(m.a, m.c, n.a, n.c) * inv_zn;
        return between(a.x, b.x, left.x) && between(a.y, b.y, left.y) &&
               between(c.x, d.x, left.x) && between(c.y, d.y, left.y);
    }

}

int compare(const Point& A, const Point& B) {
    if (abs(A.x - B.x) < EPS) {
        return A.y < B.y;
    }
    return A.x < B.x;
}

void print(const Point& A, const Point& B) {
    if (!compare(A, B)) {
        cout << B << endl;
        cout << A << endl;
    } else {
        cout << A << endl;
        cout << B << endl;
    }
}


int main() {
    Point a{}, b{}, c{}, d{}, end{}, start{};
    cin >> a.x >> a.y >> b.x >> b.y >> c.x >> c.y >> d.x >> d.y;

    if (solve(a, b, c, d, start, end)) {
        if (start.x == end.x && start.y == end.y) {
            cout << start << endl;
        } else {
            print(start, end);
        }

    } else {
        cout << "Empty";
    }
    return 0;
}