#include <iostream>
#include <vector>
#include <algorithm>
#pragma GCC optimize("Ofast")
#include <iomanip>
#include <set>

#include <cmath>

using namespace std;

struct Point{
    long long x, y;
    Point(long long x = 0, long long y = 0) : x(x), y(y) {}

};
vector<Point> p;
int an(Point a, Point b, Point c) {
    long long q = (b.x - a.x) * (c.y - b.y) - (b.y - a.y) * (c.x - b.x);
    return (q > 0) - (q < 0);
}

bool inside(Point q, int n) {
    if (an(p[1], p[2], q) < 0 || an(p[1], p[n], q) > 0) return false;

    int l = 2, r = n, m;

    while (l + 1 < r) {
        m = (l + r) / 2;
        if (an(p[1], p[m], q) < 0) {
            r = m;
        } else {
            l = m;
        }
    }
    return an(p[l], p[r], q) >= 0;

}
int main() {
    int n, m ,k;
    cin >> n >> m >> k;
    p.resize(n + m);
    for (int i = 1; i <= n; i++) {
        int x, y;
        cin >> x >> y;
        p[i] = Point(x, y);
    }
    int res = 0;

    for (int i = 1; i <= m; ++i) {
        int x, y;
        cin >> x >> y;
        if (inside(Point(x, y), n)) {
            res++;
        }

    }
    if (res < k) {
        cout << "NO" << endl;
    } else {
        cout << "YES" << endl;
    }

}